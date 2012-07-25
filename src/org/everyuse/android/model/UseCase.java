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

public class UseCase implements Parcelable {
	public long id;
	public String username;
	public String item;
	public String purpose;
	public String purpose_type;
	public String photo_file_name;
	public String photo_url_base;
	public String photo_url_thumb;
	public String photo_url_large;
	public Date created_at;
	public Date updated_at;

	// comments
	public int favorites_count;
	public int funs_count;
	public int metoos_count;

	public UseCase() {

	}

	public UseCase(long id, String item, String purpose, String purpose_type,
			String photo_file_name, String username, Date created_at,
			Date updated_at, int favorites_count, int funs_count,
			int metoos_count) {
		this.id = id;
		this.username = username;
		this.item = item;
		this.purpose = purpose;
		this.purpose_type = purpose_type;
		this.photo_file_name = photo_file_name;
		this.created_at = created_at;
		this.updated_at = updated_at;

		// comments
		this.favorites_count = favorites_count;
		this.funs_count = funs_count;
		this.metoos_count = metoos_count;

		populatePhotoUrls(photo_file_name);
	}

	private void populatePhotoUrls(String photo_file_name) {
		this.photo_url_base = URLHelper.PHOTOS_URL + "/" + id;
		this.photo_url_thumb = photo_url_base + "/thumb/" + photo_file_name;
		this.photo_url_large = photo_url_base + "/large/" + photo_file_name;
	}

	public UseCase(Parcel source) {
		this(source.readLong(), source.readString(), source.readString(),
				source.readString(), source.readString(), source.readString(),
				OtherHelper.parseDate(source.readString()), OtherHelper
						.parseDate(source.readString()), source.readInt(),
				source.readInt(), source.readInt());
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
		long id = json.getLong("id");
		String item = json.getString("item");
		String purpose = json.getString("purpose");
		String purpose_type = json.getString("purpose_type");
		String photo_file_name = json.getString("converted_file_name");
		String username = json.getString("username");
		Date created_at = OtherHelper.parseDate(json.getString("created_at"));
		Date updated_at = OtherHelper.parseDate(json.getString("updated_at"));
		int favorites_count = json.getInt("favorites_count");
		int funs_count = json.getInt("funs_count");
		int metoos_count = json.getInt("metoos_count");

		return new UseCase(id, item, purpose, purpose_type, photo_file_name,
				username, created_at, updated_at, favorites_count, funs_count,
				metoos_count);
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
		dest.writeLong(id);
		dest.writeString(item);
		dest.writeString(purpose);
		dest.writeString(purpose_type);
		dest.writeString(photo_file_name);
		dest.writeString(username);
		dest.writeString(OtherHelper.encodeDate(created_at));
		dest.writeString(OtherHelper.encodeDate(updated_at));
		dest.writeInt(favorites_count);
		dest.writeInt(funs_count);
		dest.writeInt(metoos_count);
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
