package com.karebo.teamapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.adapter.jobnumberAdapter
import com.karebo.teamapp.databinding.FragmentAccessStatusBinding
import com.karebo.teamapp.dataclass.CodeListDataClass
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.karebo.teamapp.roomdata.RoomDb
import com.karebo.teamapp.roomdata.mainbody
import com.karebo.teamapp.roomdata.photobody

import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import com.the.firsttask.utils.NetworkUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class AccessStatus : Fragment() {


    private var _binding: FragmentAccessStatusBinding? = null
    private val binding get() = _binding!!
    var adapterSiteStatus: ArrayAdapter<String>? = null
    var list:MutableList<String> = mutableListOf()
    private var adapter: jobnumberAdapter? = null
    var data=meterauditDataModel()



    var  locationn : Location? =null

    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        _binding = FragmentAccessStatusBinding.inflate(
            inflater,container,false)
        val root: View = binding.root




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
                locationn=location
                LoaderHelper.dissmissLoader()

            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                Log.e("Location", "Oops location failed with exception: $exception")
                LoaderHelper.dissmissLoader()
            }












        accessStatus()
        siteStatus()

        loadCompleteJobNumber()

        val args = arguments
        if(args!=null){
            val destination = args!!.getString("data")
            if(destination=="from signature"){
                binding.spAccessStatus.setSelection(1)
            }
        }


        binding.tvAddress.text = ConstantHelper.ADDRESS

        binding.btSubmit.setOnClickListener{

            if(binding.spAccessStatus.selectedItemPosition==2||
                binding.spAccessStatus.selectedItemPosition==3||
                binding.spAccessStatus.selectedItemPosition==4||
                binding.spAccessStatus.selectedItemPosition==5){
                addInModel()
            }



            showConfirmDialog(root)

        }




        binding.btNext.setOnClickListener{
//            val bundle = Bundle()
//            val JsonString: String =
//                GsonParser.gsonParser!!.toJson(data)
//            bundle.putString("data", JsonString)
            if(binding.spSiteStatus.selectedItemPosition==0){
                Toast.makeText(requireContext(),"select site status",Toast.LENGTH_SHORT ).show()
            }
            else{
                addInModel()
                Navigation.findNavController(root).navigate(
                    R.id.action_nav_accessstatus_to_nav_meterlocation
                )
            }

        }
        binding.spAccessStatus.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("TAG", "onItemSelected: "+binding.spAccessStatus.selectedItemPosition )
                if(binding.spAccessStatus.selectedItemPosition ==0){
                    binding.btNext.visibility=View.INVISIBLE
                    binding.btSubmit.visibility=View.INVISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE
                  Toast.makeText(requireContext(),"select access Status",Toast.LENGTH_LONG).show()
                }
                else if(binding.spAccessStatus.selectedItemPosition ==1){
                    binding.btNext.visibility=View.VISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE

                    var jobnumber=SharedPreferenceHelper.getInstance(requireContext()).getJobNumber().toInt()
                    if(jobnumber>0){
                        binding.btSubmit.visibility=View.VISIBLE
                    }

                }

                else if(binding.spAccessStatus.selectedItemPosition ==3 ){
                    binding.btSubmit.visibility=View.VISIBLE
                    binding.btNext.visibility=View.INVISIBLE
                    binding.etName.visibility=View.INVISIBLE
                    binding.etNumber.visibility=View.INVISIBLE
                }

                else if(binding.spAccessStatus.selectedItemPosition ==2 ||
                    binding.spAccessStatus.selectedItemPosition ==4 ||
                    binding.spAccessStatus.selectedItemPosition ==5
                        ){
                    binding.btNext.visibility=View.INVISIBLE
                    binding.btSubmit.visibility=View.VISIBLE
                    binding.etName.visibility=View.VISIBLE
                    binding.etNumber.visibility=View.VISIBLE
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    return root

    }







    private fun requestlocationPermission() {

        when {
            hasPermissions2(requireContext(), *permission) -> {
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
    private fun hasPermissions2(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    private fun locationPermission() {
        locationPermissionLauncher.launch(permission)
    }


    fun addInModel() {

         var Access = JSONObject()
         var Location = JSONObject()
         var ContactInfo = JSONObject()
         var Picture = JSONObject()
         val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
         val formattedDate = sdf.format(Date())
         Log.e("TAG", "addInModel:timestemp "+formattedDate, )
         Access.put("Timestamp",formattedDate)

         Location.put("Latitude",locationn?.latitude)
         Location.put("Longitude",locationn?.longitude)
         Location.put("Accuracy",locationn?.accuracy)

         Location.put("Timestamp",formattedDate)
         Access.put("Location",Location)



         Access.put("Access",findAccess())
         Access.put("JobCardId",ConstantHelper.currentSelectd.jobCardId)
         Access.put("Team",ConstantHelper.currentSelectd.team)


         ContactInfo.put("Full Name",binding.etName.text.toString())
         ContactInfo.put("Mobile Number",binding.etNumber.text.toString())
         Access.put("ContactInfo",ContactInfo)

         Picture.put("Key",0)
         Picture.put("Value", ConstantHelper.PropertyPictureUUID)
         Access.put("Picture",Picture)


         ConstantHelper.submitMeterDataJSON.put("Access",Access)


         Log.e("json at access", ConstantHelper.submitMeterDataJSON.toString())

     }

    fun findAccess():Int{
        var i=0
        if(binding.spAccessStatus.selectedItemPosition==1){
            i=3
        }else if(binding.spAccessStatus.selectedItemPosition==2){
            i=4
        }else if(binding.spAccessStatus.selectedItemPosition==3){
            i=0
        }else if(binding.spAccessStatus.selectedItemPosition==4){
            i=1
        }
        else if(binding.spAccessStatus.selectedItemPosition==5){
            i=2
        }

        return  i
    }

    fun loadCompleteJobNumber() {



        val keys = ConstantHelper.Meters.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            list.add(key)

        }

         adapter = jobnumberAdapter(
             list,
             requireActivity()
         )
         binding.rvJobnum.adapter = adapter
         binding.rvJobnum.adapter?.notifyDataSetChanged()
    }


    fun siteStatus(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        Log.e("TAG", "Question: " + data.Toolbox.toString())
        var finalData : MutableList <String> = data.SiteStatus as MutableList<String>
        finalData.add(0,"Site Status")
        adapterSiteStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
        adapterSiteStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSiteStatus.adapter = adapterSiteStatus
    }



    fun accessStatus(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        Log.e("TAG", "Question: " + data.Toolbox.toString())

        var finalData : MutableList <String> = data.SiteAccess as MutableList<String>
        finalData.add(0,"Site Access")
        adapterSiteStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
        adapterSiteStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spAccessStatus.adapter = adapterSiteStatus
    }




fun showConfirmDialog(root:View){
    var alert: AlertDialog? = null
    val builder = AlertDialog.Builder(requireContext(),)
    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v = inflater.inflate(R.layout.dialog_submitmeter, null)
    builder.setView(v)
    builder.setCancelable(false)
    alert = builder.create()

    val okButton = v.findViewById<Button>(R.id.bt_submit)
    val cancelButton = v.findViewById<Button>(R.id.bt_cancel)
    val title = v.findViewById<TextView>(R.id.tv_title_submit_dialog)

    title.setText("Do You want to submit Job Card?")

    okButton.setOnClickListener{
        SharedPreferenceHelper.getInstance(requireContext()).setJobNumber("0")
        alert.dismiss()
//        submitMeter(root)
        addAllPhoto(root)
    }

    cancelButton.setOnClickListener{
        alert.dismiss()
    }

    alert?.show()


}


    fun addAllPhoto(root: View){

        LoaderHelper.showLoader(requireContext())

        if(NetworkUtils.isConnected==false){

            val photobodyDao=RoomDb.getAppDatabase((requireContext()))?.photobodydao()

            ConstantHelper.photoList.forEach {

//            requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
                photobodyDao?.addphotobody(photobody(it.uuid,it.bodyy))
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
//            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
            }

            LoaderHelper.dissmissLoader()
            activity?.runOnUiThread {
                submitMeter(root)

            }

        }else{

            val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)

        val requests =  mutableListOf<Observable<ResponseBody>>()

            ConstantHelper.photoList.forEach {

                requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
                Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
//               Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
            }




        Observable.merge(requests)
            .take(requests.size.toLong())
            // executed when the channel is closed or disposed
            .doFinally {
                Log.e("TAG", "addAllPhoto final: ", )

                ConstantHelper. photoList = mutableListOf()
                LoaderHelper.dissmissLoader()
                activity?.runOnUiThread {
                    submitMeter(root)

                }
//
            }
            .subscribeOn(Schedulers.io())
            // it's a question if you want to observe these on main thread, depends on context of your application
            .subscribe(
                { ResponseBody ->
                    // here you get both the deviceId and the responseBody
                    Log.e("TAG", "addAllPhoto responce: "+ResponseBody.string(), )



                    if (ResponseBody == null ) {
                        Log.e("TAG", "addAllPhoto responce: "+ ResponseBody?.string(), )

                        // request for this deviceId failed, handle it
                    }
                },
                { error ->
                    Log.e("TAG", "Throwable: " + error)
                }
            )


        }

////        val client = ApiClient()
////        val api = client.getClient()?.create(Api::class.java)
////
////
////        val requests =  mutableListOf<Observable<ResponseBody>>()
//
//        val photobodyDao=RoomDb.getAppDatabase((requireContext()))?.photobodydao()
//
//        ConstantHelper.photoList.forEach {
//
////            requests.add( api?.addPhoto64(it.uuid,it.bodyy)!!)
//            photobodyDao?.addphotobody(photobody(it.uuid,it.bodyy))
//            Log.e("TAG", "addAllPhoto uuid: ${it.uuid}", )
////            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
//        }
//
//        LoaderHelper.dissmissLoader()
//                activity?.runOnUiThread {
//                    submitMeter(root)
//
//                }

        //Log.e("TAG", "requests: "+requests.toString() )

//



    }




    fun submitMeter(root:View){


        LoaderHelper.showLoader(requireContext())

       // Log.e("TAG", "submitMeter: "+ConstantHelper.submitMeterDataJSON.toString() )

        if(NetworkUtils.isConnected==false){
            val mainbodyDao=RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
            mainbodyDao?.addMainBody(mainbody(ConstantHelper.currentSelectd.jobCardId.toString(),ConstantHelper.submitMeterDataJSON.toString()))


            Toast.makeText(requireContext(),"successFull Added offline", Toast.LENGTH_SHORT)
                .show()
            Log.e("TAG", "submitmeter: offline ", )



            setJobId()
            LoaderHelper.dissmissLoader()
            ConstantHelper. submitMeterDataJSON = JSONObject()
            ConstantHelper. Meters = JSONObject()
            ConstantHelper. meterModelJson = JSONObject()
            ConstantHelper. Components = JSONObject()
            ConstantHelper. Feedback = JSONObject()
            ConstantHelper. photoList = mutableListOf()

            Navigation.findNavController(root).navigate(
                R.id.action_nav_accessstatus_to_nav_meteraudit
            )

        }
        else{
            val client = ApiClient()
            val api = client.getClient()?.create(Api::class.java)
            val call = api?.submitMeter2(ConstantHelper.submitMeterDataJSON.toString())
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if(response.isSuccessful){
                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of Photo " + statuscode)

                    if(statuscode==200){


                        setJobId()
                        LoaderHelper.dissmissLoader()
                        ConstantHelper. submitMeterDataJSON = JSONObject()
                        ConstantHelper. Meters = JSONObject()
                        ConstantHelper. meterModelJson = JSONObject()
                        ConstantHelper. Components = JSONObject()
                        ConstantHelper. Feedback = JSONObject()
                        ConstantHelper. photoList = mutableListOf()

                        Navigation.findNavController(root).navigate(
                            R.id.action_nav_accessstatus_to_nav_meteraudit
                        )

                        Toast.makeText(requireContext(),"successFull Added", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("TAG", "submitmeter: "+response.body()?.string(), )

                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Log.e("TAG", "submitmeter2: "+response.body()?.string(), )
                        Toast.makeText(requireContext(),"some error occured in api submeter"+ response.body()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
//                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
//                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
                    LoaderHelper.dissmissLoader()
                    Log.e("TAG", "submitmeter3: "+response.errorBody()?.string(), )
                   Toast.makeText(requireContext(),
                        response.code().toString()
                                +" "+response.message(), Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Log.e("TAG", "onFailure: "+t.localizedMessage, )
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                    .show()
            }

        })

        }

//

    }




    fun setJobId(){
        var completeJobNumber=SharedPreferenceHelper.getInstance(requireContext()).getCompleteJobId()

        var completeJobNumberList: Array<String>?=null
        if(completeJobNumber!="null"){
            completeJobNumberList= GsonParser.gsonParser!!.fromJson(
                completeJobNumber,
                Array<String>::class.java
            )
        }

        var list: MutableList<String> = mutableListOf()
        if(completeJobNumberList!=null){
            list= completeJobNumberList.toMutableList()
        }
        Log.e("TAG", "setJobId: "+list.toString(), )
        list.add(ConstantHelper.currentSelectd.jobCardId!!.toString())


        val JsonString: String = GsonParser.gsonParser!!.toJson(list)
        SharedPreferenceHelper.getInstance(requireContext()).setCompleteJobId(JsonString)



        var newList:MutableList<meterauditDataModel> = mutableListOf()

        ConstantHelper.list.forEach {
            if(!list.contains(it.jobCardId)){

                newList.add(it)
            }
        }

        ConstantHelper.list=newList

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
                    R.id.action_nav_accessstatus_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_accessstatus_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }
}