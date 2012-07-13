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
	private int item_layout;
	private ImageDownloader image_downloader;

	public UseCaseSingleAdapter() {

	}

	public UseCaseSingleAdapter(Context context, int item_layout,
			List<UseCase> data_list) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data_list = data_list;
		this.item_layout = item_layout;
		this.image_downloader = new ImageDownloader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(item_layout, null);

			ViewHolder holder = new ViewHolder();
			holder.photo = (ImageView) convertView.findViewById(R.id.photo);
			holder.text = (TextView) convertView.findViewById(R.id.text);

			convertView.setTag(holder);
		}

		//
		UseCase use_case = (UseCase) getItem(position);

		//
		ViewHolder holder = (ViewHolder) convertView.getTag();
		image_downloader.download(use_case.photo_url_thumb, holder.photo);
		holder.text.setText(use_case.item);

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

	private class ViewHolder {
		public ImageView photo;
		public TextView text;
	}
}
