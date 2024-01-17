package com.manoj.clean.ui.common.base.compooundviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.manoj.clean.databinding.ErrorViewBinding

class ErrorView(context: Context, attributeSet: AttributeSet?) :
    FrameLayout(context, attributeSet) {
    var onRetry: (() -> Unit?)? = null

    init {
        val bindingImpl = ErrorViewBinding.inflate(LayoutInflater.from(context), this, false)
        bindingImpl.imageViewRefresh.setOnClickListener {
            onRetry?.invoke()
        }
    }
}