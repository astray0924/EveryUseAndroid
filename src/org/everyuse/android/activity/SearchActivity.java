package org.everyuse.android.activity;

import org.everyuse.android.R;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;

public class SearchActivity extends SherlockListActivity {
	private AsyncTask<Void, Void, Void> SearchTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Get the intent, verify the action and get the query
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String q = intent.getStringExtra(SearchManager.QUERY);
			search(q);
		}
	}

	private void search(String q) {
		
	}
}
