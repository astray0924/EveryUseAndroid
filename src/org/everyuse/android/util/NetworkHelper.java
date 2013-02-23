package org.everyuse.android.util;

import org.everyuse.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class NetworkHelper {
	public static boolean IS_NETWORK_CONNECTED = false;

	public static boolean checkNetworkConnection(Context context) {
		final ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static void checkAndEnableNetwork(final Activity activity) {
		if (!checkNetworkConnection(activity)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setMessage(R.string.msg_turn_on_network)
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									activity.startActivity(new Intent(
											Settings.ACTION_WIFI_SETTINGS));

									dialog.dismiss();
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
		} else {
			IS_NETWORK_CONNECTED = true;
		}
	}

}
