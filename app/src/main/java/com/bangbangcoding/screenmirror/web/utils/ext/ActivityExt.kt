package com.bangbangcoding.screenmirror.web.utils.ext

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.bangbangcoding.screenmirror.R

inline fun AppCompatActivity.addFragment(containerViewId: Int, f: () -> Fragment): Fragment {
    return f().apply {
        supportFragmentManager.beginTransaction().add(containerViewId, this).commit()
    }
}

inline fun AppCompatActivity.addFragment(
    containerViewId: Int,
    bundle: Bundle,
    f: () -> Fragment
): Fragment {
    return f().apply {
        arguments = bundle
        supportFragmentManager.beginTransaction().add(containerViewId, this).commit()
    }
}

inline fun AppCompatActivity.replaceFragment(containerViewId: Int, f: () -> Fragment): Fragment {
    return f().apply {
        supportFragmentManager.beginTransaction().replace(containerViewId, this).commit()
    }
}

/**
 * Displays a snackbar to the user with a [StringRes] message.
 *
 * NOTE: If there is an accessibility manager enabled on
 * the device, such as LastPass, then the snackbar animations
 * will not work.
 *
 * @param resource the string resource to display to the user.
 */
fun Activity.snackbar(@StringRes resource: Int) {
    val view = findViewById<View>(android.R.id.content)
    val snackbar = Snackbar.make(view, resource, Snackbar.LENGTH_SHORT)
//    val text = snackbar.view.findViewById<TextView>(android.R.id.snackbar_text)
    snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    snackbar.show()
}

fun Activity.snackbarAction(
    @StringRes resource: Int,
    @StringRes actionRes: Int,
    listener: View.OnClickListener
) {
    val view = findViewById<View>(android.R.id.content)
    val snackbar = Snackbar.make(view, resource, Snackbar.LENGTH_LONG)
    snackbar.setAction(
        actionRes, listener
    )
    snackbar.view.setBackgroundColor(Color.parseColor("#0099FF"))
    snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    snackbar.setActionTextColor(ContextCompat.getColor(view.context, R.color.white))
    val snackText = snackbar.view.findViewById<TextView>(
        com.google.android.material.R.id.snackbar_text)
    snackText.textSize = 16f;
    val snackTextAction = snackbar.view.findViewById<TextView>(
        com.google.android.material.R.id.snackbar_action)
    snackTextAction.textSize = 18f;
    snackbar.show()
}

/**
 * Display a snackbar to the user with a [String] message.
 *
 * @param message the message to display to the user.
 * @see snackbar
 */
fun Activity.snackbar(message: String) {
    val view = findViewById<View>(android.R.id.content)
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
//    val text = snackbar.view.findViewById<TextView>(android.R.id.snackbar_text)
    snackbar.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    snackbar.show()
}