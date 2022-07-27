package com.karebo.teamapp
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log

import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.ActivityDrawerBinding
import com.karebo.teamapp.dataclass.photoUploadDataClass
import com.karebo.teamapp.roomdata.RoomDb
import com.karebo.teamapp.roomdata.mainbody
import com.karebo.teamapp.roomdata.photobody
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.utils.ConstantHelper
import com.the.firsttask.utils.NetworkUtils
import io.reactivex.Completable.merge
import io.reactivex.Observable
import io.reactivex.Observable.merge
import io.reactivex.Single.merge
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody

class Drawer : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding: ActivityDrawerBinding



    lateinit var  notification: Notification
    var notificationManager: NotificationManager? = null
    var channel: NotificationChannel? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityDrawerBinding.inflate(layoutInflater)
     setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)
        firebaseAnalytics = Firebase.analytics

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_about, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NetworkUtils.checkConnectivity(this@Drawer)










        NetworkUtils.getNetworkState().observe(this@Drawer){networkState ->
            when (networkState) {
                ConstantHelper.NETWORK_CONNECT -> {

                    lifecycleScope.launchWhenStarted {
                        withContext(Dispatchers.Default) {
                            Log.e("TAG", "call", )
                            addAllPhoto()
                            submitAllMeter()
                        }
                    }
                    Log.e("TAG", "network: Connect", )
                }
                ConstantHelper.NETWORK_LOST -> {

                    Log.e("TAG", "network: Lost", )
                }

            }

        }








    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.drawer, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    override fun onResume() {
        NetworkUtils.checkConnectivity(this@Drawer)
        Log.e("TAG", "onResume: ", )
        super.onResume()
    }

//    override fun onRestart() {
//        NetworkUtils.checkConnectivity(this@Drawer)
//        Log.e("TAG", "onRestart: ", )
//        super.onRestart()
//    }
//
//    override fun onPause() {
////        NetworkUtils.checkConnectivity(this@Drawer)
//        NetworkUtils.getNetworkState().removeObservers(this@Drawer)
//        Log.e("TAG", "onPause: ", )
//        super.onPause()
//    }
//
//
//    override fun onStop() {
//        NetworkUtils.getNetworkState().removeObservers(this@Drawer)
//        Log.e("TAG", "onStop: ", )
//        super.onStop()
//    }

    override fun onDestroy() {
        NetworkUtils.getNetworkState().removeObservers(this@Drawer)
        Log.e("TAG", "onDestroy: ", )
        super.onDestroy()
    }






    fun addAllPhoto(){


        Log.e("TAG", "addAllPhoto: api call on nwteork connect ", )
        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val requests =  mutableListOf<Observable<ResponseBody>>()

        val photobodyDao= RoomDb.getAppDatabase((this))?.photobodydao()
        var listData=  photobodyDao?.getAllphotobody()

        listData?.forEach {

            requests.add( api?.addPhoto64(it.id,it.body)!!)
            Log.e("TAG", "addAllPhoto uuid: ${it.id}", )
//            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
        }

        if(requests.size>0){
            createNotification(1,"Upload ${requests.size} Photos IN processing")

        }

        Log.e("TAG", "requests: "+requests.toString() )


        Observable.merge(requests)
            .take(requests.size.toLong())
            // executed when the channel is closed or disposed
            .doFinally {
                Log.e("TAG", "addAllPhoto final: ", )
                listData = mutableListOf()
                photobodyDao?.deletephotobody()
                if(requests.size>0){
                createNotification(1,"Upload ${requests.size} Photos SuccessFully")}
//                LoaderHelper.dissmissLoader()

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

    private fun createNotification(id:Int,des:String) {
        notification =  NotificationCompat.Builder(this, ConstantHelper.NOTIFICATION_CHANNEL_ID )
            .setAutoCancel(true)
            .setContentTitle("Upload data")
            .setContentText(des )
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(false)
            .setWhen(System.currentTimeMillis())
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setChannelId( ConstantHelper.NOTIFICATION_CHANNEL_ID )
            .build()




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel= NotificationChannel(ConstantHelper.NOTIFICATION_CHANNEL_ID,"Alaram Name",
                NotificationManager.IMPORTANCE_HIGH )
            channel!!.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            channel!!.enableVibration(true)
            channel!!.description=des

            assert(notificationManager != null)
            notificationManager?.createNotificationChannel(channel!!)
        }
        assert (notificationManager != null)
        notificationManager!!.notify(id, notification)
    }


    fun submitAllMeter(){


        Log.e("TAG", "allMeter: api call on nwteork connect ", )
        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val requests =  mutableListOf<Observable<ResponseBody>>()

        val mainbodydao= RoomDb.getAppDatabase((this))?.mainbodydao()
        var listData=  mainbodydao?.getAllMainBody()

        listData?.forEach {

            requests.add( api?.submitMeter(it.body)!!)
            Log.e("TAG", "submitMeter id: ${it.id}", )
//            Log.e("TAG", "addAllPhoto body: ${it.bodyy}", )
        }

        if(requests.size>0){
            createNotification(2,"Upload ${requests.size} JobCard IN processing")

        }

        Log.e("TAG", "requests: "+requests.toString() )


        Observable.merge(requests)
            .take(requests.size.toLong())
            // executed when the channel is closed or disposed
            .doFinally {
                Log.e("TAG", "submitMeter final: ", )
                listData = mutableListOf()
                mainbodydao?.deleteMainBody()
                if(requests.size>0){
                createNotification(2,"Upload ${requests.size} JobCard SuccessFully")}
//                LoaderHelper.dissmissLoader()

//
            }
            .subscribeOn(Schedulers.io())
            // it's a question if you want to observe these on main thread, depends on context of your application
            .subscribe(
                { ResponseBody ->
                    // here you get both the deviceId and the responseBody
                    Log.e("TAG", "submitMeter responce: "+ResponseBody.string(), )

                    if (ResponseBody == null ) {
                        Log.e("TAG", "submitMeter responce: "+ ResponseBody?.string(), )
                        // request for this deviceId failed, handle it
                    }
                },
                { error ->
                    Log.e("TAG", "Throwable: " + error)
                }
            )




    }




}