package com.karebo.teamapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.karebo.teamapp.databinding.FragmentAccessStatusBinding
import com.karebo.teamapp.databinding.FragmentMeterInformationFormBinding


class MeterInformationForm : Fragment() {

    private var _binding: FragmentMeterInformationFormBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeterInformationFormBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
        binding.btNext.setOnClickListener{
//            Navigation.findNavController(root).navigate(
//                R.id.action_nav_meterinformation_to_nav_tidprocessone,
//
//                )
        }
        return root
    }


}