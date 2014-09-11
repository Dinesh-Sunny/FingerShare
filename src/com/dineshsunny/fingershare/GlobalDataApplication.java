package com.dineshsunny.fingershare;

import android.app.Application;

import com.parse.Parse;

public class GlobalDataApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate(); // to make sure all data is called from super class
							// from onCreate
		Parse.initialize(this, "hloj3W8eaNatUCFa720u6vYEkINJmmiwMbzIqoNk",
				"X2J6u8qJTcTYZQ53jeqwkVpMpBsm28HDL4P9Lpm0");
		// Using the below three lines my app successfully passed data object to
		// the data table

		// ParseObject testObject = new ParseObject("TestObject");
		// testObject.put("foo", "bar");
		// testObject.saveInBackground();
	}

}
