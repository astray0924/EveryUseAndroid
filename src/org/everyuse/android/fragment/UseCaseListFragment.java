package org.everyuse.android.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.everyuse.android.R;
import org.everyuse.android.activity.DetailActivity;
import org.everyuse.android.adapter.UseCaseSingleAdapter;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.widget.DynamicListView;
import org.everyuse.android.widget.DynamicListView.OnListLoadListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UseCaseListFragment extends ListFragment {
	protected ArrayList<UseCase> mDataList;
	protected BaseAdapter mAdapter;
	protected DynamicListView mListView;

	private AsyncTask<String, Void, Boolean> load_data_task = null;
	protected int page = START_PAGE;

	protected static final int PER_PAGE = 10;
	protected static final int START_PAGE = 1;
	protected static final int HTTP_ERROR_CODE = 300;

	public static final String EXTRA_DATA_LIST = "data_list";
	public static final String EXTRA_DATA_URL = "data_url";
	public static final String EXTRA_DATA_URL_RAW = "data_url_raw";
	private String data_url;
	private String data_url_raw;

	public UseCaseListFragment() {

	}

	public UseCaseListFragment(String data_url_raw) {

		if (data_url_raw == null || data_url_raw.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_data_url));
		}

		this.data_url_raw = data_url_raw;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataList = new ArrayList<UseCase>();
		mAdapter = new UseCaseSingleAdapter(getActivity(),
				R.layout.list_item_usecase_single, mDataList);

		setListAdapter(mAdapter);

		if (savedInstanceState != null) {
			data_url = savedInstanceState.getString(EXTRA_DATA_URL);
			data_url_raw = savedInstanceState.getString(EXTRA_DATA_URL_RAW);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// TODO 개선 필요
		Bundle args = getArguments();
		if (args != null) {
			String data_url_raw = args.getString(EXTRA_DATA_URL_RAW);

			if (data_url_raw == null || data_url_raw == "") {
				Log.d("ListFragment",
						getString(R.string.msg_intent_parameter_not_set));
			} else {
				this.data_url_raw = data_url_raw;
				resetList();
			}

		}

		initialize();
	}

	private void initialize() {

		mListView = (DynamicListView) getListView();
		mListView.setOnListLoadListener(new OnListLoadListener() {

			@Override
			public void onLoad() {
				if (load_data_task == null) {
					data_url = buildDataURLWithQuery(data_url_raw);

					if (data_url == null || data_url.equals("")) {
						throw new IllegalStateException(
								getString(R.string.msg_missing_data_url));
					}

					load_data_task = new LoadDataTask();
					load_data_task.execute(data_url);
				}

			}

		});
	}

	protected String buildDataURLWithQuery(String data_url_raw) {
		if (data_url_raw == null || data_url_raw.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_data_url));
		}

		// build query string using parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String
				.valueOf(getCurrentPage())));
		params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		return data_url_raw + ".json" + "?" + query_string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(EXTRA_DATA_URL, data_url);
		outState.putString(EXTRA_DATA_URL_RAW, data_url_raw);
	}

	private class LoadDataTask extends AsyncTask<String, Void, Boolean> {
		private HttpClient client;

		@Override
		protected void onPreExecute() {
			client = new DefaultHttpClient();
		}

		@Override
		protected Boolean doInBackground(String... args) {
			String data_url = args[0];
			
			Log.d("data_url", data_url + "");

			if (data_url == null || data_url.equals("")) {
				throw new IllegalArgumentException(
						getString(R.string.msg_missing_data_url));
			}

			HttpGet method = new HttpGet(args[0]);
			Boolean success = false;

			try {
				HttpResponse response = client.execute(method);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					int code = response.getStatusLine().getStatusCode();

					if (code >= HTTP_ERROR_CODE) { // HTTP error code
						success = false;
					} else { // success
						String res_string = EntityUtils.toString(entity);
						JSONArray data_list = new JSONArray(res_string);

						// if no items were fetched, end the list
						if (data_list.length() == 0) {
							mListView.setLoadEndFlag(true);
							return true;
						}

						for (int i = 0; i < data_list.length(); i++) {
							JSONObject json = data_list.getJSONObject(i);
							UseCase u = UseCase.parseFromJSON(json);

							mDataList.add(u);
						}

						success = true;
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				mAdapter.notifyDataSetChanged();
				increasePage();
			} else {
				Toast.makeText(getActivity(), R.string.msg_data_load_fail,
						Toast.LENGTH_SHORT).show();
			}

			load_data_task = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usecase_list, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putParcelableArrayListExtra(DetailActivity.EXTRA_DATA_LIST,
				mDataList);
		intent.putExtra(DetailActivity.EXTRA_STRAT_INDEX, position);
		startActivity(intent);
	}

	protected void resetList() {
		resetPage();
		mDataList.clear();
		mAdapter.notifyDataSetChanged();
		mListView.setLoadEndFlag(false);
	}

	protected synchronized int getCurrentPage() {
		return page;
	}

	protected synchronized void increasePage() {
		page++;
	}

	protected synchronized void resetPage() {
		page = START_PAGE;
	}
}
