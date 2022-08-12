package com.bangbangcoding.screenmirror.web.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.web.list.RecyclerViewDialogItemAdapter
import com.bangbangcoding.screenmirror.web.list.RecyclerViewStringAdapter
import com.bangbangcoding.screenmirror.web.utils.DeviceUtils
import com.bangbangcoding.screenmirror.web.utils.ext.dimen
import com.bangbangcoding.screenmirror.web.utils.ext.inflater

object BrowserDialog {

    @JvmStatic
    fun show(
        activity: Activity,
        @StringRes title: Int,
        vararg items: DialogItem
    ) = show(activity, activity.getString(title), *items)

    fun showWithIcons(activity: Activity, title: String?, vararg items: DialogItem) {
        val builder = AlertDialog.Builder(activity)

        val layout = activity.inflater.inflate(R.layout.dialog_list, null)

        val titleView = layout.findViewById<TextView>(R.id.dialog_title)
        val recyclerView = layout.findViewById<RecyclerView>(R.id.dialog_list)

        val itemList = items.filter(DialogItem::isConditionMet)

        val adapter = RecyclerViewDialogItemAdapter(itemList)

        if (title?.isNotEmpty() == true) {
            titleView.text = title
        }

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = adapter
            setHasFixedSize(true)
        }

        builder.setView(layout)

        val dialog = builder.show()

        setDialogSize(activity, dialog)

        adapter.onItemClickListener = { item ->
            item.onClick()
            dialog.dismiss()
        }
    }

    @JvmStatic
    fun show(activity: Activity, title: String?, vararg items: DialogItem) {
        val builder = AlertDialog.Builder(activity)

        val layout = activity.inflater.inflate(R.layout.dialog_list, null)

        val titleView = layout.findViewById<TextView>(R.id.dialog_title)
        val recyclerView = layout.findViewById<RecyclerView>(R.id.dialog_list)

        val itemList = items.filter(DialogItem::isConditionMet)

        val adapter = RecyclerViewStringAdapter(
            itemList,
            convertToString = { activity.getString(this.title) })

        if (title?.isNotEmpty() == true) {
            titleView.text = title
        }

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = adapter
            setHasFixedSize(true)
        }

        builder.setView(layout)

        val dialog = builder.show()

        setDialogSize(activity, dialog)

        adapter.onItemClickListener = { item ->
            item.onClick()
            dialog.dismiss()
        }
    }

    @JvmStatic
    fun showPositiveNegativeDialog(
        activity: Activity,
        @StringRes title: Int,
        @StringRes message: Int,
        messageArguments: Array<Any>? = null,
        positiveButton: DialogItem,
        negativeButton: DialogItem,
        onCancel: () -> Unit
    ) {
        val messageValue = if (messageArguments != null) {
            activity.getString(message, *messageArguments)
        } else {
            activity.getString(message)
        }
        val dialog = AlertDialog.Builder(activity).apply {
            setTitle(title)
            setMessage(messageValue)
            setOnCancelListener { onCancel() }
            setPositiveButton(positiveButton.title) { _, _ -> positiveButton.onClick() }
            setNegativeButton(negativeButton.title) { _, _ -> negativeButton.onClick() }
        }.show()

        setDialogSize(activity, dialog)
    }

    @JvmStatic
    fun showEditText(
        activity: Activity,
        @StringRes title: Int,
        @StringRes hint: Int,
        @StringRes action: Int,
        textInputListener: (String) -> Unit
    ) = showEditText(activity, title, hint, null, action, textInputListener)

    @JvmStatic
    fun showEditText(
        activity: Activity,
        @StringRes title: Int,
        @StringRes hint: Int,
        currentText: String?,
        @StringRes action: Int,
        textInputListener: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_text, null)
        val editText = dialogView.findViewById<EditText>(R.id.dialog_edit_text)

        editText.setHint(hint)
        if (currentText != null) {
            editText.setText(currentText)
        }

        val editorDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(
                action
            ) { _, _ -> textInputListener(editText.text.toString()) }

        val dialog = editorDialog.show()
        setDialogSize(activity, dialog)
    }

    @JvmStatic
    fun setDialogSize(context: Context, dialog: Dialog) {
        var maxWidth = context.dimen(R.dimen.dialog_max_size)
        val padding = context.dimen(R.dimen.padding_3)
        val screenSize = DeviceUtils.getScreenWidth(context)
        if (maxWidth > screenSize - 2 * padding) {
            maxWidth = screenSize - 2 * padding
        }
        val window = dialog.window
        window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Show the custom dialog with the custom builder arguments applied.
     */
    fun showCustomDialog(activity: Activity?, block: AlertDialog.Builder.(Activity) -> Unit) {
        activity?.let {
            AlertDialog.Builder(activity).apply {
                block(it)
                val dialog = show()
                setDialogSize(it, dialog)
            }
        }
    }

}
