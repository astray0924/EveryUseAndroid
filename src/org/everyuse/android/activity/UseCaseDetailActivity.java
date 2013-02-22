package org.everyuse.android.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.everyuse.android.R;
import org.everyuse.android.fragment.UseCaseDetailFragment;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.model.User;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class UseCaseDetailActivity extends SherlockFragmentActivity implements
		UseCaseDetailFragment.OnFollowUpdateListener {
	public static String EXTRA_DATA = "DATA";
	public static String EXTRA_DATA_LIST = "DATA_LIST";
	public static String EXTRA_STRAT_INDEX = "START_INDEX";
	private final String TAG = this.getClass().getSimpleName();

	private ArrayList<UseCase> data_list;
	private int start_index;

	private static ViewPager pager;
	private static ItemsPagerAdapter pager_adapter;

	private AsyncTask<URL, Void, Boolean> delete_task;
	private ProgressDialog dialog;

	private boolean is_author;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case android.R.id.home:
			intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menu_edit:
			intent = new Intent(this, UseCaseCreateActivity.class);
			intent.putExtra(UseCaseCreateActivity.EXTRA_USE_CASE,
					getCurrentUseCase());
			startActivity(intent);
			break;
		case R.id.menu_discard:
			showDiscardDialog(getCurrentUseCase());
			break;
		case R.id.menu_new:
			intent = new Intent(this, UseCaseCreateActivity.class);
			intent.putExtra(UseCaseCreateActivity.EXTRA_REF_ALL_ID,
					getCurrentUseCase().id);
			startActivity(intent);
			break;
		}

		return true;
	}

	private UseCase getCurrentUseCase() {
		try {
			return data_list.get(pager.getCurrentItem());
		} catch (IndexOutOfBoundsException e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	private void showDiscardDialog(final UseCase use_case) {
		AlertDialog.Builder discard_bld = new AlertDialog.Builder(this);
		discard_bld
				.setMessage(getString(R.string.msg_discard))
				.setCancelable(true)
				.setPositiveButton(getString(R.string.msg_yes),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								try {
									URL url = getDeleteURL(use_case);

									// 아이템 삭제
									delete_task = new DeleteTask();
									delete_task.execute(url);
								} catch (MalformedURLException e) {
									Log.e(TAG, e.getMessage());
								}

							}
						})
				.setNegativeButton(getString(R.string.msg_no),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// TODO Auto-generated method stub

							}
						});

		AlertDialog alert = discard_bld.create();
		alert.setTitle("Delete?");
		alert.show();
	}

	private URL getDeleteURL(UseCase use_case) throws MalformedURLException {
		return new URL(URLHelper.USE_CASES_URL + "/" + use_case.id);
	}

	private class DeleteTask extends AsyncTask<URL, Void, Boolean> {
		private HttpURLConnection conn;
		private String responseString;
		private String errMsg;
		private Activity activity;

		@Override
		protected void onPreExecute() {
			activity = UseCaseDetailActivity.this;

			dialog.show();
		}

		@Override
		protected Boolean doInBackground(URL... params) {
			if (params.length == 0) {
				return false;
			}

			URL url = params[0];

			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setRequestMethod("DELETE");

				int code = conn.getResponseCode();

				if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
					return false;
				} else {
					return true;
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				conn.disconnect();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();

			if (result) {
				Toast.makeText(activity,
						activity.getString(R.string.msg_delete_success),
						Toast.LENGTH_LONG).show();

				// MainActivity에 리스트 초기화 하도록 지시
				Intent intent = new Intent(activity, MainActivity.class);
				intent.putExtra(MainActivity.EXTRA_REFRESH_LISTS, true);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				activity.finish();
			} else {
				Toast.makeText(activity,
						activity.getString(R.string.msg_delete_fail),
						Toast.LENGTH_LONG).show();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_usecase_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// handle intent
		handleIntent(getIntent());

		// initialize
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isAuthor()) {
			getSupportMenuInflater().inflate(R.menu.detail, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	private boolean isAuthor() {
		UseCase use_case = getCurrentUseCase();

		if (use_case != null) {
			return UserHelper.isCurrentUser(UseCaseDetailActivity.this,
					getCurrentUseCase().writer_id);
		} else {
			Log.d(TAG, "Current UseCase is null");

			return false;
		}

	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		// ViewPager내의 페이지가 바뀔때마다 액션바의 메뉴를 다시 그리게 함
		// 현재 사용자가 보고있는 사례의 저자일 경우에만 편집 기능을 활성화하기 위함
		invalidateOptionsMenu();
	}

	private void initialize() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Set up the view pager
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				// ViewPager내의 페이지가 바뀔때마다 액션바의 메뉴를 다시 그리게 함
				// 현재 사용자가 보고있는 사례의 저자일 경우에만 편집 기능을 활성화하기 위함
				invalidateOptionsMenu();
			}

		});

		pager_adapter = new ItemsPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pager_adapter);
		pager.setCurrentItem(start_index);

		// 대기 다이얼러그 생성
		dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getString(R.string.msg_wait));
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (delete_task != null) {
					delete_task.cancel(true);
				}

			}

		});

	}

	private void handleIntent(Intent intent) {
		data_list = intent.getParcelableArrayListExtra(EXTRA_DATA_LIST);
		start_index = intent.getIntExtra(EXTRA_STRAT_INDEX, 0);

		// data가 인스턴스로 하나만 넘겨졌다면 그걸로 리스트 생성
		UseCase data = intent.getParcelableExtra(EXTRA_DATA);
		if (data != null) {
			data_list = new ArrayList<UseCase>();
			data_list.add(data);
		}

		if (data_list == null) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data));
		}
	}

	private class ItemsPagerAdapter extends FragmentStatePagerAdapter {

		public ItemsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			UseCase data = data_list.get(position);
			UseCaseDetailFragment fragment = UseCaseDetailFragment
					.newInstance(data);
			return fragment;
		}

		@Override
		public int getCount() {
			return data_list.size();
		}

	}

	@Override
	public void onFollowUpdate(int page) {
		// Toast.makeText(this, "test", 200).show();

	}

}
