@file:Suppress("DEPRECATION")

package com.bangbangcoding.screenmirror.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.androidadvance.topsnackbar.TSnackbar
import com.bangbangcoding.screenmirror.R
import com.shashank.sony.fancytoastlib.FancyToast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Common {
    var isProfileEdit: Boolean = false
    var isProfileMainEdit: Boolean = false
    var isAddOrUpdated: Boolean = false

    //toast messages
    fun getToast(activity: Activity, strTxtToast: String) {
        Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
    }

    fun isValidAmount(strPattern: String): Boolean {
        return Pattern.compile(
            "^[0-9]+([.][0-9]{2})?\$"
        ).matcher(strPattern).matches();
    }

    fun getLog(strKey: String, strValue: String) {
        Log.e(">>>---  $strKey  ---<<<", strValue)
    }

    fun isValidEmail(strPattern: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(strPattern).matches();
    }


    fun isCheckNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openActivity(activity: Activity, destinationClass: Class<*>?) {
        activity.startActivity(Intent(activity, destinationClass))
        activity.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }


    fun showErrorFullMsg(activity: Activity, message: String) {
        FancyToast.makeText(activity, message, FancyToast.LENGTH_SHORT, FancyToast.WARNING, false)
            .show()
    }

    fun showSuccessFullMsg(activity: Activity, message: String) {
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundColor(activity.resources.getColor(R.color.purple_500))
        val textView =
            snackbarView.findViewById<View>(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }


    fun setImageUpload(strParameter: String, mSelectedFileImg: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            strParameter,
            mSelectedFileImg.name,
            RequestBody.create("image/*".toMediaType(), mSelectedFileImg)
        )
    }

    fun setRequestBody(bodyData: String): RequestBody {
        return bodyData.toRequestBody("text/plain".toMediaType())
    }


    fun closeKeyBoard(activity: Activity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            try {
                val imm: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }



    //"2021-10-26 05:05:56"
    fun getDateTime(strDate: String): String? {
        val curFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US)
        return dateObj?.let { postFormater.format(it) }
    }
    
    fun shareUs(activity: Activity){
        val intent = Intent("android.intent.action.SEND")
        intent.type = "text/plain"
        intent.putExtra(
            "android.intent.extra.SUBJECT",
            activity.resources.getString(R.string.app_name)
        )
        val sb = StringBuilder()
        sb.append("Download this amazing ")
        sb.append(activity.resources.getString(R.string.app_name))
        sb.append(" app from play store\n\n")
        val sb2 = sb.toString()
        val sb3 = StringBuilder()
        sb3.append(sb2)
        sb3.append("https://play.google.com/store/apps/details?id=")
        sb3.append(activity.packageName)
        sb3.append("\n\n")
        intent.putExtra("android.intent.extra.TEXT", sb3.toString())
        activity.startActivity(Intent.createChooser(intent, "Choose one"))
    }

    fun goToPrivacyPage(activity: Activity){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://google.com")
        activity.startActivity(intent)
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}