package org.everyuse.android.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.everyuse.android.util.URLHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UseCase implements Parcelable {
	public long id;
	public int writer_id;
	public String writer_name;
	public String item;
	public String purpose;
	public String purpose_type;
	public String photo_file_name_large;
	public String photo_file_name_thumb;
	public String place;
	public String lang;
	public Date created_at;
	public Date updated_at;
	public int favorites_count;
	public int wows_count;
	public int metoos_count;

	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
	private static DateFormat year_month_day_format = DateFormat.getDateInstance(DateFormat.MEDIUM);

	private static DateFormat today_format = new SimpleDateFormat("hh:mm aaa");
	private static DateFormat other_day_format = new SimpleDateFormat("yyyy-MM-dd");

	public String getPurposeString() {
		if (purpose_type == null || purpose == null) {
			return "";
		} else {
			if (isKoreanLocale()) {
				return purpose + purpose_type;
			} else if (isEmptyLocale()) {
				return purpose + " " + purpose_type;
			} else {
				return purpose_type + " " + purpose;
			}

		}

	}

	private boolean isKoreanLocale() {
		return lang.toString().equals(Locale.KOREA) || lang.toString().equals(Locale.KOREAN);
	}

	private boolean isEmptyLocale() {
		return lang.toString().equals("");
	}

	public String getMetaInfoString() {
		return "by " + writer_name + ", " + getDateString(created_at);
	}

	private String getDateString(Date date) {
		Date today = new Date();
		String today_str = year_month_day_format.format(today);
		String compare_str = year_month_day_format.format(date);

		if (today_str.equals(compare_str)) {
			return "Today, " + today_format.format(date);
		} else {
			return other_day_format.format(date);
		}
	}

	public UseCase() {

	}

	public UseCase(UseCase use_case) {
		this.id = use_case.id;
		this.writer_id = use_case.writer_id;
		this.writer_name = use_case.writer_name;
		this.item = use_case.item;
		this.purpose = use_case.purpose;
		this.purpose_type = use_case.purpose_type;
		this.place = use_case.place;
		this.lang = use_case.lang;
		this.photo_file_name_large = use_case.photo_file_name_large;
		this.photo_file_name_thumb = use_case.photo_file_name_thumb;
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
		return getPhotoBaseURL() + "/" + photo_file_name_thumb;
	}

	public String getPhotoLargeURL() {
		return getPhotoBaseURL() + "/" + photo_file_name_large;
	}

	public String toString() {
		return item + " " + getPurposeString();

	}

	public int compareTo(UseCase c) {
		return (int) (id - c.id);
	}

	public static UseCase parseFromJSON(JSONObject json) throws JSONException {

		return gson.fromJson(json.toString(), UseCase.class);
	}

	public static ArrayList<UseCase> parseMultipleFromJSON(JSONArray json) throws JSONException {
		ArrayList<UseCase> use_case_list = new ArrayList<UseCase>();

		for (int i = 0; i < json.length(); i++) {
			JSONObject item = json.getJSONObject(i);
			UseCase use_case = parseFromJSON(item);

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
