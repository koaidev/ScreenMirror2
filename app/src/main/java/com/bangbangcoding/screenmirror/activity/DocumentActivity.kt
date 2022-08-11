package com.bangbangcoding.screenmirror.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.adapter.DocumentAdapter
import com.bangbangcoding.screenmirror.databinding.ActivityDocumentBinding
import com.bangbangcoding.screenmirror.databinding.PopupFilterBinding
import com.bangbangcoding.screenmirror.model.DocumentItem
import com.bangbangcoding.screenmirror.viewmodel.DocumentViewModel

class DocumentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentBinding
    private lateinit var documentViewModel: DocumentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
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
         * word
         * */
        settingBinding.cbWord.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckWord.value = isChecked
            if (isChecked) {
                settingBinding.cbExcel.isChecked = false
                settingBinding.cbSlide.isChecked = false
                settingBinding.cbTxt.isChecked = false
                settingBinding.cbPdf.isChecked = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()


            } else {
                documentViewModel.getAllDocuments(application.contentResolver)
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
        settingBinding.cbPdf.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckPDF.value = isChecked
            if (isChecked) {
                settingBinding.cbExcel.isChecked = false
                settingBinding.cbSlide.isChecked = false
                settingBinding.cbTxt.isChecked = false
                settingBinding.cbWord.isChecked = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()
            } else {
                documentViewModel.getAllDocuments(application.contentResolver)
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
        settingBinding.cbExcel.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckXLS.value = isChecked
            if (isChecked) {
                settingBinding.cbWord.isChecked = false
                settingBinding.cbSlide.isChecked = false
                settingBinding.cbTxt.isChecked = false
                settingBinding.cbPdf.isChecked = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()

            } else {
                documentViewModel.getAllDocuments(application.contentResolver)
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
        settingBinding.cbSlide.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckPPT.value = isChecked
            if (isChecked) {
                settingBinding.cbExcel.isChecked = false
                settingBinding.cbWord.isChecked = false
                settingBinding.cbTxt.isChecked = false
                settingBinding.cbPdf.isChecked = false
                documentViewModel.isCheckWord.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckTxt.value = false
                popupSetting.dismiss()

            } else {
                documentViewModel.getAllDocuments(application.contentResolver)
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
        settingBinding.cbTxt.setOnCheckedChangeListener { _, isChecked ->
            documentViewModel.isCheckTxt.value = isChecked
            if (isChecked) {
                settingBinding.cbExcel.isChecked = false
                settingBinding.cbSlide.isChecked = false
                settingBinding.cbWord.isChecked = false
                settingBinding.cbPdf.isChecked = false
                documentViewModel.isCheckPPT.value = false
                documentViewModel.isCheckXLS.value = false
                documentViewModel.isCheckPDF.value = false
                documentViewModel.isCheckWord.value = false
                popupSetting.dismiss()

            } else {
                documentViewModel.getAllDocuments(application.contentResolver)
            }
        }

        documentViewModel.isCheckTxt.observe(this) {
            if (it) {
//                documentViewModel.getAllTXTs(application.contentResolver)
            }
        }
    }


    private fun setRCVDocument() {
        val documentAdapter = DocumentAdapter(activity = this@DocumentActivity)
        binding.rcvDocument.adapter = documentAdapter
        documentViewModel.documentItems.observe(this) {
            documentAdapter.updateData(it)
            binding.txtNumberFile.text = if(it.size> 0)"${it.size} Files" else "0 Files"
            checkTotalSize(it)
        }
    }

    private fun checkTotalSize(documents: ArrayList<DocumentItem>) {
        var total = 0
        documents.forEach{
            total += it.size?.div(1000) ?: 0
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