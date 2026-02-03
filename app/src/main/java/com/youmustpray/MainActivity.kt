package com.youmustpray

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.youmustpray.databinding.ActivityMainBinding
import java.time.ZoneId

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        loadPrayerTimes()
    }

    private val matCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.statusText.text = "Mat verified. Lock would release now."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshButton.setOnClickListener { requestPermissionsAndLoad() }
        binding.lockButton.setOnClickListener { LockActivity.start(this) }
        binding.unlockButton.setOnClickListener {
            matCaptureLauncher.launch(Intent(this, MatCaptureActivity::class.java))
        }

        requestPermissionsAndLoad()
    }

    private fun requestPermissionsAndLoad() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            )
        )
    }

    private fun loadPrayerTimes() {
        if (!hasLocationPermission()) {
            renderPrayerTimes(DefaultLocation.AMMAN_LAT, DefaultLocation.AMMAN_LON, "Location permission denied. Using Amman.")
            return
        }

        LocationHelper.fetchLocation(this) { location ->
            val latitude = location?.latitude ?: DefaultLocation.AMMAN_LAT
            val longitude = location?.longitude ?: DefaultLocation.AMMAN_LON

            renderPrayerTimes(latitude, longitude, "Ready for prayer lock.")
        }
    }

    private fun renderPrayerTimes(latitude: Double, longitude: Double, status: String) {
        val result = PrayerSchedule.buildPrayerTimes(
            latitude = latitude,
            longitude = longitude,
            zoneId = ZoneId.systemDefault()
        )

        binding.locationText.text =
            "Location: %.4f, %.4f (Jordan)".format(latitude, longitude)
        binding.nextPrayerText.text = result.nextPrayer
        binding.prayerTimesText.text = result.formattedSchedule
        binding.statusText.text = status
    }

    private fun hasLocationPermission(): Boolean {
        val fine = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
}
