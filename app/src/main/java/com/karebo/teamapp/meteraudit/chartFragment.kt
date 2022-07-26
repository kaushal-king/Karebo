package com.karebo.teamapp.meteraudit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.karebo.teamapp.Api.Api
import com.karebo.teamapp.Api.ApiClient
import com.karebo.teamapp.R
import com.karebo.teamapp.databinding.FragmentChartBinding
import com.karebo.teamapp.dataclass.CounterDataClass
import com.karebo.teamapp.utils.LoaderHelper
import com.the.firsttask.sharedpreference.SharedPreferenceHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class chartFragment : Fragment() {

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!
    val failedvalues: ArrayList<BarEntry> = ArrayList()
    val auditedvalues: ArrayList<BarEntry> = ArrayList()
    val metersvalues: ArrayList<BarEntry> = ArrayList()
    val completevalues: ArrayList<BarEntry> = ArrayList()
    val assignvalues: ArrayList<BarEntry> = ArrayList()

   var numMap :  HashMap<Int, String> =  HashMap();



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChartBinding.inflate(
            inflater,container,false)
        val root: View = binding.root

        numMap.put(10, "Failed");
        numMap.put(20, "Audited");
        numMap.put(30, "Meters");
        numMap.put(40, "Completed");
        numMap.put(50, "Assigned");

        binding.swipeChart.setOnRefreshListener(OnRefreshListener {
           loadCharData()
            binding.swipeChart.isRefreshing = false
        })
        LoaderHelper.showLoader(requireContext())
        loadCharData()

        binding.chart.getLegend().setEnabled(false);
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawValueAboveBar(true)
        binding.chart.getDescription().setEnabled(false)
        binding.chart.setMaxVisibleValueCount(60)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawGridBackground(false)

        val xl: XAxis =         binding.chart.getXAxis()


        xl.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
//                Log.e("TAG", "getFormattedValue: $value")
                return numMap[value.toInt()]!!
            }
        })
        xl.position = XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.granularity = 10f
        xl.setLabelCount(6, false)

        val yl: YAxis =     binding.chart.getAxisLeft()
        //yl.typeface = tfLight
        yl.setDrawAxisLine(true)
        yl.setDrawGridLines(true)
        //        y.setTypeface(tfLight);
        yl.setLabelCount(6, false)
        yl.axisMinimum = 0f // this replaces setStartAtZero(true)

        val yr: YAxis =         binding.chart.getAxisRight()
        //yr.typeface = tfLight
        yr.setDrawAxisLine(true)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f // this replaces setStartAtZero(true)

        binding.chart.setFitBars(true)
        binding.chart.animateY(2500)

        val l: Legend =         binding.chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.xEntrySpace = 4f








        return root
    }




    private fun setData(s:String,values: ArrayList<BarEntry>) {
        val barWidth = 9f
        val spaceForBar = 10f


        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
        val set4: BarDataSet
        val set5: BarDataSet



        if (binding.chart.getData() != null &&
            binding.chart.getData().getDataSetCount() > 0
        ) {
            set1 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.splash_bg_color)

            set1.values = failedvalues
            set1.label = "Failed"
            set1.setColors(R.color.green2)

            set2 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.splash_bg_color)

            set2.values = auditedvalues
            set2.label = "Audited"
            set2.setColors(R.color.green2)


            set3 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.splash_bg_color)
            set3.setColors(R.color.green2)
            set3.values = metersvalues
            set3.label = "Meters"

            set4 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.splash_bg_color)
            set4.setColors(R.color.green2)
            set4.values = completevalues
            set4.label = "Completed"

            set5 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.splash_bg_color)
            set5.setColors(R.color.green2)
            set5.values = assignvalues
            set5.label = "Assign"

            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "Failed")

            set1.setColors(
                intArrayOf(R.color.green2),
                requireContext()
            )
            set1.values = failedvalues
            set1.label = "Failed"


            set2 = BarDataSet(values, "Audited")
            set2.setColors(
                intArrayOf(R.color.green2),
                requireContext()
            )
            set2.values = auditedvalues
            set2.label = "Audited"


            set3 = BarDataSet(values, "Meters")
            set3.setColors(
                intArrayOf(R.color.green2),
                requireContext()
            )
            set3.values = metersvalues
            set3.label = "Meters"


            set4 = BarDataSet(values, "Completed")
            set4.setColors(
                intArrayOf(R.color.green2),
                requireContext()
            )
            set4.values = completevalues
            set4.label = "Completed"


            set5 = BarDataSet(values, "Assigned")

            set5.setColors(
                intArrayOf(R.color.green2),
                requireContext()
            )



            set5.values = assignvalues
            set5.label = "Assigned"

            val dataSets: ArrayList<IBarDataSet> = ArrayList()

            dataSets.add(set1)
            dataSets.add(set2)
            dataSets.add(set3)
            dataSets.add(set4)
            dataSets.add(set5)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)

            // data.setValueTypeface(tfLight)
            data.barWidth = barWidth
            binding.chart.setData(data)
        }
    }




    fun loadCharData(){
        var  pin= SharedPreferenceHelper.getInstance(requireContext()).getOtp()


        val client = ApiClient()
        val api = client.getClient()?.create(Api::class.java)
        val call = api?.count(pin)
        call?.enqueue(object : Callback<CounterDataClass> {
            override fun onResponse(
                call: Call<CounterDataClass>,
                response: Response<CounterDataClass>
            ) {

                if(response.isSuccessful){

                    var statuscode=response.code()
                    Log.e("TAG", "Statuscode of ChartData " + statuscode)

                    if(statuscode==200){
                        failedvalues.add(
                            BarEntry(
                                1.toFloat()*10f, response.body()?.Failed!!.toFloat()
                            )
                        )
                        auditedvalues
                            .add(
                                BarEntry(
                                    2.toFloat()*10f, response.body()?.Audited!!.toFloat()
                                )
                            )
                        metersvalues
                            .add(
                                BarEntry(
                                    3.toFloat()*10f, response.body()?.Meters!!.toFloat()
                                )
                            )
                        completevalues
                            .add(
                                BarEntry(
                                    4.toFloat()*10f, response.body()?.Completed!!.toFloat()
                                )
                            )
                        assignvalues
                            .add(
                                BarEntry(
                                    5.toFloat()*10f, response.body()?.Assigned!!.toFloat()
                                )
                            )

                        setData("",failedvalues)
                        LoaderHelper.dissmissLoader()

                    }
                    else    {
                        LoaderHelper.dissmissLoader()
                        Toast.makeText(requireContext(), "some error occured", Toast.LENGTH_SHORT)
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

            override fun onFailure(call: Call<CounterDataClass>, t: Throwable) {
                LoaderHelper.dissmissLoader()
                Toast.makeText(requireContext(), "Offline Mode", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }


}