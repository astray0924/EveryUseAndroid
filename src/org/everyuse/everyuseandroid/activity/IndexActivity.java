package org.everyuse.everyuseandroid.activity;

import org.everyuse.everyuseandroid.R;
import org.everyuse.everyuseandroid.fragment.IndexFragment;
import org.everyuse.everyuseandroid.fragment.RegisterFragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class IndexActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		IndexFragment indexFragment = new IndexFragment();
		RegisterFragment registerFragment = new RegisterFragment();
		
		// add fragments into ViewGroup
		transaction.add(R.id.layout_activity_index, indexFragment);
		transaction.add(R.id.layout_activity_index, registerFragment);
		
		transaction.commit();
		
		Intent intent = new Intent(IndexActivity.this, MainActivity.class);
		startActivity(intent);
		
//		if (!isLoggedIn()) {
//			
//		} else {
//					
//		}
	}
	
	private boolean isLoggedIn() {
		return true;
	}
}
