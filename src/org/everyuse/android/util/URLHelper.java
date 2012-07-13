package org.everyuse.android.util;

public class URLHelper {
	public static String BASE_URL 						= "http://wikiuse.kaist.ac.kr";
	public static String USERS_URL 						= BASE_URL + "/users";
	public static String USE_CASES_RECENT_URL 					= BASE_URL + "/use_cases";
	public static String USE_CASES_TOP_URL 				= USE_CASES_RECENT_URL + "/top";
	public static String USE_CASE_GROUP_ITEM_URL 		= USE_CASES_RECENT_URL + "/item";
	public static String USE_CASE_GROUP_PURPOSE_URL 	= USE_CASES_RECENT_URL + "/purpose";
	public static String LOGIN_URL 						= BASE_URL + "/login";
	public static String USER_SESSIONS_URL 				= BASE_URL + "/user_sessions";
	public static String PHOTOS_URL 					= BASE_URL + "/photos";
	public static String SEARCH_URL						= BASE_URL + "/search";
	public static String COMMENT_URL					= BASE_URL + "/comments";
	public static String FAVORITE_ADD_URL				= BASE_URL + "/favorite/add";
	public static String FAVORITE_DELETE_URL			= BASE_URL + "/favorite/delete";
	public static String FUN_ADD_URL					= BASE_URL + "/fun/add";
	public static String FUN_DELETE_URL					= BASE_URL + "/fun/delete";
	public static String METOO_ADD_URL					= BASE_URL + "/metoo/add";
	public static String METOO_DELETE_URL				= BASE_URL + "/metoo/delete";
	
	public static String getMyFavoritedURL(int user_id) {
		return USERS_URL + "/" + user_id + "/favorited";
	}
}