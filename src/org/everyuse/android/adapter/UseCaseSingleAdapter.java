package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UseCaseSingleAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<UseCase> data_list;
	private ImageDownloader image_downloader;

	public UseCaseSingleAdapter() {

	}

	public UseCaseSingleAdapter(Context context, List<UseCase> data_list) {
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

			UseCaseSingleViewHolder holder = new UseCaseSingleViewHolder();
			holder.photo = (ImageView) convertView.findViewById(R.id.iv_photo);
			holder.item = (TextView) convertView.findViewById(R.id.tv_item);
			holder.purpose = (TextView) convertView
					.findViewById(R.id.tv_purpose);
			holder.purpose_type = (TextView) convertView
					.findViewById(R.id.tv_purpose_type);
			holder.other_info = (TextView) convertView
					.findViewById(R.id.tv_other_info);

			convertView.setTag(holder);
		}

		UseCase use_case = (UseCase) getItem(position);

		UseCaseSingleViewHolder holder = (UseCaseSingleViewHolder) convertView
				.getTag();
		image_downloader.download(use_case.getPhotoThumbURL(), holder.photo);
		holder.item.setText(use_case.item);
		holder.purpose.setText(use_case.purpose);
		holder.purpose_type.setText(use_case.purpose_type);
		holder.other_info.setText(use_case.getOtherInfoString());

		return convertView;
	}

	public List<UseCase> getDataList() {
		return data_list;
	}

	public int getCount() {
		return data_list.size();
	}

	public Object getItem(int position) {
		return data_list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public static class UseCaseSingleViewHolder {
		public ImageView photo;
		public TextView item;
		public TextView purpose;
		public TextView purpose_type;
		public TextView other_info;
	}

}
