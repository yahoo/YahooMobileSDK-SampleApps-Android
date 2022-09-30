/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

	public static final String SITE_ID = "8a809418014d4dba274de5017840037f";


	@Override
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		setContentView(R.layout.activity_main);

		// Set any known data about the user
		// Set the AppInfo to be passed along on ad requests
		// Ensure SiteId is set using your site ID


		ListView adFormatListView = findViewById(R.id.ad_format_list);
		adFormatListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
			getResources().getStringArray(R.array.ad_formats)));

		adFormatListView.setOnItemClickListener((parent, view, position, id) -> {

			Intent intent;

			if (id == 0) {
				intent = new Intent(MainActivity.this, InlineBannerActivity.class);
			} else if (id == 1) {
				intent = new Intent(MainActivity.this, InlineRectangleActivity.class);
			} else if (id == 2) {
				intent = new Intent(MainActivity.this, InterstitialActivity.class);
			} else if (id == 3) {
				intent = new Intent(MainActivity.this, NativeActivity.class);
			} else {
				intent = new Intent(MainActivity.this, InlineBannerActivity.class);
			}

			startActivity(intent);
		});

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.inflateMenu(R.menu.main_toolbar);

		toolbar.setOnMenuItemClickListener(menuItem -> {

			if (R.id.settings == menuItem.getItemId()) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				MainActivity.this.startActivity(intent);

				return true;
			}

			return false;
		});
	}
}
