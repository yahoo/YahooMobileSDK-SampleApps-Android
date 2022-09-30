/*
 * Copyright Yahoo, Licensed under the terms of the Apache 2.0 license . See LICENSE file in project root for terms.
 */

package com.yahoo.mobile.ads.sampleapp

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {


    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        setContentView(R.layout.activity_main)

        // Set any known data about the user
        // Set the AppInfo to be passed along on ad requests
        // Ensure SiteId is set using your site ID


        val adFormatListView = findViewById<ListView>(R.id.ad_format_list)
        adFormatListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.ad_formats))

        adFormatListView.onItemClickListener = OnItemClickListener { _, _, _, id ->
            val intent: Intent = when (id) {
                0L -> Intent(this@MainActivity, InlineBannerActivity::class.java)
                1L -> Intent(this@MainActivity, InlineRectangleActivity::class.java)
                2L -> Intent(this@MainActivity, InterstitialActivity::class.java)
                3L -> Intent(this@MainActivity, NativeActivity::class.java)
                else -> Intent(this@MainActivity, InlineBannerActivity::class.java)
            }

            startActivity(intent)

        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main_toolbar)

        toolbar.setOnMenuItemClickListener(
            object : Toolbar.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {

                    if (R.id.settings == menuItem.itemId) {
                        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                        this@MainActivity.startActivity(intent)

                        return true
                    }

                    return false
                }
            })
    }

}
