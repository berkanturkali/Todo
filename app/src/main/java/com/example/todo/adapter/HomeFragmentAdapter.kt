package com.example.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.databinding.TodoItemLayoutBinding
import com.example.todo.databinding.TodoItemSeperatorLayoutBinding
import com.example.todo.model.Todo
import com.example.todo.model.TodoModel
import com.example.todo.util.DateUtil
import com.google.android.material.checkbox.MaterialCheckBox
import java.text.SimpleDateFormat

private const val TAG = "HomeFragmentAdapter"

class HomeFragmentAdapter(
    private val listener: OnTodoClickListener
) :
    PagingDataAdapter<TodoModel, RecyclerView.ViewHolder>(TodoComparator) {

    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    companion object {
        private val TodoComparator = object : DiffUtil.ItemCallback<TodoModel>() {
            override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return (oldItem is TodoModel.TodoItem && newItem is TodoModel.TodoItem &&
                        oldItem.todo.id == newItem.todo.id) ||
                        (oldItem is TodoModel.SeperatorItem && newItem is TodoModel.SeperatorItem &&
                                oldItem.date == newItem.date)
            }

            override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.todo_item_layout -> {
                TodoViewHolder(
                    TodoItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                TodoSeparatorViewHolder(
                    TodoItemSeperatorLayoutBinding.inflate(
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
                is TodoModel.SeperatorItem -> {
                    (holder as TodoSeparatorViewHolder).bind(todoModel.date)
                }
            }
        }
    }


    inner class TodoViewHolder(private val binding: TodoItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    item?.let {
                        it as TodoModel.TodoItem
                        listener.onTodoClick(it.todo)
                    }
                }
            }
            binding.todoCheckbox.setOnClickListener {
                val position = bindingAdapterPosition
                val item = getItem(position) as TodoModel.TodoItem
                listener.onCheckboxListener(
                    item.todo,
                    (it as MaterialCheckBox).isChecked,

                    )
            }
        }
        fun bind(todo: Todo) {
            binding.apply {
                todoTv.text = todo.todo
                categoryTv.text = todo.category
                dateTv.text = DateUtil.getRelativeTimeSpanString(todo.date)
                todoCheckbox.isChecked = todo.isCompleted
                binding.todoTv.paint.isStrikeThruText = todo.isCompleted
                if (todo.isImportant) icImportant.setImageResource(R.drawable.ic_important_star) else icImportant.setImageResource(
                    R.drawable.ic_not_important_star
                )
            }
        }
    }

    inner class TodoSeparatorViewHolder(private val binding: TodoItemSeperatorLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(separator: String) {
            var day: String? = when {
                DateUtil.isToday(separator.toLong()) -> {
                    "Today"
                }
                DateUtil.isYesterday(separator.toLong()) -> {
                    "Yesterday"
                }
                DateUtil.isTomorrow(separator.toLong()) -> {
                    "Tomorrow"
                }
                else -> {
                    simpleDateFormat.format(separator.toLong())
                }
            }
            binding.separatorDescription.text = day
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TodoModel.TodoItem -> R.layout.todo_item_layout
            is TodoModel.SeperatorItem -> R.layout.todo_item_seperator_layout
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    interface OnTodoClickListener {
        fun onTodoClick(todo: Todo)
        fun onCheckboxListener(todo: Todo, isChecked: Boolean)
    }
}