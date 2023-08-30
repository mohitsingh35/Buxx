package com.ncs.tradezy.googleAuth

import androidx.lifecycle.ViewModel
import com.ncs.tradezy.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GoogleSignInViewModel : ViewModel() {
    private val _state= MutableStateFlow(GoogleSignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data!=null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { GoogleSignInState()  }
    }
}