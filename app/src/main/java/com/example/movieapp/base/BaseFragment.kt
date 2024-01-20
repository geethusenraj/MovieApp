package com.example.movieapp.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.utils.DialogUtil

abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<*>> : Fragment() {

    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null
    private var mActivity: BaseActivity<*, *>? = null
    private var progressDialog: Dialog? = null
    private var progressText: TextView? = null
    private var dialog: Dialog? = null

    abstract fun getLayoutId(): Int
    abstract fun getBindingVariable(): Int
    abstract fun getViewModel(): V

    open fun showProgress(message: String?) {
        val msg = Message.obtain()
        msg.what = BaseActivity.HANDLER_SHOW_PROGRESS
        msg.obj = message
        mProgressHandler.sendMessage(msg)
    }

    open fun dismissProgress() {
        mProgressHandler.sendEmptyMessage(BaseActivity.HANDLER_DISMISS_PROGRESS)
    }

    private val mProgressHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BaseActivity.HANDLER_SHOW_PROGRESS -> {
                    showLoader(msg.obj as String)
                }
                BaseActivity.HANDLER_DISMISS_PROGRESS -> {
                    dismissLoader()
                }
            }
        }
    }

    private fun showLoader(message: String?) {
        if (activity != null && !activity?.isFinishing!!) {
            if (progressDialog == null) {
                initLoadingDialog()
            }
            if (!TextUtils.isEmpty(message)) {
                progressText?.text = message
            } else {
                progressText?.text = ""
            }
            progressDialog?.show()
        }
    }

    private fun dismissLoader() {
        if (progressDialog != null && progressDialog?.isShowing!!) {
            //get the Context object that was used to great the dialog
            val context = (progressDialog?.context as ContextWrapper).baseContext
            // if the Context used here was an activity AND it hasn't been finished or destroyed
            // then dismiss it
            if (context is Activity) {
                if (!context.isFinishing && !context.isDestroyed) {
                    dismissWithTryCatch(progressDialog)
                }
            } else  // if the Context used wasn't an Activity, then dismiss it too
                dismissWithTryCatch(progressDialog)
        }
    }

    open fun dismissWithTryCatch(dialog: Dialog?) {
        try {
            progressDialog?.dismiss()
        } catch (e: IllegalArgumentException) {
            // Do nothing.
        } catch (e: Exception) {
            // Do nothing.
        } finally {
            progressDialog = null
        }
    }

    private fun initLoadingDialog() {
        if (activity != null && !activity?.isFinishing!!) {
            progressDialog = Dialog(requireActivity())
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            if (progressDialog?.window != null) {
                progressDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
                //progressDialog.getWindow().setGravity(Gravity.TOP);
                progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            val inflater = layoutInflater
            val dialogView: View = inflater.inflate(R.layout.progressbar_view, null)
            progressDialog?.setContentView(dialogView)
            progressText = dialogView.findViewById(R.id.status_text)
        }
    }

    fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }

    fun setDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding?.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding?.lifecycleOwner = this
        mViewDataBinding?.executePendingBindings()
    }

    open fun getBaseActivity(): BaseActivity<*, *>? {
        return mActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity<*, *>
    }

    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

    fun showErrorDialog(message: String, title: String?){
        if (dialog == null || activity != null && !activity?.isFinishing!!) {
            dialog = DialogUtil.genericAlertDialog(this.context, title,
                message, activity?.resources?.getString(R.string.ok)) {
                dialog?.dismiss()
                dialog = null
            }
            dialog?.show()
        }
    }


}