package com.karebo.teamapp


import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.karebo.teamapp.databinding.FragmentSignatureBinding
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Signature : Fragment() {


    private var _binding: FragmentSignatureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignatureBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        binding.btClear.setOnClickListener{
            binding.ink2.clear()
        }
        binding.btSubmit.setOnClickListener{

            var jobnumber=SharedPreferenceHelper.getInstance(requireContext()).getJobNumber().toInt()
            jobnumber++
            SharedPreferenceHelper.getInstance(requireContext()).setJobNumber(jobnumber.toString())
            addInModel()

            val bundle = Bundle()
            bundle.putString("data","from signature" )
            Navigation.findNavController(root).navigate(
                R.id.action_nav_signature_to_nav_accessstatus,bundle

                )
        }
        return root
    }

     fun addInModel() {
         val drawing: Bitmap = binding.ink2.getBitmap(resources.getColor(R.color.white))
         var base64= ConstantHelper.bitmapToBase64(drawing)
         ConstantHelper.Feedback.put("Signature64",base64)

         ConstantHelper.Components.put("Feedback",ConstantHelper.Feedback)
         ConstantHelper.meterModelJson.put("Components", ConstantHelper.Components)
         ConstantHelper.meterModelJson.put("Serial",ConstantHelper.SERIAL)
         ConstantHelper.meterModelJson.put("Edges", JSONObject.NULL)




         val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
         val formattedDate = sdf.format(Date())
         Log.e("TAG", "addInModel:timestemp "+formattedDate, )
         ConstantHelper.Duration.put("Value",formattedDate)

         ConstantHelper.submitMeterDataJSON.put("Duration",ConstantHelper.Duration)





         ConstantHelper.Meters.put(ConstantHelper.SERIAL,ConstantHelper.meterModelJson)

         ConstantHelper.submitMeterDataJSON.put("Meters",ConstantHelper.Meters)





         //clear Constant value
         ConstantHelper.meterModelJson =  JSONObject()
         ConstantHelper.Components =  JSONObject()
         ConstantHelper.Feedback =  JSONObject()
         ConstantHelper.Duration = JSONObject()


         ConstantHelper.SERIAL =  ""
         ConstantHelper. PropertyPictureUUID=""
         ConstantHelper. ZeroTokenPictureUUID=""
         ConstantHelper. TamperedWiresUUID=""
         ConstantHelper. TamperedWires2UUID=""
         ConstantHelper. TamperedWires3UUID=""
         ConstantHelper. KRNPictureUUID=""
         ConstantHelper. Last5TokenScreenshotUUID=""


         Log.e("json at Signature", ConstantHelper.meterModelJson.toString())
         Log.e("json at Signature final", ConstantHelper.submitMeterDataJSON.toString())
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
                    R.id.action_nav_signature_to_nav_meteraudit
                )
                true
            }
            R.id.action_logout -> {

                SharedPreferenceHelper.getInstance(requireContext()).clearData()
                Navigation.findNavController(binding.root).navigate(
                    R.id.action_nav_signature_to_nav_about
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item);
    }

}