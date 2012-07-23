package org.everyuse.android.widget;

import org.everyuse.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

public class DynamicExpandableListView extends ExpandableListView implements
		OnScrollListener {
	private boolean mLoadEnded = false;
	private LayoutInflater inflater;
	private View footer;
	private OnListLoadListener mHandler;

	public DynamicExpandableListView(Context context) {
		super(context);
		initialize();
	}

	public DynamicExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public DynamicExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		// inflate footer
		footer = (LinearLayout) inflater.inflate(R.layout.list_item_footer,
				null);
		footer.setClickable(false);

		// set self as onScrollListener
		setOnScrollListener(this);
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {

		if (getFooterViewsCount() == 0) {
			addFooterView(footer, null, false);
		}

		super.setAdapter(adapter);
	}

	public void setOnListLoadListener(OnListLoadListener handler) {
		this.mHandler = handler;
	}

	public void setLoadEndFlag(boolean isEnded) {
		mLoadEnded = isEnded;

		if (mLoadEnded) {
			post(new Runnable() {

				public void run() {
					removeFooterView(footer);
					setOnScrollListener(null);
				}

			});

		} else {
			post(new Runnable() {

				public void run() {
					if (getFooterViewsCount() == 0) {
						addFooterView(footer, null, false);
					}

					setOnScrollListener(DynamicExpandableListView.this);
				}

			});

		}
	}

	public boolean loadEnded() {
		return mLoadEnded;
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastItem = firstVisibleItem + visibleItemCount;

		if (lastItem == totalItemCount && !mLoadEnded) {
			if (mHandler != null) {
				post(new Runnable() {
					public void run() {
						mHandler.onLoad();
					}
				});
			}
		}
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	public interface OnListLoadListener {
		public void onLoad();
	}

}
