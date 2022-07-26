package com.karebo.teamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.dataclass.CodeListDataClass
import com.karebo.teamapp.utils.GsonParser
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class splesh2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splesh2)

        loadCodeList()
    }
    fun splash() {
        Handler().postDelayed({
            val intent = Intent(this@splesh2, Drawer::class.java)
            startActivity(intent)
            finish()

        }, 2000)
    }

    fun loadCodeList(){
        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.codelist()
        call?.enqueue(object : Callback<CodeListDataClass> {
            override fun onResponse(
                call: Call<CodeListDataClass>,
                response: Response<CodeListDataClass>
            ) {
                if(response.isSuccessful){
                var statuscode=response.code()
                    Log.e("TAG", "Statuscode of codelist "+statuscode, )

                if(statuscode==200){
                   // Log.e("TAG", "onResponse: "+response.body().toString(), )
                    val codelistString: String =
                        GsonParser.gsonParser!!.toJson(response.body())
                        SharedPreferenceHelper.getInstance(this@splesh2).setCodeList(codelistString)
                    val intent = Intent(this@splesh2, Drawer::class.java)
                    startActivity(intent)
                }
                else    {
                    Toast.makeText(this@splesh2,
                        "Some Error occured", Toast.LENGTH_SHORT)
                        .show()
                }


                }
                else{
                    Toast.makeText(this@splesh2,
                        "Some Error occured", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<CodeListDataClass>, t: Throwable) {
                Log.e("TAG", "onFailure: "+t.localizedMessage, )
                Toast.makeText(this@splesh2, "offline Mode", Toast.LENGTH_SHORT)
                    .show()


                if(SharedPreferenceHelper.getInstance(this@splesh2).getTeamKey()!="null"&&
                    SharedPreferenceHelper.getInstance(this@splesh2).getOtp()!="null" ){
                    val intent = Intent(this@splesh2, Drawer::class.java)
                    startActivity(intent)
                }
            }

        })
    }

//    fun netConnected() {
//        val connectivityManager =
//            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
//            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
//        ) {
//            connected = true
//        } else {
//            connected = false
//        }
//    }
}