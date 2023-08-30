package com.ncs.tradezy

import androidx.lifecycle.ViewModel
import com.ncs.tradezy.repository.RealTimeUserResponse
import com.ncs.tradezy.repository.RealtimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthActivityViewModel @Inject constructor(
    private val repo: RealtimeRepository
) : ViewModel(){

    fun insertUser(item: RealTimeUserResponse.RealTimeUsers)=repo.insertuser(item)

}