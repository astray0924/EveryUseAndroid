package org.everyuse.android.fragment;
import org.everyuse.android.util.URLHelper;

import android.os.Bundle;

public class RecentListFragment extends UseCaseSingleListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setDataURL(URLHelper.USE_CASES_RECENT_URL);
	}

}
