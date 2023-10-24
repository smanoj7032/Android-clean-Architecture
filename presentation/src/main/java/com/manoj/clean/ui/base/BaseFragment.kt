package com.manoj.clean.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.manoj.clean.R
import com.manoj.clean.ui.base.compooundviews.ErrorView

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>
    protected val binding: VB by lazy { inflateViewBinding(layoutInflater) }

    protected abstract fun inflateViewBinding(inflater: LayoutInflater): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    protected fun <T> LiveData<T>.observe(observer: Observer<in T>) = observe(this@BaseFragment, observer)
    fun onError(error: Throwable, showErrorView: Boolean) {
        parentActivity?.onError(error, showErrorView)
    }
    fun onLoading(show: Boolean) {
        val progressBar: View? = view?.findViewById(R.id.progress_bar)
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        val errorView: ErrorView? = view?.findViewById(R.id.error_view)
        errorView?.visibility = if (show) View.GONE else View.VISIBLE
    }

}