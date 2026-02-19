package com.example.financeaudit.presentation.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.financeaudit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    titleContent: @Composable () -> Unit,

    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    centeredTitle: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        titleContentColor = contentColor,
        navigationIconContentColor = contentColor,
        actionIconContentColor = contentColor
    )

    val finalNavigationIcon: @Composable () -> Unit = {
        if (navigationIcon != null) {
            navigationIcon()
        } else if (onBackClick != null) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back_ios),
                    contentDescription = "Back"
                )
            }
        }
    }

    if (centeredTitle) {
        CenterAlignedTopAppBar(
            title = { titleContent() },
            navigationIcon = finalNavigationIcon,
            actions = actions,
            colors = colors,
            modifier = modifier,
            scrollBehavior = scrollBehavior
        )
    } else {
        TopAppBar(
            title = { titleContent() },
            navigationIcon = finalNavigationIcon,
            actions = actions,
            colors = colors,
            modifier = modifier,
            scrollBehavior = scrollBehavior
        )
    }
}