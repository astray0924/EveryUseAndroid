package org.everyuse.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.activity.UserMenuDetailActivity;
import org.everyuse.android.model.User;
import org.everyuse.android.util.RelationshipHelper;
import org.everyuse.android.util.UserHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class UserMenuFragment extends SherlockFragment implements
		OnClickListener {
	// Strings for logging
	private final String TAG = this.getClass().getSimpleName();

	private User user;
	private List<String> menu_list = new ArrayList<String>();
	public static final String EXTRA_USER = "user";

	private RelationshipHelper relationshipHelper;

	public UserMenuFragment() {
		super();
	}

	public static UserMenuFragment newInstance(User user) {
		UserMenuFragment f = new UserMenuFragment();
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

		View view = inflater.inflate(R.layout.fragment_user_menu, null);
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

		View view = getView();

		// 유저 정보 출력
		TextView tv_username = (TextView) view.findViewById(R.id.tv_username);
		tv_username.setText(user.username);

		// follow 버튼 초기화
		ToggleButton tgl_follow = (ToggleButton) view
				.findViewById(R.id.tgl_follow);
		relationshipHelper = new RelationshipHelper(getActivity(), user.id,
				tgl_follow);
		relationshipHelper.updateRelationshipInfo();


		// 메뉴 핸들러 초기화
		TextView menu_shared = (TextView) view.findViewById(R.id.menu_shared);
		menu_shared.setOnClickListener(this);
		
		TextView menu_commented = (TextView) view.findViewById(R.id.menu_commented);
		menu_commented.setOnClickListener(this);
		
		TextView menu_scraped = (TextView) view.findViewById(R.id.menu_scraped);
		menu_scraped.setOnClickListener(this);

		// 현재 사용자 여부 체크 & 적절한 메뉴 보이기
		if (isCurrentUser()) {
			TextView menu_following = (TextView) view.findViewById(R.id.menu_following);
			menu_following.setVisibility(View.VISIBLE);
			menu_following.setOnClickListener(this);
			
			TextView menu_follower = (TextView) view.findViewById(R.id.menu_follower);
			menu_follower.setVisibility(View.VISIBLE);
			menu_follower.setOnClickListener(this);
		}
	}

	private boolean isCurrentUser() {
		return UserHelper.isCurrentUser(getActivity(), user);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		int position = -1;

		switch (id) {
		case R.id.menu_shared:
			position = UserMenuDetailActivity.MENU_SHARED;
			break;
		case R.id.menu_commented:
			position = UserMenuDetailActivity.MENU_COMMENTED;
			break;
		case R.id.menu_scraped:
			position = UserMenuDetailActivity.MENU_SCRAPED;
			break;
		case R.id.menu_following:
			position = UserMenuDetailActivity.MENU_FOLLOWING;
			break;
		case R.id.menu_follower:
			position = UserMenuDetailActivity.MENU_FOLLOWER;
			break;
		default:
			break;
		}

		// 선택된 메뉴에 적절한 동작 수행
		Intent intent = new Intent(getActivity(), UserMenuDetailActivity.class);
		intent.putExtra(UserMenuDetailActivity.EXTRA_MENU_SELECTED, position);
		intent.putExtra(UserMenuDetailActivity.EXTRA_USER, user);
		startActivity(intent);
	}
}
