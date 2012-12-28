package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.adapter.UseCaseAdapter.UseCaseViewHolder;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
	private final String TAG = getClass().getSimpleName();

	private LayoutInflater inflater;
	private ImageDownloader image_downloader;
	private List<SearchItem> mDataList;

	public SearchAdapter(Context context, List<SearchItem> mDataList) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.image_downloader = new ImageDownloader();
		this.mDataList = mDataList;
	}

	@Override
	public int getCount() {
		if (mDataList == null) {
			return 0;
		} else {
			return mDataList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SearchItem item = (SearchItem) getItem(position);
		if (item != null) {
			if (item.is_section) {
				convertView = inflater
						.inflate(R.layout.list_section_item, null);
				final TextView tv = (TextView) convertView
						.findViewById(R.id.section_separator);
				tv.setText(item.title);
			} else {
				convertView = inflater.inflate(
						R.layout.list_item_usecase_single, null);
				UseCaseViewHolder holder = UseCaseAdapter
						.attachViewToViewHolder(convertView);
				convertView.setTag(holder);
				UseCaseAdapter.fillDataToViewHolder(convertView, item.use_case,
						image_downloader);
			}
		}

		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		for (SearchItem i : mDataList) {
			Log.i(TAG, String.valueOf(i));
		}
	}

	@Override
	public boolean isEmpty() {
		return (getCount() == 0);
	}

	@Override
	public boolean isEnabled(int position) {
		SearchItem item = mDataList.get(position);

		if (item.is_section) {
			return false;
		} else {
			return true;
		}
	}

	public static class SearchItem {
		public String title;
		public UseCase use_case;
		public String search_category;
		public boolean is_section;

		public SearchItem(String title, UseCase use_case, String search_category, boolean is_section) {
			this.title = title;
			this.use_case = use_case;
			this.search_category = search_category;
			this.is_section = is_section;
		}

		public static SearchItem createSectionItem(String title) {
			return new SearchItem(title, null, null, true);
		}

		public static SearchItem createItem(UseCase use_case, String search_category) {
			return new SearchItem(use_case.toString(), use_case, search_category, false);
		}

		@Override
		public String toString() {
			return title;
		}
	}

}
