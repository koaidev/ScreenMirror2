package com.bangbangcoding.screenmirror.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.ActivityMainBinding
import com.bangbangcoding.screenmirror.databinding.OopsDialogBinding
import com.bangbangcoding.screenmirror.databinding.PopupSettingBinding
import com.bangbangcoding.screenmirror.livedata.ConnectionLiveData
import com.bangbangcoding.screenmirror.utils.Common
import com.bangbangcoding.screenmirror.utils.PERMISSION_ALL
import com.bangbangcoding.screenmirror.utils.PermissionHelper
import com.bangbangcoding.screenmirror.web.ui.WebActivity
import com.google.android.material.snackbar.Snackbar
import com.shashank.sony.fancytoastlib.FancyToast


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /**
         * Check valid network
         * */
        val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this) { isConnected ->
            isConnected?.let {
                // do job
                if (it) {
                    binding.imgNetwork.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_wifi_connect_0,
                            resources.newTheme()
                        )
                    )
                    binding.txtNotiNetwork.text = "Network connected"
                } else {
                    binding.imgNetwork.setImageDrawable(
                        resources.getDrawable(
                            R.drawable.ic_baseline_wifi_off_24,
                            resources.newTheme()
                        )
                    )
                    binding.txtNotiNetwork.text = "Network not connected"
                }
            }
        }

    }

    private fun init() {
        /**
         * Handle click event
         */
        binding.mirror.setOnClickListener {
            showNotSupportDialog()
        }
        binding.cardMedia.setOnClickListener {
            Common.openActivity(this@MainActivity, MediaActivity::class.java)
        }
        binding.cardWeb.setOnClickListener {
            Common.openActivity(this@MainActivity, WebActivity::class.java)
        }
        binding.cardGuide.setOnClickListener {
            val intent = Intent(this@MainActivity, TutorialActivity::class.java)
            intent.putExtra("fromMain", true)
            startActivity(intent)
        }
        binding.cardDocument.setOnClickListener {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Common.openActivity(this@MainActivity, DocumentActivity::class.java)

                } else { //request for the permission
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            } else {
                //below android 11=======
                Common.openActivity(this@MainActivity, DocumentActivity::class.java)
            }
        }
        showPopupDialogSetting()
        /**
         * End handle click event
         */
    }

    private fun showNotSupportDialog() {
        val oopsBinding = OopsDialogBinding.inflate(layoutInflater)
        val dialogNotSupport = Dialog(this, R.style.MyDialog)
        dialogNotSupport.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogNotSupport.setContentView(oopsBinding.root)
        dialogNotSupport.setCanceledOnTouchOutside(false)
        dialogNotSupport.show()
        oopsBinding.btnYes.setOnClickListener{
            dialogNotSupport.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionHelper.hasPermissions(this, *PermissionHelper.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PermissionHelper.PERMISSIONS, PERMISSION_ALL)
        } else {
            init()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupDialogSetting() {

        val settingBinding = PopupSettingBinding.inflate(layoutInflater)
        val popupSetting = PopupWindow(this)
        settingBinding.cardSetting.setCardBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )
        settingBinding.cardSetting.background.alpha = 0
        settingBinding.cardSetting.cardBackgroundColor.withAlpha(0)
        popupSetting.contentView = settingBinding.root
        popupSetting.isOutsideTouchable = true
        settingBinding.ctRateUs.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        }

        settingBinding.ctShare.setOnClickListener {
            Common.shareUs(this)
        }

        settingBinding.ctPrivacy.setOnClickListener {
            Common.goToPrivacyPage(this)
        }
        binding.imgSetting.setOnClickListener {
            if (!popupSetting.isShowing) {
                popupSetting.showAsDropDown(binding.imgSetting)
            }
        }
        popupSetting.setTouchInterceptor(OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupSetting.dismiss()
                return@OnTouchListener true
            }
            false
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ALL -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    init()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Snackbar.make(
                        binding.root,
                        "Bạn cần cung cấp quyền truy cập để ứng dụng có thể hoạt động!",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Đồng ý") {
                            FancyToast.makeText(
                                this,
                                "Vui lòng đóng ứng dụng và cấp lại quyền truy cập.",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.CONFUSING,
                                false
                            ).show()
                            this@MainActivity.finish()
                        }.show()
                }
                return
            }
            101 -> {
                if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Common.openActivity(this@MainActivity, DocumentActivity::class.java)

                } else {
                    FancyToast.makeText(
                        this,
                        "Bạn cần cấp quyền để truy cập document.",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.WARNING, false
                    ).show()
                }
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


}