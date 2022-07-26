package com.karebo.teamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.karebo.teamapp.databinding.FragmentMeterLocationBinding
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MeterLocation : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener {
    private var _binding: FragmentMeterLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    lateinit var latlong: meterauditDataModel
    lateinit var finalLatLng: LatLng
    var outsideMap=false

    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    var polygonList: MutableList<LatLng> = mutableListOf()
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);

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
        _binding = FragmentMeterLocationBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        latlong=ConstantHelper.currentSelectd
        binding.tvAddress.text =ConstantHelper.ADDRESS




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        binding.btNext.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(latlong)
//            bundle.putString("data", JsonString)
            addInModel()
            Navigation.findNavController(root).navigate(
                R.id.action_nav_meterlocation_to_nav_barcodescan
                )
        }

        //scrollFunction()






        return root
    }

     fun addInModel() {

         ConstantHelper.meterModelJson.put("Latitude",finalLatLng.latitude)
         ConstantHelper.meterModelJson.put("Longitude",finalLatLng.longitude)
         ConstantHelper.meterModelJson.put("Id",ConstantHelper.currentSelectd.jobCardId)


         var MeterLocation = JSONObject()
         var Location = JSONObject()
         Location.put("Latitude",finalLatLng.latitude)
         Location.put("Longitude",finalLatLng.longitude)
         Location.put("Accuracy",0)
         var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
         var formattedDate = sdf.format(Date())
         Location.put("Timestamp",formattedDate)

         MeterLocation.put("Location",Location)

         if(outsideMap==false){
             MeterLocation.put("Type",0)
         }
         else{
             if(binding.spMapMarker.selectedItemPosition==0){
                 MeterLocation.put("Type",1)
             }else{
                 MeterLocation.put("Type",2)
             }
         }

         MeterLocation.put("Description",binding.etMapDescription.text.toString())
         ConstantHelper.Components.put("MeterLocation",MeterLocation)
//         ConstantHelper.meterModelJson.put("Components",ConstantHelper.Components)

         Log.e("json at location", ConstantHelper.Components.toString())
         Log.e("json at location", ConstantHelper.meterModelJson.toString())


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                updateMapLocation(location)
//            }
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

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

        finalLatLng= LatLng(latlong!!.latitude as Double,latlong!!.longitude as Double)
        mMap.addMarker(
            MarkerOptions().position(LatLng(latlong!!.latitude as Double, latlong!!.longitude as Double))
                .title(latlong!!.latitude.toString()+" "+latlong!!.longitude)
                .draggable(true)// below line is use to add custom marker on our map.

        )?.showInfoWindow()

//
        var polyOption=PolylineOptions()

        for (coordinate in latlong!!.vertices!!) {

            Log.e("TAG", "onMapReady: "+coordinate )
            var splitt : List<String> =coordinate!!.toString().split(" ")
            Log.e("TAG", "onMapReady: "+splitt )
           var lat=splitt[0].toDouble()
           var long= splitt[1].toDouble()
            Log.e("TAG", "onMapReady: " + lat + "   " + long)
            polygonList.add(LatLng(long,lat))
            polyOption.add(LatLng(long,lat))

        }

        val color: Int = Color.argb(255, 236, 4,4 )
        polyOption.clickable(false)
        polyOption.color(resources.getColor(R.color.red))



        val polyline1 = googleMap.addPolyline(
            polyOption
        )


       updateMapLocationLatLong( LatLng(latlong!!.latitude as Double, latlong!!.longitude as Double) )

        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)
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

    override fun onMarkerDrag(arg0: Marker) {
        Log.e("TAG", "onMarkerDrag...")

    }

    override fun onMarkerDragEnd(arg0: Marker) {
        Log.e(
            "TAG",
            "onMarkerDragStart..." + arg0.position.latitude + "..." + arg0.position.longitude
        )
        finalLatLng= LatLng(arg0.position.latitude,arg0.position.longitude)
        if (!PolyUtil.containsLocation(LatLng(arg0.position.latitude,arg0.position.longitude), polygonList, false)) {
            showField()
        }
        else{
            hideField()
        }

        }
    override fun onMarkerDragStart(arg0: Marker) {
        Log.e(
                    "TAG",
                    "onMarkerDragEnd..." + arg0.position.latitude + "..." + arg0.position.longitude
                )
    }


    fun showField(){
        outsideMap=true
        binding.linearLayout.visibility=View.VISIBLE

        binding.etMapDescription.visibility=View.VISIBLE
    }

    fun hideField(){
        outsideMap=false
        binding.linearLayout.visibility=View.GONE

        binding.etMapDescription.visibility=View.GONE
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.drawer, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_jobcard -> {
                ConstantHelper. submitMeterDataJSON = JSONObject()
                ConstantHelper. Meters = JSONObject()
                ConstantHelper. meterModelJson = JSONObject()
                ConstantHelper. Components = JSONObject()
                ConstantHelper. Feedback = JSONObject()
                ConstantHelper. photoList = mutableListOf()
                ConstantHelper.Duration = JSONObject()

                ConstantHelper.SERIAL =  ""
                ConstantHelper. PropertyPictureUUID=""
                ConstantHelper. ZeroTokenPictureUUID=""
                ConstantHelper. TamperedWiresUUID=""
                ConstantHelper. TamperedWires2UUID=""
                ConstantHelper. TamperedWires3UUID=""
                ConstantHelper. KRNPictureUUID=""
                ConstantHelper. Last5TokenScreenshotUUID=""
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_meterlocation_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_meterlocation_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }


}