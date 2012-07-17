package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.R;
import org.everyuse.android.model.UseCaseListOption;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class UseCaseListWithOptionFragment extends UseCaseListFragment {

	private Spinner sp_option;
	private int option_array_id = NO_OPTION_ARRAY;
	private static final int NO_OPTION_ARRAY = -1;
	private String option_name = "type"; // 기본값은 "type"

	public UseCaseListWithOptionFragment() {
		super();
	}

	public UseCaseListWithOptionFragment(String data_url) {
		super(data_url);
	}

	public UseCaseListWithOptionFragment(String data_url, int option_array_id) {
		this(data_url);
		this.option_array_id = option_array_id;
	}

	public UseCaseListWithOptionFragment(String data_url, int option_array_id,
			String option_name) {
		this(data_url, option_array_id);
		this.option_name = option_name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usecase_list_with_option,
				null);
	}

	@Override
	protected String getDataURLWithQuery() {
		// build query string using parameters
		String url = getRawDataURL();
		UseCaseListOption option_value = getSelectedOption();

		if (url == null || url.equals("") || option_value == null
				|| option_value.equals("")) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data_url));
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
		params.add(new BasicNameValuePair(option_name, String.valueOf(
				option_value).toLowerCase()));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		return url + ".json" + "?" + query_string;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (option_array_id == NO_OPTION_ARRAY) { // 리스트 옵션 목록이 설정되지 않았음
			throw new IllegalStateException(
					getString(R.string.msg_missing_sort_option));
		}

		sp_option = (Spinner) getActivity().findViewById(R.id.sp_option);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), option_array_id,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_option.setAdapter(adapter);
		sp_option.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				resetList();
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	private UseCaseListOption getSelectedOption() {
		String selected = sp_option.getSelectedItem().toString().toLowerCase();
		UseCaseListOption option = null;

		if (selected.equals("all")) {
			option = UseCaseListOption.ALL;
		} else if (selected.equals("item")) {
			option = UseCaseListOption.ITEM;
		} else if (selected.equals("purpose")) {
			option = UseCaseListOption.PURPOSE;
		} else if (selected.equals("fun")) {
			option = UseCaseListOption.FUN;
		} else if (selected.equals("me too")) {
			option = UseCaseListOption.METOO;
		}

		return option;
	}
}
