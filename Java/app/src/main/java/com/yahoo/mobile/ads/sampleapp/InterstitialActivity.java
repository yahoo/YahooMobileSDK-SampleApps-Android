/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yahoo.ads.ErrorInfo;
import com.yahoo.ads.interstitialplacement.InterstitialAd;
import com.yahoo.ads.interstitialplacement.InterstitialPlacementConfig;
import com.yahoo.audiences.YahooAudiences;
import com.yahoo.audiences.YahooAudiencesEvent;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class InterstitialActivity extends AppCompatActivity implements InterstitialAd.InterstitialAdListener {

	public static final String PLACEMENT_ID = "240383";

	private static final String TAG = InterstitialActivity.class.getSimpleName();

	private InterstitialAd interstitialAd;
	private View loadButton;
	private View showButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interstitial);

		loadButton = findViewById(R.id.load);
		loadButton.setOnClickListener(v -> loadInterstitialAd());

		showButton = findViewById(R.id.show);
		showButton.setOnClickListener(v -> {

			if (interstitialAd != null) {
				interstitialAd.show(InterstitialActivity.this);

				showButton.setEnabled(false);
				loadButton.setEnabled(true);
			}
		});

		showButton.setEnabled(false);

		// Go ahead and load an ad
		loadInterstitialAd();
	}


	// InterstitialAd.InterstitialAdListener callbacks
	@Override
	public void onError(InterstitialAd interstitialAd, ErrorInfo errorInfo) {

		Log.i(TAG, "Interstitial ad encountered an error");
	}


	@Override
	public void onShown(InterstitialAd interstitialAd) {

		Log.i(TAG, "Interstitial ad shown");
	}


	@Override
	public void onClosed(InterstitialAd interstitialAd) {

		Log.i(TAG, "Interstitial ad closed");
	}


	@Override
	public void onClicked(InterstitialAd interstitialAd) {

		Log.i(TAG, "Interstitial ad clicked");
		// While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
		// Yahoo Audiences, this code is to demonstrate how to capture all
		// clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
		// with Yahoo Audiences, it is important to capture events from all monetization sources.
		YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null);
	}


	@Override
	public void onAdLeftApplication(InterstitialAd interstitialAd) {

		Log.i(TAG, "Interstitial ad caused user to leave application");
	}


	@Override
	public void onLoaded(InterstitialAd interstitialAd) {

		if (interstitialAd != null) {

			this.interstitialAd = interstitialAd;

			// Note that Creative Info is only available for some ads
			Log.i(TAG, "Interstitial ad Creative Info: " + InterstitialActivity.this.interstitialAd.getCreativeInfo());

			Log.i(TAG, getResources().getString(R.string.load_successful));
			Toast.makeText(InterstitialActivity.this,
				getResources().getString(R.string.load_successful), Toast.LENGTH_SHORT).show();

			showButton.setEnabled(true);
		}
	}


	@Override
	public void onLoadFailed(InterstitialAd interstitialAd, ErrorInfo errorInfo) {

		Log.w(TAG, errorInfo.toString());

		Toast.makeText(InterstitialActivity.this,
			getResources().getString(R.string.load_failed), Toast.LENGTH_SHORT).show();

		loadButton.setEnabled(true);
	}


	@Override
	public void onEvent(InterstitialAd interstitialAd, String source, String eventId, Map<String, Object> arguments) {

		Log.i(TAG, "Interstitial ad event occurred");

	}


	private void loadInterstitialAd() {

		loadButton.setEnabled(false);

		// Set optional request metadata.  Setting metadata improves ad selection.

		// RequestMetadata requestMetadata =
		//	 	new RequestMetadata.Builder().setAppData(<your app data>).putExtra("keywords", <your keywords>)
		//	 	.putExtra("customTargeting", <your custom targeting info>).setSupportedOrientations(<your supported orientations>)
		//	 	.build();

		// InterstitialPlacementConfig interstitiaPlacementConfig = new InterstitialPlacementConfig(PLACEMENT_ID, requestMetadata);

		InterstitialPlacementConfig interstitialPlacementConfig = new InterstitialPlacementConfig(PLACEMENT_ID, null);
		InterstitialAd interstitialAd = new InterstitialAd(InterstitialActivity.this, PLACEMENT_ID, InterstitialActivity.this);
		interstitialAd.load(interstitialPlacementConfig);
	}
}
