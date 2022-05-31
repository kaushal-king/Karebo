package com.karebo.teamapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.FragmentTripTestBinding
import com.karebo.teamapp.dataclass.photoUploadDataClass
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.utils.ConstantHelper
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class TripTest : Fragment() {

    private var _binding: FragmentTripTestBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
    private var PhototampringFile: File? = null
    private var PhototampringFile2: File? = null
    private var PhototampringFile3: File? = null
    var temperCount=0

    var  locationn : Location? =null

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
        _binding = FragmentTripTestBinding.inflate(
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
        Log.e("TAG", "location: ")
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





        hideEveryThing()

        binding.swMajorAppliance.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){

                showEcerything()
            }else{
                hideEveryThing()
            }
        })

        binding.swApplianceOff.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.swTrip.post(Runnable { binding.swTrip.fullScroll(ScrollView.FOCUS_DOWN) })
//                binding.swTrip.fullScroll(View.FOCUS_UP)
                binding.btNext.visibility=View.VISIBLE
            }else{

                binding.btNext.visibility=View.GONE
            }
        })


        binding.swTampering.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){

                showTampringBt()
            }else{
                hideTampring()
            }
        })





        binding.btImgTampring.setOnClickListener{
            temperCount=1
            selectImageType(requireContext())
        }

        binding.btImgTampring2.setOnClickListener{
            temperCount=2
            selectImageType(requireContext())
        }



        binding.btImgTampring3.setOnClickListener{
            temperCount=3
            selectImageType(requireContext())
        }



        binding.swReplaceSeals.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                showReplace()
            }else{
                hideReplace()
            }
        })


        binding.btNext.setOnClickListener{
            addInModel()
            Navigation.findNavController(root).navigate(
                R.id.action_nav_triptest_to_nav_occupancy,
            )
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
         var TripTest = JSONObject()
         var SealNumbers = JSONObject()
         var ReplacementSealNumbers = JSONObject()
         var TamperPictures = JSONArray()

         TripTest.put("ResetCode","5649 3153 7254 5031 3471")
         TripTest.put("VoltageStickBeep",binding.swStickBeep.isChecked)
         TripTest.put("MajorAppliancesOff",binding.swMajorAppliance.isChecked)

         SealNumbers.put("Seal Number 1",binding.etSealNumber1.text.toString())
         SealNumbers.put("Seal Number 2",binding.etSealNumber2.text.toString())
         TripTest.put("SealNumbers",SealNumbers)

         TripTest.put("SealReplaced",binding.swReplaceSeals.isChecked)
         ReplacementSealNumbers.put("Seal Number 1",binding.etNewSealNumber1.text)
         ReplacementSealNumbers.put("Seal Number 2",binding.etNewSealNumber2.text)
         TripTest.put("ReplacementSealNumbers",ReplacementSealNumbers)

         if(ConstantHelper.TamperedWiresUUID!=""){
             TamperPictures.put(ConstantHelper.TamperedWiresUUID)
         }
         if(ConstantHelper.TamperedWires2UUID!=""){
             TamperPictures.put(ConstantHelper.TamperedWires2UUID)
         }
         if(ConstantHelper.TamperedWires3UUID!=""){
             TamperPictures.put(ConstantHelper.TamperedWires3UUID)
         }

//         TamperPictures.put(ConstantHelper.TamperedWiresUUID)
         TripTest.put("TamperPictures",TamperPictures)

         TripTest.put("PowerLimitSettings", binding.spPowerLimit.selectedItem.toString().toInt())

         ConstantHelper.Components.put("TripTest",TripTest)
         ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
         Log.e("json at TripTest", ConstantHelper.meterModelJson.toString())

    }


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())

                    if(temperCount==1){
                        loadTampringImage()
                    }else if(temperCount==2){
                        loadTampringImage2()
                    }else if(temperCount==3){
                        loadTampringImage3()
                    }







//                val options = BitmapFactory.Options()
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888
//                val bitmap = BitmapFactory.decodeFile(mPhotoFile!!.path, options)
//                val intent = Intent(mActivity, CardCropActivity::class.java)
//                ConstantHelper.bitmap = bitmap
//                requireActivity().startActivity(intent)

            }


        }




    private var activityCameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.containsValue(false)) {
            dispatchTakePictureIntent()
        }
//        permissions.entries.forEach {
//            if(!it.value)
//            {
//                Log.e("permission", "${it.key} permission not grant", )
//            }
//          //  Log.e("DEBUG", "${it.key} = ${it.value}")
//        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("imagePath",mPhotoFile.toString())
        Log.e("TAG", "onSaveInstanceState: ")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        setImageFile(savedInstanceState?.get("imagePath").toString())
        Log.e("TAG", "onRestoreInstanceState: " + savedInstanceState?.get("imagePath"))
        super.onViewStateRestored(savedInstanceState)
    }



    fun getImageFile(): File {
        return mPhotoFile!!
    }
    fun setImageFile(path:String) {
        mPhotoFile= File(path)
    }



    private fun selectImageType(context: Context) {

        requestStoragePermission()

    }



    private fun requestStoragePermission() {
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        when {

            hasPermissions(requireContext(), *permission) -> {
                Log.e("permission", "camera has permission ")

                    dispatchTakePictureIntent()


            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG).show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            Log.e("permission", "hasPermissions: $permissions")
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                Toast.makeText(requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT).show()
                Log.d("file not", ex.localizedMessage)
                // Error occurred while creating the File
            }
            if (photoFile != null) {

                val photoURI = FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + ".provider",
                    photoFile
                )
                mPhotoFile = photoFile
                Log.e("TAG", mPhotoFile.toString())
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activityResultLauncher.launch(takePictureIntent)

            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "KareboForm")
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("TAG", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("TAG", image.toString())
        return image
    }





    fun showTampringBt(){
        binding.btImgTampring.visibility=View.VISIBLE
        binding.btImgTampring2.visibility=View.VISIBLE
        binding.btImgTampring3.visibility=View.VISIBLE
    }
    fun showTampringimg(){
        binding.btImgTampring.visibility=View.VISIBLE
        binding.btImgTampring2.visibility=View.VISIBLE
        binding.btImgTampring3.visibility=View.VISIBLE


        if(PhototampringFile!=null || PhototampringFile2!=null||PhototampringFile3!=null){
            binding.llTemperImages.visibility=View.VISIBLE
            if(PhototampringFile!=null ){
                binding.ivTampringPhoto.visibility=View.VISIBLE
            }
            if(PhototampringFile2!=null ){
                binding.ivTampringPhoto2.visibility=View.VISIBLE
            }
            if(PhototampringFile3!=null ){
                binding.ivTampringPhoto3.visibility=View.VISIBLE
            }
        }
    }
    fun hideTampring(){
        binding.btImgTampring.visibility=View.GONE
        binding.btImgTampring2.visibility=View.GONE
        binding.btImgTampring3.visibility=View.GONE

        binding.llTemperImages.visibility=View.GONE
        binding.ivTampringPhoto.visibility=View.GONE
        binding.ivTampringPhoto.visibility=View.GONE
        binding.ivTampringPhoto.visibility=View.GONE
    }


    fun hideReplace(){
        binding.llNewSealNumber1.visibility=View.GONE
        binding.llNewSealNumber2.visibility=View.GONE
    }

    fun showReplace(){
        binding.llNewSealNumber1.visibility=View.VISIBLE
        binding.llNewSealNumber2.visibility=View.VISIBLE
    }


fun showEcerything(){


    binding.llStillOn.visibility=View.VISIBLE
    binding.llSealsBroken.visibility=View.VISIBLE
    binding.llSealNumber.visibility=View.VISIBLE
    binding.llSealNumber2.visibility=View.VISIBLE
    binding.llReplaceSeals.visibility=View.VISIBLE
    binding.llTampering.visibility=View.VISIBLE
    binding.llApplianceOff.visibility=View.VISIBLE
    binding.llPowerLimit.visibility=View.VISIBLE

    if(PhototampringFile!=null || PhototampringFile2!=null||PhototampringFile3!=null){
        binding.llTemperImages.visibility=View.VISIBLE
        if(PhototampringFile!=null ){
            binding.ivTampringPhoto.visibility=View.VISIBLE
        }
        if(PhototampringFile2!=null ){
            binding.ivTampringPhoto2.visibility=View.VISIBLE
        }
        if(PhototampringFile3!=null ){
            binding.ivTampringPhoto3.visibility=View.VISIBLE
        }
    }
    if(binding.swTampering.isChecked){
        binding.btImgTampring.visibility=View.VISIBLE
        binding.btImgTampring2.visibility=View.VISIBLE
        binding.btImgTampring3.visibility=View.VISIBLE
    }

    if(binding.swApplianceOff.isChecked){
        binding.btNext.visibility=View.VISIBLE
    }
    if(binding.swReplaceSeals.isChecked){
        binding.llNewSealNumber1.visibility=View.VISIBLE
        binding.llNewSealNumber2.visibility=View.VISIBLE
    }

}
    fun hideEveryThing()
    {


        binding.llStillOn.visibility=View.GONE
        binding.llSealsBroken.visibility=View.GONE
        binding.llSealNumber.visibility=View.GONE
        binding.llSealNumber2.visibility=View.GONE
        binding.llReplaceSeals.visibility=View.GONE
        binding.llNewSealNumber1.visibility=View.GONE
        binding.llNewSealNumber2.visibility=View.GONE
        binding.llTampering.visibility=View.GONE
        binding.llTemperImages.visibility=View.GONE
        binding.ivTampringPhoto.visibility=View.GONE
        binding.ivTampringPhoto2.visibility=View.GONE
        binding.ivTampringPhoto3.visibility=View.GONE
        binding.btImgTampring.visibility=View.GONE
        binding.btImgTampring2.visibility=View.GONE
        binding.btImgTampring3.visibility=View.GONE
        binding.llPowerLimit.visibility=View.GONE
        binding.btNext.visibility=View.GONE
        binding.llApplianceOff.visibility=View.GONE


    }




    fun loadTampringImage() {
        PhototampringFile=File(mPhotoFile?.path!!)
        addPhoto(6)
        Log.e("TAG", "loadTokenImage: " + PhototampringFile)
        Glide.with(requireContext())
            .load(PhototampringFile)
            .into(binding.ivTampringPhoto)
        showTampringimg()
    }



    fun loadTampringImage2() {
        PhototampringFile2=File(mPhotoFile?.path!!)
        addPhoto(6)
        Log.e("TAG", "loadTokenImage: " + PhototampringFile2)
        Glide.with(requireContext())
            .load(PhototampringFile2)
            .into(binding.ivTampringPhoto2)
        showTampringimg()
    }


    fun loadTampringImage3() {
        PhototampringFile3=File(mPhotoFile?.path!!)
        addPhoto(6)
        Log.e("TAG", "loadTokenImage: " + PhototampringFile3)
        Glide.with(requireContext())
            .load(PhototampringFile3)
            .into(binding.ivTampringPhoto3)
        showTampringimg()
    }




    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()
        val newUUID2 = UUID.randomUUID()
        val newUUID3 = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)


        var id=""
        if(temperCount==1){
            id=newUUID.toString()
        }else if(temperCount==2){
            id=newUUID2.toString()
        }else   if(temperCount==3){
            id=newUUID3.toString()
        }



        var base64Image: String=""
        if(type==6){


            if(temperCount==1){
                if(PhototampringFile!=null){
                    base64Image = ConstantHelper.getBase64(PhototampringFile!!)
                }
            }else if(temperCount==2){
                if(PhototampringFile2!=null){
                    base64Image = ConstantHelper.getBase64(PhototampringFile2!!)
                }
            }else   if(temperCount==3){
                if(PhototampringFile3!=null){
                    base64Image = ConstantHelper.getBase64(PhototampringFile3!!)
                }
            }




        }



//        Log.e("TAG", "addAuditPhoto64: "+base64Image, )

        var body = JSONObject()
        var geoLocation = JSONObject()
        body.put("image",base64Image)
        body.put("type",type)

        geoLocation.put("latitude",locationn?.latitude)
        geoLocation.put("longitude",locationn?.longitude)
        geoLocation.put("accuracy",locationn?.accuracy)
        var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        var formattedDate = sdf.format(Date())
        geoLocation.put("timestamp",formattedDate)
        body.put("geoLocation",geoLocation)

        body.put("score",0)

        Log.e("TAG", "photo add body: " + body.toString())


        ConstantHelper.photoList.add( photoUploadDataClass( id,body.toString()) )
        LoaderHelper.dissmissLoader()
        if(type==6){

            if(temperCount==1){
                ConstantHelper.TamperedWiresUUID=newUUID.toString()
            }else if(temperCount==2){
                ConstantHelper.TamperedWires2UUID=newUUID2.toString()
            }else   if(temperCount==3){
                ConstantHelper.TamperedWires3UUID=newUUID3.toString()
            }



        }


//        val call = api?.addPhoto64(id,body.toString())
//        call?.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: Response<ResponseBody>
//            ) {
//
//                if(response.isSuccessful){
//                    var statuscode=response.code()
//                    Log.e("TAG", "Statuscode of Photo " + statuscode)
//
//                    if(statuscode==200){
//
//                        LoaderHelper.dissmissLoader()
//                        if(type==6){
//
//                            if(temperCount==1){
//                                ConstantHelper.TamperedWiresUUID=newUUID.toString()
//                            }else if(temperCount==2){
//                                ConstantHelper.TamperedWires2UUID=newUUID2.toString()
//                            }else   if(temperCount==3){
//                                ConstantHelper.TamperedWires3UUID=newUUID3.toString()
//                            }
//
//
//
//                        }
//
//                        Log.e("TAG", "Audotphoto: " + response.body()?.string())
//
//                    }
//                    else    {
//                        LoaderHelper.dissmissLoader()
//
//                        Toast.makeText(requireContext(), response.body()?.string(), Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//
//                }
//                else{
////                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
////                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
//                    LoaderHelper.dissmissLoader()
//
//                    Toast.makeText(requireContext(),
//                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                LoaderHelper.dissmissLoader()
//
//                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//        })

    }



}