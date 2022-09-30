/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp

import android.app.Application
import android.util.Log
import com.yahoo.ads.YASAds

const val SITE_ID = "8a809418014d4dba274de5017840037f"

@Suppress("UNUSED")
class SampleAppApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        // Enable debug logging
        YASAds.setLogLevel(Log.DEBUG)

        // Required for all integrations
        YASAds.initialize(this, SITE_ID)
    }
}
