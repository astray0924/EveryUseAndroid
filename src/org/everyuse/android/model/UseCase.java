package org.everyuse.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.everyuse.android.util.OtherHelper;
import org.everyuse.android.util.URLHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class UseCase implements Parcelable {
	public long id;
	public String username;
	public String item;
	public String purpose;
	public String purpose_type;
	public String photo_file_name;
	public Date created_at;
	public Date updated_at;
	public int favorites_count;
	public int wows_count;
	public int metoos_count;
	
	public static class CurrentUserComment {
		public Comment favorite;
		public Comment wow;
		public Comment metoo;
	}
	
	public static class Comment {
		public int user_id;
		public int use_case_id;
	}

	private static Gson gson = new Gson();

	public UseCase() {

	}
	
	public UseCase(UseCase use_case) {
		this.id = use_case.id;
		this.username = use_case.username;
		this.item = use_case.item;
		this.purpose = use_case.purpose;
		this.purpose_type = use_case.purpose_type;
		this.photo_file_name = use_case.photo_file_name;
		this.created_at = use_case.created_at;
		this.updated_at = use_case.updated_at;
		this.favorites_count = use_case.favorites_count;
		this.wows_count = use_case.wows_count;
		this.metoos_count = use_case.metoos_count;
	}

	public UseCase(Parcel source) {
		this(gson.fromJson(source.readString(), UseCase.class));
	}

	public String getPhotoBaseURL() {
		return URLHelper.PHOTOS_URL + "/" + id;
	}

	public String getPhotoThumbURL() {
		return getPhotoBaseURL() + "/thumb/" + photo_file_name;
	}

	public String getPhotoLargeURL() {
		return getPhotoBaseURL() + "/large/" + photo_file_name;
	}

	public String getPurposeText() {
		return purpose_type.equals("") ? purpose : purpose_type + ":" + purpose;
	}

	public String toString() {
		return "Item: " + item + ", Purpose: " + purpose;
	}

	public int compareTo(UseCase c) {
		return (int) (id - c.id);
	}

	public static UseCase parseSingleFromJSON(JSONObject json)
			throws JSONException {
		return gson.fromJson(json.toString(), UseCase.class);
	}

	public static List<UseCase> parseFromJSON(JSONArray json)
			throws JSONException {
		List<UseCase> use_case_list = new ArrayList<UseCase>();

		for (int i = 0; i < json.length(); i++) {
			JSONObject item = json.getJSONObject(i);
			UseCase use_case = parseSingleFromJSON(item);

			use_case_list.add(use_case);
		}

		return use_case_list;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(gson.toJson(this));
	}

	public static final Parcelable.Creator<UseCase> CREATOR = new Parcelable.Creator<UseCase>() {

		public UseCase createFromParcel(Parcel source) {
			return new UseCase(source);
		}

		public UseCase[] newArray(int size) {
			return new UseCase[size];
		}

	};
}
