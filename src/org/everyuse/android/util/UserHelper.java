package org.everyuse.android.util;

import org.everyuse.android.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserHelper {
	public static boolean DEVELOPER_MODE = true;

	public static void storeUser(Context context, User user) {
		if (user == null) {
			throw new IllegalArgumentException();
		}

		// store into shared preferences
		SharedPreferences prefs = context.getSharedPreferences("USER",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("id", user.id);
		editor.putString("username", user.username);
		editor.putString("email", user.email);
		editor.putString("persistence_token", user.persistence_token);
		editor.putString("single_access_token", user.single_access_token);
		editor.commit();
	}

	public static boolean isCurrentUser(Context context, User user) {
		User current_user = getCurrentUser(context);
		return (current_user.equals(user));
	}

	public static boolean isCurrentUser(Context context, int user_id) {
		User current_user = getCurrentUser(context);
		return (current_user.id == user_id);
	}

	public static int getCurrentUserId(Context context) {
		return getCurrentUser(context).id;
	}

	public static User getCurrentUser(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("USER",
				Activity.MODE_PRIVATE);
		int id = prefs.getInt("id", 0);
		String username = prefs.getString("username", "");
		String email = prefs.getString("email", "");
		String persistence_token = prefs.getString("persistence_token", "");
		String single_access_token = prefs.getString("single_access_token", "");

		if (id == 0) {
			return null;
		} else {
			return new User(id, username, email, persistence_token,
					single_access_token);
		}

	}

	public static boolean isAuthenticated(Context context) {
		return (getCurrentUser(context) != null);
	}

	public static void disposeUser(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("USER",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

}
