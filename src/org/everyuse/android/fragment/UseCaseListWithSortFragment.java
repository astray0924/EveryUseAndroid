package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.R;
import org.everyuse.android.model.UseCaseSortOption;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class UseCaseListWithSortFragment extends UseCaseListFragment {

	private Spinner sp_sort;
	private int sort_option_array_id = 0;

	public UseCaseListWithSortFragment(String data_url) {
		super(data_url);
	}

	public UseCaseListWithSortFragment(String data_url, int sort_option_array_id) {
		super(data_url);
		this.sort_option_array_id = sort_option_array_id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usecase_list_with_sort, null);
	}

	@Override
	protected String getDataURLWithQuery() {
		// build query string using parameters
		String url = getRawDataURL();
		UseCaseSortOption sort_option = getSelectedSortOption();

		if (url == null || url.equals("") || sort_option == null) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data_url));
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("page", String.valueOf(page)));
		params.add(new BasicNameValuePair("limit", String.valueOf(PER_PAGE)));
		params.add(new BasicNameValuePair("type", String.valueOf(sort_option)
				.toLowerCase()));
		String query_string = URLEncodedUtils.format(params, "UTF-8");

		return url + ".json" + "?" + query_string;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (sort_option_array_id == 0) { // 정렬 옵션 array가 설정되지 않았음
			throw new IllegalStateException(
					getString(R.string.msg_missing_sort_option));
		}

		sp_sort = (Spinner) getActivity().findViewById(R.id.sp_sort_option);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), sort_option_array_id,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_sort.setAdapter(adapter);
		sp_sort.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				resetList();
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	private UseCaseSortOption getSelectedSortOption() {
		String selected = sp_sort.getSelectedItem().toString().toLowerCase();
		UseCaseSortOption sort_option = null;

		if (selected.equals("all")) {
			sort_option = UseCaseSortOption.ALL;
		} else if (selected.equals("item")) {
			sort_option = UseCaseSortOption.ITEM;
		} else if (selected.equals("purpose")) {
			sort_option = UseCaseSortOption.PURPOSE;
		} else if (selected.equals("fun")) {
			sort_option = UseCaseSortOption.FUN;
		} else if (selected.equals("me too")) {
			sort_option = UseCaseSortOption.METOO;
		}

		return sort_option;
	}
}
