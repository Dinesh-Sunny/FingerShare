package com.dineshsunny.fingershare;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;



public class FriendsFragment extends ListFragment {
	public static final String TAG = FriendsFragment.class.getSimpleName();  
	
	
	protected ParseRelation<ParseUser> mFriendRelation;
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mFriends;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_friends, container,
				false);
		return rootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendRelation = mCurrentUser
				.getRelation(ParseConstants.KEY_FRIEND_RELATION);
		
		getActivity().setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser> query = mFriendRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if(e == null){
					mFriends = friends;
					
					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for (ParseUser friend : mFriends) {
						usernames[i] = friend.getUsername();
						i++;
					}
					// setting the adapter
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(),
							android.R.layout.simple_list_item_1,
							usernames);
					setListAdapter(adapter);
					
					
				}else{
					Log.i(TAG, e.getMessage());
					// Building an alert dialog
					AlertDialog.Builder UserBuilder = new AlertDialog.Builder(
							getListView().getContext());
					UserBuilder.setTitle(e.getMessage());
					UserBuilder.setMessage(R.string.error_title);
					UserBuilder.setPositiveButton(android.R.string.ok, null);
					UserBuilder.create();
					UserBuilder.show();
					
				}
				}
				
		});
	}

}
