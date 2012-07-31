package org.everyuse.android.util;

import org.everyuse.android.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;


public class UserHelper {
	public static boolean DEVELOPER_MODE = true;

	public static void storeUser(Context context, User user) {
		if (user == null) {
			throw new IllegalArgumentException();
		}

		// store into shared preferences
		SharedPreferences prefs = context.getSharedPreferences("USER",
				Activity.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("id", user.id);
		editor.putString("username", user.username);
		editor.putString("email", user.email);
		editor.putString("persistence_token", user.persistence_token);
		editor.putString("single_access_token", user.single_access_token);
		editor.putString("user_group", user.user_group);
		editor.commit();
	}

	public static User getCurrentUser(Context context) {
		SharedPreferences prefs = context
				.getSharedPreferences("USER", Activity.MODE_WORLD_READABLE);
		int id = prefs.getInt("id", 0);
		String username = prefs.getString("username", "");
		String email = prefs.getString("email", "");
		String persistence_token = prefs.getString("persistence_token", "");
		String single_access_token = prefs.getString("single_access_token", "");
		String user_group = prefs.getString("user_group", "");

		if (id == 0) {
			return null;
		} else {
			return new User(id, username, email, persistence_token, single_access_token, user_group);
		}

	}
	
	public static boolean isAuthenticated(Context context) {
		return (getCurrentUser(context) != null);
	}

	public static void disposeUser(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("USER",
				Activity.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

	public static Bitmap rotateBitmap(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}
}
