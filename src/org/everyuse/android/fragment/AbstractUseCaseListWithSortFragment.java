package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.R;
import org.everyuse.android.model.UseCaseSortOption;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class AbstractUseCaseListWithSortFragment extends
		AbstractUseCaseListFragment {

	private Spinner sp_sort;

	protected void resetList() {
		mDataList.clear();
		mAdapter.notifyDataSetChanged();
		mListView.setLoadEndFlag(false);
		page = 1;
	}

	@Override
	protected String getDataURLWithQuery() {
		// build query string using parameters
		String url = getDataURLRaw();
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

		sp_sort = (Spinner) getActivity().findViewById(R.id.sp_sort_option);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.comment,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_sort.setAdapter(adapter);
		sp_sort.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				UseCaseSortOption sort_option = getSelectedSortOption();

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
		} else if (selected.equals("favorite")) {
			sort_option = UseCaseSortOption.FAVORITE;
		}

		return sort_option;
	}
}
