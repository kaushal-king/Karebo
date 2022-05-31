package com.karebo.teamapp.utils

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.FadingCircle
import com.karebo.teamapp.R


class DialogHelper(context: Context) {

    private var context=context
    var alert: AlertDialog? = null

companion object{
    private var INSTANCE: DialogHelper? = null
    fun getInstance(
        mctx: Context,
    ): DialogHelper {
        return INSTANCE ?: synchronized(this) {
            val instance = DialogHelper(mctx)
            instance
        }
    }
}


     fun showLoader() {
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