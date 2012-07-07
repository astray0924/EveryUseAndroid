package org.everyuse.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.everyuse.android.model.Comment;
import org.everyuse.android.model.CommentType;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CommentHelper {
	private Context context;
	private int user_id;
	private long use_case_id;

	private AsyncHttpClient client;

	private static final int ADD = 0;
	private static final int DELETE = 1;

	public CommentHelper() {
	}

	public CommentHelper(Context context, int user_id, long use_case_id) {
		this.context = context;
		this.user_id = user_id;
		this.use_case_id = use_case_id;

		client = new AsyncHttpClient();
	}

	public Comment getUserComment() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
		params.add(new BasicNameValuePair("use_case_id", String.valueOf(use_case_id)));
		String query = URLEncodedUtils.format(params, "utf-8");

		String url = URLHelper.COMMENT_URL + ".json" + "?" + query;
		HttpGet method = new HttpGet(url);

		BufferedReader in = null;
		Comment comment = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(method);

			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			JSONObject json = new JSONObject(sb.toString());
			comment = Comment.parseFromJSON(json);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return comment;
	}

	public int setComment(final CommentType type) {
		final String url = getURL(type, ADD);

		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, type.toString(), 200).show();
			}

			@Override
			public void onFailure(Throwable throwable) {
//				Toast.makeText(context, "Failed to " + type.toString(), 200).show();
			}
		});

		return 0;
	}

	public int unsetComment(final CommentType type) {
		String url = getURL(type, DELETE);

		RequestParams params = new RequestParams();
		params.put("comment[user_id]", String.valueOf(user_id));
		params.put("comment[use_case_id]", String.valueOf(use_case_id));

		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				Toast.makeText(context, "Un" + type.toString(), 200).show();
			}

			@Override
			public void onFailure(Throwable throwable) {
//				Toast.makeText(context, "Failed to Un" + type.toString(), 200).show();
			}
		});

		return 0;
	}

	private String getURL(CommentType type, int mode) {
		String url = null;

		switch (mode) {
		case ADD:
			switch (type) {
			case FAVORITE:
				url = URLHelper.FAVORITE_ADD_URL + ".json";
				break;
			case FUN:
				url = URLHelper.FUN_ADD_URL + ".json";
				break;
			case METOO:
				url = URLHelper.METOO_ADD_URL + ".json";
				break;
			}
			break;
		case DELETE:
			switch (type) {
			case FAVORITE:
				url = URLHelper.FAVORITE_DELETE_URL + ".json";
				break;
			case FUN:
				url = URLHelper.FUN_DELETE_URL + ".json";
				break;
			case METOO:
				url = URLHelper.METOO_DELETE_URL + ".json";
				break;
			}
			break;
		}

		return url;
	}
}
