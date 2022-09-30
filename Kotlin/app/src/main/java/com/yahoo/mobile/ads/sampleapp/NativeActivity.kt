/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp


import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.yahoo.ads.ErrorInfo
import com.yahoo.ads.nativeplacement.NativeAd
import com.yahoo.ads.nativeplacement.NativePlacementConfig
import com.yahoo.ads.yahoonativecontroller.NativeComponent
import com.yahoo.ads.yahoonativecontroller.NativeImageComponent
import com.yahoo.ads.yahoonativecontroller.NativeTextComponent
import com.yahoo.audiences.YahooAudiences
import com.yahoo.audiences.YahooAudiencesEvent

private val TAG = NativeActivity::class.java.simpleName
private const val PLACEMENT_ID = "240549"

@Suppress("DEPRECATION")
class NativeActivity : AppCompatActivity(), NativeAd.NativeAdListener {

    private val adTypes = arrayOf("simpleImage", "simpleVideo")

    private var nativeAd: NativeAd? = null
    private var adContainer: LinearLayout? = null
    private var showImmediately = true


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)

        adContainer = findViewById(R.id.ad_container)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {

            toolbar.inflateMenu(R.menu.native_toolbar)

            toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { menuItem ->
                if (R.id.native_load == menuItem.itemId) {
                    loadNativeAd()
                    return@OnMenuItemClickListener true
                }
                if (R.id.native_show == menuItem.itemId) {
                    if (nativeAd != null) {
                        displayNativeAd()
                    } else {
                        Toast.makeText(this@NativeActivity, "Native ad not ready", Toast.LENGTH_SHORT).show()
                    }

                    return@OnMenuItemClickListener true
                }

                false
            })
        }

        // Go ahead and request an ad
        loadNativeAd()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {

        super.onConfigurationChanged(newConfig)

        // Reconstruct the native ad view based on the new configuration
        displayNativeAd()
    }


    override fun onDestroy() {

        super.onDestroy()

        if (nativeAd != null) {
            adContainer?.removeAllViews()
            nativeAd?.destroy()
            nativeAd = null
        }
    }


    private fun displayNativeAd() {

        nativeAd?.let {

            // Clean up any previously existing ads
            adContainer?.removeAllViews()

            val nativeAdView = RelativeLayout(this)
            nativeAdView.setBackgroundColor(resources.getColor(android.R.color.white))

            val resources = resources
            val padding = resources.getDimensionPixelSize(R.dimen.native_ad_padding)

            // Construct the header of the Ad
            val header = RelativeLayout(this)
            header.id = R.id.native_header_container
            header.setPadding(padding, 0, padding, 0)

            (it.getComponent("iconImage") as? NativeImageComponent)?.let { iconImageComponent ->
                val iconView = ImageView(this)
                iconImageComponent.prepareView(iconView)
                iconView.id = R.id.native_icon

                val iconSize = resources.getDimensionPixelSize(R.dimen.native_icon_size)
                val layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_icon_top_margin)

                header.addView(iconView, layoutParams)
            }

            (it.getComponent("title") as? NativeTextComponent)?.let { titleTextComponent ->
                val titleView = TextView(this)
                titleView.id = R.id.native_title
                titleTextComponent.prepareView(titleView)
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    toScaledPixels(resources.getDimension(R.dimen.title_text_size)))

                val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_title_top_margin)
                layoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.native_title_left_margin)
                layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.native_icon)

                header.addView(titleView, layoutParams)
            }

            (it.getComponent("disclaimer") as? NativeTextComponent)?.let { disclaimerTextComponent ->
                val disclaimerView = TextView(this)
                disclaimerTextComponent.prepareView(disclaimerView)
                disclaimerView.id = R.id.native_disclaimer_text
                disclaimerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    toScaledPixels(resources.getDimension(R.dimen.disclaimer_text_size)))

                val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_disclaimer_text_top_margin)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

                header.addView(disclaimerView, layoutParams)
            }

            var layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)

            nativeAdView.addView(header, layoutParams)

            // Attach the body of the ad which includes the body text and main image
            (it.getComponent("body") as? NativeTextComponent)?.let { bodyTextComponent ->
                val bodyView = TextView(this)
                bodyTextComponent.prepareView(bodyView)
                bodyView.id = R.id.native_body_text
                bodyView.setPadding(padding, 0, padding, 0)
                bodyView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    toScaledPixels(resources.getDimension(R.dimen.body_text_size)))

                layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

                layoutParams.addRule(RelativeLayout.BELOW, R.id.native_header_container)
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_section_top_margin)

                nativeAdView.addView(bodyView, layoutParams)
            }

            (it.getComponent("mainImage") as? NativeImageComponent)?.let { mainImageComponent ->
                val mainImageView = ImageView(this)
                mainImageComponent.prepareView(mainImageView)
                mainImageView.id = R.id.native_media

                val mediaWidth = resources.getDimensionPixelSize(R.dimen.native_main_image_width)
                val mediaHeight = resources.getDimensionPixelSize(R.dimen.native_main_image_height)
                layoutParams = RelativeLayout.LayoutParams(mediaWidth, mediaHeight)
                layoutParams.addRule(RelativeLayout.BELOW, R.id.native_body_text)
                layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.native_section_top_margin)

                nativeAdView.addView(mainImageView, layoutParams)
            }

            // Construct the footer layout
            val footerTopPadding = resources.getDimensionPixelSize(R.dimen.native_footer_top_padding)

            val footer = RelativeLayout(this)
            footer.id = R.id.native_footer_container

            (it.getComponent("callToAction") as? NativeTextComponent)?.let { callToActionTextComponent ->
                val callToActionView = TextView(this)
                callToActionView.id = R.id.native_cta
                callToActionTextComponent.prepareView(callToActionView)

                callToActionView.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                    toScaledPixels(resources.getDimension(R.dimen.cta_text_size)))

                layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

                footer.addView(callToActionView, layoutParams)
            }

            layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            footer.setPadding(padding, footerTopPadding, padding, padding)
            layoutParams.addRule(RelativeLayout.BELOW, R.id.native_media)
            nativeAdView.addView(footer, layoutParams)

            // Attach the completed native ad to the view hierarchy
            val adWidth = resources.getDimensionPixelSize(R.dimen.native_ad_width)

            val lp = LinearLayout.LayoutParams(adWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nativeAdView.elevation = resources.getDimension(R.dimen.native_ad_elevation)

                adContainer?.addView(nativeAdView, lp)

            } else {
                val shadowView = FrameLayout(this)
                shadowView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                val shadowPadding = resources.getDimensionPixelSize(R.dimen.native_ad_shadow)
                shadowView.setPadding(shadowPadding, shadowPadding, shadowPadding, shadowPadding)

                val shadowLayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

                shadowView.addView(nativeAdView, shadowLayoutParams)
                adContainer?.addView(shadowView, lp)
            }

            // Register the ad container view with the native ad after the components have been attached.
            nativeAd?.registerContainerView(adContainer)
        }
    }


    // NativeAd.NativeAdListener callbacks

    override fun onLoaded(nativeAd: NativeAd) {
        this.nativeAd = nativeAd

        Log.i(TAG, resources.getString(R.string.load_successful))

        Toast.makeText(
            this@NativeActivity,
            resources.getString(R.string.load_successful), Toast.LENGTH_SHORT
        ).show()

        if (showImmediately) {
            displayNativeAd()

            showImmediately = false
        }
    }


    override fun onLoadFailed(nativeAd: NativeAd, errorInfo: ErrorInfo) {
        Log.w(TAG, errorInfo.toString())

        Toast.makeText(
            this@NativeActivity,
            resources.getString(R.string.load_failed), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(nativeAd: NativeAd, errorInfo: ErrorInfo) {

        Log.i(TAG, "Native ad encountered an error")
    }


    override fun onClosed(nativeAd: NativeAd) {

        Log.i(TAG, "Native ad closed")
    }

    override fun onClicked(nativeAd: NativeAd, nativeComponent: NativeComponent) {

        Log.i(TAG, "Native ad clicked")
        // While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
        // Yahoo Audiences, this code is to demonstrate how to capture all
        // clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
        // with Yahoo Audiences, it is important to capture events from all monetization sources.
        YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null)
    }


    override fun onAdLeftApplication(nativeAd: NativeAd) {

        Log.i(TAG, "Ad caused user to leave application")
    }


    override fun onEvent(nativeAd: NativeAd, source: String, eventId: String, arguments: Map<String, Any>?) {

        Log.i(TAG, "Native ad event occurred: $eventId")
    }


    private fun toScaledPixels(pixels: Float): Float {

        return pixels / resources.displayMetrics.scaledDensity
    }


    private fun loadNativeAd() {

        adContainer?.removeAllViews()

        // Set optional request metadata.  Setting metadata improves ad selection.

        // val requestMetadata =
        //	 	RequestMetadata.Builder().setAppData(<your app data>).putExtra("keywords", <your keywords>)
        //	 	.putExtra("customTargeting", <your custom targeting info>).setSupportedOrientations(<your supported orientations>).build()

        // val placementConfig = NativePlacementConfig(PLACEMENT_ID, requestMetadata, adTypes)

        val placementConfig = NativePlacementConfig(PLACEMENT_ID, null, adTypes)
        val nativeAd = NativeAd(this, PLACEMENT_ID, this)
        nativeAd.load(placementConfig)
    }
}
