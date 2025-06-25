package com.features.github.searchuser

sealed interface SearchUserViewEvent {
    data class Search(val query: String) : SearchUserViewEvent
}

data class SearchUserViewState(
    val query: String = ""
)

sealed interface SearchUserSideEffect {
    object NavigateBack : SearchUserSideEffect
}
