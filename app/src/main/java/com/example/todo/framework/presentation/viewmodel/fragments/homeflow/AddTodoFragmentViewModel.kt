package com.example.todo.framework.presentation.viewmodel.fragments.homeflow

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.business.domain.model.Todo
import com.example.todo.business.repo.abstraction.TodoRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AddTodoFragmentViewMode"

@HiltViewModel
class AddTodoFragmentViewModel @Inject constructor(
    private val todoRepo: TodoRepo,
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val notifyIntent: Intent
) : ViewModel() {

    private val _addedInfo = MutableLiveData<Event<Resource<String>>>()
    val addedInfo: LiveData<Event<Resource<String>>> get() = _addedInfo

    private val _updateInfo = MutableLiveData<Event<Resource<String>>>()

    val updateInfo: LiveData<Event<Resource<String>>> get() = _updateInfo

    private val _isValidDate = MutableLiveData<Event<Boolean>>()
    val isValidDate: LiveData<Event<Boolean>> get() = _isValidDate

    private val _timeSelection = MutableLiveData<Long?>()

    private var randomPendingId = (0..Int.MAX_VALUE).random()

    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Main) {
            _addedInfo.value = Event(todoRepo.add(todo))
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.Main) {
            _updateInfo.value = Event(Resource.Loading())
            _updateInfo.value = Event(todoRepo.update(todo))
        }
    }

    fun setNotificationDate(time: Long?) {
        time?.let {
            if (System.currentTimeMillis() > it) {
                _isValidDate.value = Event(false)
                return
            }
        }
        _timeSelection.value = time
    }

    fun setNotificationOn(message: String, isImportant: Boolean) {
        _timeSelection.value?.let {
            setNotification(it, message, isImportant)
        }
    }

    fun newId(): Int {
        randomPendingId = (0..Int.MAX_VALUE).random()
        return randomPendingId
    }

    private fun setNotification(
        alarmTime: Long,
        todoMessage: String,
        isImportant: Boolean
    ) {
        notifyIntent.putExtra("message", todoMessage)
        notifyIntent.putExtra("isImportant", isImportant)
        val notifyPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            randomPendingId,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                notifyPendingIntent
            )
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                notifyPendingIntent
            )
        }
    }

    fun cancelNotification(id: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            notifyIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    fun getTitle(isEditMode: Boolean) = if (isEditMode) "Edit Todo" else "Create New Todo"

    fun getButtonText(isEditMode: Boolean) = if (isEditMode) "Update" else "Add"
}