package com.karebo.teamapp.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.FadingCircle
import com.karebo.teamapp.R

object LoaderHelper {

    var alert: AlertDialog? = null


    fun showLoader(context:Context) {
        val builder = AlertDialog.Builder(context, R.style.WrapContentDialog)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.dialog_loader, null)
        builder.setView(v)
        builder.setCancelable(false)
        alert = builder.create()
        val progressBar = v.findViewById<ProgressBar>(R.id.loader)
        val cc = FadingCircle()
        progressBar.indeterminateDrawable = cc
        alert?.show()

    }

    fun dissmissLoader(){
        alert?.dismiss()
    }

}