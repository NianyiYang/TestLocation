package com.yny.testlocation

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager

    private var longitudeBest = 0.0
    private var latitudeBest: Double = 0.0
    private var longitudeGPS = 0.0
    private var latitudeGPS: Double = 0.0
    private var longitudeNetwork = 0.0
    private var latitudeNetwork: Double = 0.0

    private lateinit var tvLocation: TextView

    private var log = ""

    private val handler = Handler()

    private val service: ScheduledExecutorService = ScheduledThreadPoolExecutor(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        tvLocation = findViewById(R.id.tv_location) as TextView
        findViewById(R.id.btn_location).setOnClickListener {
            if (!checkLocation()) {
                return@setOnClickListener
            }

            service.scheduleAtFixedRate(mTimerTask, 1000, 1000, TimeUnit.MILLISECONDS)
        }
    }

    private val mTimerTask: TimerTask = object : TimerTask() {
        override fun run() {
            handler.post {
                //            val criteria = Criteria()
//            criteria.accuracy = Criteria.ACCURACY_FINE
//            criteria.isAltitudeRequired = false
//            criteria.isBearingRequired = false
//            criteria.isCostAllowed = true
//            criteria.powerRequirement = Criteria.POWER_LOW
//            val provider = locationManager.getBestProvider(criteria, true)

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    (2 * 60 * 1000).toLong(),
                    10f,
                    locationListenerBest
                )
            }
        }
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled()) showAlert()
        return isLocationEnabled()
    }

    private fun showAlert() {
        val dialog: android.support.v7.app.AlertDialog.Builder =
            android.support.v7.app.AlertDialog.Builder(this)
        dialog.setTitle("请开启定位")
            .setMessage(
                """
            您的定位功能被设置为"关闭".
            请开启定位以使用 App
            """.trimIndent()
            )
            .setPositiveButton("去开启") { paramDialogInterface, paramInt ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("取消") { paramDialogInterface, paramInt ->
                finish()
            }
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private val locationListenerBest: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitudeBest = location.longitude
            latitudeBest = location.latitude
            runOnUiThread {
                log += "longitude : $longitudeBest  latitude : $latitudeBest \n";
                tvLocation.text = log
            }
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private val locationListenerNetwork: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitudeNetwork = location.longitude
            latitudeNetwork = location.latitude
            runOnUiThread {
                log += "longitude : $longitudeBest  latitude : $latitudeBest \n";
                tvLocation.text = log
            }
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    private val locationListenerGPS: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            longitudeGPS = location.longitude
            latitudeGPS = location.latitude
            runOnUiThread {
                log += "longitude : $longitudeBest  latitude : $latitudeBest \n";
                tvLocation.text = log
            }
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}