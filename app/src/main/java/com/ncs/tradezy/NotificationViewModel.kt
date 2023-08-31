package com.ncs.tradezy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repo: RealtimeRepository
) : ViewModel(){
    private val _res: MutableState<NotificationState> = mutableStateOf(
        NotificationState()
    )
    val res: State<NotificationState> = _res

    init {
        viewModelScope.launch {
            repo.getNotification().collect{
                when(it){
                    is ResultState.Success->{
                        _res.value= NotificationState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value= NotificationState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value= NotificationState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun insertNotification(item: NotificationContent.NotificationItem)=repo.insertNotification(item)

}