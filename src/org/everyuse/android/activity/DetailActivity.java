package org.everyuse.android.activity;

import java.util.ArrayList;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;
import org.everyuse.android.util.ImageDownloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DetailActivity extends SherlockFragmentActivity {
	public static String EXTRA_DATA_LIST = "DATA_LIST";
	public static String EXTRA_STRAT_INDEX = "START_INDEX";

	private ArrayList<UseCase> data_list;
	private int start_index;

	private ItemsPagerAdapter pager_adapter;
	private ViewPager pager;
	private static ImageDownloader image_downloader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_detail);

		// handle intent
		handleIntent(getIntent());

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// Set up the view pager
		pager = (ViewPager) findViewById(R.id.pager);
		pager_adapter = new ItemsPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pager_adapter);

		pager.setCurrentItem(start_index);

		image_downloader = new ImageDownloader();
	}

	private void handleIntent(Intent intent) {
		data_list = intent.getParcelableArrayListExtra(EXTRA_DATA_LIST);
		start_index = intent.getIntExtra(EXTRA_STRAT_INDEX, 0);

		if (data_list == null) {
			throw new IllegalStateException(
					getString(R.string.msg_missing_data));
		}
	}

	private class ItemsPagerAdapter extends FragmentStatePagerAdapter {

		public ItemsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// Log.d("DetailActivity", position + "");

			return DetailFragment.newInstance(data_list.get(position));
		}

		@Override
		public int getCount() {
			return data_list.size();
		}

	}

	public static class DetailFragment extends Fragment {
		private static String DATA = "DATA";

		public DetailFragment() {

		}

		public static DetailFragment newInstance(UseCase data) {
			Log.d("DetailActivity", data + "");

			DetailFragment f = new DetailFragment();

			// supply single UseCase as an argument.
			Bundle args = new Bundle();
			args.putParcelable(DATA, data);
			f.setArguments(args);

			return f;
		}

		private void fillPage(View page, UseCase data) {
			ImageView photo = (ImageView) page.findViewById(R.id.photo);
			TextView text = (TextView) page.findViewById(R.id.text);

			image_downloader.download(data.photo_url_large, photo);
			text.setText(data.item + ":" + data.purpose);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			Log.d("DetailActivity", "onActivityCreated()");

			super.onActivityCreated(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater
		 * , android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.d("DetailActivity", "onCreateView()");

			View page = inflater
					.inflate(R.layout.fragment_usecase_detail, null);
			UseCase data = getArguments().getParcelable(DATA);
			fillPage(page, data);

			return page;
		}
	}
}