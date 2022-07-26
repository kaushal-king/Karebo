package com.the.firsttask.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.the.firsttask.utils.ConstantHelper


class SharedPreferenceHelper(ctx: Context) {
    var context: Context = ctx


    companion object {

        private var INSTANCE: SharedPreferenceHelper? = null

        fun getInstance(context: Context): SharedPreferenceHelper {

            return INSTANCE ?: synchronized(this) {
                var instance = SharedPreferenceHelper(context)
                INSTANCE = instance
                INSTANCE as SharedPreferenceHelper
            }
        }
    }

    fun setCodeList(codelistString: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("codelist", codelistString)
        editor.apply()
    }

    fun getCodeList(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val codelist = sharedPreferences.getString("codelist", null)
        return codelist.toString()

    }


    fun setTeamKey(codelistString: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("teamKey", codelistString)
        editor.apply()
    }

    fun getTeamKey(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val team = sharedPreferences.getString("teamKey", null)
        return team.toString()

    }



    fun setOtp(otp: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("otp", otp)
        editor.apply()
    }

    fun getOtp(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val team = sharedPreferences.getString("otp", null)
        return team.toString()

    }


    fun setJobCard(jobCard: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("jobcard", jobCard)
        editor.apply()
    }

    fun getJobCard(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val jobCard = sharedPreferences.getString("jobcard", null)
        return jobCard.toString()

    }

    fun setJobNumber(jobNumber: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("jobnumber", jobNumber)
        editor.apply()
    }

    fun getJobNumber(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val jobNumber = sharedPreferences.getString("jobnumber", "0")
        return jobNumber.toString()

    }


    fun setMeterdata(jobNumber: String?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("meterdata", jobNumber)
        editor.apply()
    }

    fun getMeterData(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val meterdata = sharedPreferences.getString("meterdata", null)
        return meterdata.toString()

    }


    fun setCompleteJobId(jobid: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("jobid", jobid)
        editor.apply()
    }

    fun getCompleteJobId(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )

        val jobid = sharedPreferences.getString("jobid", null)
        return jobid.toString()

    }




    fun setAllMeterCode(meterDetails: String?) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("meterDetails", meterDetails)
        editor.apply()
    }

    fun getAllMeterCode(): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val meterDetails = sharedPreferences.getString("meterDetails", null)
        return meterDetails.toString()

    }

    fun clearPreferences() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            ConstantHelper.SHARED_PREFERENCE_ID,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }



    fun clearData(){
        setTeamKey("null")
        setOtp("null")

    }
}