package org.everyuse.android.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.R;
import org.everyuse.android.util.URLHelper;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;

public class SearchActivity extends SherlockListActivity {

	private final String TAG = this.getClass().getSimpleName();

	private AsyncTask<URL, Void, Boolean> load_data_task;
	URL url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// URL connection 초기화
		load_data_task = new LoadDataTask();

		// Get the intent, verify the action and get the query
		handleIntent(getIntent());
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
			String q = intent.getStringExtra(SearchManager.QUERY);
			try {
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
		load_data_task.execute(url);
	}

	private class LoadDataTask extends AsyncTask<URL, Void, Boolean> {
		private HttpURLConnection conn;
		private String res;

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
				conn.setRequestProperty("Cache-Control","no-cache");
				
				int code = conn.getResponseCode();
				
				if (code >= HttpURLConnection.HTTP_BAD_REQUEST) {
					return false;
				} else {		// 성공
					InputStream in = new BufferedInputStream(conn.getInputStream());
					int bytes_read = -1;
					byte[] buffer = new byte[1024];
					
					StringBuffer sb = new StringBuffer();
					
					while ((bytes_read = in.read(buffer)) >= 0) {
						sb.append(new String(buffer), 0, bytes_read);
					}
					
					res = sb.toString();
					
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG, e.getMessage());
			}
			
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(SearchActivity.this, res, Toast.LENGTH_SHORT).show();				
			}
			
			Log.i(TAG, res);
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

}
