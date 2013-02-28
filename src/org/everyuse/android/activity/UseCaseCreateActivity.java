package org.everyuse.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.model.User;
import org.everyuse.android.util.ErrorHelper;
import org.everyuse.android.util.ImageDownloader;
import org.everyuse.android.util.ImageHelper;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class UseCaseCreateActivity extends SherlockActivity {
	private final String TAG = getClass().getSimpleName();

	private AutoCompleteTextView et_item;
	private AutoCompleteTextView et_purpose;
	private Spinner sp_purpose_type;
	private Spinner sp_place;
	private ImageView iv_photo;
	private ImageButton btn_photo_select;

	private ImageDownloader image_downloader = new ImageDownloader();
	private UseCase old_use_case = null;

	private long ref_all_id = 0L;
	private long ref_item_id = 0L;
	private long ref_purpose_id = 0L;

	// photo
	private boolean new_photo_selected = false;
	private File raw_photo_file;
	private File processed_photo_file;
	private File upload_photo_file;
	private static final String STATE_PHOTO_PATH = "photo_path";
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;

	// EXTRAs
	public static final String EXTRA_USE_CASE = "extra_use_case";
	public static final String EXTRA_ITEM = "extra_item";
	public static final String EXTRA_REF_ALL_ID = "extra_ref_all";
	public static final String EXTRA_REF_ITEM_ID = "extra_ref_item";
	public static final String EXTRA_REF_PURPOSE_ID = "extra_ref_purpose";

	// modes
	private static final String MODE_CREATE = "mode_create";
	private static final String MODE_EDIT = "mode_edit";
	private String mode = MODE_CREATE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		setTitle(R.string.title_activity_create);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// UI 초기화
		initUI();

		// handle intent
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		handleIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return true;
	}

	private void handleIntent(Intent intent) {
		if (intent != null) {
			// edit 모드
			Parcelable parcel = intent.getParcelableExtra(EXTRA_USE_CASE);
			if (parcel != null && parcel instanceof UseCase) { // MODE_EDIT
				old_use_case = (UseCase) parcel;
				fillWithUseCase(old_use_case);
				mode = MODE_EDIT;
			} else { // MODE_CREATE
				mode = MODE_CREATE;
			}

			// EXTRA_ITEM
			String item_text = intent.getStringExtra(EXTRA_ITEM);
			if (item_text != null) {
				et_item.setText(item_text);
			}

			// EXTRA_REF
			ref_all_id = intent.getLongExtra(EXTRA_REF_ALL_ID, 0);
			ref_item_id = intent.getLongExtra(EXTRA_REF_ITEM_ID, 0);
			ref_purpose_id = intent.getLongExtra(EXTRA_REF_PURPOSE_ID, 0);
		}
	}

	private void fillWithUseCase(UseCase use_case) {
		et_item.setText(use_case.item);
		et_purpose.setText(use_case.purpose);

		// 위해, 로써 선택
		setStringArraySpinner(sp_purpose_type, use_case.purpose_type, R.array.purpose_type);

		// 장소 선택
		setStringArraySpinner(sp_place, use_case.place, R.array.place_student);

		// 기존 사진 표시
		image_downloader.download(use_case.getPhotoLargeURL(), iv_photo);
	}

	private void setStringArraySpinner(Spinner spinner, String textToSelect, int stringArrayId) {
		List<String> stringArray = Arrays.asList(getResources().getStringArray(stringArrayId));
		int index = stringArray.indexOf(textToSelect);

		if (index != -1) {
			spinner.setSelection(index);
		} else {
			spinner.setSelection(0);
		}
	}

	private void initUI() {
		et_item = (AutoCompleteTextView) findViewById(R.id.et_item);
		et_purpose = (AutoCompleteTextView) findViewById(R.id.et_purpose);

		// purpose type Spinner 초기화
		sp_purpose_type = (Spinner) findViewById(R.id.sp_purpose_type);
		ArrayAdapter<CharSequence> purpose_type_adapter = ArrayAdapter.createFromResource(this, R.array.purpose_type,
				android.R.layout.simple_spinner_item);
		purpose_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_purpose_type.setAdapter(purpose_type_adapter);

		// 장소 Spinner 초기화
		sp_place = (Spinner) findViewById(R.id.sp_place);
		int place_array_id = R.array.place_student; // TODO 장소 어레이는 일단 학생용으로...

		if (place_array_id != 0) {
			ArrayAdapter<CharSequence> place_adapter = ArrayAdapter.createFromResource(this, place_array_id,
					android.R.layout.simple_spinner_item);
			place_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_place.setAdapter(place_adapter);
		}

		// 사진 ImageView 초기화
		iv_photo = (ImageView) findViewById(R.id.iv_photo);

		btn_photo_select = (ImageButton) findViewById(R.id.btn_photo_select);
		btn_photo_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doTakePhotoAction();
					}
				};

				DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						doTakeAlbumAction();
					}
				};

				DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				};

				new AlertDialog.Builder(UseCaseCreateActivity.this).setTitle("Select the method")
						.setPositiveButton(getString(R.string.btn_from_camera), cameraListener)
						.setNeutralButton(getString(R.string.btn_from_gallery), albumListener)
						.setNegativeButton("Cancel", cancelListener).show();
			}

		});

		Button btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 서버로 데이터 전송
				new SubmitTask().execute();
			}

		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Log.i(TAG, "onConfigurationChanged()");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (raw_photo_file != null) {
			outState.putString(STATE_PHOTO_PATH, raw_photo_file.getAbsolutePath());
		}

		Log.i(TAG, "onSaveInstanceState()");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		if (savedInstanceState != null) {
			raw_photo_file = getFileStreamPath(savedInstanceState.getString(STATE_PHOTO_PATH));
		}

		Log.i(TAG, "onRestoreInstanceState()");
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.i(TAG, "onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.i(TAG, "onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (raw_photo_file != null) {
			raw_photo_file.delete();
		}

		if (processed_photo_file != null) {
			processed_photo_file.delete();
		}

		if (upload_photo_file != null) {
			upload_photo_file.delete();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume()");
	}

	private String getUploadImageFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
		sdf.setTimeZone(TimeZone.getTimeZone("Korea/Seoul"));
		String timestamp = sdf.format(new Date());
		String username = UserHelper.getCurrentUser(this).username;

		return timestamp + "_" + username + ".jpg";
	}

	private File getAlbumDir() {
		return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "EveryUse");
	}

	private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
		private HttpClient httpClient;

		private ProgressDialog indicator;
		private String msg_error;
		private UseCase new_use_case;
		private Activity activity;

		// input
		private String input_item;
		private String input_purpose;
		private String input_purpose_type;
		private String input_place;

		@Override
		protected void onPreExecute() {
			indicator = new ProgressDialog(UseCaseCreateActivity.this, ProgressDialog.STYLE_SPINNER);
			indicator.setMessage("Please wait...");
			indicator.setCanceledOnTouchOutside(false);
			indicator.setCancelable(true);
			indicator.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);

					Toast.makeText(UseCaseCreateActivity.this, "Upload canceled", Toast.LENGTH_SHORT).show();
				}

			});
			indicator.show();

			httpClient = new DefaultHttpClient();
			activity = UseCaseCreateActivity.this;

			// 입력된 'item'
			input_item = et_item.getText().toString();

			// 입력된 'purpose'
			input_purpose = et_purpose.getText().toString();

			// purpose type이 선택되지 않았다면, 그냥 빈 String으로 입력
			Object purpose_type_selected = sp_purpose_type.getSelectedItem();
			input_purpose_type = (purpose_type_selected == null) ? "" : purpose_type_selected.toString().toLowerCase();

			// place가 선택되지 않았다면, 그냥 빈 String 으로 입력
			Object place_selected = sp_place.getSelectedItem();
			input_place = (place_selected == null) ? "" : sp_place.getSelectedItem().toString().toLowerCase();

			// 선택된 사진
			if (new_photo_selected) {
				String file_name = getUploadImageFileName();
				boolean rename_success = processed_photo_file.renameTo(new File(getCacheDir(), file_name));

				if (rename_success) {
					upload_photo_file = new File(getCacheDir(), file_name);

					new_photo_selected = false;
				} else {
					upload_photo_file = null;
					Toast.makeText(UseCaseCreateActivity.this, "Unable to generate the photo to upload",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			String url = URLHelper.USE_CASES_URL;
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Charset charset = Charset.forName("UTF-8");

			try {
				User user = UserHelper.getCurrentUser(getApplicationContext());

				entity.addPart("use_case[item]", new StringBody(input_item, charset));
				entity.addPart("use_case[purpose]", new StringBody(input_purpose, charset));
				entity.addPart("use_case[purpose_type]", new StringBody(input_purpose_type, charset));
				entity.addPart("use_case[place]", new StringBody(input_place, charset));
				entity.addPart("use_case[lang]", new StringBody(Locale.getDefault().toString(), charset));
				entity.addPart("use_case[ref_all_id]", new StringBody(Long.toString(ref_all_id), charset));
				entity.addPart("use_case[ref_item_id]", new StringBody(Long.toString(ref_item_id), charset));
				entity.addPart("use_case[ref_purpose_id]", new StringBody(Long.toString(ref_purpose_id), charset));

				// MODE_CREATE이거나, MODE_EDIT이면서 새로 업로드할 사진 파일이 존재할떄만
				if (hasNewPhotoToUpload()) {
					entity.addPart("use_case[photo]", new FileBody(upload_photo_file, "image/png"));
				}

				entity.addPart("user_credentials", new StringBody(user.single_access_token, Charset.forName("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			HttpUriRequest httpRequest = null;
			if (mode.equals(MODE_EDIT) && old_use_case != null) {
				httpRequest = new HttpPut(url + "/" + old_use_case.id + ".json");
				((HttpPut) httpRequest).setEntity(entity);
			} else {
				httpRequest = new HttpPost(url + ".json");
				((HttpPost) httpRequest).setEntity(entity);
			}

			try {
				HttpResponse response = httpClient.execute(httpRequest);
				int statusCode = response.getStatusLine().getStatusCode();

				if (mode.equals(MODE_CREATE)) {
					HttpEntity resEntity = response.getEntity();
					String responseString = EntityUtils.toString(resEntity);

					if (statusCode >= 300) { // error occurred
						try {
							msg_error = ErrorHelper.getMostProminentError(responseString);
						} catch (JSONException e) {
							Log.d("PostActivity", responseString);
						}

						return false;
					} else {
						JSONObject json = null;
						try {
							json = new JSONObject(responseString);
							new_use_case = UseCase.parseFromJSON(json);
						} catch (JSONException e) {
							e.printStackTrace();
							return false;
						}

						return true;
					}
				} else if (mode.equals(MODE_EDIT)) {
					if (statusCode >= 300) {
						return false;
					} else {
						return true;
					}
				}

				return false;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		private boolean hasNewPhotoToUpload() {
			return mode == MODE_CREATE || (mode == MODE_EDIT && upload_photo_file != null);
		}

		@Override
		protected void onPostExecute(Boolean success) {
			indicator.dismiss();

			if (success) {
				if (isMode(MODE_CREATE)) {
					Toast.makeText(UseCaseCreateActivity.this, R.string.msg_create_success, Toast.LENGTH_SHORT).show();

					Intent intent = new Intent(UseCaseCreateActivity.this, UseCaseDetailActivity.class);
					intent.putExtra(UseCaseDetailActivity.EXTRA_DATA, new_use_case);
					startActivity(intent);
				} else if (isMode(MODE_EDIT)) {
					Toast.makeText(UseCaseCreateActivity.this, R.string.msg_create_success, Toast.LENGTH_SHORT).show();

					// TODO 업데이트된 UseCase를 보여주도록 구현해야함.
					Intent intent = new Intent(activity, MainActivity.class);
					intent.putExtra(MainActivity.EXTRA_REFRESH_LISTS, true);
					startActivity(intent);

					activity.finish();
					return;
				} else {
					throw new IllegalStateException("Undefined mode detected");
				}

				finish();
			} else {
				Toast.makeText(UseCaseCreateActivity.this, msg_error, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private boolean isMode(String mode_compare) {
		return mode.equals(mode_compare);
	}

	/**
	 * 카메라에서 이미지 가져오기
	 */
	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 임시로 사용할 파일의 경로를 생성
		try {
			raw_photo_file = File.createTempFile("EveryUse", ".jpg", getExternalCacheDir());

			if (raw_photo_file == null) {
				Toast.makeText(this, getString(R.string.msg_fail_create_temp_file), Toast.LENGTH_SHORT).show();
				return;
			}

			Log.d(TAG, "raw_photo_file: " + raw_photo_file);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(raw_photo_file));
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
			Toast.makeText(this, getString(R.string.msg_fail_create_temp_file), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction() {
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_ALBUM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PICK_FROM_CAMERA: {
				// Resize and rotate the original bitmap
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;
				Bitmap bitmap = ImageHelper.rotateBitmap(
						BitmapFactory.decodeFile(raw_photo_file.getAbsolutePath(), options), 90);

				Log.d(TAG, "raw_photo_file (at onActivityResult) : " + raw_photo_file);

				new SaveBitmapTask().execute(bitmap);
				break;
			}
			case PICK_FROM_ALBUM: {
				Uri photo_uri = data.getData();
				InputStream is = null;

				try {
					is = getContentResolver().openInputStream(photo_uri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// Resize and rotate the original bitmap
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;
				Bitmap resized = BitmapFactory.decodeStream(is, null, options);

				new SaveBitmapTask().execute(resized);
				break;
			}
			}
		}
	}

	private class SaveBitmapTask extends AsyncTask<Bitmap, Void, Void> {
		private ProgressDialog indicator;
		private Bitmap bitmap;

		@Override
		protected void onPreExecute() {
			indicator = new ProgressDialog(UseCaseCreateActivity.this, ProgressDialog.STYLE_SPINNER);
			indicator.setMessage("Processing...");
			indicator.show();

			try {
				processed_photo_file = File.createTempFile("EVERYUSE_PROCESSED_", ".jpg");
			} catch (IOException e) {
				Toast.makeText(UseCaseCreateActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				return;
			}
		}

		@Override
		protected Void doInBackground(Bitmap... photo) {
			bitmap = photo[0];

			if (bitmap != null) {
				try {
					FileOutputStream out = new FileOutputStream(processed_photo_file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			indicator.dismiss();

			// 업로드할 파일 set
			if (processed_photo_file != null) {
				iv_photo.setImageBitmap(bitmap);
				new_photo_selected = true;
			} else {
				Toast.makeText(getApplicationContext(), "Cannot process photo", Toast.LENGTH_SHORT).show();
			}
		}

	}
}
