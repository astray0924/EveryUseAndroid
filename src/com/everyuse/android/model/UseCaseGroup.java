package com.everyuse.android.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class UseCaseGroup implements Parcelable {
	public UseCaseType type;
	public String title;
	public String photo_url_thumb;
	public List<UseCase> member_list;
	
	public UseCaseGroup(String title) {
		this.title = title;
	}

	public UseCaseGroup(UseCaseType type, String title, String photo_url_thumb, List<UseCase> items) {
		this.type = type;
		this.title = title;
		this.photo_url_thumb = photo_url_thumb;
		this.member_list = items;
	}

	public List<UseCase> getMembers() {
		return member_list;
	}

	public int getItemCount() {
		return member_list.size();
	}

	public String toString() {
		return "Title: " + title + ", Item Count:" + member_list.size();
	}

	public int describeContents() {
		return 0;
	}

	public static UseCaseGroup parseSingleFromJSON(UseCaseType type, String title, JSONArray member_array)
			throws JSONException {
		if (member_array.length() == 0) {
			throw new IllegalArgumentException();
		}

		List<UseCase> group_members = new ArrayList<UseCase>();
		String photo_url = null;

		for (int i = 0; i < member_array.length(); i++) {
			JSONObject member_json = member_array.getJSONObject(i);
			UseCase use_case = UseCase.parseFromJSON(member_json);
			group_members.add(use_case);

			if (i == 0) {
				photo_url = use_case.photo_url_thumb;
			}
		}

		return new UseCaseGroup(type, title, photo_url, group_members);
	}

	public static List<UseCaseGroup> parseMultipleFromJSON(UseCaseType category, JSONObject entries) throws JSONException {
		List<UseCaseGroup> group_list = new ArrayList<UseCaseGroup>();

		@SuppressWarnings({ "unchecked" })
		Iterator<String> iter = entries.keys();
		while (iter.hasNext()) {
			String title = iter.next();
			JSONArray member_array = entries.getJSONArray(title);

			UseCaseGroup group = parseSingleFromJSON(category, title, member_array);
			group_list.add(group);
		}

		return group_list;
	}

	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(type.name());
		dest.writeString(title);
		dest.writeString(photo_url_thumb);

		UseCase[] item_array = new UseCase[member_list.size()];
		dest.writeParcelableArray(member_list.toArray(item_array), 0);
	}

	public static final Creator<UseCaseGroup> CREATOR = new Creator<UseCaseGroup>() {

		public UseCaseGroup createFromParcel(Parcel source) {
			UseCaseType category = Enum.valueOf(UseCaseType.class, source.readString());
			String title = source.readString();
			String photo_url = source.readString();
			List<Parcelable> encoded_items = Arrays.asList(source.readParcelableArray(UseCaseType.class
					.getClassLoader()));
			List<UseCase> items = new ArrayList<UseCase>();

			for (Parcelable e : encoded_items) {
				items.add((UseCase) e);
			}

			return new UseCaseGroup(category, title, photo_url, items);
		}

		public UseCaseGroup[] newArray(int size) {
			return new UseCaseGroup[size];
		}

	};
}
