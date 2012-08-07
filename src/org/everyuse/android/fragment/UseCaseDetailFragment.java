package org.everyuse.android.fragment;

import org.everyuse.android.R;
import org.everyuse.android.activity.CreateActivity;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.CommentsHelper;
import org.everyuse.android.util.CommentsHelper.Comments;
import org.everyuse.android.util.CommentsHelper.OnCommentsUpdateHandler;
import org.everyuse.android.util.ImageDownloader;
import org.everyuse.android.util.RelationshipHelper;
import org.everyuse.android.util.RelationshipHelper.OnRelationshipUpdateHandler;
import org.everyuse.android.util.RelationshipHelper.Relationship;
import org.everyuse.android.util.UserHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class UseCaseDetailFragment extends Fragment {
	private static String DATA = "DATA";
	private static ImageDownloader image_downloader;
	private CommentsHelper commentsHelper;
	private RelationshipHelper relationshipHelper;

	// Views
	private ToggleButton tgl_follow;
	private ToggleButton tgl_wow;
	private ToggleButton tgl_metoo;
	private ToggleButton tgl_scrap;

	private OnFollowUpdateListener followCallback;

	public interface OnFollowUpdateListener {
		public void onFollowUpdate(int page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			followCallback = (OnFollowUpdateListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFollowUpdateListener");
		}
	}

	public UseCaseDetailFragment() {
		// image downloader 초기화
		image_downloader = new ImageDownloader();
	}

	public static UseCaseDetailFragment newInstance(UseCase data) {
		UseCaseDetailFragment f = new UseCaseDetailFragment();

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
		final View page = inflater.inflate(R.layout.fragment_usecase_detail,
				null);
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
		tgl_wow.setEnabled(false);
		tgl_metoo.setEnabled(false);
		tgl_scrap.setVisibility(View.GONE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
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
		relationshipHelper
				.setOnRelationshipUpdateHandler(new OnRelationshipUpdateHandler() {

					@Override
					public void onUpdate(Relationship relationship) {
						// TODO 의미있는 숫자 리턴
						followCallback.onFollowUpdate(0);

					}

				});
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
				TextView tv_item = (TextView) page.findViewById(R.id.tv_item);
				String pre_item = tv_item.getText().toString();

				Intent intent = new Intent(getActivity(), CreateActivity.class);
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

				Intent intent = new Intent(getActivity(), CreateActivity.class);
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