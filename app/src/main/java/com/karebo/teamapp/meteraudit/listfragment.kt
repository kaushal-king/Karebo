package com.karebo.teamapp.meteraudit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.karebo.teamapp.adapter.meterauditadapter
import com.karebo.teamapp.databinding.FragmentListfragmentBinding

import com.karebo.teamapp.dataclass.meterauditDataModel
import com.the.firsttask.utils.ConstantHelper

class listfragment() : Fragment() {


    private var _binding: FragmentListfragmentBinding? = null
    private val binding get() = _binding!!
    private var adapter: meterauditadapter? = null
     lateinit var viw:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListfragmentBinding.inflate(
            inflater,container,false)
        val root: View = binding.root
viw=root

        loadMeterAudit()

        return root
    }

    private fun loadMeterAudit() {
        var list: List<meterauditDataModel>? = ConstantHelper.list

        adapter = meterauditadapter(
            list!!,
            requireActivity(), viw

        )
        binding.rvQuestion.adapter = adapter
        binding.rvQuestion.adapter?.notifyDataSetChanged()


    }




}