package com.karebo.teamapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.karebo.teamapp.databinding.FragmentMeterAuditBinding
import com.karebo.teamapp.meteraudit.chartFragment
import com.karebo.teamapp.meteraudit.listfragment
import com.karebo.teamapp.meteraudit.mapfragment
import com.karebo.teamapp.roomdata.RoomDb
import com.karebo.teamapp.utils.LoaderHelper

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
        binding.tlMeterAudit.selectTab( binding.tlMeterAudit.getTabAt(1));


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
            var fragment:Fragment=listfragment()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.offline, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.offline_count)

        val mainbodydao= RoomDb.getAppDatabase((requireContext()))?.mainbodydao()
        var count=  mainbodydao?.getAllMainBodyCount()

        item.setTitle(count.toString());
        super.onPrepareOptionsMenu(menu)
    }



}