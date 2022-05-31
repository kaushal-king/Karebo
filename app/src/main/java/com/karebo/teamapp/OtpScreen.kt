package com.karebo.teamapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.Navigation
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.FragmentOtpScreenBinding
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OtpScreen : Fragment() {


    private var _binding: FragmentOtpScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpScreenBinding.inflate(
            inflater,container,false)

        val root: View = binding.root

        val args = arguments
        var msg=""
        if(args != null){
             msg = args!!.getString("msg")!!
        }
        else{
            msg="You Already have PIN No need to Submit"
        }


        binding.tvOtpSentMessage2.text=msg

        binding.llResendOtp.setOnClickListener{
            resendOtp()
        }

        if(SharedPreferenceHelper.getInstance(requireContext()).getOtp()!="null"){
            binding.etOtp.setText(SharedPreferenceHelper.getInstance(requireContext()).getOtp())
            loadJobCard(binding.etOtp.text.toString(),root)
            Log.e("TAG", "Stored PIN: "+SharedPreferenceHelper.getInstance(requireContext()).getOtp() )

        }

        binding.btLoadJobCard.setOnClickListener{
            if( binding.etOtp.text.isEmpty()){
                Toast.makeText(requireContext(),"Enter PIN", Toast.LENGTH_LONG).show()
            }
            else{

                loadJobCard(binding.etOtp.text.toString(),root)



            }

        }

        return root
    }

//    fun navigateMeterAudit(root:View){
//        val bundle = Bundle()
//        bundle.putString("otp", binding.etOtp.text.toString())
//        if(SharedPreferenceHelper.getInstance(requireContext()).getJobCard()!="null"){
//            var job=SharedPreferenceHelper.getInstance(requireContext()).getJobCard()
//            var joblist=GsonParser.gsonParser!!.fromJson(job, listjobcard::class.java)
//            Log.e("TAG", "joblist.jobcard: "+joblist.jobcard.toString(), )
//            ConstantHelper.list= joblist.jobcard!!
//
//        }
//        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_drawer).navigate(
//            R.id.action_nav_otpscreen_to_nav_meteraudit,bundle
//        )
//
//
////        Navigation.findNavController(root).navigate(
////
////        )
//    }

     fun loadJobCard(otp:String,root:View) {

         LoaderHelper.showLoader(requireContext())
         val client = ApiClient()
         val api = client.getClient()?.create(Api::class.java)
         val call = api?.checkOtp(otp)
         call?.enqueue(object : Callback<List<meterauditDataModel>> {
             override fun onResponse(
                 call: Call<List<meterauditDataModel>>,
                 response: Response<List<meterauditDataModel>>
             ) {

                 if(response.isSuccessful){

                     var statuscode=response.code()
                     Log.e("TAG", "Statuscode of LoadjobCard " + statuscode)

                     if(statuscode==200){

                         Log.e("TAG", "onResponse: "+response.body().toString(), )
                         SharedPreferenceHelper.getInstance(requireContext()).setOtp(otp)


                         compareAndSave(response.body()!!)
//                         ConstantHelper.list= response.body()!!
//
//                         val JsonString: String =
//                             GsonParser.gsonParser!!.toJson(response.body()!!)
//                         SharedPreferenceHelper.getInstance(requireContext()).setJobCard(JsonString)


                         try {
                             val inputMethodManager: InputMethodManager =
                                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                             inputMethodManager.hideSoftInputFromWindow(root!!.windowToken, 0)
                         } catch (e: Exception) {
                             print("exception is" + e.stackTraceToString())
                         }


                         Navigation.findNavController(root).navigate(
                                R.id.action_nav_otpscreen_to_nav_meteraudit,
                                )

                     }
                     else    {

                         LoaderHelper.dissmissLoader()
                         Toast.makeText(requireContext(), "error occured", Toast.LENGTH_SHORT)
                             .show()
                     }


                 }
                 else{
                     LoaderHelper.dissmissLoader()
                     SharedPreferenceHelper.getInstance(requireContext()).setOtp("null")
                     Navigation.findNavController(root).navigate(
                         R.id.action_nav_otpscreen_to_nav_question,
                     )
                     Toast.makeText(requireContext(),
                         response.errorBody()?.string(), Toast.LENGTH_SHORT)
                         .show()
                 }

             }

             override fun onFailure(call: Call<List<meterauditDataModel>>, t: Throwable) {
                 LoaderHelper.dissmissLoader()
                 Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                     .show()
             }

         })


    }




    fun resendOtp(){
        LoaderHelper.showLoader(requireContext())
        var  teamkey=SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()


        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.resendPin(teamkey)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if(response.isSuccessful){

                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of Resendpin " + statuscode)

                    if(statuscode==200){

                        LoaderHelper.dissmissLoader()
                        binding.tvOtpSentMessage2.text=response.body()?.string()
                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(), response.body()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
                    LoaderHelper.dissmissLoader()
                    Toast.makeText(requireContext(),
                        response.errorBody()?.string(), Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }



    fun compareAndSave(body: List<meterauditDataModel>) {



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
            list= completeJobNumberList.toList() as MutableList<String>
        }

        var newList:MutableList<meterauditDataModel> = mutableListOf()

        body.forEach {
            if(!list.contains(it.jobCardId)){

                newList.add(it)
            }
        }




        ConstantHelper.list= newList

        val JsonString: String =
            GsonParser.gsonParser!!.toJson(newList)
        SharedPreferenceHelper.getInstance(requireContext()).setJobCard(JsonString)




    }
}