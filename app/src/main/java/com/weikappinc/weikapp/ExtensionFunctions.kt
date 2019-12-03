package com.weikappinc.weikapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

fun <T : AppCompatActivity> AppCompatActivity.launchActivity(activityClass: Class<T>) {
    this.startActivity(Intent(this, activityClass))
}

fun <T : AppCompatActivity> Fragment.launchActivity(activityClass: Class<T>) {
    this.startActivity(Intent(this.context, activityClass))
}

fun AppCompatActivity.getLayoutBinding(activity: Activity, layout: Int): ViewDataBinding {
    return DataBindingUtil.setContentView(activity, layout)
}

fun Fragment.getLayoutBinding(layout: Int, inflater: LayoutInflater, container: ViewGroup?): ViewDataBinding {
    return DataBindingUtil.inflate<ViewDataBinding>(inflater, layout, container, false)
}

fun Context.showThisShort(message: String) {
    showToast(message, Toast.LENGTH_SHORT, this)
}

fun Context.showThisLong(message: String) {
    showToast(message, Toast.LENGTH_LONG, this)
}

private fun showToast(message: String, duration: Int, context: Context) {
    val toast = Toast.makeText(context, message, duration)
    val view = toast.view
    view.setBackgroundResource(R.drawable.toast_background_error_color)

    view.findViewById<TextView>(android.R.id.message).setPadding(40, 0, 40 ,0)

    toast.setGravity(Gravity.CENTER, 0, 46)

    toast.show()
}

fun ViewGroup.getLayoutBinding(layoutResource: Int) : ViewDataBinding {
    return DataBindingUtil.inflate(LayoutInflater.from(this.context), layoutResource, this, false)
}
