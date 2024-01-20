package com.example.movieapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.movieapp.R

object DialogUtil {

    fun getErrorDialogAccessDialog(context: Context?, title: String?, message: String?, btnPositiveText: String?,
                                   clickListener: View.OnClickListener?, btnNegativeText: String?, negativeListener: View.OnClickListener?): Dialog {
        val dialog = Dialog(context!!)
        if (dialog.window != null) {
            dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        dialog.setContentView(R.layout.layout_error_dialog)

        val tvTitle = dialog.findViewById<TextView>(R.id.title_tv)
        if (title == null || TextUtils.isEmpty(title)) {
            tvTitle.visibility = View.GONE
        } else {
            tvTitle.text = title
        }

        val tvMsg = dialog.findViewById<TextView>(R.id.information_tv)
        if (message == null || TextUtils.isEmpty(message)) {
            tvMsg.visibility = View.GONE
        } else {
            tvMsg.text = message
        }

        val btnPositive = dialog.findViewById<TextView>(R.id.allow_btn)
        if (btnPositiveText.isNullOrEmpty()) {
            btnPositive.visibility = View.GONE
        } else {
            btnPositive.text = btnPositiveText
            if (null != clickListener) {
                btnPositive.setOnClickListener(clickListener)
            }
        }
        val btnNegative = dialog.findViewById<TextView>(R.id.deny_btn)
        if (btnNegativeText.isNullOrEmpty()) {
            btnNegative.visibility = View.GONE
        } else {
            btnNegative.text = btnNegativeText
            if (null != negativeListener) {
                btnNegative.setOnClickListener(negativeListener)
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }

    fun genericAlertDialog(context: Context?, title: String?, message: String?, btnText: String?, clickListener: View.OnClickListener?): Dialog? {
        val dialog = Dialog(context!!)
        if (dialog.window != null) {
            dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, 3)
        }
        dialog.setContentView(R.layout.generic_alert_dialog)

        val tvTitle = dialog.findViewById<TextView>(R.id.title)
        if (title == null || TextUtils.isEmpty(title)) {
            tvTitle.visibility = View.GONE
        } else {
            tvTitle.text = title
        }
        val tvMsg = dialog.findViewById<TextView>(R.id.message)
        if (message == null || TextUtils.isEmpty(message)) {
            tvMsg.visibility = View.GONE
        } else {
            tvMsg.text = message
        }

        val btnPositive = dialog.findViewById<TextView>(R.id.okay_btn)
        if (btnText.isNullOrEmpty()) {
            btnPositive.visibility = View.GONE
        } else {
            btnPositive.text = btnText
            if (null != clickListener) {
                btnPositive.setOnClickListener(clickListener)
            }
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }
}