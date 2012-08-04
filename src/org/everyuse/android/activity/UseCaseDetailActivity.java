package org.everyuse.android.activity;

import java.util.ArrayList;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.CommentsHelper;
import org.everyuse.android.util.CommentsHelper.Comments;
import org.everyuse.android.util.CommentsHelper.OnCommentsUpdateHandler;
import org.everyuse.android.util.ImageDownloader;
import org.everyuse.android.util.RelationshipHelper;
import org.everyuse.android.util.RelationshipHelper.OnRelationshipUpdateHandler;
import org.everyuse.android.util.RelationshipHelper.Relationship;
import org.everyuse.android.util.UserHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class UseCaseDetailActivity extends SherlockFragmentActivity {
	public static String EXTRA_DATA = "DATA";
	public static String EXTRA_DATA_LIST = "DATA_LIST";
	public static String EXTRA_STRAT_INDEX = "START_INDEX";

	private ArrayList<UseCase> data_list;
	private int start_index;

	private static ViewPager pager;
	private static ItemsPagerAdapter pager_adapter;
	private static ImageDownloader image_downloader;

	public interface OnFollowUpdateListener {
		public void onFollowUpdate(int page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_usecase_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// handle intent
		handleIntent(getIntent());

		// initialize
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(com.
	 * actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return true;
	}

	private void initialize() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Set up the view pager
		pager = (ViewPager) findViewById(R.id.pager);
		pager_adapter = new ItemsPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pager_adapter);
		pager.setCurrentItem(start_index);

		// image downloader 초기화
		image_downloader = new ImageDownloader();

		// Comment helper 부착

	}

	private void handleIntent(Intent intent) {
		data_list = intent.getParcelableArrayListExtra(EXTRA_DATA_LIST);
		start_index = intent.getIntExtra(EXTRA_STRAT_INDEX, 0);

		// data가 인스턴스로 하나만 넘겨졌다면 그걸로 리스트 생성
		UseCase data = intent.getParcelableExtra(EXTRA_DATA);
		if (data != null) {
			data_list = new ArrayList<UseCase>();
			data_list.add(data);
		}

		if (data_list == null) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data));
		}
	}

	private class ItemsPagerAdapter extends FragmentStatePagerAdapter {

		public ItemsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			UseCase data = data_list.get(position);
			DetailFragment fragment = DetailFragment.newInstance(data);
			return fragment;
		}

		@Override
		public int getCount() {
			return data_list.size();
		}

	}

	public static class DetailFragment extends Fragment {
		private static String DATA = "DATA";
		private UseCase data;
		private CommentsHelper commentsHelper;
		private RelationshipHelper relationshipHelper;

		// Views
		private ToggleButton tgl_follow;
		private ToggleButton tgl_wow;
		private ToggleButton tgl_metoo;
		private ToggleButton tgl_scrap;

		public DetailFragment() {
		}

		public static DetailFragment newInstance(UseCase data) {
			DetailFragment f = new DetailFragment();

			// supply single UseCase as an argument.
			Bundle args = new Bundle();
			args.putParcelable(DATA, data);
			f.setArguments(args);

			return f;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			UseCase data = (UseCase) getArguments().getParcelable(DATA);
			long use_case_id = data.id;

			// 코멘트 헬퍼 초기화
			// use_case_id는 시작 아이템의 것으로 함
			commentsHelper = new CommentsHelper(getActivity(), use_case_id);
			commentsHelper
					.setOnCommentsUpdateHandler(new OnCommentsUpdateHandler() {

						@Override
						public void onUpdate(Comments comments) {
							updateCommentButtonState(comments);
						}

					});

			// 코멘트 가져오기
			commentsHelper.updateCurrentUserCommentsInfo();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
		 * , android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View page = inflater.inflate(
					R.layout.fragment_usecase_detail, null);
			return page;
		}

		private void display(View page, UseCase data) {
			// 사용자 정보 보여주는 뷰 초기화
			View profile_panel = page.findViewById(R.id.profile_panel);
			ImageView iv_user_photo = (ImageView) profile_panel
					.findViewById(R.id.iv_user_photo);
			TextView tv_username = (TextView) profile_panel
					.findViewById(R.id.tv_username);

			// 사용자 정보 출력
			tv_username.setText(data.writer_name);

			// Content panel
			ImageView usecase_photo = (ImageView) page
					.findViewById(R.id.iv_usecase_photo);
			TextView item = (TextView) page.findViewById(R.id.tv_item);
			TextView purpose = (TextView) page.findViewById(R.id.tv_purpose);

			image_downloader.download(data.getPhotoLargeURL(), usecase_photo);
			item.setText(data.item);
			purpose.setText(data.purpose);
		}

		private void updateCommentButtonState(Comments comments) {
			// update "Wow" button state
			if (comments.isWowed()) {
				tgl_wow.setChecked(true);
			} else {
				tgl_wow.setChecked(false);
			}
			String wow_count = String.valueOf(comments.getWowCount());
			tgl_wow.setText(wow_count);
			tgl_wow.setTextOn(wow_count + "");
			tgl_wow.setTextOff(wow_count + "");

			// update "Me too" button state
			if (comments.isMetooed()) {
				tgl_metoo.setChecked(true);
			} else {
				tgl_metoo.setChecked(false);
			}
			String metoo_count = String.valueOf(comments.getMetooCount());
			tgl_metoo.setText(metoo_count);
			tgl_metoo.setTextOn(metoo_count);
			tgl_metoo.setTextOff(metoo_count);

			// update "Scrap" button state
			if (comments.isScrapped()) {
				tgl_scrap.setChecked(true);
			} else {
				tgl_scrap.setChecked(false);
			}
		}

		private void hideCommentButtons() {
			View comment_panel = getView().findViewById(R.id.comment_panel);
			comment_panel.setVisibility(View.GONE);
			tgl_scrap.setVisibility(View.GONE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			final View page = getView();
			UseCase data = getArguments().getParcelable(DATA);

			// follow 버튼 초기화
			tgl_follow = (ToggleButton) page.findViewById(R.id.tgl_follow);
			relationshipHelper = new RelationshipHelper(getActivity(),
					data.writer_id, tgl_follow);
			relationshipHelper.updateRelationshipInfo();

			// 코멘트 버튼 초기화
			tgl_wow = (ToggleButton) page.findViewById(R.id.tgl_wow);
			tgl_wow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tgl_wow.isChecked()) {
						commentsHelper.postWow();
					} else {
						commentsHelper.deleteWow();
					}
				}

			});

			tgl_metoo = (ToggleButton) page.findViewById(R.id.tgl_metoo);
			tgl_metoo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tgl_metoo.isChecked()) {
						commentsHelper.postMetoo();
					} else {
						commentsHelper.deleteMetoo();
					}
				}

			});

			tgl_scrap = (ToggleButton) page.findViewById(R.id.tgl_scrap);
			tgl_scrap.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tgl_scrap.isChecked()) {
						commentsHelper.postScrap();
					} else {
						commentsHelper.deleteScrap();
					}
				}

			});

			Button btn_similar_item = (Button) page
					.findViewById(R.id.btn_similar_item);
			btn_similar_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					TextView tv_item = (TextView) page
							.findViewById(R.id.tv_item);
					String pre_item = tv_item.getText().toString();

					Intent intent = new Intent(getActivity(),
							CreateActivity.class);
					intent.putExtra(CreateActivity.EXTRA_ITEM, pre_item);
					startActivity(intent);
				}

			});

			Button btn_similar_purpose = (Button) page
					.findViewById(R.id.btn_similar_purpose);
			btn_similar_purpose.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					TextView tv_purpose = (TextView) page
							.findViewById(R.id.tv_purpose);
					String pre_purpose = tv_purpose.getText().toString();

					Intent intent = new Intent(getActivity(),
							CreateActivity.class);
					intent.putExtra(CreateActivity.EXTRA_PURPOSE, pre_purpose);
					startActivity(intent);

				}

			});

			// 만약 글쓴이가 현재 사용자와 같다면 코멘트 및 팔로우 기능 해제
			if (UserHelper.isCurrentUser(getActivity(), data.writer_id)) {
				hideCommentButtons();
			}
			display(page, data);

		}
	}
}
