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
class ProfileActivityViewModel @Inject constructor
    (private val repo: RealtimeRepository): ViewModel(){
    private val _res: MutableState<UserState> = mutableStateOf(
        UserState()
    )
    val res: State<UserState> = _res

    private val _updateRes:MutableState<RealTimeUserResponse> = mutableStateOf(
        RealTimeUserResponse(item = RealTimeUserResponse.RealTimeUsers(),
        )
    )
    val updateRes:State<RealTimeUserResponse> = _updateRes


    fun setData(data: RealTimeUserResponse){
        _updateRes.value=data
    }

    init {
        viewModelScope.launch {
            repo.getUser().collect{
                when(it){
                    is ResultState.Success->{
                        _res.value= UserState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value= UserState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value= UserState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
    fun update(item: RealTimeUserResponse)=repo.update(item)
    fun insertUser(item: RealTimeUserResponse.RealTimeUsers)=repo.insertuser(item)


}