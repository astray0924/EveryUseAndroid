package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.activity.UserInfoDetailActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserInfoMenuFragment extends ListFragment {
	private List<String> menu_list = new ArrayList<String>();

	public UserInfoMenuFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

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

		menu_list = buildMenuItemList();

		ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_mypage_menu, menu_list);
		setListAdapter(aa);
	}

	private List<String> buildMenuItemList() {
		List<String> menu_list = new ArrayList<String>();
		
		menu_list.add(UserInfoDetailActivity.MENU_SHARED,
				getString(R.string.menu_shared));
		
		menu_list.add(UserInfoDetailActivity.MENU_COMMENTED,
				getString(R.string.menu_commented));
		
		menu_list.add(UserInfoDetailActivity.MENU_SCRAPED,
				getString(R.string.menu_scraped));
		
		menu_list.add(UserInfoDetailActivity.MENU_FOLLOWING,
				getString(R.string.menu_following));
		
		menu_list.add(UserInfoDetailActivity.MENU_FOLLOWER,
				getString(R.string.menu_follower));

		return menu_list;
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
		 Intent intent = new Intent(getActivity(), UserInfoDetailActivity.class);
		 intent.putExtra(UserInfoDetailActivity.EXTRA_MENU_SELECTED, position);
		 startActivity(intent);
	}
}
