package com.example.mysmart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    private var mLatitudeLabel: String? = null
    private var mLongitudeLabel: String? = null
    private lateinit var database:FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        database= FirebaseDatabase.getInstance()
//        reference=database.getReference("users")
        database=FirebaseDatabase.getInstance()

}

    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLastLocation = task.result
                        Toast.makeText(this@MainActivity, "Latitude"+(mLastLocation )!!.latitude.toString()+":"+"Longitude"+(mLastLocation )!!.longitude, Toast.LENGTH_LONG).show()

                    } else {
                        Log.w(TAG, "getLastLocation:exception", task.exception)
                        showMessage("Plesse on the location ")
                    }
                }
    }

    private fun showMessage(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }
    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {

        Toast.makeText(this@MainActivity, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)


        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")



        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                // Permission denied.



            }
        }
    }

    companion object {

        private val TAG = "LocationProvider"

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }




    fun showtoast(view: View) {
        val text: TextView = findViewById(R.id.textView) as TextView
        val locationSearch:EditText = findViewById<EditText>(R.id.editTextTextPersonName)
        lateinit var location: String
        location = locationSearch.text.toString()
        var addressList: List<Address>? = null
        if (location == null || location == "") {
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else {
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList!![0]
            if (address!=null && mLastLocation!=null){
                var latitude=address.latitude
                var longitude=address.longitude
                var currLatitude=(mLastLocation )!!.latitude
                var currLongitude=(mLastLocation )!!.longitude
                reference=database.getReference("location")
                reference.setValue(UpdateLocation(latitude,longitude))
                reference=database.getReference("address")
                reference.setValue(UpdateLocation(currLatitude,currLongitude))
            }
            text.setText("latitud:"+address.latitude.toString()+", long:"+ address.longitude)
        }


    }
    fun showCamera(view: View) {
        reference=database.getReference("switchmode")
        reference.setValue(1)
//        reference.setValue(UpdateLocation(34,34))
    }
    fun showMap(view: View) {
        reference=database.getReference("switchmode")
        reference.setValue(2)
    }


}