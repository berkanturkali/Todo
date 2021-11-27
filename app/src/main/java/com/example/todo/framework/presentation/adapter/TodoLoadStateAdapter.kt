package com.example.todo.framework.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.databinding.LoadStateViewFooterBinding

class TodoLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<TodoLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): TodoLoadStateAdapter.LoadStateViewHolder {
        val binding = LoadStateViewFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TodoLoadStateAdapter.LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.binding.apply {
            retryBtn.isVisible = loadState !is LoadState.Loading
            loadStateErrorMessage.isVisible = loadState !is LoadState.Loading
            loadStateProgress.isVisible = loadState is LoadState.Loading
            if(loadState is LoadState.Error){
                loadStateErrorMessage.text = loadState.error.localizedMessage
            }
            retryBtn.setOnClickListener {
                retry.invoke()
            }
        }
    }


    inner class LoadStateViewHolder(val binding: LoadStateViewFooterBinding) :
        RecyclerView.ViewHolder(binding.root)

}
