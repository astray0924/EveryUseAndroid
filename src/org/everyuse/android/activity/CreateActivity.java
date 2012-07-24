package org.everyuse.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateActivity extends Activity {
	private EditText et_item;
	private EditText et_purpose;
	private Spinner sp_purpose_type;
	private ImageButton btn_photo_select;
	private ImageView iv_photo;

	// photo
	private Uri camera_photo_uri;
	private File photo_file;
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;

	// input
	String input_item;
	String input_purpose;
	String input_purpose_type;
	File input_photo_file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);

		setTitle(R.string.title_activity_create);

		// UI 초기화
		initUI();
	}

	private void initUI() {
		et_item = (EditText) findViewById(R.id.et_item);
		et_purpose = (EditText) findViewById(R.id.et_purpose);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);

		sp_purpose_type = (Spinner) findViewById(R.id.sp_purpose_type);
		ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter
				.createFromResource(this, R.array.purpose,
						android.R.layout.simple_spinner_item);
		spinner_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_purpose_type.setAdapter(spinner_adapter);

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

				new AlertDialog.Builder(CreateActivity.this)
						.setTitle("Select the method")
						.setPositiveButton(getString(R.string.btn_from_camera),
								cameraListener)
						.setNeutralButton(getString(R.string.btn_from_gallery),
								albumListener)
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

	/**
	 * 카메라에서 이미지 가져오기
	 */
	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 임시로 사용할 파일의 경로를 생성
		File temp_file = null;
		try {
			temp_file = File.createTempFile("everyuse_", ".png",
					getExternalCacheDir());
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Failed to create temp file", 200).show();
			return;
		}

		camera_photo_uri = Uri.fromFile(temp_file);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_photo_uri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction() {
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				PICK_FROM_ALBUM);
	}

	private class SubmitTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog indicator;
		private String msg_error;

		private UseCase created;

		@Override
		protected void onPreExecute() {
			indicator = new ProgressDialog(CreateActivity.this,
					ProgressDialog.STYLE_SPINNER);
			indicator.setMessage("Please wait...");
			indicator.show();

			input_item = et_item.getText().toString();
			input_purpose = et_purpose.getText().toString();
			input_purpose_type = sp_purpose_type.getSelectedItem().toString()
					.toLowerCase();
			input_photo_file = photo_file;

		}

		@Override
		protected Boolean doInBackground(Void... args) {
			String url = URLHelper.USE_CASES_RECENT_URL + ".json";

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				User user = UserHelper.getCurrentUser(getApplicationContext());

				entity.addPart("use_case[item]", new StringBody(input_item));
				entity.addPart("use_case[purpose]", new StringBody(
						input_purpose));
				entity.addPart("use_case[purpose_type]", new StringBody(
						input_purpose_type));
				entity.addPart("use_case[photo]", new FileBody(
						input_photo_file, "image/png"));
				entity.addPart("user_credentials", new StringBody(
						user.single_access_token, Charset.forName("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			httpPost.setEntity(entity);

			try {
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity resEntity = response.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode >= 300) { // error occurred
					String[] fields = { "item", "purpose", "photo" };
					try {
						msg_error = ErrorHelper.getMostProminentError(
								responseString, fields);
					} catch (JSONException e) {
						Log.d("PostActivity", responseString);
					}

					return false;
				} else {
					JSONObject json = null;
					try {
						json = new JSONObject(responseString);

						created = UseCase.parseSingleFromJSON(json);

						Intent intent = new Intent(CreateActivity.this,
								UseCaseDetailActivity.class);
						intent.putExtra(UseCaseDetailActivity.EXTRA_DATA,
								created);
						startActivity(intent);

					} catch (JSONException e) {
						e.printStackTrace();
						return false;
					}

					return true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			indicator.dismiss();

			if (success) {
				// if (created_use_case != null) {
				// Intent intent = new Intent(CreateActivity.this,
				// TopDetailActivity.class);
				// intent.putExtra("DATA", created_use_case);
				// startActivity(intent);
				// }

				Toast.makeText(CreateActivity.this,
						R.string.msg_create_success, Toast.LENGTH_SHORT).show();

				// TODO 올린 케이스 보여주는 기능 추가

				finish();
			} else {
				Toast.makeText(CreateActivity.this, msg_error,
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
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

				new SaveBitmapToSD().execute(resized);
				break;
			}

			case PICK_FROM_CAMERA: {
				String photo_path = getPath(camera_photo_uri);

				// Resize and rotate the original bitmap
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;
				Bitmap bitmap_resized = BitmapFactory.decodeFile(photo_path,
						options);
				Bitmap bitmap_rotated = rotateBitmap(bitmap_resized, 90);

				// 임시 파일 삭제
				File f = new File(photo_path);
				if (f.exists()) {
					f.delete();
				}

				new SaveBitmapToSD().execute(bitmap_rotated);
				break;
			}
			}
		}
	}

	private Bitmap rotateBitmap(Bitmap original, int degree) {
		Matrix mat = new Matrix();
		mat.postRotate(degree);
		Bitmap rotated = Bitmap.createBitmap(original, 0, 0,
				original.getWidth(), original.getHeight(), mat, true);

		return rotated;
	}

	private String getPath(Uri uri) {
		String selectedImagePath;
		// 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		} else {
			selectedImagePath = uri.getPath();
		}

		return selectedImagePath;
	}

	private class SaveBitmapToSD extends AsyncTask<Bitmap, Void, Void> {
		private ProgressDialog indicator;
		private File bitmap_file;
		private Bitmap bitmap;

		@Override
		protected void onPreExecute() {
			indicator = new ProgressDialog(CreateActivity.this,
					ProgressDialog.STYLE_SPINNER);
			indicator.setMessage("Processing...");
			indicator.show();
		}

		@Override
		protected Void doInBackground(Bitmap... photo) {
			bitmap = photo[0];

			if (bitmap != null) {
				try {
					bitmap_file = File.createTempFile("upload_", ".jpg",
							getExternalCacheDir());
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					FileOutputStream out = new FileOutputStream(bitmap_file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			indicator.dismiss();

			// 업로드할 파일 set
			if (bitmap_file != null) {
				photo_file = bitmap_file;

				// set preview
				iv_photo.setImageBitmap(bitmap);
			} else {
				Toast.makeText(getApplicationContext(), "Cannot process photo",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
}
