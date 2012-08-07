package org.everyuse.android.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class RegisterActivity extends SherlockActivity {
	private EditText et_username;
	private EditText et_email;
	private EditText et_password;
	private EditText et_password_confirm;
	private Spinner sp_user_group;

	// 필드값들
	private String str_username;
	private String str_email;
	private String str_password;
	private String str_password_confirm;
	private String str_user_group;

	private User new_user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setTitle(R.string.title_activity_register);

		initUI();
	}

	private void initUI() {
		// Set private value
		et_username = (EditText) findViewById(R.id.et_username);
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);
		et_password_confirm = (EditText) findViewById(R.id.et_password_confirm);

		// 유저 그룹
		sp_user_group = (Spinner) findViewById(R.id.sp_user_group);
		ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter
				.createFromResource(this, R.array.user_group_list,
						android.R.layout.simple_spinner_item);
		spinner_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_user_group.setAdapter(spinner_adapter);

		// initalize buttons
		Button btn_register_done = (Button) findViewById(R.id.btn_register_done);
		btn_register_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get User name and Password String
				str_username = et_username.getText().toString();
				str_email = et_email.getText().toString();
				str_password = et_password.getText().toString();
				str_password_confirm = et_password_confirm.getText().toString();
				str_user_group = sp_user_group.getSelectedItem().toString()
						.toLowerCase();
				
				// TODO 제대로 구현 필요
				if (str_user_group.equals("학생")) {
					str_user_group = "student";
				} else if (str_user_group.equals("주부")) {
					str_user_group = "housewife";
				}

				if (str_username.equals("") || str_email.equals("")
						|| str_password.equals("")
						|| str_password_confirm.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.msg_complete_form, Toast.LENGTH_SHORT)
							.show();
					return;
				}

				new RegisterTask().execute();
			}

		});
	}

	private class RegisterTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog indicator;
		private String msg_error;

		@Override
		protected void onPreExecute() {
			// Initialize progress dialog
			indicator = new ProgressDialog(RegisterActivity.this);
			indicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			indicator.setMessage(getString(R.string.msg_wait));
			indicator.setCancelable(false);

			// show progress dialog
			indicator.show();
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			// instantiate the http client
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(URLHelper.USERS_URL + ".json");

			// Make Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user[username]", str_username));
			params.add(new BasicNameValuePair("user[email]", str_email));
			params.add(new BasicNameValuePair("user[password]", str_password));
			params.add(new BasicNameValuePair("user[password_confirmation]",
					str_password_confirm));
			params.add(new BasicNameValuePair("user[user_group]",
					str_user_group));

			try {
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				post.setEntity(entity);

				HttpResponse res = client.execute(post);
				HttpEntity res_entity = res.getEntity();
				if (res_entity != null) {
					int code = res.getStatusLine().getStatusCode();
					try {
						String res_string = EntityUtils.toString(res_entity);

						if (code >= 300) { // error occurred
							String[] fields = { "username", "email",
									"password", "password_confirmation", "user_group" };
							msg_error = ErrorHelper.getMostProminentError(
									res_string, fields);

							return false;
						} else {
							try {
								JSONObject json = new JSONObject(res_string);
								new_user = User.parseFromJSON(json);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							return true;
						}
					} catch (ParseException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			indicator.dismiss();

			if (result) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.msg_register_success),
						Toast.LENGTH_SHORT).show();

				// store into shared preferences
				UserHelper.storeUser(getApplicationContext(), new_user);

				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);

				// return to previous activity
				finish();
			} else {
				Toast.makeText(getApplicationContext(), msg_error,
						Toast.LENGTH_SHORT).show();
			}

		}

	}
}
