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

	private Comments comments;
	private static Gson gson = new Gson();

	private OnCommentsUpdateHandler handler;

	public class Comment {
		public int id;
		public int user_id;
		public long use_case_id;
	}

	public class Comments {
		public Comment favorite;
		public Comment wow;
		public Comment metoo;
		public int wow_count;
		public int metoo_count;

		public boolean isScrapped() {
			return (favorite != null);
		}

		public boolean isWowed() {
			return (wow != null);
		}

		public boolean isMetooed() {
			return (metoo != null);

		}

		public int getWowCount() {
			return wow_count;
		}

		public int getMetooCount() {
			return metoo_count;
		}
	}

	public CommentsHelper() {

	}

	public interface OnCommentsUpdateHandler {
		public void onUpdate(Comments comments);
	}

	public void setOnCommentsUpdateHandler(OnCommentsUpdateHandler handler) {
		this.handler = handler;
	}

	public CommentsHelper(final Context context, long use_case_id) {
		this.context = context;
		this.user_id = UserHelper.getCurrentUser(context).id;
		this.use_case_id = use_case_id;

		client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");
	}

	public void updateCommentsInfo() {
		String url = URLHelper.COMMENTS_BASE_URL + ".json";
		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				comments = gson.fromJson(response, Comments.class);

				Log.d("Comments", gson.toJson(comments));
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
				if (handler != null) {
					handler.onUpdate(comments);
				}

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
		if (comments.favorite == null) {
			return true;
		}

		int comment_id = comments.favorite.id;

		return deleteComment(SCRAP, comment_id);
	}

	public boolean deleteWow() {
		if (comments.wow == null) {
			return true;
		}

		int comment_id = comments.wow.id;

		return deleteComment(WOW, comment_id);
	}

	public boolean deleteMetoo() {
		if (comments.metoo == null) {
			return true;
		}

		int comment_id = comments.metoo.id;

		return deleteComment(METOO, comment_id);
	}

	private boolean postComment(final int type) {
		final String url = getURL(type) + ".json";

		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {

				Toast.makeText(context, getCommentedString(type),
						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.d("Comments", content);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				// 코멘트를 올린 다음, 현재 사용자의 코멘트를 정보를 업데이트함
				updateCommentsInfo();
			}

		});

		return true;
	}

	private boolean deleteComment(final int type, int comment_id) {
		String url = getURL(type) + "/" + comment_id + ".json";
		client.delete(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {

				Toast.makeText(context, getUnCommentedString(type),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Throwable throwable, String content) {
				Log.d("Comments", content);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onFinish()
			 */
			@Override
			public void onFinish() {
				// 코멘트를 올린 다음, 현재 사용자의 코멘트를 정보를 업데이트함
				updateCommentsInfo();
			}

		});

		return true;
	}

	private static String getURL(int type) {
		switch (type) {
		case SCRAP:
			return URLHelper.COMMENTS_FAVORITE_URL;
		case WOW:
			return URLHelper.COMMENTS_WOW_URL;
		case METOO:
			return URLHelper.COMMENTS_METOO_URL;
		default:
			return null;
		}
	}

	private static String getCommentedString(int type) {
		switch (type) {
		case SCRAP:
			return "Scraped!";
		case WOW:
			return "Wow!";
		case METOO:
			return "Metoo!";
		default:
			return "";
		}
	}

	private static String getUnCommentedString(int type) {
		switch (type) {
		case SCRAP:
			return "UnScraped!";
		case WOW:
			return "UnWow!";
		case METOO:
			return "UnMetoo!";
		default:
			return "";
		}
	}

}
