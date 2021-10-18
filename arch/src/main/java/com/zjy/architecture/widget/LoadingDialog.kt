package com.zjy.architecture.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import com.zjy.architecture.R

/**
 * @author zhengjy
 * @since 2021/10/18
 * Description:
 */
class LoadingDialog(
    context: Context,
    var _cancelable: Boolean
) : Dialog(context, R.style.loadingdialog) {
    private var tvTips: TextView? = null

    private var tips: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        setCancelable(_cancelable)
        tvTips = findViewById(R.id.tv_tips)
        if (!tips.isNullOrEmpty()) {
            tvTips?.text = tips
        }
    }

    fun setTipsText(tips: String?): LoadingDialog {
        this.tips = tips
        if (!tips.isNullOrEmpty()) {
            tvTips?.text = tips
        }
        return this
    }

    companion object {
        protected const val TAG = "LoadingDialog"
    }
}