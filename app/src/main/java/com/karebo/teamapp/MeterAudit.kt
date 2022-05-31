package com.karebo.teamapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.loader.content.Loader
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.databinding.DialogLoaderBinding
import com.karebo.teamapp.databinding.FragmentMeterAuditBinding
import com.karebo.teamapp.dataclass.meterauditDataModel
import com.karebo.teamapp.meteraudit.chartFragment
import com.karebo.teamapp.meteraudit.listfragment
import com.karebo.teamapp.meteraudit.mapfragment
import com.karebo.teamapp.utils.GsonParser
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import com.the.firsttask.utils.ConstantHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeterAudit : Fragment() ,TabLayout.OnTabSelectedListener{

    private var _binding: FragmentMeterAuditBinding? = null
    private val binding get() = _binding!!
    var adapter1: adapterr? = null
    var manager: FragmentManager? = null
    lateinit var vieww:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeterAuditBinding.inflate(
            inflater,container,false)

        val root: View = binding.root
        vieww=root
        LoaderHelper.dissmissLoader()
        manager = childFragmentManager
        adapter1 = MeterAudit.adapterr(manager)
        binding.vpMeterAudit.setAdapter(adapter1)
        binding.vpMeterAudit.offscreenPageLimit = 3
        binding.tlMeterAudit.addOnTabSelectedListener(this)
        binding.vpMeterAudit.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tlMeterAudit))



        return root
    }



    override fun onTabSelected(tab: TabLayout.Tab?) {
        binding.vpMeterAudit.currentItem = tab!!.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }


    class adapterr(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            var fragment:Fragment=mapfragment()
            if (position == 0) {
                fragment = mapfragment()
            }
            if (position == 1) {
                fragment = listfragment()
            }
            if (position == 2) {
                fragment = chartFragment()
            }

            return fragment
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }


}