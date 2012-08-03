package org.everyuse.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class User implements Parcelable {
	public int id;
	public String username;
	public String email;
	public String persistence_token;
	public String single_access_token;
	public String user_group;

	private static final Gson gson = new Gson();

	public User() {

	}

	public User(int id, String username, String email,
			String persistence_token, String single_access_token,
			String user_group) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.persistence_token = persistence_token;
		this.single_access_token = single_access_token;
		this.user_group = user_group;
	}

	public User(User user) {
		this(user.id, user.username, user.email, user.persistence_token,
				user.single_access_token, user.user_group);
	}

	public User(Parcel source) {
		this(gson.fromJson(source.readString(), User.class));
	}

	public static User parseFromJSON(JSONObject json) throws JSONException {
		return gson.fromJson(json.toString(), User.class);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(gson.toJson(this));
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		public User[] newArray(int size) {
			return new User[size];
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			return ((User) other).id == this.id;
		} else {
			return false;
		}
	}

}
