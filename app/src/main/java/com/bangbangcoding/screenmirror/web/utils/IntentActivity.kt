package com.bangbangcoding.screenmirror.web.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bangbangcoding.screenmirror.web.ui.base.BaseActivity

/**
 * Created by Thao on 21/12/2021
 */

class IntentActivity : BaseActivity() {

    companion object {

        const val KEY_FRAGMENT = "KEY_FRAGMENT"
        const val KEY_THEME = "KEY_THEME"

        fun getIntent(
            context: Context?,
            clazz: Class<out Fragment>,
            args: Bundle? = null,
            theme: Int? = null
        ): Intent? {
            if (context == null) return null
            val intent = Intent(context, IntentActivity::class.java)
            intent.putExtra(KEY_FRAGMENT, clazz.name)
            if (args != null) intent.putExtras(args)
            if (theme != null) intent.putExtra(KEY_THEME, theme)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        if (bundle == null) finish()

        bundle?.let {
            if (bundle.containsKey(KEY_FRAGMENT)) {
                val fragmentClazzName = bundle.getString(KEY_FRAGMENT)!!
                try {
                    @Suppress("UNCHECKED_CAST")
                    val clazz = Class.forName(fragmentClazzName) as Class<out Fragment>
                    val fragment = clazz.newInstance().apply { arguments = bundle }
                    supportFragmentManager.beginTransaction().add(android.R.id.content, fragment)
                        .commit()
                    if (bundle.containsKey(KEY_THEME)) setTheme(bundle.getInt(KEY_THEME))
                } catch (e: Exception) {
                    finish()
                    return
                }
            } else {
                finish()
            }
        }
    }
}
