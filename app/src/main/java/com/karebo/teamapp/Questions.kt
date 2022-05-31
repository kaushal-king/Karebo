package com.karebo.teamapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.FragmentQuestionsBinding
import com.karebo.teamapp.dataclass.CodeListDataClass
import com.karebo.teamapp.dataclass.questionDataModel
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.adapter.QuestionAdapter
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Questions : Fragment() {

    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!
    private var adapter: QuestionAdapter? = null
    var adapterToolbox: ArrayAdapter<String>? = null
    var list:MutableList<questionDataModel> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestionsBinding.inflate(
            inflater,container,false)

        val root: View = binding.root

        loadQuestionsList()
        loadQuestion()

        binding.btSendOtp.setOnClickListener{

            addToolbox(root)

//            Navigation.findNavController(root).navigate(
//                R.id.action_nav_question_to_nav_otpscreen,
//
//                )
        }

        return root

    }

    fun addToolbox(root:View) {
        LoaderHelper.showLoader(requireContext())
        var  teamkey=SharedPreferenceHelper.getInstance(requireContext()).getTeamKey()




        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.addToolbox(teamkey)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {

                if(response.isSuccessful){
                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of Question " + statuscode)

                    if(statuscode==200){

                       LoaderHelper.dissmissLoader()
                        val bundle = Bundle()
                        bundle.putString("msg",response.body()?.string() )
                        Navigation.findNavController(root).navigate(
                            R.id.action_nav_question_to_nav_otpscreen,bundle
                            )
                        binding.tvOtp.text=response.body()?.string()
                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(), response.body()?.string(), Toast.LENGTH_SHORT)
                            .show()
                    }


                }
                else{
//                    Log.e("TAG", "AddToolBox :"+response.body()?.string(), )
//                    Log.e("TAG", "AddToolBox :"+response.errorBody()?.string(), )
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

    fun loadQuestion() {
         Log.e("TAG", "loadQuestion: ")


//         var list:MutableList<questionDataModel> = mutableListOf()
//         list.add(questionDataModel(1,"Do you have ID card and BIBs ?"))

         adapter = QuestionAdapter(
             list,
             requireActivity()
         )
         adapter!!.switchStatus.observe(viewLifecycleOwner){
//             Log.e("TAG", "loadQuestion: " + it)
             if(it.equals("false")){
                 setVisibilityFalse()
             }else{
                 setVisibility()
             }

         }

         binding.rvQuestion.adapter = adapter
         binding.rvQuestion.adapter?.notifyDataSetChanged()

//         Log.e("TAG", "loadQuestion:gone ")

    }

    private fun setVisibility() {
        //binding.llToolbox.visibility=View.VISIBLE
        binding.btSendOtp.visibility=View.VISIBLE
        binding.tvOtp.visibility=View.VISIBLE
    }

    private fun setVisibilityFalse() {
       // binding.llToolbox.visibility=View.GONE
        binding.btSendOtp.visibility=View.GONE
        binding.tvOtp.visibility=View.GONE
    }


//    fun loadToolBox(){
//        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
//        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)
////        Log.e("TAG", "Question: " + data.Toolbox.toString())
//
//        adapterToolbox = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, data.Toolbox)
//        adapterToolbox?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spToolbox.adapter = adapterToolbox
//    }

    fun loadQuestionsList(){
        var codelist=  SharedPreferenceHelper.getInstance(requireContext()).getCodeList()
        val data = GsonParser.gsonParser!!.fromJson(codelist, CodeListDataClass::class.java)

        var i=1
        data.SHEQ.forEach {
            list.add(questionDataModel(i,it))
            i++
        }
    }
}