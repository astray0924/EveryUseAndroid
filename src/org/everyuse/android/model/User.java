package org.everyuse.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	/**
	 * DB index of the user
	 */
	public int id;

	/**
	 * Username
	 */
	public String username;

	/**
	 * email
	 */
	public String email;

	/**
	 * token used for the session
	 */
	public String persistence_token;

	public String single_access_token;

	public User() {

	}

	public User(int id, String username, String email, String p_token,
			String s_token) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.persistence_token = p_token;
		this.single_access_token = s_token;
	}

	public User(Parcel source) {
		this(source.readInt(), source.readString(), source.readString(), source
				.readString(), source.readString());
	}

	public static User parseFromJSON(JSONObject json) throws JSONException {
		int id = json.getInt("id");
		String username = json.getString("username");
		String email = json.getString("email");
		String p_token = json.getString("persistence_token");
		String s_token = json.getString("single_access_token");

		return new User(id, username, email, p_token, s_token);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(username);
		dest.writeString(email);
		dest.writeString(persistence_token);
		dest.writeString(single_access_token);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		public User[] newArray(int size) {
			return new User[size];
		}

	};
}
