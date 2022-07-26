package com.the.firsttask.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.karebo.teamapp.dataclass.photoUploadDataClass
import com.karebo.teamapp.dataclass.questionDataModel
import com.karebo.teamapp.dataclass.subMeterModel
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File


object ConstantHelper {


    const val NETWORK_CONNECT="connect"
    const val NETWORK_LOST="lost"
    const val NOTIFICATION_CHANNEL_ID="com.the"
    const val SHARED_PREFERENCE_ID="MyPrefsTheme"
    var ADDRESS=""
    var SERIAL=""
    var PREPAID = true

    var PropertyPictureUUID=""
    var MeterScanUUID=""
    var MeterPictureUUID=""
    var DBBoardUUID=""
    var ZeroTokenPictureUUID=""
    var TIDRolloverUUID=""
    var TamperedWiresUUID=""
    var TamperedWires2UUID=""
    var TamperedWires3UUID=""
    var KRNPictureUUID=""
    var Last5TokenScreenshotUUID=""

    var subMeter: MutableList<subMeterModel> = mutableListOf()


    lateinit var currentSelectd:meterauditDataModel


    var submitMeterDataJSON = JSONObject()
    var Meters = JSONObject()

    var meterModelJson = JSONObject()
    var Components = JSONObject()
    var Feedback = JSONObject()
    var Duration = JSONObject()




     var list: List<meterauditDataModel> =listOf()
     var photoList: MutableList<photoUploadDataClass> = mutableListOf()

    fun getBase64(imgFile: File):String{
        var base64: String=""
        if (imgFile.exists() && imgFile.length() > 0) {
            val bm = BitmapFactory.decodeFile(imgFile.path)
            val bOut = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 8, bOut)
            val base64Image: String = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
            base64=base64Image
        }
        return base64

    }

    fun bitmapToBase64(bitmap: Bitmap):String{
        var base64: String=""

            val bm = bitmap
            val bOut = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 10, bOut)
            val base64Image: String = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT)
            base64=base64Image

        return base64

    }

}