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
	private String user_credentials;
	private long use_case_id;

	public CommentsHelper() {

	}

	public CommentsHelper(Context context, long use_case_id) {
		this.context = context;
		this.user = UserHelper.getCurrentUser(context);
		this.user_credentials = user.single_access_token;
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
//		String url = getURL(type) + "/" + comment_id;
//		HttpDelete httpDelete = new HttpDelete(url);
//
//		try {
//			client.execute(httpDelete);
//
//			return true;
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
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

	// private static Comment getComment(int type, int user_id, long
	// use_case_id) {
	// String url = getURL(type);
	//
	// // build query string using parameters
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("comment[user_id]", String
	// .valueOf(user_id)));
	// params.add(new BasicNameValuePair("comment[use_case_id]", String
	// .valueOf(use_case_id)));
	// String query_string = URLEncodedUtils.format(params, "UTF-8");
	//
	// // 쿼리가 추가된 URL 생성
	// String url_with_query = url + "?" + query_string;
	//
	// // 요청 시작
	// HttpGet httpGet = new HttpGet(url_with_query);
	// Comment comment = null;
	//
	// try {
	// HttpResponse response = client.execute(httpGet);
	// HttpEntity entity = response.getEntity();
	//
	// if (entity != null) {
	// int code = response.getStatusLine().getStatusCode();
	//
	// if (code >= 300) { // HTTP error code
	//
	// } else { // success
	// String res_string = EntityUtils.toString(entity);
	// JSONObject comment_json = new JSONObject(res_string);
	// comment = Comment.parseSingleFromJSON(comment_json);
	// }
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return comment;
	// }

	// private Context context;
	// private int user_id;
	// private long use_case_id;
	//
	// private AsyncHttpClient client;
	//
	// private static final int ADD = 0;
	// private static final int DELETE = 1;
	//
	// public CommentHelper() {
	// }
	//
	// public CommentHelper(Context context, int user_id, long use_case_id) {
	// this.context = context;
	// this.user_id = user_id;
	// this.use_case_id = use_case_id;
	//
	// client = new AsyncHttpClient();
	// }
	//
	// public Comment getUserComment() {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
	// params.add(new BasicNameValuePair("use_case_id",
	// String.valueOf(use_case_id)));
	// String query = URLEncodedUtils.format(params, "utf-8");
	//
	// String url = URLHelper.COMMENT_URL + ".json" + "?" + query;
	// HttpGet method = new HttpGet(url);
	//
	// BufferedReader in = null;
	// Comment comment = null;
	// try {
	// HttpClient client = new DefaultHttpClient();
	// HttpResponse response = client.execute(method);
	//
	// in = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	//
	// StringBuffer sb = new StringBuffer("");
	// String line = "";
	// while ((line = in.readLine()) != null) {
	// sb.append(line);
	// }
	//
	// JSONObject json = new JSONObject(sb.toString());
	// comment = Comment.parseFromJSON(json);
	//
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return comment;
	// }
	//
	// public int setComment(final CommentType type) {
	// final String url = getURL(type, ADD);
	//
	// RequestParams params = new RequestParams();
	// params.put("comment[user_id]", String.valueOf(user_id));
	// params.put("comment[use_case_id]", String.valueOf(use_case_id));
	//
	// client.post(url, params, new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(String response) {
	// Toast.makeText(context, type.toString(), 200).show();
	// }
	//
	// @Override
	// public void onFailure(Throwable throwable) {
	// // Toast.makeText(context, "Failed to " + type.toString(),
	// // 200).show();
	// }
	// });
	//
	// return 0;
	// }
	//
	// public int unsetComment(final CommentType type) {
	// String url = getURL(type, DELETE);
	//
	// RequestParams params = new RequestParams();
	// params.put("comment[user_id]", String.valueOf(user_id));
	// params.put("comment[use_case_id]", String.valueOf(use_case_id));
	//
	// client.post(url, params, new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(String response) {
	// Toast.makeText(context, "Un" + type.toString(), 200).show();
	// }
	//
	// @Override
	// public void onFailure(Throwable throwable) {
	// // Toast.makeText(context, "Failed to Un" + type.toString(), 200).show();
	// }
	// });
	//
	// return 0;
	// }
	//
	// private String getURL(CommentType type, int mode) {
	// String url = null;
	//
	// switch (mode) {
	// case ADD:
	// switch (type) {
	// case FAVORITE:
	// url = URLHelper.SCRAP_ADD_URL + ".json";
	// break;
	// case WOW:
	// url = URLHelper.WOW_ADD_URL + ".json";
	// break;
	// case METOO:
	// url = URLHelper.METOO_ADD_URL + ".json";
	// break;
	// }
	// break;
	// case DELETE:
	// switch (type) {
	// case FAVORITE:
	// url = URLHelper.SCRAP_DELETE_URL + ".json";
	// break;
	// case WOW:
	// url = URLHelper.WOW_DELETE_URL + ".json";
	// break;
	// case METOO:
	// url = URLHelper.METOO_DELETE_URL + ".json";
	// break;
	// }
	// break;
	// }
	//
	// return url;
	// }
}
