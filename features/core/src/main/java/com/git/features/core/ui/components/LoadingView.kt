package com.git.features.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.git.features.core.ui.style.Dimens

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    size: Int = Dimens.size32.value.toInt()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = Dimens.size8 / 4
        )
    }
}
