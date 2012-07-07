package org.everyuse.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
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

	public User(int id, String username, String email, String p_token, String s_token) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.persistence_token = p_token;
		this.single_access_token = s_token;
	}
	
	public static User parseFromJSON(JSONObject json) throws JSONException {
		int id = json.getInt("id");
		String username = json.getString("username");
		String email = json.getString("email");
		String p_token = json.getString("persistence_token");
		String s_token = json.getString("single_access_token");

		return new User(id, username, email, p_token, s_token);
	}
}
