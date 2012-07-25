package org.everyuse.android.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class UseCaseGroup implements Parcelable, Comparable<UseCaseGroup> {
	public String photo_url_thumb;
	public String title;
	public ArrayList<UseCase> children;

	public UseCaseGroup(String title) {
		this.title = title;
		children = new ArrayList<UseCase>();
	}

	public UseCaseGroup(String title, String photo_url_thumb,
			ArrayList<UseCase> items) {
		this.title = title;
		this.photo_url_thumb = photo_url_thumb;
		this.children = items;
	}

	public ArrayList<UseCase> getChildren() {
		return children;
	}

	public int getItemCount() {
		return children.size();
	}

	public String toString() {
		return "Title: " + title + ", Item Count:" + children.size();
	}

	public int describeContents() {
		return 0;
	}

	public static UseCaseGroup parseSingleFromJSON(JSONObject json_group)
			throws JSONException {
		if (json_group == null) {
			throw new IllegalArgumentException();
		}
		
		String photo_url = ""; 
		String title = json_group.getString("title");
		ArrayList<UseCase> children = new ArrayList<UseCase>();
		
		JSONArray json_children = json_group.getJSONArray("children");
		for (int i = 0; i < json_children.length(); i++) {
			JSONObject child = json_children.getJSONObject(i);
			UseCase use_case = UseCase.parseSingleFromJSON(child);
			children.add(use_case);
			
			if (i == 0) {
				photo_url = use_case.getPhotoThumbURL();
			}
		}


		return new UseCaseGroup(title, photo_url, children);
	}

	public static List<UseCaseGroup> parseMultipleFromJSON(JSONArray json_array)
			throws JSONException {
		List<UseCaseGroup> group_list = new ArrayList<UseCaseGroup>();

		for (int i = 0; i < json_array.length(); i++) {
			JSONObject json_group = json_array.getJSONObject(i);
			UseCaseGroup group = parseSingleFromJSON(json_group);
			group_list.add(group);
		}

		return group_list;
	}

	public void writeToParcel(Parcel dest, int flag) {
		dest.writeString(title);
		dest.writeString(photo_url_thumb);

		UseCase[] item_array = new UseCase[children.size()];
		dest.writeParcelableArray(children.toArray(item_array), 0);
	}

	public static final Creator<UseCaseGroup> CREATOR = new Creator<UseCaseGroup>() {

		public UseCaseGroup createFromParcel(Parcel source) {
			String title = source.readString();
			String photo_url = source.readString();
			List<Parcelable> encoded_items = Arrays.asList(source
					.readParcelableArray(UseCaseListOption.class
							.getClassLoader()));
			ArrayList<UseCase> items = new ArrayList<UseCase>();

			for (Parcelable e : encoded_items) {
				items.add((UseCase) e);
			}

			return new UseCaseGroup(title, photo_url, items);
		}

		public UseCaseGroup[] newArray(int size) {
			return new UseCaseGroup[size];
		}

	};

	@Override
	public int compareTo(UseCaseGroup another) {
		return title.compareTo(another.title);
	}
}
