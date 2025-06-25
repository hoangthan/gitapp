package com.features.github.searchuser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.git.features.core.ui.components.SafeAreaScaffold
import com.git.features.core.ui.style.Dimens
import com.git.features.core.ui.utils.toErrorString
import com.git.features.github.R
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.usecase.SearchUserError
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchUserScreen(
    viewModel: SearchUserViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewState by viewModel.viewState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val userPagingItems = viewModel.usersPagingData.collectAsLazyPagingItems()
    val loadState = userPagingItems.loadState.refresh

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is SearchUserSideEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SafeAreaScaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.size16),
            ) {
                Spacer(modifier = Modifier.height(Dimens.size16))

                SearchBar(
                    query = viewState.query,
                    onQueryChange = { viewModel.dispatchEvent(SearchUserViewEvent.Search(it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.size16))

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        // Show loading indicator for refresh
                        loadState is LoadState.Loading && viewState.query.isNotBlank() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        // Show refresh error
                        loadState is LoadState.Error -> {
                            val errorMessage = when (val error = loadState.error) {
                                is PagingFailure -> {
                                    error.failure.toErrorString<SearchUserError>(context) {
                                        when (it) {
                                            SearchUserError.InvalidQuery -> context.getString(R.string.error_invalid_query)
                                            SearchUserError.RateLimitExceeded -> context.getString(
                                                R.string.error_rate_limit_exceeded
                                            )
                                        }
                                    }
                                }

                                else -> context.getString(R.string.error_load_data)
                            }

                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(Dimens.size8))

                                Button(
                                    onClick = { userPagingItems.refresh() },
                                    modifier = Modifier.padding(Dimens.size8)
                                ) {
                                    Text(text = LocalContext.current.getString(R.string.retry))
                                }
                            }
                        }

                        // Show empty results message
                        userPagingItems.itemCount == 0 && viewState.query.isNotBlank() && userPagingItems.loadState.refresh is LoadState.NotLoading -> {
                            EmptyResultsMessage(modifier = Modifier.align(Alignment.Center))
                        }

                        // Show initial state message when no query
                        viewState.query.isBlank() -> {
                            InitialStateMessage(modifier = Modifier.align(Alignment.Center))
                        }

                        // Show the list with paging
                        else -> UserList(
                            pagingItems = userPagingItems, onUserClick = onUserClick
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Dimens.size16)
            ) {
                Snackbar { Text(text = it.visuals.message) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val context = LocalContext.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(text = context.getString(R.string.search_placeholder)) },
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.size12),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = context.getString(R.string.search_icon_description),
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = context.getString(R.string.clear_search_description)
                    )
                }
            }
        })
}

@Composable
private fun UserList(
    pagingItems: LazyPagingItems<GithubUser>,
    onUserClick: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = Dimens.size8),
        verticalArrangement = Arrangement.spacedBy(Dimens.size8)
    ) {
        items(
            count = pagingItems.itemCount, key = pagingItems.itemKey { it.username }) { index ->
            val user = pagingItems[index]
            if (user != null) {
                UserListItem(
                    user = user, onClick = { onUserClick(user.username) })
            }
        }

        // Show loading at the bottom when appending
        when (pagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.size44 + Dimens.size16)
                            .padding(Dimens.size8), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(Dimens.size24))
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun UserListItem(
    modifier: Modifier = Modifier,
    user: GithubUser,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.size4 / 2),
        shape = RoundedCornerShape(Dimens.size8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.size16 - 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(user.avatarUrl)
                    .crossfade(true).build(),
                contentDescription = LocalContext.current.getString(R.string.user_avatar_description),
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(Dimens.size32 + Dimens.size16)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(Dimens.size16))

            Text(
                text = user.username, style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun EmptyResultsMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocalContext.current.getString(R.string.no_users_found),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.size8))
        Text(
            text = LocalContext.current.getString(R.string.try_different_search),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InitialStateMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocalContext.current.getString(R.string.search_github_users),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.size8))
        Text(
            text = LocalContext.current.getString(R.string.enter_username_to_start),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
