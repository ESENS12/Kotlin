package kr.esens

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.online.sdk.common.location.LatLng
import com.tomtom.online.sdk.common.permission.AppPermissionHandler
import com.tomtom.online.sdk.map.*
import com.tomtom.online.sdk.map.model.MapTilesType


class MainActivity : AppCompatActivity(), OnMapReadyCallback, TomtomMapCallback.OnMapLongClickListener {
    val TAG = "MainActivity"
    lateinit var tomMap: TomtomMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLocationPermissions()
        initTomTomServices()
    }

    override fun onMapReady(tomtomMap: TomtomMap) {
        Log.e(TAG,"onMapReady Called!")
        this.tomMap = tomtomMap;

        tomMap.setMyLocationEnabled(true);
        tomMap.addOnMapLongClickListener(this);
        tomMap.getMarkerSettings().setMarkersClustering(true)
    }

    private fun initLocationPermissions() {
        val permissionHandler = AppPermissionHandler(this)
        permissionHandler.addLocationChecker()
        permissionHandler.askForNotGrantedPermissions()
    }


    override fun onMapLongClick(latLng: LatLng) {
        Log.e(TAG,"onMapLongClick called!");
    }

    private fun initTomTomServices() {
        val mapKeys = mapOf(
            ApiKeyType.MAPS_API_KEY to BuildConfig.MAPS_API_KEY
        )
        val mapProperties = MapProperties.Builder()
            .keys(mapKeys)
            .build()
        val mapFragment = MapFragment.newInstance(mapProperties)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.map_fragment, mapFragment)
            .commit()
        mapFragment.getAsyncMap(this)
    }

}