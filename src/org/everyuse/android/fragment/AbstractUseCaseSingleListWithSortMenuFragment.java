package org.everyuse.android.fragment;

import org.everyuse.android.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractUseCaseSingleListWithSortMenuFragment extends
		AbstractUseCaseSingleListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent_list, null);
	}
}
