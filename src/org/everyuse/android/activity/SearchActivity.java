package org.everyuse.android.activity;

import org.everyuse.everyuseandroid.R;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class SearchActivity extends SherlockActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
//			doMySearch(query);
		}
	}
}
