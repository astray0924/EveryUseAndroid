package org.everyuse.android.activity;

import org.everyuse.android.R;
import org.everyuse.android.model.UseCase;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DetailActivity extends SherlockFragmentActivity {
	public static String EXTRA_DATA_LIST = "DATA_LIST";
	public static String EXTRA_STRAT_INDEX = "START_INDEX";
	
	private UseCase[] data_list;
	private int index;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		handleIntent(getIntent());
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getString(R.string.title_activity_detail));
	}

	private void handleIntent(Intent intent) {
		index = intent.getIntExtra(EXTRA_STRAT_INDEX, 0);
		data_list = (UseCase[]) intent.getParcelableArrayExtra(EXTRA_DATA_LIST);
	}
}
