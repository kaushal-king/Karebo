package com.karebo.teamapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.FragmentBarcodeScanBinding
import com.karebo.teamapp.dataclass.MeterDataModel
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarcodeScan : Fragment() {


    private var _binding: FragmentBarcodeScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBarcodeScanBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
        binding.tvBarcodeNumber.visibility=View.GONE
        binding.btNext.visibility=View.GONE
        binding.tvAddress.text=ConstantHelper.ADDRESS
        binding.btNext.setOnClickListener{
            meterData(root)
        }

        binding.btSkip.setOnClickListener{
            ConstantHelper.SERIAL="TEST0123456"
            SharedPreferenceHelper.getInstance(requireContext()).setMeterdata(null)
            Navigation.findNavController(root).navigate(
                R.id.action_nav_barcodescan_to_nav_tidprocessone,
            )
        }
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val activity = requireActivity()
        codeScanner = CodeScanner(activity, binding.scannerBarcode)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                //Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                binding.tvBarcodeNumber.text=it.text

                binding.tvBarcodeNumber.visibility=View.VISIBLE
                binding.btNext.visibility=View.VISIBLE
            }
        }
        binding.scannerBarcode.setOnClickListener {
            codeScanner.startPreview()
        }


        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }






    fun meterData(root:View){
        LoaderHelper.showLoader(requireContext())

        var project=ConstantHelper.currentSelectd.project
        var municipality=ConstantHelper.currentSelectd.municipality
        var serialNumber=binding.tvBarcodeNumber.text.toString()
        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)

        Log.e("TAG", "meterData: serialNumber: "+serialNumber, )
        Log.e("TAG", "meterData municipality: "+municipality, )
        Log.e("TAG", "meterData project: "+project, )
        val call = api?.meterData(project,serialNumber,municipality)
        call?.enqueue(object : Callback<MeterDataModel> {
            override fun onResponse(
                call: Call<MeterDataModel>,
                response: Response<MeterDataModel>
            ) {

                if(response.isSuccessful){

                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of meterData " + statuscode)

                    if(statuscode==200){
                        addInModel()
                        LoaderHelper.dissmissLoader()
                        val JsonString: String =
                        GsonParser.gsonParser!!.toJson(response.body())
                        SharedPreferenceHelper.getInstance(requireContext()).setMeterdata(JsonString)
                        Navigation.findNavController(root).navigate(
                            R.id.action_nav_barcodescan_to_nav_tidprocessone,
                            )
                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(),"some error occured",Toast.LENGTH_SHORT).show()

                    }


                }
                else{
                    LoaderHelper.dissmissLoader()
                    Log.e("TAG", "Statuscode of meterData " + response.code())
                    Toast.makeText(requireContext(),
                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<MeterDataModel>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }



    fun addInModel(){
        ConstantHelper.SERIAL=binding.tvBarcodeNumber.text.toString()

    }

}