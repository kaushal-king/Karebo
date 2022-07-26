package com.karebo.teamapp.meteraudit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.karebo.teamapp.R
import com.karebo.teamapp.databinding.FragmentMapfragmentBinding
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.the.firsttask.utils.ConstantHelper
import java.util.*


class mapfragment : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener {



    private var _binding: FragmentMapfragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    var latlongBuilder = LatLngBounds.Builder()
    var root :View?=null
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

         root = binding.root
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
              )?.apply {
                tag=it.jobCardId
                showInfoWindow()
            }

            latlongBuilder.include(LatLng(it.latitude as Double, it.longitude as Double));

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

        Log.e("TAG", "onMarkerClick: "+p0.tag, )

        ConstantHelper.list.forEach {
            if(it.jobCardId==p0.tag){
                ConstantHelper.currentSelectd=it
            }
        }

        var address=findAddress(    ConstantHelper.currentSelectd)
        ConstantHelper.ADDRESS=address
        Navigation.findNavController(root!!).navigate(
            R.id.action_nav_meteraudit_to_nav_auditphoto
        )


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
        val bounds: LatLngBounds = latlongBuilder.build()
        val paddingFromEdgeAsPX = 100
        val cu = CameraUpdateFactory.newLatLngBounds(bounds,paddingFromEdgeAsPX)
        mMap.animateCamera(cu)



//        mMap.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                LatLng(
//                    location?.latitude ?: 0.0,
//                    location?.longitude ?: 0.0
//                ), 18.0f
//            )
//        )

    }



    fun findAddress(listItem: meterauditDataModel):String
    {
        var addresses: List<Address?> = listOf()
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var returnAddress:String

        val item = listItem
        try {
            addresses = geocoder.getFromLocation(item.latitude as Double, item.longitude as Double, 1);

        }catch (e:Exception){

        }
        if(addresses != null && !addresses.isEmpty()){
            var address=addresses[0]?.getAddressLine(0)
            address=address?.replace(", South Africa","")
            address=address?.replace("South Africa","")
            returnAddress=address!!
        }
        else{
            returnAddress= "No Address on "+ "("+item.latitude+","+item.longitude+")";
        }

        return returnAddress
    }

}