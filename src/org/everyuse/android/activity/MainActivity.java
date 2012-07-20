package org.everyuse.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.fragment.UserProfileFragment;
import org.everyuse.android.fragment.UseCaseListFragment;
import org.everyuse.android.fragment.UseCaseListWithOptionFragment;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab.
		// We can also use ActionBar.Tab#select() to do this if we have a
		// reference to the
		// Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_new:
			Intent intent = new Intent(MainActivity.this, CreateActivity.class);
			startActivity(intent);
			break;

		case R.id.menu_search:
			break;
		case R.id.menu_settings:
			break;
		case R.id.menu_logout:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msg_logout_prompt)
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// 사용자 정보 삭제 후 앱 종료
									UserHelper
											.disposeUser(getApplicationContext());
									Toast.makeText(getApplicationContext(),
											R.string.msg_logout_success,
											Toast.LENGTH_SHORT).show();

									// 앱 종료
									finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.setCancelable(true);
			alert.show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final int TOP = 0;
		private final int FEED = 1;
		private final int RECENT = 2;
		private final int CATEOGORY = 3;
		private final int MY = 4;

		private List<Fragment> fragment_list = new ArrayList<Fragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

			initFragments(fm);
		}

		private void initFragments(FragmentManager fm) {
			fragment_list.add(TOP, new UseCaseListWithOptionFragment(
					URLHelper.USE_CASES_TOP_URL, R.array.comment));
			fragment_list.add(FEED, new DummySectionFragment());
			fragment_list.add(RECENT, new UseCaseListFragment(
					URLHelper.USE_CASES_RECENT_URL));
			fragment_list.add(CATEOGORY, new DummySectionFragment());
			fragment_list.add(MY, new UserProfileFragment());

			FragmentTransaction ft = fm.beginTransaction();
		}

		@Override
		public Fragment getItem(int i) {
			return fragment_list.get(i);
		}

		@Override
		public int getCount() {
			return fragment_list.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case TOP:
				return getString(R.string.title_top).toUpperCase();
			case FEED:
				return getString(R.string.title_feed).toUpperCase();
			case RECENT:
				return getString(R.string.title_recent).toUpperCase();
			case CATEOGORY:
				return getString(R.string.title_category).toUpperCase();
			case MY:
				return getString(R.string.title_my).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		public DummySectionFragment() {
		}

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			// Bundle args = getArguments();
			// textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
			textView.setText("Dummy");
			return textView;
		}
	}
}
