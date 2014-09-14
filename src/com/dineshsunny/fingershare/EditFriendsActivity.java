package com.dineshsunny.fingershare;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends ListActivity {

	public static final String TAG = EditFriendsActivity.class.getSimpleName();
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendRelation;
	protected ParseUser mCurrentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_edit_friends);

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIEND_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, com.parse.ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {

					// success
					mUsers = users;
					String[] usernames = new String[mUsers.size()];
					int i = 0;
					for (ParseUser user : mUsers) {
						usernames[i] = user.getUsername();
						i++;
					}
					// setting the adapter
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							EditFriendsActivity.this,
							android.R.layout.simple_list_item_checked,
							usernames);
					setListAdapter(adapter);
					addFriendCheckMarks();

				} else {
					Log.i(TAG, e.getMessage());
					// Building an alert dialog
					AlertDialog.Builder UserBuilder = new AlertDialog.Builder(
							EditFriendsActivity.this);
					UserBuilder.setTitle(e.getMessage());
					UserBuilder.setMessage(R.string.error_title);
					UserBuilder.setPositiveButton(android.R.string.ok, null);
					UserBuilder.create();
					UserBuilder.show();
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_friends, menu);
		return true;
	}

	private void navigateToLogin() {
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// YOU CAN ALSO WRITE loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		// | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_logout) {
			ParseUser.logOut();
			navigateToLogin();
		}
		return super.onOptionsItemSelected(item);
	}

	// deciding the parse relationship
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (getListView().isItemChecked(position)) {
			// Add Friends
			mFriendRelation.add(mUsers.get(position));
			// till now it added locally.nowwe will save in the backend
			saveInbackGround();

		} else {
			// Remove Friends
						mFriendRelation.remove(mUsers.get(position));
						// till now it added locally.nowwe will save in the backend
						saveInbackGround();
		}

	}

	private void saveInbackGround() {
		mCurrentUser.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage());
				}

			}
		});
	}

	private void addFriendCheckMarks() {
		mFriendRelation.getQuery().findInBackground(
				new FindCallback<ParseUser>() {

					@Override
					public void done(List<ParseUser> friends, ParseException e) {
						if (e == null) {
							// list returned - look for match

							// here comes looping through in database.So be
							// cautious wwhen your app is growing more
							for (int i = 0; i < mUsers.size(); i++) {
								ParseUser user = mUsers.get(i);
								
								for(ParseUser friend : friends){
									if(friend.getObjectId().equals(user.getObjectId())){
										getListView().setItemChecked(i, true);
									}
								}
								
							}

						} else {
							Log.e(TAG, e.getMessage());
						}

					}
				});
	}
}
