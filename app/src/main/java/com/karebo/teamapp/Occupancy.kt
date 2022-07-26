package com.karebo.teamapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
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
import com.google.android.material.textview.MaterialTextView
import com.karebo.teamapp.databinding.FragmentOccupancyBinding
import com.karebo.teamapp.dataclass.MeterDataModel
import com.karebo.teamapp.dataclass.photoUploadDataClass
import com.karebo.teamapp.dataclass.subMeterModel
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class Occupancy : Fragment() {


    private var _binding: FragmentOccupancyBinding? = null
    private val binding get() = _binding!!
    var jsonData: MeterDataModel? =null
    var  locationn : Location? =null
    private val permission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,

        )
    lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mPhotoFile: File? = null
    private var subMeterPhotoFile: File? = null
    lateinit var img:ImageView
    lateinit var submeterId:String


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
        Log.e("TAG", "location: ")
        LoaderHelper.showLoader(requireContext())
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
            Log.e("Location", "location is found: $location")
                Log.e("Location", "location is found: $location")
                locationn=location
                findAddress(location)


        }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(),"Oops location failed to Fetch: $exception",Toast.LENGTH_SHORT).show()
                Log.e("Location", "Oops location failed with exception: $exception")
                LoaderHelper.dissmissLoader()
            }



        binding.spSubmeter.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {

               loadUI(binding.spSubmeter.selectedItem.toString())
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })



        binding.btNext.setOnClickListener{

            if( binding.etFirstName.text.isEmpty()  ||binding.etFirstName.text.equals(null)){
            Toast.makeText(requireContext(),"Add First name ",Toast.LENGTH_SHORT).show()

            }else if( binding.etSurName.text.isEmpty()  ||binding.etSurName.text.equals(null)){
                Toast.makeText(requireContext(),"Add Surname ",Toast.LENGTH_SHORT).show()

            }else if( binding.etIdPassportNumber.text.isEmpty()  ||binding.etIdPassportNumber.text.equals(null)){
                Toast.makeText(requireContext(),"Add ID/Passport Number ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etEmail.text.isEmpty()  ||binding.etEmail.text.equals(null)){
                Toast.makeText(requireContext(),"Add Email ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etCellNo.text.isEmpty()  ||binding.etCellNo.text.equals(null)){
                Toast.makeText(requireContext(),"Add CellNumber ",Toast.LENGTH_SHORT).show()

            }else if( binding.etMunicipalityAccNo.text.isEmpty()  ||binding.etMunicipalityAccNo.text.equals(null)){
                Toast.makeText(requireContext(),"Add Municipality Account No ",Toast.LENGTH_SHORT).show()

            }else if( binding.etUnitNo.text.isEmpty()  ||binding.etUnitNo.text.equals(null)){
                Toast.makeText(requireContext(),"Add Unit No ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etCmpxName.text.isEmpty()  ||binding.etCmpxName.text.equals(null)){
                Toast.makeText(requireContext(),"Add Complex Name ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etStreetAddress.text.isEmpty()  ||binding.etStreetAddress.text.equals(null)){
                Toast.makeText(requireContext(),"Add Street Name ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etSuburbName.text.isEmpty()  ||binding.etSuburbName.text.equals(null)){
                Toast.makeText(requireContext(),"Add Suburb Name ",Toast.LENGTH_SHORT).show()

            }
            else if( binding.etPostalCode.text.isEmpty()  ||binding.etPostalCode.text.equals(null)){
                Toast.makeText(requireContext(),"Add Postal Code ",Toast.LENGTH_SHORT).show()

            }
            else{
                 addInModel()
                 Navigation.findNavController(root).navigate(
                     R.id.action_nav_occupancy_to_nav_customerfeedback,
                     )
             }



        }
        return root
    }



    fun loadUI(count:String){



        if(binding.spSubmeter.selectedItemPosition==0){
            ConstantHelper.subMeter= mutableListOf()
           binding.customeView.removeAllViews()

        }else{
            ConstantHelper.subMeter= mutableListOf()
            binding.customeView.removeAllViews()
            binding.customeView.visibility=View.VISIBLE

            var number:Int=count.toInt()-binding.customeView!!.childCount
            Log.e("TAG", "loadUI: " + number.toString())

            for (i:Int in 0 until number){

                val inflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.layout_submeter, null)

                var et_desc : EditText =rowView.findViewById<EditText>(R.id.et_description_sub)

                var bt_img:Button=rowView.findViewById<Button>(R.id.bt_image_submeter)
                var img2:ImageView=rowView.findViewById<ImageView>(R.id.iv_description_photo)
                bt_img.setOnClickListener{
                    submeterId=i.toString()
                    img=img2
                    selectImageType(requireContext())
                }
                Log.e("TAG", "call load ui " + i)

                binding.customeView!!.addView(rowView, binding.customeView!!.childCount - 1)
            }


        }





    }

     fun addInModel() {



         val count: Int = binding.customeView.getChildCount()
         var v: View? = null
         var subView: View? = null
         var i = 0
         while (i < count) {
             v = binding.customeView.getChildAt(i)

             if ( v is LinearLayout) {
                 subView = v.getChildAt(0)

                var desc=( subView as EditText).text

                 Log.e("TAG", "addInModel:edittext "+desc, )
                 if(ConstantHelper.subMeter.any { it.id==i.toString()}){
                     ConstantHelper.subMeter.find { it.id==i.toString() }?.description=desc.toString()
                 }else{
                     ConstantHelper.subMeter.add(subMeterModel(i.toString(),desc.toString(),""))
                 }
             }
             i++
         }









         var Occupancy = JSONObject()
         var Attributes = JSONObject()
         var Address = JSONObject()
         var subMeter = JSONObject()
         var subMeters = JSONArray()


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


        ConstantHelper.subMeter.map {
            subMeter.put("Description",it.description)
            subMeter.put("Picture",it.imgId)
            subMeters.put(subMeter)
        }



         Occupancy.put("SubMeters",subMeters)

         Occupancy.put("Address",Address)


         ConstantHelper.Components.put("Occupancy",Occupancy)
         ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
         Log.e("json at Occupancy", ConstantHelper.meterModelJson.toString())
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
        var addresses: List<Address?> = listOf()
        var geocoder = Geocoder(requireContext(), Locale.getDefault())
        var returnAddress:String

        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);

        }
        catch (e:Exception){

        }
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






    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.drawer, menu)
    }


    fun loadSubMeterImage() {

        subMeterPhotoFile = File(mPhotoFile?.path!!)
        addPhoto(6)
        Log.e("TAG", "subMeterPhotoFile: " + subMeterPhotoFile)
        Glide.with(requireContext())
            .load(subMeterPhotoFile)
            .into(img)


    }








    private var activityResultLauncher: ActivityResultLauncher<Intent> =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("Photo", mPhotoFile.toString())

                    loadSubMeterImage()




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

            hasPermissions2(requireContext(), *permission) -> {
                Log.e("permission", "camera has permission ")
//                if (photoname == "tokenss") {
//                    Log.e("TAG", "selectImageType: Gallary")
//                    dispatchGalleryIntent()
//                } else {
                    Log.e("TAG", "selectImageType: Camera")
                    dispatchTakePictureIntent()


            }
            else -> {
                Toast.makeText(requireContext(), " Allow the Storage Permission", Toast.LENGTH_LONG)
                    .show()
                activityCameraResultLauncher.launch(permission)
            }
        }

    }

    private fun hasPermissions2(context: Context, vararg permissions: String): Boolean =
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
                //loadTokenImagess()

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




    fun addPhoto(type:Int) {


        LoaderHelper.showLoader(requireContext())




        val newUUID = UUID.randomUUID()
        var base64Image: String=""
        base64Image = ConstantHelper.getBase64(subMeterPhotoFile!!)



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



        if(ConstantHelper.subMeter.any { it.id==submeterId }){
            ConstantHelper.subMeter.find { it.id==submeterId }?.imgId=newUUID.toString()
        }else{
            ConstantHelper.subMeter.add(subMeterModel(submeterId,"",newUUID.toString()))

        }


        ConstantHelper.subMeter.map {
            Log.e("TAG", "submit submeter: " + it.id + "-----" + it.imgId)
        }





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
                    R.id.action_nav_occupancy_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_occupancy_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }
}