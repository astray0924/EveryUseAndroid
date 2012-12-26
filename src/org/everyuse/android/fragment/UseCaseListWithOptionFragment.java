package org.everyuse.android.fragment;

import org.everyuse.android.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UseCaseListWithOptionFragment extends UseCaseListFragment {
	// Strings for logging
	private final String TAG = this.getClass().getSimpleName();

	private Spinner sp_option;
	private int option_array_id;
	private String option_name = "type"; // 기본값은 "type"

	public static final String EXTRA_OPTION_ARRAY = "option_array";
	private static final int NO_OPTION_ARRAY = 0;

	public static UseCaseListWithOptionFragment newInstance(String data_url,
			int option_array_id) {
		UseCaseListWithOptionFragment f = new UseCaseListWithOptionFragment();

		Bundle b = new Bundle();
		b.putString(EXTRA_DATA_URL, data_url);
		b.putInt(EXTRA_OPTION_ARRAY, option_array_id);
		f.setArguments(b);

		return f;
	}
	
	public static UseCaseListWithOptionFragment newInstance(String data_url,
			int option_array_id, boolean refresh_on_start) {
		UseCaseListWithOptionFragment f = newInstance(data_url, option_array_id);

		Bundle b = f.getArguments();
		b.putBoolean(EXTRA_REFRESH_ON_START, refresh_on_start);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			option_array_id = args.getInt(EXTRA_OPTION_ARRAY, NO_OPTION_ARRAY);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initOptionSpinner();
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usecase_list_with_option,
				null);
	}

	@Override
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
		return super.buildDataURLWithQuery(data_url_raw) + "&" + option_name
				+ "=" + option_value.toLowerCase();
	}

	private String getSelectedOption() {
		if (sp_option == null) {
			throw new IllegalStateException("Spinner is not initialized!");
		}

		return sp_option.getSelectedItem().toString().toLowerCase()
				.replaceAll("\\s", "");

	}
}
