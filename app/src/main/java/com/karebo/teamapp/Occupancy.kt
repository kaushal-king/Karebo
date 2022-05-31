package com.karebo.teamapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo.teamapp.databinding.FragmentOccupancyBinding
import com.karebo.teamapp.dataclass.CodeListDataClass
import com.karebo.teamapp.dataclass.MeterDataModel
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import org.json.JSONObject
import java.util.*


class Occupancy : Fragment() {


    private var _binding: FragmentOccupancyBinding? = null
    private val binding get() = _binding!!
    var jsonData: MeterDataModel? =null
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
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
//                        mMap.isMyLocationEnabled = true
                    }
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOccupancyBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
        requestlocationPermission()
        setApiData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestlocationPermission()
            }
        }
        Log.e("TAG", "location: ", )
        LoaderHelper.showLoader(requireContext())
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
            Log.e("Location", "location is found: $location")
                findAddress(location)


        }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                Log.e("Location", "Oops location failed with exception: $exception")
                LoaderHelper.dissmissLoader()
            }





        binding.btNext.setOnClickListener{
            addInModel()
            Navigation.findNavController(root).navigate(
                R.id.action_nav_occupancy_to_nav_customerfeedback,

                )
        }
        return root
    }

     fun addInModel() {
         var Occupancy = JSONObject()
         var Attributes = JSONObject()
         var Address = JSONObject()


         Occupancy.put("IsTenant",binding.swIsTenant.isChecked)
         Occupancy.put("FirstName",binding.etFirstName.text.toString())
         Occupancy.put("Surname",binding.etSurName.text.toString())
         Occupancy.put("IDNumber",binding.etIdPassportNumber.text.toString())
         Occupancy.put("Mobile",binding.etCellNo.text.toString())
         Occupancy.put("Email",binding.etEmail.text.toString())
         Occupancy.put("MunicipalAccount",binding.etMunicipalityAccNo.text.toString())

         Attributes.put("Solar Panels",binding.swIsSolarPannel.isChecked)
         Attributes.put("Solar Water Heater",binding.swIsSolarWaterHeater.isChecked)
         Occupancy.put("Attributes",Attributes)

         Address.put("Unit Number",binding.etUnitNo.text.toString())
         Address.put("Complex Name",binding.etCmpxName.text.toString())
         Address.put("Street Number",binding.etStreetAddress.text.toString())
         Address.put("Street Name",binding.etStreetAddress.text.toString())
         Address.put("Suburb Name",binding.etSuburbName.text.toString())
         Address.put("Municipality",jsonData?.Municipality.toString())
         Occupancy.put("Address",Address)


         ConstantHelper.Components.put("Occupancy",Occupancy)
         ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
         Log.e("json at Occupancy", ConstantHelper.meterModelJson.toString(), )
    }


    private fun requestlocationPermission() {

        when {
            hasPermissions(requireContext(), *permission) -> {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

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


    fun findAddress(location: Location):String
    {
        var addresses: List<Address?>
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var returnAddress:String

        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        if(addresses != null && !addresses.isEmpty()){
            var address=addresses[0]?.getAddressLine(0)
            address=address?.replace(", South Africa","")
            address=address?.replace("South Africa","")
            returnAddress=address!!
            val addressPart = address.split(",")
            binding.etStreetAddress.setText(addressPart[0]+","+addressPart[1])
            binding.etPostalCode.setText(addresses[0]?.postalCode)
            binding.etSuburbName.setText(addressPart[2])
        }
        else{
            returnAddress= "No Address on "+ "("+location.latitude+","+location.longitude+")";
        }
        LoaderHelper.dissmissLoader()
        return returnAddress

    }




    fun setApiData() {

        var data= SharedPreferenceHelper.getInstance(requireContext()).getMeterData()
        var jsonData=  GsonParser.gsonParser!!.fromJson(data, MeterDataModel::class.java)


        if(jsonData!=null){
            //surname,municiparty
            binding.etSurName.setText(jsonData.CustomerName)
            binding.etMunicipalityAccNo.setText(jsonData.AccountNumber)
        }





    }

}