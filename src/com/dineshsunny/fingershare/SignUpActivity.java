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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends ActionBarActivity {
	protected TextView mSignInTextView; // to switch to signIn activity
	protected EditText mUserName;
	protected EditText mEmailId;
	protected EditText mPassword;
	protected Button mSignupButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		// getting Id's from layout
		mUserName = (EditText) findViewById(R.id.signup_username_Edittext);
		mEmailId = (EditText) findViewById(R.id.signup_email_edittext);
		mPassword = (EditText) findViewById(R.id.signup_password_edittext);
		mSignupButton = (Button) findViewById(R.id.signup_signup_button);
		// adding listeners to button
		mSignupButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View signupCheck) {
				String username = mUserName.getText().toString().trim();
				String emailId = mEmailId.getText().toString().trim();
				String password = mPassword.getText().toString();
				// Don't trim the passwords.done some research..You do again.

				if (username.isEmpty() || emailId.isEmpty()
						|| password.isEmpty()) {
					// Building an alert dialog
					AlertDialog.Builder signUpBuilder = new AlertDialog.Builder(
							SignUpActivity.this);
					signUpBuilder.setTitle(R.string.signup_builder_title);
					signUpBuilder.setMessage(R.string.signup_builder_message);
					signUpBuilder.setPositiveButton(android.R.string.ok, null);
					signUpBuilder.create();
					signUpBuilder.show();

				} else {
					// success create a new user
					setProgressBarIndeterminateVisibility(true);
					ParseUser user = new ParseUser();
					user.setUsername(username);
					user.setEmail(emailId);
					user.setPassword(password);
					user.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							if (e == null) {
								// Building an alert dialog
								// AlertDialog.Builder UserBuilder = new
								// AlertDialog.Builder(SignUpActivity.this);
								// UserBuilder.setTitle(R.string.signup_success_builder_welcome_title);
								// UserBuilder.setTitle(R.string.signup_success_builder_welcome_title
								// +" "+ user.getUsername().toString());
								// UserBuilder.setMessage(R.string.signup_success_builder_welcome_message);
								// UserBuilder.setPositiveButton(android.R.string.yes,
								// null);
								// UserBuilder.create();
								// UserBuilder.show();
								// goinig to inbox Activity
								Intent signUpsuccess = new Intent(
										SignUpActivity.this, MainActivity.class);
								signUpsuccess
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(signUpsuccess);
							} else {
								// Building an alert dialog
								AlertDialog.Builder UserBuilder = new AlertDialog.Builder(
										SignUpActivity.this);
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

		// to switch to signIn activity
		mSignInTextView = (TextView) findViewById(R.id.signup_signin_textview);
		mSignInTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent signInIntent = new Intent(SignUpActivity.this,
						LoginActivity.class);
				signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(signInIntent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
