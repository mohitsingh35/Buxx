package com.ncs.tradezy
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.JsonObject
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor
    (private val repo: RealtimeRepository): ViewModel(){
    private val _res: MutableState<HomeScreenState> = mutableStateOf(
        HomeScreenState()
    )
    val res: State<HomeScreenState> = _res




    init {
        viewModelScope.launch {
            repo.getAd().collect{
                when(it){
                    is ResultState.Success->{
                        _res.value= HomeScreenState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value= HomeScreenState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value= HomeScreenState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
}