package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPageFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_mypage, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		List<String> menu_list = new ArrayList<String>() {
			{
				add(getString(R.string.menu_shared));
				add(getString(R.string.menu_commented));
				add(getString(R.string.menu_favorite));
				add(getString(R.string.menu_following));
				add(getString(R.string.menu_follower));
			}
		};

		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
				R.layout.listitem_mypage_menu, menu_list);
		setListAdapter(aa);
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
		TextView tv = (TextView) v;
		String menu_title = tv.getText().toString();

		Toast.makeText(getActivity(), menu_title, Toast.LENGTH_LONG).show();

	}
}
