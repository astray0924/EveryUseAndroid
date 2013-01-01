package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UseCaseAdapter extends BaseAdapter {
	private final String TAG = getClass().getSimpleName();

	private LayoutInflater inflater;
	private List<UseCase> data_list;
	private ImageDownloader image_downloader;

	public UseCaseAdapter() {

	}

	public UseCaseAdapter(Context context, List<UseCase> data_list) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data_list = data_list;
		this.image_downloader = new ImageDownloader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_usecase_single,
					null);

			UseCaseViewHolder holder = attachViewToViewHolder(convertView);
			convertView.setTag(holder);
		}

		UseCase use_case = (UseCase) getItem(position);

		fillDataToViewHolder(convertView, use_case, image_downloader);

		return convertView;
	}

	public static void fillDataToViewHolder(View convertView, UseCase use_case,
			ImageDownloader image_downloader) {
		UseCaseViewHolder holder = (UseCaseViewHolder) convertView.getTag();
		image_downloader.download(use_case.getPhotoThumbURL(), holder.photo);
		holder.item.setText(use_case.item);
		holder.purpose.setText(use_case.getPurposeString());
		holder.meta_info.setText(use_case.getMetaInfoString());
		holder.wow_count.setText(String.valueOf(use_case.wows_count));
		holder.metoo_count.setText(String.valueOf(use_case.metoos_count));
	}

	public static UseCaseViewHolder attachViewToViewHolder(View convertView) {
		UseCaseViewHolder holder = new UseCaseViewHolder();
		holder.photo = (ImageView) convertView.findViewById(R.id.iv_photo);
		holder.item = (TextView) convertView.findViewById(R.id.tv_item);
		holder.purpose = (TextView) convertView.findViewById(R.id.tv_purpose);
		holder.meta_info = (TextView) convertView
				.findViewById(R.id.tv_meta_info);
		holder.wow_count = (TextView) convertView
				.findViewById(R.id.tv_wow_count);
		holder.metoo_count = (TextView) convertView
				.findViewById(R.id.tv_metoo_count);
		return holder;
	}

	public List<UseCase> getDataList() {
		return data_list;
	}

	@Override
	public int getCount() {
		return data_list.size();
	}

	@Override
	public Object getItem(int position) {
		return data_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		Log.i(TAG, "Empty: " + (getCount() == 0));

		return getCount() == 0;
	}

	public static class UseCaseViewHolder {
		public ImageView photo;
		public TextView item;
		public TextView purpose;
		public TextView meta_info;
		public TextView wow_count;
		public TextView metoo_count;
	}

}
