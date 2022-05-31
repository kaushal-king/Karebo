package com.karebo.teamapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.karebo.teamapp.R
import com.karebo.teamapp.dataclass.meterauditDataModel

class jobnumberAdapter(private val mList: List<String>,
                       var mCtx: Context,
) : RecyclerView.Adapter<jobnumberAdapter.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_complete_job, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.jobnum.text=item
    }

    override fun getItemCount(): Int {
        return mList.size
    }





    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jobnum: TextView = itemView.findViewById(R.id.tv_jobnum)


    }

}