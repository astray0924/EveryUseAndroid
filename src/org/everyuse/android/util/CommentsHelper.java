package org.everyuse.android.util;

import org.everyuse.android.R;
import org.everyuse.android.model.User;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CommentsHelper {
	private static AsyncHttpClient client;

	public static final int SCRAP = 0;
	public static final int WOW = 1;
	public static final int METOO = 2;

	private Context context;
	private User user;
	private long use_case_id;

	public CommentsHelper() {

	}

	public CommentsHelper(Context context, long use_case_id) {
		this.context = context;
		this.user = UserHelper.getCurrentUser(context);
		this.use_case_id = use_case_id;

		client = new AsyncHttpClient();
		client.addHeader("Content-type", "application/x-www-form-urlencoded");
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

	public boolean deleteScrap(int comment_id) {
		return deleteComment(SCRAP, comment_id);
	}

	public boolean deleteWow(int comment_id) {
		return deleteComment(WOW, comment_id);
	}

	public boolean deleteMetoo(int comment_id) {
		return deleteComment(METOO, comment_id);
	}

	private boolean postComment(int type) {
		final String url = getURL(type);

		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user.id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.d("Comments", content);
			}
		});

		return true;
	}

	private boolean deleteComment(int type, int comment_id) {
		String url = getURL(type) + "/" + comment_id;
		client.delete(url, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(Throwable throwable, String content) {
				Log.d("Comments", content);
			}


		});
		
		return false;
	}

	private static String getURL(int type) {
		switch (type) {
		case SCRAP:
			return URLHelper.COMMENTS_SCRAP_URL + ".json";
		case WOW:
			return URLHelper.COMMENTS_WOW_URL + ".json";
		case METOO:
			return URLHelper.COMMENTS_METOO_URL + ".json";
		default:
			return null;
		}
	}

}
