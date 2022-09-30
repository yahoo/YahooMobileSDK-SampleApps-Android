/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.ads.ErrorInfo
import com.yahoo.ads.inlineplacement.AdSize
import com.yahoo.ads.inlineplacement.InlineAdView
import com.yahoo.ads.inlineplacement.InlinePlacementConfig
import com.yahoo.audiences.YahooAudiences
import com.yahoo.audiences.YahooAudiencesEvent
import java.util.*

private val TAG = InlineRectangleActivity::class.java.simpleName
private const val PLACEMENT_ID = "240379"

class InlineRectangleActivity : AppCompatActivity(), InlineAdView.InlineAdListener {

    private var inlineAdView: InlineAdView? = null
    private var adContainer: RelativeLayout? = null
    private var adSize: AdSize? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setupViews()
        loadInlineAdView()
    }


    //InlineAdView Listener callbacks  -- start --


    override fun onLoaded(inlineAdView: InlineAdView) {

        this.inlineAdView = inlineAdView
        adContainer?.addView(inlineAdView)

        Log.i(TAG, resources.getString(R.string.load_successful))
        Toast.makeText(
            this@InlineRectangleActivity,
            resources.getString(R.string.load_successful), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onLoadFailed(inlineAdView: InlineAdView, errorInfo: ErrorInfo) {

        Log.w(TAG, errorInfo.toString())

        Log.i(TAG, resources.getString(R.string.load_failed))
        Toast.makeText(
            this@InlineRectangleActivity,
            resources.getString(R.string.load_failed),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(inlineAdView: InlineAdView, errorInfo: ErrorInfo) {

        Log.i(TAG, "Inline ad encountered an error")
    }


    override fun onResized(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad resized")
    }


    override fun onExpanded(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad expanded")
    }


    override fun onCollapsed(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad collapsed")
    }


    override fun onClicked(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad clicked")
        // While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
        // Yahoo Audiences, this code is to demonstrate how to capture all
        // clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
        // with Yahoo Audiences, it is important to capture events from all monetization sources.
        YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null)
    }


    override fun onAdLeftApplication(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad caused user to leave application")
    }


    override fun onAdRefreshed(inlineAdView: InlineAdView) {

        Log.i(TAG, "Inline ad refreshed")
    }


    override fun onEvent(inlineAdView: InlineAdView, source: String, eventId: String, arguments: Map<String, Any>?) {

        Log.i(TAG, "Inline ad event occurred")
    }


    //InlineAdView callbacks -- end --


    override fun onDestroy() {

        super.onDestroy()

        destroyAd(inlineAdView)
    }


    private fun destroyAd(inlineAdView: InlineAdView?) {

        inlineAdView?.destroy()
    }


    private fun setupViews() {

        setContentView(R.layout.activity_inline_rectangle)

        adContainer = findViewById(R.id.ad_container)

        val refreshButton = findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener { loadInlineAdView() }
    }


    private fun loadInlineAdView() {

        adContainer?.removeAllViews()
        destroyAd(inlineAdView)

        // Create a new Inline instance using your placement ID and a reference to the View to fill with the Ad
        // from your layout.

        setupAdSize()

        // Create a list of all ad sizes that your inline placement supports.  If it only supports one ad size, simply provide a list of
        // one item.  Note that the InlineAdView returned will have the actual ad size of the returned ad.  If you support multiple ad
        // sizes, size your container appropriately based on the returned ad size.
        val adSizes = ArrayList<AdSize?>()
        adSizes.add(adSize)

        val placementConfig = InlinePlacementConfig(PLACEMENT_ID, null, adSizes)
        val inlineAdView = InlineAdView(this, PLACEMENT_ID, this)
        inlineAdView.load(placementConfig)
    }


    private fun setupAdSize() {

        val width = 300
        val height = 250
        adSize = AdSize(width, height)
    }
}
