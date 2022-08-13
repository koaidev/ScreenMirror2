package com.bangbangcoding.screenmirror.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.adapter.DocumentAdapter
import com.bangbangcoding.screenmirror.databinding.ActivityDocumentBinding
import com.bangbangcoding.screenmirror.databinding.PopupFilterBinding
import com.bangbangcoding.screenmirror.db.model.DocumentItem
import com.bangbangcoding.screenmirror.db.viewmodel.DocumentViewModel
private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
class DocumentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentBinding
    private lateinit var documentViewModel: DocumentViewModel
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        documentViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init tool bar
        val toolBar = binding.toolBar
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.document)
        documentViewModel.getAllDocuments(application.contentResolver)
        setRCVDocument()

        binding.imgFilter.setOnClickListener {
            setPopupFilter()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPopupFilter() {
        val settingBinding = PopupFilterBinding.inflate(layoutInflater)
        val popupSetting = PopupWindow(this)
        popupSetting.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupSetting.contentView = settingBinding.root
        popupSetting.isOutsideTouchable = true
        popupSetting.showAsDropDown(binding.imgFilter)
        popupSetting.setTouchInterceptor(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                popupSetting.dismiss()
                return@OnTouchListener true
            }
            false
        })

        /**
         * ALL
         * */
        settingBinding.rdAll.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckAll.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.all))
                    .apply()
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()
            }
        }

        documentViewModel.isCheckAll.observe(this) {
            if (it) {
                documentViewModel.getAllDocuments(application.contentResolver)
            }
        }
        /**
         * word
         * */
        settingBinding.rdWord.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckWord.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.word))
                    .apply()
                documentViewModel.isCheckAll.value = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()
            }
        }

        documentViewModel.isCheckWord.observe(this) {
            if (it) {
//                documentViewModel.getAllWords(application.contentResolver)
            }
        }
        /**
         * pdf
         * */
        settingBinding.rdPdf.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckPDF.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.pdf))
                    .apply()
                documentViewModel.isCheckAll.value = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()
            }
        }

        documentViewModel.isCheckPDF.observe(this) {
            if (it) {
//                documentViewModel.getAllPdfs(application.contentResolver)
            }
        }
        /**
         * excel
         * */
        settingBinding.rdExcel.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckXLS.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.excel))
                    .apply()
                documentViewModel.isCheckAll.value = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()

            }
        }

        documentViewModel.isCheckXLS.observe(this) {
            if (it) {
//                documentViewModel.getAllExcels(application.contentResolver)
            }
        }
        /**
         * ppt
         * */
        settingBinding.rdPptx.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckPPT.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.slide))
                    .apply()
                documentViewModel.isCheckAll.value = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()

            }
        }

        documentViewModel.isCheckPPT.observe(this) {
            if (it) {
//                documentViewModel.getAllPPts(application.contentResolver)
            }
        }
        /**
         * txt
         * */
        settingBinding.rdTxt.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckTxt.value = isChecked
            if (isChecked) {
                sharedPref.edit().putString("current_document", resources.getString(R.string.txt))
                    .apply()
                documentViewModel.isCheckAll.value = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckWord.value = false
                popupSetting.dismiss()

            }
        }

        documentViewModel.isCheckTxt.observe(this) {
            if (it) {
//                documentViewModel.getAllTXTs(application.contentResolver)
            }
        }
        binding.imgReadFile.setOnClickListener { openDirectory() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return

            contentResolver.takePersistableUriPermission(
                directoryUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            showDirectoryContents(directoryUri)
        }
    }
    private fun showDirectoryContents(directoryUri: Uri) {

    }

    private fun openDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setRCVDocument() {
        val documentAdapter = DocumentAdapter(activity = this@DocumentActivity)
        binding.rcvDocument.adapter = documentAdapter
        documentViewModel.documentItems.observe(this) {
            documentAdapter.updateData(it)
            binding.txtNumberFile.text = "${if (it.size> 0)it.size else 0} Files"
            if (it.size == 0) {
                binding.txtNoDocumentFound.visibility = View.VISIBLE
                binding.rcvDocument.visibility = View.GONE
            } else {
                binding.txtNoDocumentFound.visibility = View.GONE
                binding.rcvDocument.visibility = View.VISIBLE
            }
            checkTotalSize(it)
        }
    }

    private fun checkTotalSize(documents: ArrayList<DocumentItem>) {
        var total = 0
        for (item in documents) {
            total += item.size / 1000
        }
        var sizeText = ""
        sizeText = if (total > 1024) {
            if (total / 1024 / 1024 > 1) {
                sizeText.plus(String.format("%.2f", (total.toDouble() / 1024 / 1024))).plus(" GB")
            } else {
                sizeText.plus(String.format("%.2f", (total.toDouble() / 1024))).plus(" MB")
            }
        } else {
            sizeText.plus(total).plus(" KB")
        }

        binding.txtNumberSize.text = if (documents.size > 0) sizeText else "0 MB"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}