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
class PromoNotificationViewModel @Inject constructor(
    private val repo: RealtimeRepository
) : ViewModel(){
    private val _res: MutableState<NotificationState> = mutableStateOf(
        NotificationState()
    )
    val res: State<NotificationState> = _res
    private val _updateRes:MutableState<NotificationContent> = mutableStateOf(
        NotificationContent(item = NotificationContent.NotificationItem(),
        )
    )
    val updateRes:State<NotificationContent> = _updateRes


    fun setData(data: NotificationContent){
        _updateRes.value=data
    }
    fun update(item: NotificationContent)=repo.updatePromoNoti(item)


    init {
        viewModelScope.launch {
            repo.getpromoNotification().collect{
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

}