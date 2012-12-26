package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.model.User;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<User> data_list;

	public UserAdapter() {

	}

	public UserAdapter(Context context, List<User> data_list) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data_list = data_list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_user,
					null);

			UserViewHolder holder = new UserViewHolder();
			holder.photo = (ImageView) convertView.findViewById(R.id.iv_photo);
			holder.username = (TextView) convertView.findViewById(R.id.tv_username);

			convertView.setTag(holder);
		}

		//
		User user = (User) getItem(position);

		//
		UserViewHolder holder = (UserViewHolder) convertView.getTag();
//		image_downloader.download(user.photo_url_thumb, holder.photo);
		holder.username.setText(user.username);

		return convertView;
	}

	public List<User> getDataList() {
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

	private class UserViewHolder {
		public ImageView photo;
		public TextView username;
	}

}
