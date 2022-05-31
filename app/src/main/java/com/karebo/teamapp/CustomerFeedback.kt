package com.karebo.teamapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.karebo.teamapp.databinding.FragmentAccessStatusBinding
import com.karebo.teamapp.databinding.FragmentCustomerFeedbackBinding
import com.the.firsttask.utils.ConstantHelper
import org.json.JSONObject

class CustomerFeedback : Fragment() {


    private var _binding: FragmentCustomerFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerFeedbackBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
        binding.btNext.setOnClickListener{
            addInModel()
            Navigation.findNavController(root).navigate(
                R.id.action_nav_customerfeedback_to_nav_signature,

                )
        }
        return root
    }

     fun addInModel() {

         var Questionnaire = JSONObject()
         Questionnaire.put("Are ypu happy with our process ?",binding.swHappy.isChecked)
         Questionnaire.put("Are you happy with our staff ?",binding.swStaff.isChecked)
         Questionnaire.put("All tokens have been used before the rollover",binding.swToken.isChecked)
         Questionnaire.put("All in order ?",binding.swOrder.isChecked)
         ConstantHelper.Feedback.put("Questionnaire",Questionnaire)



         ConstantHelper.Components.put("Feedback",ConstantHelper.Feedback)
         ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
         Log.e("json at Feedback", ConstantHelper.meterModelJson.toString(), )
    }


}