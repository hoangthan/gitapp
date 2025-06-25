package com.features.github.userdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.features.github.userdetail.UserDetailViewEvent.LoadUserDetail
import com.git.features.core.ui.components.LoadingView
import com.git.features.core.ui.components.SafeAreaScaffold
import com.git.features.core.ui.style.Colors
import com.git.features.core.ui.style.Dimens
import com.git.features.core.ui.utils.toErrorString
import com.git.features.github.R
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.usecase.GetUserDetailError
import com.libraries.github.domain.usecase.GetUserReposError
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val repoPagingItems = viewModel.reposPagingData.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    SafeAreaScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.user_details_title),
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) {
        CollapsingContent(
            userDetailState = viewState.userDetail,
            repoPagingItems = repoPagingItems,
            listState = listState,
            onRetryUserDetail = { viewModel.dispatchEvent(LoadUserDetail) },
            onRetryRepos = { repoPagingItems.retry() },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CollapsingContent(
    userDetailState: UserDetailState,
    repoPagingItems: LazyPagingItems<GithubRepo>,
    listState: LazyListState,
    onRetryUserDetail: () -> Unit,
    onRetryRepos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val headerHeight = with(density) { 280.dp.toPx() }
    val minHeaderHeight = with(density) { 120.dp.toPx() }

    // Calculate scroll offset and collapse progress
    val scrollOffset by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                listState.firstVisibleItemScrollOffset.toFloat()
            } else {
                headerHeight
            }
        }
    }

    val collapseProgress by remember {
        derivedStateOf {
            val maxScroll = headerHeight - minHeaderHeight
            min(1f, max(0f, scrollOffset / maxScroll))
        }
    }

    Box(modifier = modifier) {
        // Main content with collapsing header
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            // Collapsing header item
            item {
                CollapsingUserDetailCard(
                    userDetailState = userDetailState,
                    collapseProgress = collapseProgress,
                    onRetry = onRetryUserDetail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.size16)
                        .padding(top = Dimens.size16)
                )
            }

            // Repositories section header
            item {
                Text(
                    text = stringResource(R.string.user_details_repositories),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.size16)
                        .padding(top = Dimens.size16, bottom = Dimens.size8)
                )
            }

            // Repository list content
            when {
                repoPagingItems.loadState.refresh is LoadState.Loading -> {
                    item {
                        LoadingView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                repoPagingItems.loadState.refresh is LoadState.Error -> {
                    item {
                        val context = LocalContext.current
                        val errorMessage = when (val error =
                            (repoPagingItems.loadState.refresh as LoadState.Error).error) {
                            is RepoPagingFailure -> {
                                error.failure.toErrorString<GetUserReposError>(context) {
                                    when (it) {
                                        GetUserReposError.UserNotFound -> context.getString(R.string.user_not_found)
                                        GetUserReposError.ExceedRateLimit -> context.getString(R.string.error_rate_limit_exceeded)
                                    }
                                }
                            }

                            else -> context.getString(R.string.unknown_exception)
                        }

                        RetryableErrorContent(
                            errorMessage = errorMessage,
                            onRetry = onRetryRepos,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.size16)
                        )
                    }
                }

                repoPagingItems.itemCount == 0 && repoPagingItems.loadState.refresh is LoadState.NotLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = Dimens.size16),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = stringResource(R.string.user_details_no_repository),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                else -> {
                    items(
                        count = repoPagingItems.itemCount,
                        key = repoPagingItems.itemKey { it.name }
                    ) { index ->
                        repoPagingItems[index]?.let { repo ->
                            RepositoryItem(
                                repo = repo,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimens.size16)
                                    .padding(bottom = Dimens.size8)
                            )
                        }
                    }

                    if (repoPagingItems.loadState.append is LoadState.Loading) {
                        item {
                            LoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.size16)
                            )
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(Dimens.size16))
            }
        }
    }
}

@Composable
private fun CollapsingUserDetailCard(
    modifier: Modifier = Modifier,
    userDetailState: UserDetailState,
    collapseProgress: Float,
    onRetry: () -> Unit,
) {
    // Animate the collapse transition
    val animatedProgress by animateFloatAsState(
        targetValue = collapseProgress,
        label = "collapse_progress"
    )

    // Calculate dynamic values based on collapse progress
    val cardAlpha = 1f - (animatedProgress * 0.3f)
    val cardScale = 1f - (animatedProgress * 0.1f)
    val contentAlpha = 1f - (animatedProgress * 0.8f)

    Card(
        modifier = modifier
            .graphicsLayer {
                alpha = cardAlpha
                scaleX = cardScale
                scaleY = cardScale
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimens.size4 * (1f - animatedProgress * 0.5f)
        ),
        shape = RoundedCornerShape(Dimens.size8)
    ) {
        when (userDetailState) {
            is UserDetailState.Loading -> {
                LoadingView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            }

            is UserDetailState.Success -> {
                CollapsingUserDetailContent(
                    userDetail = userDetailState.userDetail,
                    contentAlpha = contentAlpha,
                    collapseProgress = animatedProgress
                )
            }

            is UserDetailState.Error -> {
                val context = LocalContext.current
                val errorMessage =
                    userDetailState.error.toErrorString<GetUserDetailError>(context) {
                        when (it) {
                            GetUserDetailError.UserNotFound -> context.getString(R.string.user_not_found)
                            GetUserDetailError.ExceedRateLimit -> context.getString(R.string.error_rate_limit_exceeded)
                        }
                    }

                RetryableErrorContent(
                    errorMessage = errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.alpha(contentAlpha)
                )
            }
        }
    }
}

@Composable
private fun CollapsingUserDetailContent(
    userDetail: GithubUserDetail,
    contentAlpha: Float,
    collapseProgress: Float
) {
    // Calculate dynamic sizing based on collapse
    val avatarSize = (80.dp.value * (1f - collapseProgress * 0.4f)).dp
    val verticalPadding = (Dimens.size16.value * (1f - collapseProgress * 0.5f)).dp

    Column(
        modifier = Modifier
            .padding(
                horizontal = Dimens.size16,
                vertical = verticalPadding
            )
            .alpha(contentAlpha),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            model = ImageRequest.Builder(LocalContext.current)
                .data(userDetail.avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(Dimens.size8 * (1f - collapseProgress * 0.5f)))

        Text(
            text = userDetail.name ?: userDetail.username,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize * (1f - collapseProgress * 0.2f)
            )
        )

        Spacer(modifier = Modifier.height(Dimens.size4 * (1f - collapseProgress * 0.5f)))

        Text(
            text = "@${userDetail.username}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Hide follower/following stats when collapsed
        if (collapseProgress < 0.7f) {
            Spacer(modifier = Modifier.height(Dimens.size8))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(1f - collapseProgress * 1.5f),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = userDetail.followers.toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.user_details_follower),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = userDetail.following.toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.user_details_following),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RepositoryItem(
    modifier: Modifier = Modifier,
    repo: GithubRepo,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.size4 / 2),
        shape = RoundedCornerShape(Dimens.size8),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.size16),
        ) {
            Text(
                maxLines = 1,
                text = repo.name,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )

            repo.description?.let {
                Spacer(modifier = Modifier.height(Dimens.size4))
                Text(
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.size8))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repo.language?.let {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Colors.PrimaryLight),
                    )
                    Spacer(modifier = Modifier.width(Dimens.size4))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Dimens.size16))
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.user_details_stars),
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(Dimens.size4))
                Text(
                    text = repo.stars.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RetryableErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.size16),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.size8))

        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.user_details_retry))
        }
    }
}
