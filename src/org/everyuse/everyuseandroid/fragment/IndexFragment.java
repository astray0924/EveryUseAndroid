package org.everyuse.everyuseandroid.fragment;

import org.everyuse.everyuseandroid.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IndexFragment extends Fragment {
	public IndexFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_index, container, false);
	}
}
