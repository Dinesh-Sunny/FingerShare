package com.dineshsunny.fingershare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends ActionBarActivity {
	protected TextView mSignUpTextView;
	protected EditText mUserName;
	protected EditText mPassword;
	protected Button mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// initialising the spinning loading bar by below line
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// getting Id's from layout
		mSignUpTextView = (TextView) findViewById(R.id.signup_textview);
		mUserName = (EditText) findViewById(R.id.login_username_edittext);
		mPassword = (EditText) findViewById(R.id.login_password_edittext);
		mLoginButton = (Button) findViewById(R.id.login_login_button);

		// Login button onclick listener to user login
		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String username = mUserName.getText().toString().trim();
				String password = mPassword.getText().toString();
				if (username.isEmpty() || password.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							LoginActivity.this);
					builder.setTitle(R.string.login_builder_title);
					builder.setMessage(R.string.login_builder_message);
					builder.setPositiveButton(android.R.string.ok, null);
					builder.create();
					builder.show();
				} else {
					// login success
					setProgressBarIndeterminateVisibility(true);
					ParseUser.logInInBackground(username, password,
							new LogInCallback() {

								@Override
								public void done(ParseUser user,
										ParseException e) {
									setProgressBarIndeterminateVisibility(false);
									if (e == null) {
										Intent intent = new Intent(
												LoginActivity.this,
												MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									}else {
										// Building an alert dialog
										AlertDialog.Builder UserBuilder = new AlertDialog.Builder(
												LoginActivity.this);
										UserBuilder.setTitle(e.getMessage());
										UserBuilder
												.setMessage(R.string.signup_builder_validation);
										UserBuilder.setPositiveButton(
												android.R.string.ok, null);
										UserBuilder.create();
										UserBuilder.show();
									}

								}
							});
				}

			}
		});

		// Switch activity layout from login to signup button onclick lisetener
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent signupIntent = new Intent(LoginActivity.this,
						SignUpActivity.class);
				startActivity(signupIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
