/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yahoo.ads.ErrorInfo
import com.yahoo.ads.interstitialplacement.InterstitialAd
import com.yahoo.ads.interstitialplacement.InterstitialPlacementConfig
import com.yahoo.audiences.YahooAudiences
import com.yahoo.audiences.YahooAudiencesEvent

private val TAG = InterstitialActivity::class.java.simpleName
private const val PLACEMENT_ID = "240383"

class InterstitialActivity : AppCompatActivity(), InterstitialAd.InterstitialAdListener {

    private var interstitialAd: InterstitialAd? = null
    private var loadButton: View? = null
    private var showButton: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interstitial)

        loadButton = findViewById(R.id.load)
        loadButton?.setOnClickListener { loadInterstitialAd() }

        showButton = findViewById(R.id.show)
        showButton?.setOnClickListener {
            interstitialAd?.let {
                it.show(this@InterstitialActivity)

                showButton?.isEnabled = false
                loadButton?.isEnabled = true
            }
        }

        showButton?.isEnabled = false

        // Go ahead and load an ad
        loadInterstitialAd()
    }


    // InterstitialAd.InterstitialAdListener callbacks


    override fun onLoaded(interstitialAd: InterstitialAd) {
        this.interstitialAd = interstitialAd

        Log.i(TAG, resources.getString(R.string.load_successful))

        Toast.makeText(
            this@InterstitialActivity,
            resources.getString(R.string.load_successful), Toast.LENGTH_SHORT
        ).show()

        showButton?.isEnabled = true
    }


    override fun onLoadFailed(interstitialAd: InterstitialAd, errorInfo: ErrorInfo) {
        Log.w(TAG, errorInfo.toString())

        Toast.makeText(
            this@InterstitialActivity,
            resources.getString(R.string.load_failed), Toast.LENGTH_SHORT
        ).show()

        loadButton?.isEnabled = true
    }

    override fun onError(interstitialAd: InterstitialAd, errorInfo: ErrorInfo) {

        Log.i(TAG, "Interstitial ad encountered an error")
    }


    override fun onShown(interstitialAd: InterstitialAd) {

        Log.i(TAG, "Interstitial ad shown")
    }


    override fun onClosed(interstitialAd: InterstitialAd) {

        Log.i(TAG, "Interstitial ad closed")
    }


    override fun onClicked(interstitialAd: InterstitialAd) {

        Log.i(TAG, "Interstitial ad clicked")
        // While the Yahoo Audiences Plugin automatically sends Yahoo Ads SDK (YAS) click events to
        // Yahoo Audiences, this code is to demonstrate how to capture all
        // clicks if mediating to YAS through another SDK like AppLovin. For best possible experience
        // with Yahoo Audiences, it is important to capture events from all monetization sources.
        YahooAudiences.logEvent(YahooAudiencesEvent.AD_CLICK, null)
    }


    override fun onAdLeftApplication(interstitialAd: InterstitialAd) {

        Log.i(TAG, "Interstitial ad caused user to leave application")
    }


    override fun onEvent(interstitialAd: InterstitialAd, source: String, eventId: String, arguments: Map<String, Any>?) {

        Log.i(TAG, "Interstitial ad event occurred")

    }


    private fun loadInterstitialAd() {

        runOnUiThread { loadButton?.isEnabled = false }

        // Set optional request metadata.  Setting metadata improves ad selection.

        // val requestMetadata =
        //      RequestMetadata.Builder().setAppData(<your app data>).putExtra("keywords", <your keywords>)
        //      .putExtra("customTargeting", <your custom targeting info>).setSupportedOrientations(<your supported orientations>).build()

        // val placementConfig = InterstitialPlacementConfig(PLACEMENT_ID, requestMetadata)

        val placementConfig = InterstitialPlacementConfig(PLACEMENT_ID, null)
        val interstitialAd = InterstitialAd(this, PLACEMENT_ID, this)
        interstitialAd.load(placementConfig)
    }
}
