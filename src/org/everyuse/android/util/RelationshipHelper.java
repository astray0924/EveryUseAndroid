package org.everyuse.android.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RelationshipHelper {
	private static AsyncHttpClient client;
	private Relationship relationship;
	private static Gson gson = new Gson();

	private Context context;

	private int follower_id;
	private int followed_id;

	private OnRelationshipUpdateHandler handler;

	private ToggleButton tgl_follow;

	public static class Relationship {
		public int id;
		public int followed_id;
		public int follower_id;
	}

	public interface OnRelationshipUpdateHandler {
		public void onUpdate(Relationship relationship);
	}

	public RelationshipHelper(final Context context, int followed_id,
			final ToggleButton tgl_follow) {
		this.context = context;
		this.follower_id = UserHelper.getCurrentUser(context).id;
		this.followed_id = followed_id;
		this.tgl_follow = tgl_follow;

		client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");

		tgl_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (tgl_follow.isChecked()) {
					followUser();
				} else {
					unfollowUser();
				}
			}

		});

		// 만약 글쓴이가 현재 사용자와 같다면 코멘트 및 팔로우 기능 해제
		if (UserHelper.isCurrentUser(context, followed_id)) {
			hideFollowButton();
		}

	}

	private void hideFollowButton() {
		tgl_follow.setVisibility(View.GONE);
	}

	private void updateButtonState(Relationship relationship) {
		if (relationship != null) { // not following
			tgl_follow.setChecked(true);
		} else {
			tgl_follow.setChecked(false);
		}

	}

	public void updateRelationshipInfo() {
		String url = URLHelper.RELATIONSHIP_URL + ".json";

		RequestParams params = new RequestParams();
		params.put("relationship[follower_id]", String.valueOf(follower_id));
		params.put("relationship[followed_id]", String.valueOf(followed_id));

		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Log.d("Relationship", response);

				relationship = gson.fromJson(response, Relationship.class);

			}

			@Override
			public void onFailure(Throwable error, String content) {
				Toast.makeText(context, "Failed to retrieve user comments.",
						Toast.LENGTH_LONG).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				updateButtonState(relationship);

				if (handler != null) {
					handler.onUpdate(relationship);
				}

			}
		});
	}

	public void setOnRelationshipUpdateHandler(
			OnRelationshipUpdateHandler handler) {
		this.handler = handler;
	}

	public void followUser() {
		if (follower_id == 0 || followed_id == 0) {
			throw new IllegalStateException(
					"follower or followed user id is not set!");
		}

		final String url = URLHelper.RELATIONSHIP_URL + ".json";

		RequestParams params = new RequestParams();
		params.put("relationship[follower_id]", String.valueOf(follower_id));
		params.put("relationship[followed_id]", String.valueOf(followed_id));

		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "Followed!", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Toast.makeText(context, "Failed to follow the user.",
						Toast.LENGTH_LONG).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				updateRelationshipInfo();
			}
		});
	}

	public void unfollowUser() {
		if (relationship == null) {
			Log.d("Relationship", "relationship is null.");
			return;
		}

		String url = URLHelper.RELATIONSHIP_URL + "/" + relationship.id
				+ ".json";
		client.delete(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "UnFollowed!", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFailure(Throwable throwable, String content) {
				Toast.makeText(context, "Failed to UnFollow the user.",
						Toast.LENGTH_LONG).show();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				updateRelationshipInfo();
			}

		});
	}
}
