package com.manoj.clean.ui.adapter.commonadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


abstract class RVAdapterWithPaging<M : Any, B : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<M>,
    private val layoutResId: Int,
    private val onBind: (B, M, Int) -> Unit
) : PagingDataAdapter<M, RVAdapterWithPaging.Holder<B>>(diffCallback)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<B> {
        val binding: B = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder<B>, position: Int) {
        getItem(position)?.let { item ->
            onBind(holder.binding, item, position)
            holder.binding.executePendingBindings()
        }
    }

    class Holder<S : ViewDataBinding>(val binding: S) : RecyclerView.ViewHolder(binding.root)

    companion object {
        inline fun <reified T : Any> createDiffCallback(
            crossinline compareFunction: (T, T) -> Boolean
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return compareFunction(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return compareFunction(oldItem, newItem)
                }
            }
        }
    }


    fun attachLoadStateListener(
        onLoading: (Boolean) -> Unit,
        onError: (Throwable, Boolean) -> Unit
    ) {
        var isFirstLoad = true

        this.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
                if (isFirstLoad) {
                    onLoading(true)
                    isFirstLoad = false
                }
            } else {
                onLoading(false)
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    onError(Throwable(it.error.message), true)
                }
            }
        }
    }

}
