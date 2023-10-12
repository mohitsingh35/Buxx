package com.ncs.tradezy
import android.net.Uri
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
class BuyerLocationViewModel @Inject constructor
    (private val repo: RealtimeRepository):ViewModel(){

    private val _res: MutableState<BuyerState> = mutableStateOf(
        BuyerState()
    )
    val res: State<BuyerState> = _res
    init {
        viewModelScope.launch {
            repo.getbuyer().collect{
                when(it){
                    is ResultState.Success->{
                        _res.value= BuyerState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value= BuyerState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value= BuyerState(
                            isLoading = true
                        )
                    }
                }
            }
        }

    }




}

