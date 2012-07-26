package org.everyuse.android.util;

import org.everyuse.android.R;

import android.content.res.Resources;

public class URLHelper {
	public static String BASE_URL = "http://wikiuse.kaist.ac.kr";
	public static String USERS_URL = BASE_URL + "/users";
	public static String USE_CASES_RECENT_URL = BASE_URL + "/use_cases";
	public static String USE_CASES_TOP_URL = USE_CASES_RECENT_URL + "/top";
	public static String USE_CASE_GROUPS_URL = USE_CASES_RECENT_URL + "/groups";
	public static String LOGIN_URL = BASE_URL + "/login";
	public static String USER_SESSIONS_URL = BASE_URL + "/user_sessions";
	public static String PHOTOS_URL = BASE_URL + "/photos";
	public static String SEARCH_URL = BASE_URL + "/search";
	public static String SCRAP_URL = BASE_URL + "/favorite";
	public static String WOW_URL = BASE_URL + "/wow";
	public static String METOO_URL = BASE_URL + "/metoo";

	public static String getMyFollowerURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/followers";
	}

	public static String getMyFollowingURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/followings";
	}

	public static String getMyFeedsURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/feeds";
	}

	public static String getMyScrapedURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/favorited";
	}

	public static String getMySharedURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/use_cases";
	}

	public static String getMyCommentedURL(int user_id) {
		if (user_id <= 0) {
			throw new IllegalArgumentException(Resources.getSystem().getString(
					R.string.msg_wrong_user_id));
		}

		return USERS_URL + "/" + user_id + "/commented";
	}
}