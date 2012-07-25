package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.activity.UserProfileDetailActivity;
import org.everyuse.android.model.User;
import org.everyuse.android.util.UserHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class UserProfileFragment extends ListFragment {
	private User user;
	private List<String> menu_list = new ArrayList<String>();
	public static final String EXTRA_USER = "user";

	public UserProfileFragment() {
		super();
	}

	public static UserProfileFragment newInstance(User user) {
		UserProfileFragment f = new UserProfileFragment();
		Bundle b = new Bundle();
		b.putParcelable(EXTRA_USER, user);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			user = args.getParcelable(EXTRA_USER);
		}

		if (user == null) {
			throw new IllegalStateException(
					getString(R.string.msg_intent_parameter_not_set));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (user == null) {
			throw new IllegalStateException(
					getString(R.string.msg_intent_parameter_not_set));
		}

		// 유저 정보 출력
		View view = inflater.inflate(R.layout.fragment_mypage, null);
		TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
		tv_username.setText(user.username);
		
		return view;
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

		menu_list.add(UserProfileDetailActivity.MENU_SHARED,
				getString(R.string.menu_shared));

		menu_list.add(UserProfileDetailActivity.MENU_COMMENTED,
				getString(R.string.menu_commented));

		menu_list.add(UserProfileDetailActivity.MENU_SCRAPED,
				getString(R.string.menu_scraped));

		if (isCurrentUser(user)) {
			menu_list.add(UserProfileDetailActivity.MENU_FOLLOWING,
					getString(R.string.menu_following));

			menu_list.add(UserProfileDetailActivity.MENU_FOLLOWER,
					getString(R.string.menu_follower));
		}

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
		Intent intent = new Intent(getActivity(), UserProfileDetailActivity.class);
		intent.putExtra(UserProfileDetailActivity.EXTRA_MENU_SELECTED, position);
		intent.putExtra(UserProfileDetailActivity.EXTRA_USER, user);
		startActivity(intent);
	}

	private boolean isCurrentUser(User user) {
		return (user.id == UserHelper.getCurrentUser(getActivity()).id);
	}
}
