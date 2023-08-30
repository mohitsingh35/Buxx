package com.ncs.tradezy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncs.tradezy.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor
    (private val repo: RealtimeRepository): ViewModel(){
    private val _res: MutableState<UserState> = mutableStateOf(
        UserState()
    )
    val res: State<UserState> = _res

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

}