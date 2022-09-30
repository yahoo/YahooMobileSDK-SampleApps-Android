/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yahoo.ads.ErrorInfo;
import com.yahoo.ads.inlineplacement.AdSize;
import com.yahoo.ads.inlineplacement.InlineAdView;
import com.yahoo.ads.inlineplacement.InlinePlacementConfig;
import com.yahoo.audiences.YahooAudiences;
import com.yahoo.audiences.YahooAudiencesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class InlineBannerActivity extends AppCompatActivity implements InlineAdView.InlineAdListener {

	public static final String PLACEMENT_ID = "240372";

	private static final String TAG = InlineBannerActivity.class.getSimpleName();

	private InlineAdView inlineAdView;
	private RelativeLayout adContainer;
	private AdSize adSize;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setupViews();
		loadInlineAdView();
	}


	//InlineAdView Listener callbacks  -- start --


	@Override
	public void onLoaded(final InlineAdView inlineAdView) {

		if (inlineAdView != null) {

			this.inlineAdView = inlineAdView;

			// Note that Creative Info is only available for some ads
			Log.i(TAG, "Inline ad Creative Info: " + InlineBannerActivity.this.inlineAdView.getCreativeInfo());

			adContainer.addView(InlineBannerActivity.this.inlineAdView);

			Log.i(TAG, getResources().getString(R.string.load_successful));
			Toast.makeText(InlineBannerActivity.this,
				getResources().getString(R.string.load_successful), Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onLoadFailed(InlineAdView inlineAdView, ErrorInfo errorInfo) {

		Log.w(TAG, errorInfo.toString());

		Log.i(TAG, getResources().getString(R.string.load_failed));
		Toast.makeText(InlineBannerActivity.this,
			getResources().getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onError(InlineAdView inlineAdView, ErrorInfo errorInfo) {

		Log.i(TAG, "Inline ad encountered an error");
	}


	@Override
	public void onResized(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad resized");
	}


	@Override
	public void onExpanded(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad expanded");
	}


	@Override
	public void onCollapsed(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad collapsed");
	}


	@Override
	public void onClicked(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad clicked");
		// While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
		// Yahoo Audiences, this code is to demonstrate how to capture all
		// clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
		// with Yahoo Audiences, it is important to capture events from all monetization sources.
		YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null);
	}


	@Override
	public void onAdLeftApplication(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad did cause user to leave application");
	}


	@Override
	public void onAdRefreshed(InlineAdView inlineAdView) {

		Log.i(TAG, "Inline ad refreshed");
	}


	@Override
	public void onEvent(InlineAdView inlineAdView, String source, String eventId, Map<String, Object> arguments) {

		Log.i(TAG, "Inline ad event occurred");
	}


	//InlineAdView callbacks -- end --


	@Override
	protected void onDestroy() {

		super.onDestroy();

		destroyAd(inlineAdView);
	}


	private void destroyAd(@Nullable InlineAdView inlineAdView) {

		if (inlineAdView != null) {
			inlineAdView.destroy();
		}
	}


	private void setupViews() {

		setContentView(R.layout.activity_inline_banner);

		adContainer = findViewById(R.id.ad_container);

		Button refreshButton = findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(view -> loadInlineAdView());
	}


	private void loadInlineAdView() {

		adContainer.removeAllViews();
		destroyAd(inlineAdView);

		// Create a new Inline instance using your placement ID and a reference to the View to fill with the Ad
		// from your layout.

		setupAdSize();

		// Create a list of all ad sizes that your inline placement supports.  If it only supports one ad size, simply provide a list of
		// one item.  Note that the InlineAdView returned will have the actual ad size of the returned ad.  If you support multiple ad
		// sizes, size your container appropriately based on the returned ad size.
		List<AdSize> adSizes = new ArrayList<>();
		adSizes.add(adSize);

		InlinePlacementConfig inlinePlacementConfig = new InlinePlacementConfig(PLACEMENT_ID, null, adSizes);
		InlineAdView inlineAd = new InlineAdView(this, PLACEMENT_ID, this);
		inlineAd.load(inlinePlacementConfig);
	}


	private void setupAdSize() {

		int width = 320, height = 50;
		adSize = new AdSize(width, height);
	}
}
