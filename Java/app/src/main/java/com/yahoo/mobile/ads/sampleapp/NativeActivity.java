/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.ads.ErrorInfo;
import com.yahoo.ads.nativeplacement.NativeAd;
import com.yahoo.ads.nativeplacement.NativePlacementConfig;
import com.yahoo.ads.yahoonativecontroller.NativeComponent;
import com.yahoo.ads.yahoonativecontroller.NativeImageComponent;
import com.yahoo.ads.yahoonativecontroller.NativeTextComponent;
import com.yahoo.audiences.YahooAudiences;
import com.yahoo.audiences.YahooAudiencesEvent;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class NativeActivity extends AppCompatActivity implements NativeAd.NativeAdListener {

	public static final String PLACEMENT_ID = "240549";
	private static final String TAG = NativeActivity.class.getSimpleName();
	private NativeAd nativeAd;
	private LinearLayout adContainer;
	private boolean showImmediately = true;
	private final String[] adTypes = new String[]{"simpleImage", "simpleVideo"};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native);

		adContainer = findViewById(R.id.ad_container);

		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {

			toolbar.inflateMenu(R.menu.native_toolbar);

			toolbar.setOnMenuItemClickListener(menuItem -> {

				if (R.id.native_load == menuItem.getItemId()) {
					loadNativeAd();
					return true;
				}
				if (R.id.native_show == menuItem.getItemId()) {
					if (nativeAd != null) {
						displayNativeAd();
					} else {
						Toast.makeText(NativeActivity.this, "Native ad not ready", Toast.LENGTH_SHORT).show();
					}

					return true;
				}

				return false;
			});
		}

		// Go ahead and request an ad
		loadNativeAd();
	}


	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		// Reconstruct the native ad view based on the new configuration
		displayNativeAd();
	}


	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (nativeAd != null) {
			adContainer.removeAllViews();
			nativeAd.destroy();
			nativeAd = null;
		}
	}


	private void displayNativeAd() {

		if (nativeAd != null) {

			// Clean up any previously existing ads
			adContainer.removeAllViews();

			final Resources resources = getResources();

			RelativeLayout nativeAdView = new RelativeLayout(NativeActivity.this);
			nativeAdView.setBackgroundColor(resources.getColor(android.R.color.white));

			int padding = resources.getDimensionPixelSize(R.dimen.native_ad_padding);

			// Construct the header of the Ad
			RelativeLayout header = new RelativeLayout(NativeActivity.this);
			header.setId(R.id.native_header_container);
			header.setPadding(padding, 0, padding, 0);

			NativeImageComponent iconImageComponent = (NativeImageComponent) nativeAd.getComponent("iconImage");
			if (iconImageComponent != null) {
				ImageView imageView = new ImageView(this);
				iconImageComponent.prepareView(imageView);
				imageView.setId(R.id.native_icon);

				int iconSize = resources.getDimensionPixelSize(R.dimen.native_icon_size);
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iconSize, iconSize);
				layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_icon_top_margin);

				header.addView(imageView, layoutParams);
			}

			NativeTextComponent titleTextComponent = (NativeTextComponent) nativeAd.getComponent("title");
			if (titleTextComponent != null) {
				TextView titleTextView = new TextView(this);
				titleTextComponent.prepareView(titleTextView);
				titleTextView.setId(R.id.native_title);
				titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					toScaledPixels(resources.getDimension(R.dimen.title_text_size)));

				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

				layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_title_top_margin);
				layoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.native_title_left_margin);
				layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.native_icon);

				header.addView(titleTextView, layoutParams);
			}

			NativeTextComponent disclaimerTextComponent = (NativeTextComponent) nativeAd.getComponent("disclaimer");
			if (disclaimerTextComponent != null) {
				TextView disclaimerTextView = new TextView(this);
				disclaimerTextComponent.prepareView(disclaimerTextView);
				disclaimerTextView.setId(R.id.native_disclaimer_text);
				disclaimerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					toScaledPixels(resources.getDimension(R.dimen.disclaimer_text_size)));

				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

				layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_disclaimer_text_top_margin);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

				header.addView(disclaimerTextView, layoutParams);
			}

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			nativeAdView.addView(header, layoutParams);

			// Attach the body of the ad which includes the body text and main image
			NativeTextComponent bodyTextComponent = (NativeTextComponent) nativeAd.getComponent("body");
			if (bodyTextComponent != null) {
				TextView bodyTextView = new TextView(this);
				bodyTextComponent.prepareView(bodyTextView);
				bodyTextView.setId(R.id.native_body_text);
				bodyTextView.setPadding(padding, 0, padding, 0);
				bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
					toScaledPixels(resources.getDimension(R.dimen.body_text_size)));

				layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

				layoutParams.addRule(RelativeLayout.BELOW, R.id.native_header_container);
				layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_section_top_margin);

				nativeAdView.addView(bodyTextView, layoutParams);
			}

			WindowManager windowManager = (WindowManager) NativeActivity.this.getSystemService(WINDOW_SERVICE);

			if (windowManager != null) {
				Display defaultDisplay = windowManager.getDefaultDisplay();

				Point displaySize = new Point();

				defaultDisplay.getSize(displaySize);

				// Portrait
				if (displaySize.y > displaySize.x) {
					NativeImageComponent mainImageComponent = (NativeImageComponent) nativeAd.getComponent("mainImage");

					if (mainImageComponent != null) {
						ImageView mainImageView = new ImageView(this);
						mainImageView.setId(R.id.native_main_image);
						mainImageComponent.prepareView(mainImageView);

						int mainImageWidth = resources.getDimensionPixelSize(R.dimen.native_main_image_width);
						int mainImageHeight = resources.getDimensionPixelSize(R.dimen.native_main_image_height);
						layoutParams = new RelativeLayout.LayoutParams(mainImageWidth, mainImageHeight);
						layoutParams.addRule(RelativeLayout.BELOW, R.id.native_body_text);
						layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_section_top_margin);

						nativeAdView.addView(mainImageView, layoutParams);
					}
				}

				// Construct the footer layout
				int footerTopPadding = resources.getDimensionPixelSize(R.dimen.native_footer_top_padding);

				RelativeLayout footer = new RelativeLayout(NativeActivity.this);
				footer.setId(R.id.native_footer_container);

				NativeTextComponent callToActionTextComponent = (NativeTextComponent) nativeAd.getComponent("callToAction");

				if (callToActionTextComponent != null) {
					TextView callToActionView = new TextView(this);
					callToActionTextComponent.prepareView(callToActionView);

					callToActionView.setId(R.id.native_cta);

					callToActionView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
						toScaledPixels(resources.getDimension(R.dimen.cta_text_size)));

					layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);

					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

					footer.addView(callToActionView, layoutParams);
				}

				layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

				// Only use the main image if the display is portrait
				if (displaySize.y > displaySize.x) {
					footer.setPadding(padding, footerTopPadding, padding, padding);
					layoutParams.addRule(RelativeLayout.BELOW, R.id.native_main_image);
				} else {
					footer.setPadding(padding, padding, padding, padding);
					layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_section_top_margin);
					layoutParams.addRule(RelativeLayout.BELOW, R.id.native_body_text);
				}

				nativeAdView.addView(footer, layoutParams);
			}

			// Attach the completed native ad to the view hierarchy
			int adWidth = resources.getDimensionPixelSize(R.dimen.native_ad_width);

			LinearLayout.LayoutParams lp =
				new LinearLayout.LayoutParams(adWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				nativeAdView.setElevation(resources.getDimension(R.dimen.native_ad_elevation));

				adContainer.addView(nativeAdView, lp);

			} else {
				FrameLayout shadowView = new FrameLayout(NativeActivity.this);
				shadowView.setBackgroundColor(resources.getColor(android.R.color.darker_gray));
				int shadowPadding = resources.getDimensionPixelSize(R.dimen.native_ad_shadow);
				shadowView.setPadding(shadowPadding, shadowPadding, shadowPadding, shadowPadding);

				FrameLayout.LayoutParams shadowLayoutParams =
					new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);

				shadowView.addView(nativeAdView, shadowLayoutParams);
				adContainer.addView(shadowView, lp);
			}

			// Register the ad container view with the native ad after the components have been attached.  This must be called to track
			// impressions.
			nativeAd.registerContainerView(adContainer);
		}
	}


	// NativeAd.NativeAdListener callbacks


	@Override
	public void onLoaded(NativeAd nativeAd) {

		if (nativeAd != null) {
			this.nativeAd = nativeAd;

			// Note that Creative Info is only available for some ads
			Log.i(TAG, "Native ad Creative Info: " + NativeActivity.this.nativeAd.getCreativeInfo());

			Log.i(TAG, getResources().getString(R.string.load_successful));
			Toast.makeText(NativeActivity.this,
				getResources().getString(R.string.load_successful), Toast.LENGTH_SHORT).show();

			if (showImmediately) {
				displayNativeAd();

				showImmediately = false;
			}
		}
	}


	@Override
	public void onLoadFailed(NativeAd nativeAd, ErrorInfo errorInfo) {

		Log.w(TAG, errorInfo.toString());

		Toast.makeText(NativeActivity.this,
			getResources().getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onError(NativeAd nativeAd, ErrorInfo errorInfo) {

		Log.i(TAG, "Native ad encountered an error");
	}


	@Override
	public void onClosed(NativeAd nativeAd) {

		Log.i(TAG, "Native ad closed");
	}


	@Override
	public void onClicked(NativeAd nativeAd, NativeComponent nativeComponent) {

		Log.i(TAG, "Native ad clicked");
		// While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
		// Yahoo Audiences, this code is to demonstrate how to capture all
		// clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
		// with Yahoo Audiences, it is important to capture events from all monetization sources.
		YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null);
	}


	@Override
	public void onAdLeftApplication(NativeAd nativeAd) {

		Log.i(TAG, "Ad caused user to leave application");
	}


	@Override
	public void onEvent(NativeAd nativeAd, String source, String eventId, Map<String, Object> arguments) {

		Log.i(TAG, "Native ad event occurred: " + eventId);
	}


	private float toScaledPixels(float pixels) {

		return pixels / getResources().getDisplayMetrics().scaledDensity;
	}


	private void loadNativeAd() {

		adContainer.removeAllViews();

		// Set optional request metadata.  Setting metadata improves ad selection.

		// RequestMetadata requestMetadata =
		//	 	new RequestMetadata.Builder().setAppData(<your app data>).putExtra("keywords", <your keywords>)
		//	 	.putExtra("customTargeting", <your custom targeting info>).setSupportedOrientations(<your supported orientations>)
		//	 	.build();

		// NativePlacementConfig nativePlacementConfig = new NativePlacementConfig(PLACEMENT_ID, requestMetadata, adTypes);

		NativePlacementConfig nativePlacementConfig = new NativePlacementConfig(PLACEMENT_ID, null, adTypes);
		NativeAd nativeAd = new NativeAd(this, PLACEMENT_ID, this);
		nativeAd.load(nativePlacementConfig);
	}
}