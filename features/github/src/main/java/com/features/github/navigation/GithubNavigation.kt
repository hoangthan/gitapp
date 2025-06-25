package com.features.github.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.features.github.searchuser.SearchUserScreen
import com.features.github.userdetail.UserDetailScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class GithubRoute {
    @Serializable
    object SearchUser : GithubRoute()

    @Serializable
    data class UserDetails(val username: String) : GithubRoute()
}

fun NavGraphBuilder.githubNavGraph(navController: NavController) {
    composable<GithubRoute.SearchUser> {
        SearchUserScreen(
            onUserClick = { username ->
                navController.navigate(GithubRoute.UserDetails(username))
            }
        )
    }

    composable<GithubRoute.UserDetails> {
        UserDetailScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}