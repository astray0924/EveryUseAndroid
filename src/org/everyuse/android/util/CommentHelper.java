package org.everyuse.android.util;


public class CommentHelper {
//	private Context context;
//	private int user_id;
//	private long use_case_id;
//
//	private AsyncHttpClient client;
//
//	private static final int ADD = 0;
//	private static final int DELETE = 1;
//
//	public CommentHelper() {
//	}
//
//	public CommentHelper(Context context, int user_id, long use_case_id) {
//		this.context = context;
//		this.user_id = user_id;
//		this.use_case_id = use_case_id;
//
//		client = new AsyncHttpClient();
//	}
//
//	public Comment getUserComment() {
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
//		params.add(new BasicNameValuePair("use_case_id", String.valueOf(use_case_id)));
//		String query = URLEncodedUtils.format(params, "utf-8");
//
//		String url = URLHelper.COMMENT_URL + ".json" + "?" + query;
//		HttpGet method = new HttpGet(url);
//
//		BufferedReader in = null;
//		Comment comment = null;
//		try {
//			HttpClient client = new DefaultHttpClient();
//			HttpResponse response = client.execute(method);
//
//			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//
//			StringBuffer sb = new StringBuffer("");
//			String line = "";
//			while ((line = in.readLine()) != null) {
//				sb.append(line);
//			}
//
//			JSONObject json = new JSONObject(sb.toString());
//			comment = Comment.parseFromJSON(json);
//
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return comment;
//	}
//
//	public int setComment(final CommentType type) {
//		final String url = getURL(type, ADD);
//
//		RequestParams params = new RequestParams();
//		params.put("comment[user_id]", String.valueOf(user_id));
//		params.put("comment[use_case_id]", String.valueOf(use_case_id));
//
//		client.post(url, params, new AsyncHttpResponseHandler() {
//			@Override
//			public void onSuccess(String response) {
//				Toast.makeText(context, type.toString(), 200).show();
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
////				Toast.makeText(context, "Failed to " + type.toString(), 200).show();
//			}
//		});
//
//		return 0;
//	}
//
//	public int unsetComment(final CommentType type) {
//		String url = getURL(type, DELETE);
//
//		RequestParams params = new RequestParams();
//		params.put("comment[user_id]", String.valueOf(user_id));
//		params.put("comment[use_case_id]", String.valueOf(use_case_id));
//
//		client.post(url, params, new AsyncHttpResponseHandler() {
//			@Override
//			public void onSuccess(String response) {
//				Toast.makeText(context, "Un" + type.toString(), 200).show();
//			}
//
//			@Override
//			public void onFailure(Throwable throwable) {
////				Toast.makeText(context, "Failed to Un" + type.toString(), 200).show();
//			}
//		});
//
//		return 0;
//	}
//
//	private String getURL(CommentType type, int mode) {
//		String url = null;
//
//		switch (mode) {
//		case ADD:
//			switch (type) {
//			case FAVORITE:
//				url = URLHelper.SCRAP_ADD_URL + ".json";
//				break;
//			case WOW:
//				url = URLHelper.WOW_ADD_URL + ".json";
//				break;
//			case METOO:
//				url = URLHelper.METOO_ADD_URL + ".json";
//				break;
//			}
//			break;
//		case DELETE:
//			switch (type) {
//			case FAVORITE:
//				url = URLHelper.SCRAP_DELETE_URL + ".json";
//				break;
//			case WOW:
//				url = URLHelper.WOW_DELETE_URL + ".json";
//				break;
//			case METOO:
//				url = URLHelper.METOO_DELETE_URL + ".json";
//				break;
//			}
//			break;
//		}
//
//		return url;
//	}
}
