package org.everyuse.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.fragment.UseCaseListWithOptionFragment;
import org.everyuse.android.model.User;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class UserProfileActivity extends FragmentActivity {
	private User user;
	private int user_id;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private List<Fragment> fragment_list;

	public static final String EXTRA_MENU_SELECTED = "menu_selected";
	public static final int MENU_NOT_SELECTED = -1;
	public static final int MENU_SHARED = 0;
	public static final int MENU_COMMENTED = 1;
	public static final int MENU_SCRAPED = 2;
	public static final int MENU_FOLLOWING = 3;
	public static final int MENU_FOLLOWER = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile); 

		initCurrentUserInfo();
		initFragmentManager();
		initFragments();

		handleIntent(getIntent());
	}

	private void initFragmentManager() {
		fragmentManager = getSupportFragmentManager();
	}

	private void handleIntent(Intent intent) {
		int menu_selected = intent.getIntExtra(EXTRA_MENU_SELECTED,
				MENU_NOT_SELECTED);
		if (menu_selected == MENU_NOT_SELECTED) {
			throw new IllegalStateException(
					getString(R.string.msg_require_intent_parameter));
		}

		setFragment(menu_selected);
	}

	private void setFragment(int menu_selected) {
		fragmentTransaction = fragmentManager.beginTransaction();

		if (fragmentTransaction == null) {
			throw new IllegalStateException("FragmentTransaction is null!");
		}

		// Replace whatever is in the fragment_container view with this
		// fragment,
		fragmentTransaction.replace(R.id.fragment_container,
				fragment_list.get(menu_selected));

		// Commit the transaction
		fragmentTransaction.commit();
	}

	private void initCurrentUserInfo() {
		user = UserHelper.getCurrentUser(this);

		if (user == null) {
			throw new IllegalStateException(
					getString(R.string.msg_user_session_invalid));
		} else {
			user_id = user.id;
		}
	}

	private void initFragments() {
		fragmentTransaction = fragmentManager.beginTransaction();
		
		// initialize fragments
		fragment_list = new ArrayList<Fragment>();
		fragment_list.add(MENU_SHARED, new UseCaseListWithOptionFragment(
				URLHelper.getMySharedURL(user_id), R.array.use_case_time));

		// add fragments
		for (Fragment fg : fragment_list) {
			fragmentTransaction.add(R.id.fragment_container, fg);
		}
		fragmentTransaction.commit();
	}
}
