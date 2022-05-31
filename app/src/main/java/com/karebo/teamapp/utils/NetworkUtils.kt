package com.the.firsttask.utils


import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData


class NetworkUtils {
    var networkState: MutableLiveData<String> = MutableLiveData()


    companion object {
        var isConnected: Boolean = false
        var networkUtil: NetworkUtils=NetworkUtils()


        fun getNetworkState(): MutableLiveData<String> {
            return networkUtil.networkState
        }


        private val connectivityCallback: NetworkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
                networkUtil.networkState.postValue(ConstantHelper.NETWORK_CONNECT)
                Log.d("network",ConstantHelper.NETWORK_CONNECT )

            }

            override fun onLost(network: Network) {
                isConnected = false
                networkUtil.networkState.postValue(ConstantHelper.NETWORK_LOST)
                Log.d("network", ConstantHelper.NETWORK_LOST)
            }
        }

        fun checkConnectivity(context: Context) {

            val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
            connectivityManager?.registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(), connectivityCallback
            )
            Log.d("network", "registerNetworkCallback: ")
        }


        fun unregisterCallback(context:Context){

            val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
            try {
                connectivityManager?.unregisterNetworkCallback(connectivityCallback)
            }
            catch (e:Exception) {
                Log.d("network", "unregister failed")
            }

            Log.d("network", "unregisterCallback: ", )
        }
    }

}