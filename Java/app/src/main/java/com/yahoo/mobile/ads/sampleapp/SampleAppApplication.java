/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp;

import android.app.Application;
import android.util.Log;

import com.yahoo.ads.YASAds;


public class SampleAppApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();

		// Enable debug logging
		YASAds.setLogLevel(Log.DEBUG);

		// Required for all integrations
		YASAds.initialize(this, MainActivity.SITE_ID);
	}
}
