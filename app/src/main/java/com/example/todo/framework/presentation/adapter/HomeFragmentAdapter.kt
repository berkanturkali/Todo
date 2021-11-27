package com.example.todo.framework.presentation.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.domain.model.TodoModel
import com.example.todo.databinding.TodoItemBinding
import com.example.todo.databinding.TodoItemSeparatorBinding
import com.example.todo.util.Constants
import com.example.todo.util.getDate
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HomeFragmentAdapter"

class HomeFragmentAdapter(
    private val listener: OnTodoClickListener
) :
    PagingDataAdapter<TodoModel, RecyclerView.ViewHolder>(TodoComparator) {

    private val timeFormatter = SimpleDateFormat(Constants.TIME_PATTERN, Locale.getDefault())

    companion object {
        private val TodoComparator = object : DiffUtil.ItemCallback<TodoModel>() {
            override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return (oldItem is TodoModel.TodoItem && newItem is TodoModel.TodoItem &&
                        oldItem.todo.id == newItem.todo.id) ||
                        (oldItem is TodoModel.SeparatorItem && newItem is TodoModel.SeparatorItem &&
                                oldItem.date == newItem.date)
            }

            override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.todo_item -> {
                TodoViewHolder(
                    TodoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                TodoSeparatorViewHolder(
                    TodoItemSeparatorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todoModel = getItem(position)
        todoModel?.let {
            when (todoModel) {
                is TodoModel.TodoItem -> {
                    (holder as TodoViewHolder).bind(todoModel.todo)
                }
                is TodoModel.SeparatorItem -> {
                    (holder as TodoSeparatorViewHolder).bind(todoModel.date)
                }
            }
        }
    }


    inner class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let {
                        it as TodoModel.TodoItem
                        listener.onTodoClick(it.todo,position)
                    }
                }
            }
        }

        fun bind(todo: Todo) {
            binding.apply {
                todoTv.text = todo.todo
                categoryTv.text = todo.category
                val date = Date(todo.date)
                dateTv.text = timeFormatter.format(date)
                val completeIcon =
                    if (todo.isCompleted) R.drawable.ic_complete else R.drawable.ic_not_complete
                completeIv.setImageResource(completeIcon)
                val drawable = if (todo.notifyMe) R.drawable.alarm_icon else R.drawable.clock_icon
                dateTv.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, 0, 0, 0)
                binding.todoTv.paint.isStrikeThruText = todo.isCompleted
                if (todo.isImportant) icImportant.setImageResource(R.drawable.ic_important_star) else icImportant.setImageResource(
                    R.drawable.ic_not_important_star
                )
            }
        }
    }

    inner class TodoSeparatorViewHolder(private val binding: TodoItemSeparatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(separator: String) {
            binding.separatorDescription.text = separator.toLong().getDate()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TodoModel.TodoItem -> R.layout.todo_item
            is TodoModel.SeparatorItem -> R.layout.todo_item_separator
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    interface OnTodoClickListener {
        fun onTodoClick(todo: Todo,position:Int)
    }
}