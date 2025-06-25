package com.git.assessment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.features.github.navigation.GithubRoute.SearchUser
import com.features.github.navigation.githubNavGraph

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SearchUser
    ) {
        githubNavGraph(navController)
    }
}