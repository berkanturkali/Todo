package com.example.todo.framework.presentation.viewmodel

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
import com.example.todo.business.domain.model.StatsResult
import com.example.todo.business.domain.model.User
import com.example.todo.receiver.AlarmReceiver
import com.example.todo.business.repo.TodoRepo
import com.example.todo.business.repo.UserRepo
import com.example.todo.util.Event
import com.example.todo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainTodoFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val todoRepo: TodoRepo,
    @ApplicationContext val app: Context
) : ViewModel() {

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(app, AlarmReceiver::class.java)

    private val _userInfo = MutableLiveData<Resource<User>>()
    val userInfo: LiveData<Resource<User>> get() = _userInfo

    private val _timeSelection = MutableLiveData<Long>()

    private val _filterItemClicked = MutableLiveData<Event<Boolean>>()
    val filterItemClicked: LiveData<Event<Boolean>> get() = _filterItemClicked

    private val _progress = MutableLiveData<Event<Boolean>>()
    val progress: LiveData<Event<Boolean>> get() = _progress

    private val _isRemoveCompletedItemsClicked = MutableLiveData<Event<Boolean>>()
    val isRemoveCompletedItemsClicked: LiveData<Event<Boolean>> get() = _isRemoveCompletedItemsClicked

    private val _stats = MutableLiveData<Resource<StatsResult>>()
    val stats: LiveData<Resource<StatsResult>> = _stats

    private var randomPendingId = (0..Int.MAX_VALUE).random()

    private val _isValidDate = MutableLiveData<Event<Boolean>>()
    val isValidDate: LiveData<Event<Boolean>> get() = _isValidDate

    fun getMe() {
        viewModelScope.launch(Dispatchers.Main) {
            _userInfo.value = userRepo.getMe()
        }
    }

    fun getStats() {
        viewModelScope.launch(Dispatchers.Main) {
            _stats.value = todoRepo.getStats()
        }
    }

    fun setNotificationTime(time: Long) {
        if (System.currentTimeMillis() > time) {
            _isValidDate.value = Event(false)
            return
        }
        _timeSelection.value = time
    }

    fun showProgress() {
        _progress.value = Event(true)
    }

    fun hideProgress() {
        _progress.value = Event(false)
    }

    fun setFilterItemClicked(isClicked: Boolean) {
        _filterItemClicked.value = Event(isClicked)
    }

    fun setRemoveCompletedItemsClicked(isClicked: Boolean) {
        _isRemoveCompletedItemsClicked.value = Event(isClicked)
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
            app,
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
            app,
            id,
            notifyIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }
}