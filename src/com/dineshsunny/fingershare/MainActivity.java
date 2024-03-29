package com.dineshsunny.fingershare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int TAKE_VIDEO_REQUEST = 1;
	public static final int CHOOSE_PHOTO_REQUEST = 2;
	public static final int CHOOSE_VIDEO_REQUEST = 3;

	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final int MEDIA_TYPE_VIDEO = 5;
	public static final int file_SIZE_LIMIT = 1024*1024*10; //10mb

	protected Uri mMediaUri;

	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0: // Take Picture
				checkExternalAndTakePhotoIntent();

				break;
			case 1: // Take Video
				Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if (mMediaUri == null) {
					// dispaly an error
					Toast.makeText(MainActivity.this,
							R.string.external_storage_not_found, Toast.LENGTH_LONG)
							.show();
				} else {
					videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
					videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
					videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);// 0 = lowest resolution
					startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
				}
				
				break;
			case 2: // Choose Picture
				
				Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");
				startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
				
				break;
			case 3: // Choose Video

				Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				chooseVideoIntent.setType("video/*");
				Toast.makeText(MainActivity.this, R.string.video_select_limit, Toast.LENGTH_LONG).show();
				startActivityForResult(chooseVideoIntent, CHOOSE_VIDEO_REQUEST);
				
				break;
			}

		}

		private void checkExternalAndTakePhotoIntent() {
			Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

			if (mMediaUri == null) {
				// dispaly an error
				Toast.makeText(MainActivity.this,
						R.string.external_storage_not_found, Toast.LENGTH_LONG)
						.show();
			} else {
				photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				startActivityForResult(photoIntent, TAKE_PHOTO_REQUEST);
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.

			if (isExternalDeviceExists()) {
				// get the URI of external storage

				// 1. Get the external storage directory
				String appname = MainActivity.this.getString(R.string.app_name);
				File storageDevice = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appname);
				// 2.Create a sub direcotry
				
				if(! storageDevice.exists()){
					if(! storageDevice.mkdirs()){
						Log.e(TAG, "Failed To Create Directory");
						return null;
					}
				}
				// create a file name
				 
				 File mediaFile;
				 Date now = new Date();
				 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
				// 4.Create a file
				 String path = storageDevice.getPath() + File.separator;
				 if (mediaType == MEDIA_TYPE_IMAGE){
				        mediaFile = new File(path +
				        "IMG_"+ timeStamp + ".jpg");
				    } else if(mediaType == MEDIA_TYPE_VIDEO) {
				        mediaFile = new File(path +
				        "VID_"+ timeStamp + ".mp4");
				    } else {
				        return null;
				    }
				 Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
				// 5.return the file's URI

				return Uri.fromFile(mediaFile);
			} else {
				return null;
			}
		}

		private boolean isExternalDeviceExists() {
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			} else {
				return false;
			}
		}
	};

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}. Yipee ...
	 * Dinesh Sunny's project!!
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // for
																		// friendsfragment
		setContentView(R.layout.activity_main);

		ParseAnalytics.trackAppOpened(getIntent()); // analytics of parse cloud

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			// do stuff with the user
			navigateToLogin();
		} else {
			Log.i(TAG, currentUser.getUsername());
		}

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));

		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
				
			if(resultCode == CHOOSE_PHOTO_REQUEST || resultCode == CHOOSE_VIDEO_REQUEST){
				if(data == null){
					Toast.makeText(this, "Something went wrong with your gallery", Toast.LENGTH_LONG).show();
				}else{
					mMediaUri = data.getData();
				}
				
				Log.i(TAG, "Media URI: "+ mMediaUri);
				if(requestCode == CHOOSE_VIDEO_REQUEST){
					//make sure the video is less than 10MB
					int fileSize = 0;
					InputStream inputStream=null;
					try {
						inputStream = getContentResolver().openInputStream(mMediaUri);
						fileSize = inputStream.available();
					} catch (FileNotFoundException e) {
						Toast.makeText(this, R.string.selected_file_problem, Toast.LENGTH_LONG).show();
						return;
					}catch (IOException e) {
						Toast.makeText(this, R.string.selected_file_problem, Toast.LENGTH_LONG).show();
						return;
					}finally{
						try {
							inputStream.close();
						} catch (IOException e) {
							//Intenionally blank
						}
					}
					if (fileSize >= file_SIZE_LIMIT) {
						Toast.makeText(this, R.string.file_error_chose_warning, Toast.LENGTH_LONG).show();
						return;
					}
					
				}
				
			}else{
			
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			mediaScanIntent.setData(mMediaUri);
			sendBroadcast(mediaScanIntent);
			}
		}else if(resultCode != RESULT_CANCELED){
			Toast.makeText(this , "There was an error", Toast.LENGTH_LONG).show();
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_logout:
			ParseUser.logOut();
			navigateToLogin();
		case R.id.menu_edit_friends:
			Intent intent = new Intent(this, EditFriendsActivity.class);
			startActivity(intent);
		case R.id.menu_camera:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(R.array.camera_options, mDialogListener);
			builder.create();
			builder.show();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
}
