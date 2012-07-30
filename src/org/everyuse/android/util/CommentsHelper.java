package org.everyuse.android.util;

import org.everyuse.android.R;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CommentsHelper {
	private static AsyncHttpClient client;

	public static final int SCRAP = 0;
	public static final int WOW = 1;
	public static final int METOO = 2;

	private Context context;
	private int user_id;
	private long use_case_id;

	private CurrentUserComments comments;
	private static Gson gson = new Gson();

	public class CurrentUserComments {
		public Comment current_user_favorite;
		public Comment current_user_wow;
		public Comment current_user_metoo;
	}

	public class Comment {
		public int id;
		public int user_id;
		public long use_case_id;
	}

	public CommentsHelper() {

	}

	public CommentsHelper(final Context context, long use_case_id) {
		this.context = context;
		this.user_id = UserHelper.getCurrentUser(context).id;
		this.use_case_id = use_case_id;

		client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");

		// 보는 글에 달린 현재 사용자의 댓글들을 가져옴
		fetchCurrentUserComments();
	}

	private void fetchCurrentUserComments() {
		String url = URLHelper.COMMENTS_BASE_URL + ".json";
		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				comments = gson.fromJson(response, CurrentUserComments.class);

				Log.d("Comments", gson.toJson(comments));
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Toast.makeText(context, "Failed to retrieve user comments.",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	public void setUseCaseID(long use_case_id) {
		this.use_case_id = use_case_id;
	}

	public boolean postScrap() {
		if (use_case_id == 0) {
			throw new IllegalStateException(
					context.getString(R.string.msg_missing_use_case_id));
		}

		return postComment(SCRAP);
	}

	public boolean postWow() {
		if (use_case_id == 0) {
			throw new IllegalStateException(
					context.getString(R.string.msg_missing_use_case_id));
		}

		return postComment(WOW);
	}

	public boolean postMetoo() {
		if (use_case_id == 0) {
			throw new IllegalStateException(
					context.getString(R.string.msg_missing_use_case_id));
		}

		return postComment(METOO);
	}

	public boolean deleteScrap() {
		if (comments.current_user_favorite == null) {
			return true;
		}

		int comment_id = comments.current_user_favorite.id;

		return deleteComment(SCRAP, comment_id);
	}

	public boolean deleteWow() {
		if (comments.current_user_wow == null) {
			return true;
		}

		int comment_id = comments.current_user_wow.id;

		return deleteComment(WOW, comment_id);
	}

	public boolean deleteMetoo() {
		if (comments.current_user_metoo == null) {
			return true;
		}

		int comment_id = comments.current_user_metoo.id;

		return deleteComment(METOO, comment_id);
	}

	private boolean postComment(int type) {
		final String url = getURL(type) + ".json";

		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "post success", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.d("Comments", content);
			}
		});

		// 코멘트를 올린 다음, 현재 사용자의 코멘트를 정보를 업데이트함
		fetchCurrentUserComments();

		return true;
	}

	private boolean deleteComment(int type, int comment_id) {
		String url = getURL(type) + "/" + comment_id + ".json";
		client.delete(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "delete success", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onFailure(Throwable throwable, String content) {
				Log.d("Comments", content);
			}

		});

		// 코멘트를 삭제한 다음, 현재 사용자의 코멘트를 정보를 업데이트함
		fetchCurrentUserComments();

		return false;
	}

	private static String getURL(int type) {
		switch (type) {
		case SCRAP:
			return URLHelper.COMMENTS_SCRAP_URL;
		case WOW:
			return URLHelper.COMMENTS_WOW_URL;
		case METOO:
			return URLHelper.COMMENTS_METOO_URL;
		default:
			return null;
		}
	}

}
