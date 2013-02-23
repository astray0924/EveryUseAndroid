package org.everyuse.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			if (NetworkHelper.checkNetworkConnection(context)) {
				NetworkHelper.IS_NETWORK_CONNECTED = true;
			} else {
				NetworkHelper.IS_NETWORK_CONNECTED = false;
			}
		}

	}

}
