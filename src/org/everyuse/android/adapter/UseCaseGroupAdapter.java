/**
 * 
 */
package org.everyuse.android.adapter;

import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.adapter.UseCaseAdapter.UseCaseViewHolder;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.model.UseCaseGroup;
import org.everyuse.android.util.ImageDownloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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

			UseCaseViewHolder holder = UseCaseAdapter
					.attachViewToViewHolder(convertView);
			convertView.setTag(holder);
		}

		UseCase use_case = (UseCase) getChild(groupPosition, childPosition);

		UseCaseAdapter.fillDataToViewHolder(convertView, use_case,
				image_downloader);

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

	@Override
	public boolean isEmpty() {
		return (this.getGroupCount() == 0);
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
