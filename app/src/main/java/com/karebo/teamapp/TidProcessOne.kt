package com.karebo.teamapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
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
import com.karebo.teamapp.databinding.FragmentTidProcessBinding
import com.karebo.teamapp.dataclass.CodeListDataClass
import com.karebo.teamapp.dataclass.MeterDataModel
import com.karebo.teamapp.dataclass.photoUploadDataClass
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class TidProcessOne : Fragment() {

    private var _binding: FragmentTidProcessBinding? = null
    private val binding get() = _binding!!
    private var mPhotoFile: File? = null
    private var PhotokrnFile: File? = null
    private var PhototokenFile: File? = null
    private var PhototokenssFile: File? = null
    var adapterMeterStatus: ArrayAdapter<String>? = null


    var  locationn : Location? =null

    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    var photoname: String?=null

    var spinnerManufacture: ArrayAdapter<String>? = null
    var SpinnerModel: ArrayAdapter<String>? = null


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



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTidProcessBinding.inflate(
            inflater, container, false
        )
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









        loadManufecture()
        meterStatus()
        setApiData()

        binding.etMeterSerialNo.setText(ConstantHelper.SERIAL)

        binding.btNext.setOnClickListener {

            if(binding.etMeterSerialNo.text.isEmpty()|| binding.etMeterSerialNo.equals(null)){
                Toast.makeText(requireContext(),"Enter Meter Serail No",Toast.LENGTH_SHORT).show()
            }
            else if(binding.spMeterStatus.selectedItemPosition==0){
                Toast.makeText(requireContext(),"Select Meter status",Toast.LENGTH_SHORT).show()

            }

            else if(binding.spMeterStatus.selectedItemPosition!=3 &&
                binding.spMeterStatus.selectedItemPosition!=6) {

                if (binding.swPrepraid.isChecked == true) {

                    if (binding.spKrn.selectedItemPosition == 0) {
                        Toast.makeText(requireContext(), "Select KRN ", Toast.LENGTH_SHORT).show()

                    } else if (binding.spKrn.selectedItemPosition == 1 && (PhotokrnFile == null || PhototokenssFile == null)) {

                        if (PhotokrnFile == null) {
                            Log.e("TAG", "spKrn call: 2",)
                            Toast.makeText(
                                requireContext(),
                                "Add LCD Screen Photo ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (PhototokenssFile == null) {
                            Log.e("TAG", "spKrn call: 3",)
                            Toast.makeText(
                                requireContext(),
                                "Add Last 5 Token photo ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } else if (binding.swToken.isChecked && PhototokenFile == null) {
                        Log.e("TAG", "Token call: ",)
                        if (PhototokenFile == null) {
                            Toast.makeText(
                                requireContext(),
                                "Add Zero Token photo ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (PhotokrnFile == null) {
                        Toast.makeText(
                            requireContext(),
                            "Add LCD Screen Photo ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        addInModel()
                        ConstantHelper.SERIAL = binding.etMeterSerialNo.text.toString()
                        Navigation.findNavController(root).navigate(
                            R.id.action_nav_tidprocessone_to_nav_triptest,
                        )
                    }

                } else {

                    if (PhotokrnFile == null) {
                        Toast.makeText(
                            requireContext(),
                            "Add LCD Screen Photo ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        addInModel()
                        ConstantHelper.SERIAL = binding.etMeterSerialNo.text.toString()
                        Navigation.findNavController(root).navigate(
                            R.id.action_nav_tidprocessone_to_nav_triptest,
                        )
                    }


                }
            }else{
                addInModel()
                ConstantHelper.SERIAL = binding.etMeterSerialNo.text.toString()
                Navigation.findNavController(root).navigate(
                    R.id.action_nav_tidprocessone_to_nav_occupancy,
                )
            }





        }

        binding.btImageLcdKrn.setOnClickListener {
            photoname = "krn"
            selectImageType(requireContext())
        }

        binding.btImageToken.setOnClickListener {
            photoname = "token"
            selectImageType(requireContext())
        }

        binding.btTokenImgSs.setOnClickListener {
            photoname = "tokenss"
            selectImageType(requireContext())
        }

        binding.btClear.setOnClickListener{
            binding.ink.clear()
        }

        binding.spManufacturer.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                loadModel()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })

        binding.spKrn.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {

                if(binding.spKrn.selectedItemPosition ==0){
                    Toast.makeText(requireContext(),"select krn",Toast.LENGTH_LONG).show()
                    hideSignature()
                    hideTokenssImg()
                }
                else if(binding.spKrn.selectedItemPosition==1){
                    showSignature()
                    showTokenssbtnImg()


                }else if(binding.spKrn.selectedItemPosition==2){
                    hideSignature()
                    hideTokenssImg()
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                hideSignature()
            }
        })



        binding.spMeterStatus.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {

                if(binding.spMeterStatus.selectedItemPosition ==3 ||
                    binding.spMeterStatus.selectedItemPosition ==6){
                    binding.llMeterStatusResult.visibility=View.GONE
                }
                else {
                    binding.llMeterStatusResult.visibility=View.VISIBLE
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                hideSignature()
            }
        })


        binding.swToken.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showTokenImgBtn()
                hideKrnImg()
            } else {
                hideTokenImg()
                hideKrnImg()
            }
        })

        binding.swPrepraid.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
              setPrepaidView()
            } else {
                setPrepaidView()
            }
        })






        binding.ink.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    binding.scrView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    binding.scrView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        })



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



    fun  setPrepaidView(){

        if(binding.swPrepraid.isChecked==true){

            binding.llKrn.visibility=View.VISIBLE
            binding.llRollover.visibility=View.VISIBLE
            binding.llToken.visibility=View.VISIBLE
            krnAffectWidget(true)

        }else{

            binding.llKrn.visibility=View.GONE
            binding.llRollover.visibility=View.GONE
            binding.llToken.visibility=View.GONE
            krnAffectWidget(false)

        }


    }


    fun krnAffectWidget(mode:Boolean){


        if(binding.swToken.isChecked){
            if(mode){
                showTokenImgBtn()
            }else{
                hideTokenImg()
            }
            hideKrnImg()
            }
        else {
            hideTokenImg()
            hideKrnImg()
        }



        if(mode){
            if(binding.spKrn.selectedItemPosition ==0){
                Toast.makeText(requireContext(),"select krn",Toast.LENGTH_LONG).show()
                hideSignature()
                hideTokenssImg()
            }
            else if(binding.spKrn.selectedItemPosition==1){
                showSignature()
                showTokenssbtnImg()


            }else if(binding.spKrn.selectedItemPosition==2){
                hideSignature()
                hideTokenssImg()
            }
        }else{
            hideSignature()
            hideTokenssImg()
        }

    }



    fun addInModel() {

        if(binding.spMeterStatus.selectedItemPosition==3 ||
            binding.spMeterStatus.selectedItemPosition==6){
            var TID = JSONObject()
            var code=findMeterCode()
            Log.e("TAG", "code  : " + code)

            ConstantHelper.PREPAID= binding.swPrepraid.isChecked

            ConstantHelper.meterModelJson.put("Code",code)
            TID.put("MeterStatus",binding.spMeterStatus.selectedItem)

            ConstantHelper.Components.put("TID",TID)
            ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
            Log.e("json at Tid", ConstantHelper.meterModelJson.toString())

        }else{

            var TID = JSONObject()
            var KRNNumberNPicture = JSONObject()
            var Last5TokensScreenshot = JSONObject()
            var ZeroTokenNPicture = JSONObject()
            var Value = JSONArray()

            var code=findMeterCode()
            Log.e("TAG", "code  : " + code)

            ConstantHelper.PREPAID= binding.swPrepraid.isChecked

            ConstantHelper.meterModelJson.put("Code",code)
            TID.put("MeterStatus",binding.spMeterStatus.selectedItem)
            TID.put("Prepaid",binding.swPrepraid.isChecked)
            TID.put("FaultDescription",binding.etDescription.text)

            KRNNumberNPicture.put("Key",2)
            KRNNumberNPicture.put("Value",ConstantHelper.KRNPictureUUID)
            TID.put("KRNNumberNPicture",KRNNumberNPicture)

            var base64=JSONObject.NULL
            try{
                val drawing: Bitmap = binding.ink.getBitmap(resources.getColor(android.R.color.white))
                base64= ConstantHelper.bitmapToBase64(drawing)
            }catch (e:Exception){

            }


            if(binding.swPrepraid.isChecked==true){
                TID.put("Last5TokenSignature64",base64)

                Last5TokensScreenshot.put("Key",ConstantHelper.Last5TokenScreenshotUUID)
                Last5TokensScreenshot.put("Value",Value)

                ZeroTokenNPicture.put("Key",true)
                ZeroTokenNPicture.put("Value",ConstantHelper.ZeroTokenPictureUUID)
            }
            else{
                TID.put("Last5TokenSignature64","")

                Last5TokensScreenshot.put("Key","")
                Last5TokensScreenshot.put("Value","")

                ZeroTokenNPicture.put("Key",false)
                ZeroTokenNPicture.put("Value","")
            }





            TID.put("Last5TokensScreenshot",Last5TokensScreenshot)

            TID.put("RolloverStatus",binding.swRollover.isChecked)

            TID.put("ZeroTokenNPicture",ZeroTokenNPicture)

            TID.put("CreditUnits",binding.etCredit.text.toString().toDouble())

            ConstantHelper.Components.put("TID",TID)
            ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
            Log.e("json at Tid", ConstantHelper.meterModelJson.toString())

        }







    }

    fun setApiData() {








//
        var data=SharedPreferenceHelper.getInstance(requireContext()).getMeterData()
        var jsonData=  GsonParser.gsonParser!!.fromJson(data, MeterDataModel::class.java)
            if(jsonData!=null){


                // pre paid switch set
                Log.e("TAG", "setApiData: " + jsonData.MeterType)
                if(jsonData?.MeterType=="Prepaid"){
                    binding.swPrepraid.isChecked = true
                }

                //set krn spinner

                Log.e("TAG", "setApiData: "+jsonData?.KRN, )
                if(jsonData?.KRN==1){
                    binding.spKrn.setSelection(1)
                }else if(jsonData?.KRN==2){
                    binding.spKrn.setSelection(2)
                }else{
                    binding.spKrn.setSelection(0)
                }




                //set model and menufecture
                var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
                val codeListdata = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
                codeListdata.ElectricityMeter.forEach {
                    //Log.e("TAG", "setApiData Model1: ${it.Model}", )
                    if(it.Code==jsonData?.Model){
                        Log.e("TAG", "setApiData Model: ${it.Manufacturer}")
                        binding.spManufacturer.setSelection(spinnerManufacture!!.getPosition(it.Manufacturer))
                    }
                }
            }




     }


    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())
                Log.e("TAG", "photoname $photoname")
                if (photoname == "krn") {
                    loadKrnImage()

                } else if (photoname == "token") {
                    loadTokenImage()

                }else if (photoname == "tokenss") {
                    Log.e("TAG", "tokensss ")
                    loadTokenImagess()

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
        outState.putString("imagePath", mPhotoFile.toString())
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

    fun setImageFile(path: String) {
        mPhotoFile = File(path)
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
                if (photoname == "tokenss") {
                    Log.e("TAG", "selectImageType: Gallary")
                    dispatchGalleryIntent()
                } else {
                    Log.e("TAG", "selectImageType: Camera")
                    dispatchTakePictureIntent()
                }

            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG)
                    .show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            Log.e("permission", "hasPermissions: $permissions")
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private var activityResultLauncherGallary: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                var result = result.data
                var selectedImage: Uri = result?.data!!
                mPhotoFile = File(getRealPathFromUri(selectedImage))

                val sourcePath = requireContext().getExternalFilesDir(null).toString()
                Log.e("Photo2", mPhotoFile.toString())

                try {
                    copyFileStream(
                        File(sourcePath + "/" + mPhotoFile!!.name),
                        selectedImage,
                        requireContext()
                    )
                }catch (e:Exception){

                }

                Log.e("Photo2", mPhotoFile.toString())
                loadTokenImagess()

            }


        }


    @Throws(IOException::class)
    private fun copyFileStream(dest: File, uri: Uri, context: Context) {
        var `is`: InputStream? = null
        var os: OutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            os = FileOutputStream(dest)
            val buffer = ByteArray(1024)
            var length: Int
            while (`is`!!.read(buffer).also { length = it } > 0) {
                os.write(buffer, 0, length)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            `is`!!.close()
            os!!.close()
            Log.e("TAG", "$dest uri")
            mPhotoFile = dest

        }
    }

    private fun dispatchGalleryIntent() {

        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activityResultLauncherGallary.launch(pickPhoto)
//        startActivityForResult(
//            pickPhoto,
//            REQUEST_GALLERY_PHOTO
//        )
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
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "KareboForm"
            )
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("TAG", "failed to create directory")
        }
        val image = File(storageDir.path + File.separator + imageFileName)
        Log.e("TAG", image.toString())
        return image
    }


    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireContext().contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


    fun showTokenImgBtn() {
        binding.btImageToken.visibility = View.VISIBLE
    }

    fun showTokenImg() {
        binding.btImageToken.visibility = View.VISIBLE
        binding.ivTokenPhoto.visibility = View.VISIBLE

    }

    fun hideTokenImg() {
        binding.btImageToken.visibility = View.GONE
        binding.ivTokenPhoto.visibility = View.GONE
    }


    fun showTokenssImg() {
        binding.btTokenImgSs.visibility = View.VISIBLE
        binding.ivTokenSs.visibility = View.VISIBLE

    }
    fun showTokenssbtnImg() {
        binding.btTokenImgSs.visibility = View.VISIBLE


    }

    fun hideTokenssImg() {
        binding.btTokenImgSs.visibility = View.GONE
        binding.ivTokenSs.visibility = View.GONE
    }


    fun showKrnImg() {

        binding.ivKrnPhotoLcd.visibility = View.VISIBLE

    }

    fun hideKrnImg() {

        binding.ivKrnPhotoLcd.visibility = View.GONE
    }

    fun  showSignature(){
        binding.ink.visibility=View.VISIBLE
        binding.tvTitleSignature.visibility=View.VISIBLE
        binding.btClear.visibility=View.VISIBLE
    }
    fun  hideSignature(){
        binding.ink.visibility=View.GONE
        binding.tvTitleSignature.visibility=View.GONE
        binding.btClear.visibility=View.GONE
    }


    fun loadTokenImage() {

        PhototokenFile = File(mPhotoFile?.path!!)
        addPhoto(4)
        Log.e("TAG", "loadTokenImage: " + PhototokenFile)
        Glide.with(requireContext())
            .load(PhototokenFile)
            .into(binding.ivTokenPhoto)
        showTokenImg()

    }

    fun loadTokenImagess() {

        PhototokenssFile = File(mPhotoFile?.path!!)
        addPhoto(8)
        Log.e("TAG", "loadTokenImagess: " + PhototokenssFile)
        Glide.with(requireContext())
            .load(PhototokenssFile)
            .into(binding.ivTokenSs)
        showTokenssImg()

    }


    fun loadKrnImage() {

        PhotokrnFile = File(mPhotoFile?.path!!)
        addPhoto(7)
        Log.e("TAG", "loadTokenImage: " + PhotokrnFile)
        Glide.with(requireContext())
            .load(PhotokrnFile)
            .into(binding.ivKrnPhotoLcd)
        showKrnImg()

    }



    fun loadManufecture() {
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var manufecture: MutableList<String> = mutableListOf()
        data.ElectricityMeter.forEach {
            if (!manufecture.contains(it.Manufacturer)) {
                manufecture.add(it.Manufacturer)
            }

        }

        Log.e("TAG", "Question: " + manufecture.toString())



        spinnerManufacture = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            manufecture
        )

        spinnerManufacture?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spManufacturer.adapter = spinnerManufacture
    }

    fun loadModel() {
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var model: MutableList<String> = mutableListOf()
        data.ElectricityMeter.forEach {

            if (it.Manufacturer == binding.spManufacturer.selectedItem) {
                if (!model.contains(it.Model)) {
                    model.add(it.Model)
                }

            }

        }

        Log.e("TAG", "Question: " + model.toString())

        SpinnerModel =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, model)
        SpinnerModel?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spModel.adapter = SpinnerModel
    }


    fun findMeterCode() :String{
        var codelist = SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
        var code:String=""
        data.ElectricityMeter.forEach {
            if(it.Manufacturer==binding.spManufacturer.selectedItem
                && it.Model==binding.spModel.selectedItem){
                Log.e("TAG", "findMeterCode: "+it.Code)
                code=it.Code
            }


        }

        return code
    }


    fun meterStatus(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
//        Log.e("TAG", "Question: " + data.Toolbox.toString())

        var finalData : MutableList <String> = data.MeterStatus as MutableList<String>
        finalData.add(0,"Meter Status")
        adapterMeterStatus = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, finalData)
        adapterMeterStatus?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMeterStatus.adapter = adapterMeterStatus
    }





    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()

//        val client = ApiClient()
//        val api = client.getClient()?.create(Api::class.java)
        var base64Image: String=""
           if(type==7){
                base64Image = ConstantHelper.getBase64(PhotokrnFile!!)
           }else if(type==8){
               base64Image = ConstantHelper.getBase64(PhototokenssFile!!)
           }else if(type==4){
               base64Image = ConstantHelper.getBase64(PhototokenFile!!)
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


        ConstantHelper.photoList.add( photoUploadDataClass( newUUID.toString(),body.toString()) )
        LoaderHelper.dissmissLoader()
        if(type==7){
            ConstantHelper.KRNPictureUUID=newUUID.toString()

        }else if(type==8){
            ConstantHelper.Last5TokenScreenshotUUID=newUUID.toString()

        }else if(type==4){
            ConstantHelper.ZeroTokenPictureUUID=newUUID.toString()

        }


//        val call = api?.addPhoto64(newUUID.toString(),body.toString())
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
//                        if(type==7){
//                            ConstantHelper.KRNPictureUUID=newUUID.toString()
//
//                        }else if(type==8){
//                            ConstantHelper.Last5TokenScreenshotUUID=newUUID.toString()
//
//                        }else if(type==4){
//                            ConstantHelper.ZeroTokenPictureUUID=newUUID.toString()
//
//                        }
//                       // ConstantHelper.PropertyPictureUUID=newUUID.toString()
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
                    R.id.action_nav_tidprocessone_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_tidprocessone_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }




}