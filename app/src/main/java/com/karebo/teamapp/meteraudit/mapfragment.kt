package com.karebo.teamapp.meteraudit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.karebo.teamapp.R
import com.karebo.teamapp.databinding.FragmentMapfragmentBinding
import com.the.firsttask.utils.ConstantHelper


class mapfragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener {



    private var _binding: FragmentMapfragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (!permissions.containsValue(false)) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap.isMyLocationEnabled = true
                    }
                }
            }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapfragmentBinding.inflate(
            inflater,container,false)

        val root: View = binding.root
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                updateMapLocation(location)
//            }


        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestlocationPermission()
            }
        }

        ConstantHelper.list?.map {

            mMap.addMarker(
                  MarkerOptions().position(LatLng(it.latitude as Double, it.longitude as Double))
//                      .title(it.cardType) // below line is use to add custom marker on our map.

              )?.showInfoWindow()
        }

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(ConstantHelper.list[0].Coordinates[0],
//            ConstantHelper.list[0].Coordinates[1])))
        if(ConstantHelper.list.isNotEmpty()){
            updateMapLocationLatLong(LatLng(
                ConstantHelper.list!![0].latitude as Double,
                ConstantHelper.list!![0].longitude as Double))
        }


        mMap.setOnMarkerClickListener(this)
    }

    private fun requestlocationPermission() {

        when {
            hasPermissions(requireContext(), *permission) -> {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                }
            }
            else -> {
                Toast.makeText(requireContext(), " Allow the  Permission", Toast.LENGTH_LONG).show()
                locationPermission()
            }
        }

    }
    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {


        locationPermissionLauncher.launch(permission)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    private fun updateMapLocation(location: Location?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                ), 18.0f
            )
        )

    }


    private fun updateMapLocationLatLong(location: LatLng) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                ), 18.0f
            )
        )

    }

}