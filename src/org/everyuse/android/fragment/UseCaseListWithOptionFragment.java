package org.everyuse.android.fragment;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCaseListOption;

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

	private Spinner sp_option;
	private int option_array_id;
	private static final int NO_OPTION_ARRAY = 0;
	private String option_name = "type"; // 기본값은 "type"

	public static String EXTRA_OPTION_ARRAY = "option_array";

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 옵션 배열 복원
		if (savedInstanceState != null) {
			option_array_id = savedInstanceState.getInt(EXTRA_OPTION_ARRAY,
					NO_OPTION_ARRAY);
		}

	}

	@Override
	public void onStart() {
		super.onStart();

		Bundle args = getArguments();
		if (args != null) {
			int option_array_id = args.getInt(EXTRA_OPTION_ARRAY);
			
			if (option_array_id == 0) {
				Log.d("ListFragment", getString(R.string.msg_intent_parameter_not_set));
			} else {
				// TODO 구현 필요
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (option_array_id == NO_OPTION_ARRAY) { // 리스트 옵션 목록이 설정되지 않았음
			throw new IllegalStateException(
					getString(R.string.msg_missing_option_array));
		}

		sp_option = (Spinner) getView().findViewById(R.id.sp_option);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_usecase_list_with_option,
				null);
	}

	@Override
	protected String buildDataURLWithQuery(String data_url_raw) {
		UseCaseListOption option_value = getSelectedOption();

		if (data_url_raw == null || data_url_raw.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_data_url));
		}

		if (option_value == null || option_value.equals("")) {
			throw new IllegalArgumentException(
					getString(R.string.msg_missing_option_value));
		}

		// build query string using parameters
		return super.buildDataURLWithQuery(data_url_raw) + "&" + option_name
				+ "=" + String.valueOf(option_value).toLowerCase();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(EXTRA_OPTION_ARRAY, option_array_id);
	}

	private UseCaseListOption getSelectedOption() {
		if (sp_option == null) {
			throw new IllegalStateException("Spinner is not initialized!");
		}

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
		} else if (selected.equals("time")) {
			option = UseCaseListOption.TIME;
		}

		return option;
	}
}
