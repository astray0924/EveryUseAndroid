package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.activity.UserProfileActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserProfileFragment extends ListFragment {
	private List<String> menu_list = new ArrayList<String>();
	private static final int MENU_SHARED = 0;
	private static final int MENU_COMMENTED = 1;
	private static final int MENU_FAVORITED = 2;
	private static final int MENU_FOLLOWING = 3;
	private static final int MENU_FOLLOWER = 4;

	public UserProfileFragment() {
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
		menu_list.add(MENU_SHARED, getString(R.string.menu_shared));
		menu_list.add(MENU_COMMENTED, getString(R.string.menu_commented));
		menu_list.add(MENU_FAVORITED, getString(R.string.menu_favorited));
		menu_list.add(MENU_FOLLOWING, getString(R.string.menu_following));
		menu_list.add(MENU_FOLLOWER, getString(R.string.menu_follower));

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
		Intent intent = new Intent(getActivity(), UserProfileActivity.class);

		switch (position) {
		case MENU_SHARED:
			intent.putExtra(UserProfileActivity.EXTRA_MENU_SELECTED,
					UserProfileActivity.MENU_SHARED);
			break;
		case MENU_COMMENTED:
		case MENU_FAVORITED:
		case MENU_FOLLOWING:
		case MENU_FOLLOWER:
		}

		startActivity(intent);
	}
}
