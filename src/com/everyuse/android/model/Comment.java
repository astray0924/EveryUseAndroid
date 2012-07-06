package com.everyuse.android.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {
	public int favorites_count;
	public int funs_count;
	public int metoos_count;

	public Comment(int favorite_count, int fun_count, int metoo_count) {
		this.favorites_count = favorite_count;
		this.funs_count = fun_count;
		this.metoos_count = metoo_count;
	}
	
	public static Comment parseFromJSON(JSONObject json) throws JSONException {
		int favorite_count = json.getInt("favorites_count");
		int fun_count = json.getInt("funs_count");
		int metoo_count = json.getInt("metoos_count");
		
		return new Comment(favorite_count, fun_count, metoo_count);
	}

}
