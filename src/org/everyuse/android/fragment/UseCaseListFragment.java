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
import org.everyuse.android.adapter.UseCaseSingleAdapter;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.widget.DynamicListView;
import org.everyuse.android.widget.DynamicListView.OnListLoadListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	protected List<UseCase> mDataList;
	protected BaseAdapter mAdapter;
	protected DynamicListView mListView;

	private AsyncTask<String, Void, Boolean> load_data_task = null;

	protected final int PER_PAGE = 10;
	protected int page = 1;

	private String data_url_raw;

	public static final String DATA_URL = "data_url";

	public UseCaseListFragment(String data_url) {
		setRawDataURL(data_url);
	}

	protected void setRawDataURL(String data_url_raw) {
		this.data_url_raw = data_url_raw;
	}

	protected String getRawDataURL() {
		return data_url_raw;
	}

	protected String getDataURLWithQuery() {
		// build query string using parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String
				.valueOf(getCurrentPage())));
		params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		String url_raw = getRawDataURL();
		if (url_raw == null || url_raw.equals("")) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data_url));
		}

		return url_raw + ".json" + "?" + query_string;
	}

	private class LoadDataTask extends AsyncTask<String, Void, Boolean> {
		private HttpClient client;

		@Override
		protected void onPreExecute() {
			client = new DefaultHttpClient();
		}

		@Override
		protected Boolean doInBackground(String... args) {
			HttpGet method = new HttpGet(args[0]);
			Boolean success = false;

			try {
				HttpResponse response = client.execute(method);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					int code = response.getStatusLine().getStatusCode();

					if (code >= 300) { // HTTP error code
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataList = new Vector<UseCase>();
		mAdapter = new UseCaseSingleAdapter(getActivity(),
				R.layout.list_item_usecase_single, mDataList);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent_list, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initialize();
	}

	private void initialize() {
		mListView = (DynamicListView) getListView();
		mListView.setOnListLoadListener(new OnListLoadListener() {

			@Override
			public void onLoad() {
				if (load_data_task == null) {
					String data_url_with_query = getDataURLWithQuery();

					Log.i("data_url", data_url_with_query);

					load_data_task = new LoadDataTask();
					load_data_task.execute(data_url_with_query);
				}

			}

		});

		setListAdapter(mAdapter);
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

	}

	protected synchronized int getCurrentPage() {
		return page;
	}

	protected synchronized void increasePage() {
		page++;
	}

	protected synchronized void resetPage() {
		page = 1;
	}
}
