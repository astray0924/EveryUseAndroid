/**
 * 
 */
package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.adapter.UseCaseSingleAdapter.UseCaseSingleViewHolder;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.model.UseCaseGroup;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author KyoungRok
 * 
 */
public class UseCaseGroupAdapter extends BaseExpandableListAdapter {
	private LayoutInflater inflater;
	private ImageDownloader image_downloader;
	private List<UseCaseGroup> group_list;

	public UseCaseGroupAdapter() {

	}

	public UseCaseGroupAdapter(Context context, List<UseCaseGroup> group_list) {
		this.image_downloader = new ImageDownloader();
		this.group_list = group_list;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return group_list.get(groupPosition).children.get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_usecase_single,
					null);

			UseCaseSingleViewHolder holder = new UseCaseSingleViewHolder();
			holder.photo = (ImageView) convertView.findViewById(R.id.iv_photo);
			holder.item = (TextView) convertView.findViewById(R.id.tv_item);
			holder.purpose = (TextView) convertView
					.findViewById(R.id.tv_purpose);
			holder.other_info = (TextView) convertView
					.findViewById(R.id.tv_other_info);

			convertView.setTag(holder);
		}

		//
		UseCase use_case = (UseCase) getChild(groupPosition, childPosition);

		//
		UseCaseSingleViewHolder holder = (UseCaseSingleViewHolder) convertView
				.getTag();
		image_downloader.download(use_case.getPhotoThumbURL(), holder.photo);
		holder.item.setText(use_case.item);
		holder.purpose.setText(use_case.purpose);
		holder.other_info.setText(use_case.getOtherInfoString());

		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return group_list.get(groupPosition).children.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return group_list.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return group_list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View group_view = inflater.inflate(R.layout.list_item_usecase_group,
				null);
		TextView tv_title = (TextView) group_view
				.findViewById(R.id.tv_group_title);
		tv_title.setText(group_list.get(groupPosition).getGroupTitle());

		return group_view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private class UseCaseGroupViewHolder {

	}

}
