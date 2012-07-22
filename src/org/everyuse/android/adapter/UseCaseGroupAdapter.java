/**
 * 
 */
package org.everyuse.android.adapter;

import java.util.Collections;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.model.UseCaseGroup;
import org.everyuse.android.util.ImageDownloader;
import org.everyuse.android.widget.UseCaseSingleViewHolder;

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
	private String[] titles;
	private UseCase[][] children;
	private LayoutInflater inflater;
	private ImageDownloader image_downloader;

	public UseCaseGroupAdapter() {
		this.image_downloader = new ImageDownloader();
	}

	public UseCaseGroupAdapter(Context context,
			List<UseCaseGroup> group_list) {
		this();

		// 일단 title로 정렬 (알파벳 순서)
		Collections.sort(group_list);

		// 리스트를 groups, children으로 평평하게 만듬
		int group_count = group_list.size();

		titles = new String[group_count];
		children = new UseCase[group_count][];
		for (int i = 0; i < group_count; i++) {
			String group_title = group_list.get(i).title;
			List<UseCase> group_children = group_list.get(i).getChildren();

			titles[i] = group_title;
			children[i] = (UseCase[]) group_children.toArray();
		}

		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public UseCaseGroupAdapter(Context context, String[] groups,
			UseCase[][] children) {
		this();
		this.titles = groups;
		this.children = children;
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
		return children[groupPosition][childPosition];
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
			holder.photo = (ImageView) convertView.findViewById(R.id.photo);
			holder.text = (TextView) convertView.findViewById(R.id.text);

			convertView.setTag(holder);
		}

		//
		UseCase use_case = (UseCase) getChild(groupPosition, childPosition);

		//
		UseCaseSingleViewHolder holder = (UseCaseSingleViewHolder) convertView
				.getTag();
		image_downloader.download(use_case.photo_url_thumb, holder.photo);
		holder.text.setText(use_case.item);

		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return children[groupPosition].length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return titles[groupPosition];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return titles.length;
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
		tv_title.setText(titles[groupPosition]);

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
