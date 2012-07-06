package com.everyuse.android.util;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.ImageView;

import com.everyuse.android.model.User;

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
		editor.putString("p_token", user.persistence_token);
		editor.putString("s_token", user.single_access_token);
		editor.commit();
	}

	public static User getCurrentUser(Context context) {
		SharedPreferences prefs = context
				.getSharedPreferences("USER", Activity.MODE_WORLD_READABLE);
		int id = prefs.getInt("id", 0);
		String username = prefs.getString("username", "");
		String email = prefs.getString("email", "");
		String p_token = prefs.getString("p_token", "");
		String s_token = prefs.getString("s_token", "");

		if (id == 0) {
			return null;
		} else {
			return new User(id, username, email, p_token, s_token);
		}

	}
	
	public static boolean isLoggedIn(Context context) {
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
