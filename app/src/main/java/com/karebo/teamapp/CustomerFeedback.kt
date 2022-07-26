package com.karebo.teamapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.karebo.teamapp.databinding.FragmentAccessStatusBinding
import com.karebo.teamapp.databinding.FragmentCustomerFeedbackBinding
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
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


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.drawer, menu)
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
                    R.id.action_nav_customerfeedback_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_customerfeedback_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }

}