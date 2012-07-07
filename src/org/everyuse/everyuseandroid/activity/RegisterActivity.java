package org.everyuse.everyuseandroid.activity;

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
import org.everyuse.android.model.User;
import org.everyuse.android.util.ErrorHelper;
import org.everyuse.android.util.URLHelper;
import org.everyuse.android.util.UserHelper;
import org.everyuse.everyuseandroid.R;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends Activity {
	private EditText et_username;
	private EditText et_email;
	private EditText et_password;
	private EditText et_password_confirm;

	// 필드값들
	String str_username;
	String str_email;
	String str_password;
	String str_password_confirm;

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

				if (str_username.equals("") || str_email.equals("")
						|| str_password.equals("")
						|| str_password_confirm.equals("")) {
					Toast.makeText(getApplicationContext(), R.string.msg_complete_form, Toast.LENGTH_SHORT).show();
					return;
				}

				new RegisterTask().execute();
			}

		});
	}

	private class RegisterTask extends AsyncTask<Void, Void, HttpResponse> {
		private ProgressDialog indicator;
		
		@Override
		protected void onPreExecute() {
			// Initialize progress dialog
			indicator = new ProgressDialog(getApplicationContext());
			indicator.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			indicator.setMessage("Please wait...");
			indicator.setCancelable(false);
			
			// show progress dialog
			indicator.show();
		}

		@Override
		protected HttpResponse doInBackground(Void... args) {
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

			try {
				HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				post.setEntity(entity);

				HttpResponse res = client.execute(post);
				return res;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse result) {
			if (result == null) {
				Toast.makeText(RegisterActivity.this,
						getString(R.string.msg_no_response), Toast.LENGTH_SHORT).show();
				return;
			}

			HttpEntity res_entity = result.getEntity();
			if (res_entity != null) {
				int code = result.getStatusLine().getStatusCode();
				try {
					String res_string = EntityUtils.toString(res_entity);

					if (code >= 300) { // error occurred
						String[] fields = { "username", "email", "password",
								"password_confirmation" };
						String error = ErrorHelper.getMostProminentError(
								res_string, fields);
						Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Registration was successful!", Toast.LENGTH_SHORT).show();

						User user = null;

						try {
							JSONObject json = new JSONObject(res_string);
							user = User.parseFromJSON(json);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// store into shared preferences
						UserHelper.storeUser(getApplicationContext(), user);

						Intent intent = new Intent(RegisterActivity.this,
								MainActivity.class);
						startActivity(intent);

						// return to previous activity
						finish();
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			indicator.dismiss();
		}

	}
}
