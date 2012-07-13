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
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.widget.DynamicListView;
import org.everyuse.android.widget.DynamicListView.OnListLoadListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class UseCaseSingleListFragment extends ListFragment {
	private List<UseCase> mDataList;
	private BaseAdapter mAdapter;
	private DynamicListView mListView;

	private AsyncTask<Void, Void, Boolean> load_data_task = null;

	private static final int PER_PAGE = 10;
	private static int page = 1;

	private String data_url;

	private void initialize() {
		mDataList = new Vector<UseCase>();
		mAdapter = new UseCaseSingleAdapter(getActivity(),
				R.layout.list_item_usecase_single, mDataList);
		mListView = (DynamicListView) getListView();
		mListView.setOnListLoadListener(new OnListLoadListener() {

			@Override
			public void onLoad() {
				if (load_data_task == null) {
					load_data_task = new LoadDataTask();
					load_data_task.execute();
				}

			}

		});

		setListAdapter(mAdapter);
	}

	protected void setDataURL(String data_url) {
		this.data_url = data_url;
	}

	protected String getDataURL() {
		return data_url;
	}

	private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
		private HttpClient client;
		private String data_url;

		@Override
		protected void onPreExecute() {
			client = new DefaultHttpClient();

			// build query string using parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("page", String.valueOf(page)));
			params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
			String query_string = URLEncodedUtils.format(params, "UTF-8");

			String url = getDataURL();
			if (url == null || url.equals("")) {
				throw new IllegalStateException(getString(R.string.msg_data_url_missing));
			}

			data_url = url + ".json" + "?" + query_string;
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			HttpGet method = new HttpGet(data_url);
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
				page++;
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
}
