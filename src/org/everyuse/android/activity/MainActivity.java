package org.everyuse.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.everyuse.android.R;
import org.everyuse.android.fragment.UseCaseGroupListFragment;
import org.everyuse.android.fragment.UseCaseListFragment;
import org.everyuse.android.fragment.UseCaseListWithOptionFragment;
import org.everyuse.android.fragment.UserMenuFragment;
import org.everyuse.android.util.NetworkStateHelper;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.SearchView;
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
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	private static String SELECTED_TAB_KEY = "SELECTED_TAB_KEY";
	private int selected_tab = 0;

	public static final String EXTRA_REFRESH_LISTS = "refresh_lists";

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Boolean refresh_list = intent.getBooleanExtra(EXTRA_REFRESH_LISTS,
				false);
		if (refresh_list) {
			for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
				Fragment fragment = mSectionsPagerAdapter.getItem(i);

				if (fragment != null
						&& (fragment instanceof UseCaseListFragment || fragment instanceof UseCaseGroupListFragment)) {
					UseCaseListFragment use_case_frag = (UseCaseListFragment) fragment;
					use_case_frag.refresh();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		saveSelectedTabPosition(selected_tab);
	}

	@Override
	protected void onResume() {
		super.onResume();

		selected_tab = getSelectedTabPosition();
		getSupportActionBar().setSelectedNavigationItem(selected_tab);

		NetworkStateHelper.checkAndEnableNetwork(this);
	}

	private void saveSelectedTabPosition(int selected_tab) {
		SharedPreferences.Editor editor = getPreferences(0).edit();
		editor.putInt(SELECTED_TAB_KEY, selected_tab);
		editor.commit();
	}

	private int getSelectedTabPosition() {
		return getPreferences(0).getInt(SELECTED_TAB_KEY, 0);
	}

	@Override
	public boolean onSearchRequested() {
		return super.onSearchRequested();
	}

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
						selected_tab = position;
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
		getSupportMenuInflater().inflate(R.menu.main, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_new:
			Intent create_intent = new Intent(MainActivity.this,
					CreateActivity.class);
			startActivity(create_intent);
			break;
		/*
		 * case R.id.menu_search: break;
		 */
		case R.id.menu_settings:
			Intent pref_intent = new Intent(MainActivity.this,
					MainPreferenceActivity.class);
			startActivity(pref_intent);
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
		case R.id.menu_about:
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
		selected_tab = tab.getPosition();
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
		
		private final int TAB_COUNT = MY + 1;

		private ArrayList<Fragment> fragment_list = new ArrayList<Fragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);

			initFragments();
		}

		private void initFragments() {
			// add Top Fragment
			fragment_list.add(TOP, UseCaseListWithOptionFragment.newInstance(
					URLHelper.USE_CASES_TOP_URL, R.array.comment));

			// add Feed Fragment
			int user_id = UserHelper.getCurrentUserId(MainActivity.this);
			fragment_list.add(FEED, UseCaseListFragment.newInstance(URLHelper
					.getMyFeedsURL(user_id)));

			// add Recent Fragment
			fragment_list.add(RECENT,
					UseCaseListFragment.newInstance(URLHelper.USE_CASES_URL));

			// add Category Fragment
			fragment_list.add(CATEOGORY, UseCaseGroupListFragment.newInstance(
					URLHelper.USE_CASE_GROUPS_URL, R.array.use_case));

			// add My Menu Fragment
			fragment_list.add(MY, UserMenuFragment.newInstance(UserHelper
					.getCurrentUser(MainActivity.this)));
		}

		@Override
		public Fragment getItem(int i) {
			Fragment frag = fragment_list.get(i);

			if (frag == null) {
				throw new IllegalStateException(
						"Fragment not initialized properly.");
			}

			return frag;
		}

		@Override
		public int getCount() {
			return TAB_COUNT;
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

}
