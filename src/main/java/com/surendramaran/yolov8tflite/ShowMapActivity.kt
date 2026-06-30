package com.surendramaran.yolov8tflite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mDatabase = FirebaseDatabase.getInstance().getReference("pothole_locations")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mediaPlayer: MediaPlayer? = null
    private var isSoundCooldown = false
    private val handler = Handler(Looper.getMainLooper())
    private var potholeLocations = mutableListOf<LatLng>()

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1f
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { currentLocation ->
                checkNearbyPotholes(currentLocation)
                updateMapCamera(currentLocation)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mediaPlayer = MediaPlayer.create(this, R.raw.warning)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (checkLocationPermission()) {
            startLocationUpdates()
            mMap!!.isMyLocationEnabled = true
            loadPotholeLocations()
            getInitialLocation()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            false
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getInitialLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun loadPotholeLocations() {
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                potholeLocations.clear()
                for (snapshot in dataSnapshot.children) {
                    val latitude = snapshot.child("latitude").getValue(Double::class.java)!!
                    val longitude = snapshot.child("longitude").getValue(Double::class.java)!!
                    val location = LatLng(latitude, longitude)
                    potholeLocations.add(location)
                    mMap?.addMarker(MarkerOptions().position(location).title("Pothole"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun checkNearbyPotholes(currentLocation: Location) {
        if (isSoundCooldown) return

        for (pothole in potholeLocations) {
            val potholeLocation = Location("pothole").apply {
                latitude = pothole.latitude
                longitude = pothole.longitude
            }

            val distance = currentLocation.distanceTo(potholeLocation)
            if (distance <= 10) { // 10 meters threshold
                playWarningSound()
                startCooldown()
                break
            }
        }
    }

    private fun playWarningSound() {
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.start()
        }
    }

    private fun startCooldown() {
        isSoundCooldown = true
        handler.postDelayed({ isSoundCooldown = false }, 10000) // 10 seconds cooldown
    }

    private fun updateMapCamera(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }
    override fun onBackPressed() {
        // Navigate to WelcomeActivity
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Optional: Bring existing instance to the front
        startActivity(intent)
        overridePendingTransition(0, 0) // Disable animations for faster transition
        super.onBackPressed() // Call super after starting the new activity
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}