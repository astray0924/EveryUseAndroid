package org.everyuse.android.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.everyuse.android.activity.UseCaseDetailActivity;
import org.everyuse.android.adapter.UseCaseGroupAdapter;
import org.everyuse.android.model.UseCaseGroup;
import org.everyuse.android.util.NetworkHelper;
import org.everyuse.android.widget.DynamicExpandableListView;
import org.everyuse.android.widget.DynamicExpandableListView.OnListLoadListener;
import org.everyuse.android.widget.ExpandableListFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Spinner;
import android.widget.Toast;

public class UseCaseGroupListFragment extends ExpandableListFragment implements
		OnChildClickListener {
	// Strings for logging
	private final String TAG = this.getClass().getSimpleName();

	protected ArrayList<UseCaseGroup> mDataList;
	protected BaseExpandableListAdapter mAdapter;
	protected DynamicExpandableListView mListView;

	private AsyncTask<String, Void, Boolean> load_data_task = null;
	protected int page = START_PAGE;

	protected static final int PER_PAGE = 10;
	protected static final int START_PAGE = 1;
	protected static final int HTTP_ERROR_CODE = 300;

	public static final String EXTRA_DATA_LIST = "data_list";
	public static final String EXTRA_DATA_URL = "data_url_raw";
	private String data_url;
	private String data_url_raw;

	// 옵션 기능 파트
	private Spinner sp_option;
	private int option_array_id;
	private String option_name = "type"; // 기본값은 "type"

	public static final String EXTRA_OPTION_ARRAY = "option_array";
	private static final int NO_OPTION_ARRAY = 0;

	public static UseCaseGroupListFragment newInstance(String data_url,
			int option_array_id) {
		UseCaseGroupListFragment f = new UseCaseGroupListFragment();

		Bundle b = new Bundle();
		b.putString(EXTRA_DATA_URL, data_url);
		b.putInt(EXTRA_OPTION_ARRAY, option_array_id);
		f.setArguments(b);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDataList = new ArrayList<UseCaseGroup>();
		mAdapter = new UseCaseGroupAdapter(getActivity(), mDataList);
		setListAdapter(mAdapter);

		Bundle args = getArguments();

		if (args != null) {
			data_url_raw = args.getString(EXTRA_DATA_URL);
			option_array_id = args.getInt(EXTRA_OPTION_ARRAY, NO_OPTION_ARRAY);
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

		initOptionSpinner();

		initialize();
	}

	private void fetchData() {
		if (NetworkHelper.IS_NETWORK_CONNECTED) {
			data_url = buildDataURLWithQuery(data_url_raw);

			if (data_url == null || data_url.equals("")) {
				throw new IllegalStateException(
						getString(R.string.msg_missing_data_url));
			}

			load_data_task = new LoadDataTask();
			load_data_task.execute(data_url);
		} else {
			Toast.makeText(getActivity(), "No data connection!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initOptionSpinner() {
		if (option_array_id == NO_OPTION_ARRAY) { // 리스트 옵션 목록이 설정되지 않았음
			throw new IllegalStateException(
					getString(R.string.msg_missing_option_array));
		}

		sp_option = (Spinner) getView().findViewById(R.id.sp_sort_by);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), option_array_id,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_option.setAdapter(adapter);
		sp_option.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				refresh();
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	private void initialize() {

		mListView = (DynamicExpandableListView) getExpandableListView();
		mListView.setOnChildClickListener(this);
		mListView.setOnListLoadListener(new OnListLoadListener() {

			@Override
			public void onLoad() {
				if (load_data_task == null
						&& NetworkHelper.IS_NETWORK_CONNECTED) {
					fetchData();
				}

			}

		});
	}

	private String getSelectedOption() {
		if (sp_option == null) {
			throw new IllegalStateException("Spinner is not initialized!");
		}

		return sp_option.getSelectedItem().toString().toLowerCase()
				.replaceAll("\\s", "");

	}

	protected String buildDataURLWithQuery(String data_url_raw) {
		if (data_url_raw == null || data_url_raw.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_data_url));
		}

		String option_value = getSelectedOption();
		if (option_value == null || option_value.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_option_value));
		}

		// build query string using parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String
				.valueOf(getCurrentPage())));
		params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
		params.add(new BasicNameValuePair(option_name, String.valueOf(
				option_value).toLowerCase()));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		return data_url_raw + ".json" + "?" + query_string;
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
						if (data_list.length() < PER_PAGE) {
							mListView.setLoadEnded(true);
						}

						for (int i = 0; i < data_list.length(); i++) {
							JSONObject json = data_list.getJSONObject(i);
							UseCaseGroup group = UseCaseGroup
									.parseSingleFromJSON(json);

							mDataList.add(group);
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
				increasePage();
			} else {
				Toast.makeText(getActivity(), R.string.msg_data_load_fail,
						Toast.LENGTH_SHORT).show();
			}

			mAdapter.notifyDataSetChanged();
			load_data_task = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(
				R.layout.fragment_usecase_group_list_with_option, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.everyuse.android.widget.ExpandableListFragment#onChildClick(android
	 * .widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Intent intent = new Intent(getActivity(), UseCaseDetailActivity.class);
		intent.putParcelableArrayListExtra(
				UseCaseDetailActivity.EXTRA_DATA_LIST,
				mDataList.get(groupPosition).getChildren());
		intent.putExtra(UseCaseDetailActivity.EXTRA_STRAT_INDEX, childPosition);
		startActivity(intent);

		return true;
	}

	protected void refresh() {
		resetPage();
		mDataList.clear();
		mAdapter.notifyDataSetChanged();
		mListView.setLoadEnded(false);
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
