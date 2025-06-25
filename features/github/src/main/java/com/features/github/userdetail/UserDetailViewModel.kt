package com.features.github.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.features.github.navigation.GithubRoute.UserDetails
import com.git.data.core.coroutine.DispatcherProvider
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.usecase.GetUserDetailParam
import com.libraries.github.domain.usecase.GetUserDetailsUseCase
import com.libraries.github.domain.usecase.GetUserRepoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class UserDetailViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val getUserRepoUseCase: GetUserRepoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val routeData = savedStateHandle.toRoute(UserDetails::class)

    private val _viewState = MutableStateFlow(UserDetailViewState())
    val viewState: StateFlow<UserDetailViewState> = _viewState.asStateFlow()

    val reposPagingData: Flow<PagingData<GithubRepo>> = flowOf(routeData.username)
        .flatMapLatest(::getReposPagingConfig)
        .flowOn(dispatcherProvider.io)
        .cachedIn(viewModelScope)

    init {
        loadUserDetail()
    }

    fun dispatchEvent(event: UserDetailViewEvent) {
        when (event) {
            UserDetailViewEvent.LoadUserDetail -> loadUserDetail()
        }
    }

    private fun loadUserDetail() {
        viewModelScope.launch(dispatcherProvider.io) {
            _viewState.update { it.copy(userDetail = UserDetailState.Loading) }
            val result = getUserDetailsUseCase(GetUserDetailParam(username = routeData.username))
            when (result) {
                is Result.Error -> {
                    _viewState.update { it.copy(userDetail = UserDetailState.Error(result.failure)) }
                }

                is Result.Success -> {
                    _viewState.update { it.copy(userDetail = UserDetailState.Success(result.value)) }
                }
            }
        }
    }

    private fun getReposPagingConfig(username: String): Flow<PagingData<GithubRepo>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 3),
            pagingSourceFactory = { UserRepoPagingSource(getUserRepoUseCase, username, PAGE_SIZE) },
        ).flow
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}