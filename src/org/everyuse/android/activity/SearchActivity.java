package org.everyuse.android.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.R;
import org.everyuse.android.adapter.SearchAdapter;
import org.everyuse.android.adapter.SearchAdapter.SearchItem;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.URLHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockListActivity implements
		OnClickListener {

	private final String TAG = this.getClass().getSimpleName();
	public static final String SEARCH_CATEGORY_ITEM = "item";
	public static final String SEARCH_CATEGORY_PURPOSE = "purpose";

	private AsyncTask<URL, Void, Boolean> load_data_task;
	private BaseAdapter mAdapter;
	private List<SearchItem> mDataList;
	private Map<String, ArrayList<UseCase>> mDataMap;

	private Button btn;
	private String q;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// 버튼 및 핸들러 초기화
		btn = (Button) findViewById(R.id.btn_create_use);
		btn.setOnClickListener(this);

		// ListView 관련 컴포넌트 초기화
		mDataList = new ArrayList<SearchItem>();
		mDataMap = new HashMap<String, ArrayList<UseCase>>();
		mAdapter = new SearchAdapter(this, mDataList);
		setListAdapter(mAdapter);

		// 대기 다이얼러그 생성
		dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getString(R.string.msg_wait));
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (load_data_task != null) {
					load_data_task.cancel(true);
				}

			}

		});

		// Get the intent, verify the action and get the query
		handleIntent(getIntent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, UseCaseDetailActivity.class);

		SearchItem search_item = (SearchItem) mAdapter.getItem(position);
		String search_category = search_item.search_category;

		ArrayList<UseCase> data_list = mDataMap.get(search_category);
		intent.putParcelableArrayListExtra(
				UseCaseDetailActivity.EXTRA_DATA_LIST, data_list);

		UseCase use_case = search_item.use_case;
		int pos = data_list.indexOf(use_case);
		intent.putExtra(UseCaseDetailActivity.EXTRA_STRAT_INDEX, pos);
		startActivity(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			q = intent.getStringExtra(SearchManager.QUERY);
			try {
				getActionBar().setTitle("Search \"" + q + "\"");

				search(q);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.d(TAG, e.getMessage());
			}
		}
	}

	private void search(String q) throws MalformedURLException {
		// 쿼리 파라미터 생성
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("q", String.valueOf(q)));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		URL url = new URL(URLHelper.SEARCH_URL + ".json?" + query_string);

		// 실제로 검색 수행
		load_data_task = new LoadDataTask();
		load_data_task.execute(url);
	}

	private class LoadDataTask extends AsyncTask<URL, Void, Boolean> {
		private HttpURLConnection conn;
		private String responseString;
		private String errMsg;
		private Activity activity;

		@Override
		protected void onPreExecute() {
			activity = SearchActivity.this;

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
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Cache-Control", "no-cache");

				int code = conn.getResponseCode();

				if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
					return false;
				} else { // 성공
					InputStream in = new BufferedInputStream(
							conn.getInputStream());
					int bytes_read = -1;
					byte[] buffer = new byte[1024];

					StringBuffer sb = new StringBuffer();

					while ((bytes_read = in.read(buffer)) >= 0) {
						sb.append(new String(buffer), 0, bytes_read);
					}

					responseString = sb.toString();

					// 데이터 리스트 업데이트하기
					mDataList.clear();
					try {
						JSONObject responseObject = new JSONObject(
								responseString);
						ArrayList<UseCase> result_list_by_item = UseCase
								.parseMultipleFromJSON(responseObject
										.getJSONArray(SEARCH_CATEGORY_ITEM));
						ArrayList<UseCase> result_list_by_purpose = UseCase
								.parseMultipleFromJSON(responseObject
										.getJSONArray(SEARCH_CATEGORY_PURPOSE));
						int count = responseObject.getInt("result_count");

						mDataMap.clear();
						mDataList.clear();

						if (count != 0) {
							// 아이템 맵 생성 (추후 사용 위함)
							// 검색 결과를 검색 타입별로 분류해서 맵으로 정리
							mDataMap.put(SEARCH_CATEGORY_ITEM,
									result_list_by_item);
							mDataMap.put(SEARCH_CATEGORY_PURPOSE,
									result_list_by_purpose);

							// 어댑터에서 사용할 아이템 리스트 채우기
							mDataList.add(SearchItem
									.createSectionItem(SEARCH_CATEGORY_ITEM));

							for (UseCase u : mDataMap.get(SEARCH_CATEGORY_ITEM)) {
								mDataList.add(SearchItem.createItem(u,
										SEARCH_CATEGORY_ITEM));
							}

							mDataList
									.add(SearchItem
											.createSectionItem(SEARCH_CATEGORY_PURPOSE));

							for (UseCase u : mDataMap
									.get(SEARCH_CATEGORY_PURPOSE)) {
								mDataList.add(SearchItem.createItem(u,
										SEARCH_CATEGORY_ITEM));
							}

						}

						Log.i(TAG, "결과 반환 개수: " + count);
					} catch (JSONException e) {
						e.printStackTrace();
						Log.d(TAG, e.getMessage());
						errMsg = activity
								.getString(R.string.msg_data_parse_fail);
						return false;
					}

					// 네트워크 연결 해제
					conn.disconnect();
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG, e.getMessage());
				errMsg = activity.getString(R.string.msg_data_load_fail);

			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) {
				// 리스트 어댑터 업데이트
				mAdapter.notifyDataSetChanged();

				Log.i(TAG, "Update notified!");

				if (mAdapter != null) {
					if (mAdapter.isEmpty()) {
						String btn_string = SearchActivity.this
								.getString(R.string.btn_create_use);
						btn.setText(btn_string + " " + "\"" + q + "\"");
						btn.setVisibility(View.VISIBLE);
					} else {
						btn.setVisibility(View.GONE);
					}
				}
			} else {
				Toast.makeText(SearchActivity.this, errMsg, Toast.LENGTH_LONG)
						.show();
			}

			dialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.search, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, UseCaseCreateActivity.class);
		intent.putExtra(UseCaseCreateActivity.EXTRA_ITEM, q);
		startActivity(intent);

	}

}
