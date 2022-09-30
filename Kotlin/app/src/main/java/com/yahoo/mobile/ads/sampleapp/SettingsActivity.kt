/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

@file:Suppress("DEPRECATION")

package com.yahoo.mobile.ads.sampleapp

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceFragment
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(android.R.id.content, InterstitialPreferenceFragment()).commit()
    }

    /**
     * This fragment shows notification pref_interstitials only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class InterstitialPreferenceFragment : PreferenceFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }
    }
}
