package com.ncs.tradezy

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: RealtimeRepository
) : ViewModel(){
    private val _res: MutableState<MessageState> = mutableStateOf(
        MessageState()
    )
    val res: State<MessageState> = _res
    private val _updateRes:MutableState<MessageResponse> = mutableStateOf(
        MessageResponse(item = MessageResponse.MessageItems(),
        )
    )
    val updateRes:State<MessageResponse> = _updateRes


    fun setData(data: MessageResponse){
        _updateRes.value=data
    }


    init {
        viewModelScope.launch {
            repo.getMessage().collect{
                when(it){
                    is ResultState.Success->{
                        _res.value= MessageState(
                            item = it.data
                        )
                    }
                    is ResultState.Failure->{
                        _res.value= MessageState(
                            error = it.msg.toString()
                        )
                    }
                    ResultState.Loading->{
                        _res.value= MessageState(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    fun update(item: MessageResponse)=repo.updateMessage(item)
    fun insertMessage(item: MessageResponse.MessageItems)=repo.insertMessage(item)
    fun insertImages(images: List<Uri>,otherDetails:MessageResponse.MessageItems)=repo.insertImages(images,otherDetails)

}