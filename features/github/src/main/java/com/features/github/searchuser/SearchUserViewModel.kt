package com.features.github.searchuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.git.data.core.coroutine.DispatcherProvider
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.usecase.SearchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchUserViewModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val searchUserUseCase: SearchUserUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(SearchUserViewState())
    val viewState: StateFlow<SearchUserViewState> = _viewState.asStateFlow()

    private val _sideEffect = Channel<SearchUserSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val usersPagingData: Flow<PagingData<GithubUser>> = _viewState.map { it.query }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query -> createPager(query).flow }
        .flowOn(dispatcherProvider.io)
        .cachedIn(viewModelScope)

    fun dispatchEvent(event: SearchUserViewEvent) {
        when (event) {
            is SearchUserViewEvent.Search -> _viewState.update { it.copy(query = event.query) }
        }
    }

    private fun createPager(query: String): Pager<Int, GithubUser> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 3),
            pagingSourceFactory = { SearchUserPagingSource(searchUserUseCase, query, PAGE_SIZE) },
        )
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
