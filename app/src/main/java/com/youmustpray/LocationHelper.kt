package com.youmustpray

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper

object LocationHelper {
    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context, callback: (Location?) -> Unit) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val bestLocation = listOfNotNull(gpsLocation, networkLocation)
            .minByOrNull { it.accuracy }

        if (bestLocation != null) {
            callback(bestLocation)
            return
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback(location)
                manager.removeUpdates(this)
            }
        }

        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.getMainLooper())
        } else {
            callback(null)
        }
    }
}
