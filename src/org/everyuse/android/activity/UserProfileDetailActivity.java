package org.everyuse.android.activity;

import org.everyuse.android.R;
import org.everyuse.android.fragment.UseCaseListWithOptionFragment;
import org.everyuse.android.fragment.UserListFragment;
import org.everyuse.android.fragment.UserProfileFragment;
import org.everyuse.android.model.User;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

public class UserProfileDetailActivity extends FragmentActivity {
	private User user;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	public static final String EXTRA_USER = "extra_user";
	public static final String EXTRA_MENU_SELECTED = "menu_selected";
	public static final int MENU_NOT_SELECTED = -1;
	public static final int MENU_SHARED = 0;
	public static final int MENU_COMMENTED = 1;
	public static final int MENU_SCRAPED = 2;
	public static final int MENU_FOLLOWING = 3;
	public static final int MENU_FOLLOWER = 4;
	public static final int MENU_USER = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		initFragmentManager();

		handleIntent(getIntent());
	}
	
	private String getTitle(int menu_selected) {
		User current_user = UserHelper.getCurrentUser(this);
		String username = "";
		String title = "";
		
		if (current_user.equals(user)) {
			username = "My";
		} else {
			username = user.username + "\'s";
		}

		// 유저 이름 (혹은 My)를 스트링에 붙여넣는다
		title += username + " ";

		// 선택된 메뉴에 따라 title을 달리한다.
		switch (menu_selected) {

		case MENU_SHARED:
			title += "Shared Uses";
			break;
		case MENU_COMMENTED:
			title += "Commented Uses";
			break;
		case MENU_SCRAPED:
			title += "Scraped Uses";
			break;
		case MENU_FOLLOWING:
			title += "Following";
			break;
		case MENU_FOLLOWER:
			title += "Follower";
			break;
		case MENU_USER:
			title += "Profile";
			break;
		}
		
		return title;
	}

//	private void setTitle(String title) {
//		TextView tv_title = (TextView) findViewById(R.id.tv_title);
//		tv_title.setText(title);
//	}

	private void initFragmentManager() {
		fragmentManager = getSupportFragmentManager();
	}

	private void handleIntent(Intent intent) {
		user = intent.getParcelableExtra(EXTRA_USER);
		int menu_selected = intent.getIntExtra(EXTRA_MENU_SELECTED,
				MENU_NOT_SELECTED);

		if (menu_selected == MENU_NOT_SELECTED || user == null) {
			throw new IllegalStateException(
					getString(R.string.msg_intent_parameter_not_set));
		}

		setFragment(menu_selected);
		String title = getTitle(menu_selected);
		setTitle(title);
	}

	private void setFragment(int menu_selected) {
		fragmentTransaction = fragmentManager.beginTransaction();

		if (fragmentTransaction == null) {
			throw new IllegalStateException("FragmentTransaction is null!");
		}

		// Replace whatever is in the fragment_container view with this
		// fragment,
		fragmentTransaction.replace(R.id.fragment_container,
				getFragment(menu_selected));

		// Commit the transaction
		fragmentTransaction.commit();
	}

	private Fragment getFragment(int menu_selected) {
		switch (menu_selected) {

		case MENU_SHARED:
			return UseCaseListWithOptionFragment.newInstance(
					URLHelper.getMySharedURL(user.id), R.array.use_case_time);
		case MENU_COMMENTED:
			return UseCaseListWithOptionFragment.newInstance(
					URLHelper.getMyCommentedURL(user.id), R.array.comment);
		case MENU_SCRAPED:
			return UseCaseListWithOptionFragment.newInstance(
					URLHelper.getMyScrapedURL(user.id), R.array.use_case_time);
		case MENU_FOLLOWING:
			return UserListFragment.newInstance(URLHelper
					.getMyFollowingURL(user.id));
		case MENU_FOLLOWER:
			return UserListFragment.newInstance(URLHelper
					.getMyFollowerURL(user.id));
		case MENU_USER:
			return UserProfileFragment.newInstance(user);

		}

		Log.d("UserInfoDetailActivity", menu_selected + "");
		return null;
	}

}
