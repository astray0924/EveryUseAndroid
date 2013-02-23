package org.everyuse.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.everyuse.android.R;
import org.everyuse.android.model.User;
import org.everyuse.android.util.ErrorHelper;
import org.everyuse.android.util.NetworkHelper;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class IndexActivity extends SherlockFragmentActivity {
	// Strings for logging
	private final String TAG = this.getClass().getSimpleName();

	private EditText et_email;
	private EditText et_password;
	private String str_email;
	private String str_password;

	private HttpClient client;

	@Override
	protected void onPause() {
		super.onPause();

		Log.i(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.i(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy");
	}

	@Override
	protected void onResume() {
		super.onResume();

		NetworkHelper.checkAndEnableNetwork(this);

		Log.i(TAG, "onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.i(TAG, "onRestart");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		Log.i(TAG, "onCreate");

		initUI();

		client = new DefaultHttpClient();

		// TODO: 아이디와 패스워드를 저장하고, 시작 할때마다 로그인하는 방식으로 하자
		if (isAuthenticated()) {
			Intent intent = new Intent(IndexActivity.this, MainActivity.class);
			startActivity(intent);

			finish();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.msg_register_prompt)
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = new Intent(
											IndexActivity.this,
											RegisterActivity.class);
									startActivity(intent);
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
		}
	}

	private boolean isAuthenticated() {
		return UserHelper.isAuthenticated(getApplicationContext());
	}

	private void initUI() {
		// initialize text fields
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);

		// initialize buttons
		Button btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get Username and Password String
				str_email = et_email.getText().toString();
				str_password = et_password.getText().toString();

				if (str_email.equals("") || str_password.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.msg_complete_form, Toast.LENGTH_SHORT)
							.show();

					return;
				}

				new LoginTask().execute();
			}

		});

		Button btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IndexActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}

		});
	}

	private class LoginTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog indicator;
		private String error;

		@Override
		protected void onPreExecute() {
			// Initialize progress dialog
			indicator = new ProgressDialog(IndexActivity.this,
					ProgressDialog.STYLE_SPINNER);
			indicator.setMessage(getString(R.string.msg_wait));
			indicator.show();
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			if (str_email.equals("") || str_password.equals("")) {
				error = getString(R.string.msg_complete_form);
				return false;
			}

			// Make Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_session[username]",
					str_email));
			params.add(new BasicNameValuePair("user_session[password]",
					str_password));

			String res_string = "";
			try {
				HttpPost post = new HttpPost(URLHelper.USER_SESSIONS_URL
						+ ".json");
				HttpEntity req_entity = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(req_entity);

				HttpResponse response = client.execute(post);
				HttpEntity res_entity = response.getEntity();

				if (res_entity != null) {
					int code = response.getStatusLine().getStatusCode();
					res_string = EntityUtils.toString(res_entity);

					if (code >= 300) { // error
						error = ErrorHelper.getMostProminentError(res_string);

						return false;
					} else {
						JSONObject json = new JSONObject(res_string)
								.getJSONObject("record");
						User user = User.parseFromJSON(json);

						// store into shared preferences
						UserHelper.storeUser(getApplicationContext(), user);

						return true;
					}

				}
			} catch (IOException e) {
				error = getString(R.string.msg_no_response);
			} catch (JSONException e) {
				Log.d(TAG, e.getMessage());

				error = getString(R.string.msg_fail_interpret_response);
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			indicator.dismiss();

			if (!result) { // login failed
				Toast.makeText(getApplicationContext(), error,
						Toast.LENGTH_SHORT).show();
			} else { // login success
				Toast.makeText(getApplicationContext(),
						getString(R.string.msg_login_success),
						Toast.LENGTH_SHORT).show();

				// move to the main
				Intent intent = new Intent(IndexActivity.this,
						MainActivity.class);
				startActivity(intent);

				// finish the activity
				finish();
			}
		}
	}
}
