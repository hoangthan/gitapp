package com.features.github.userdetail

import com.git.data.core.model.Failure
import com.libraries.github.domain.model.GithubUserDetail

sealed interface UserDetailViewEvent {
    object LoadUserDetail : UserDetailViewEvent
}

data class UserDetailViewState(
    val userDetail: UserDetailState = UserDetailState.Loading,
)

sealed interface UserDetailState {
    object Loading : UserDetailState
    data class Error(val error: Failure) : UserDetailState
    data class Success(val userDetail: GithubUserDetail) : UserDetailState
}
