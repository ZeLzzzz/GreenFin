package com.example.financeaudit.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonVariant {
    Primary,
    Ghost,
    Icon
}

@Composable
fun ButtonC(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,

    height: Dp = 56.dp,
    size: Dp = 48.dp,

    shape: Shape? = null,
    fullWidth: Boolean = true,

    containerColor: Color? = null,
    contentColor: Color? = null,
    borderColor: Color? = null,

    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shouldScale = isPressed && enabled && variant != ButtonVariant.Ghost

    val scale by animateFloatAsState(
        targetValue = if (shouldScale) 0.95f else 1f,
        label = "buttonScale"
    )

    val finalContainerColor = containerColor ?: when (variant) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        ButtonVariant.Ghost, ButtonVariant.Icon -> Color.Transparent
    }

    val finalContentColor = contentColor ?: when (variant) {
        ButtonVariant.Primary -> Color.Black
        ButtonVariant.Ghost, ButtonVariant.Icon -> MaterialTheme.colorScheme.onBackground
    }

    val borderStroke = if (borderColor != null) {
        BorderStroke(1.dp, borderColor)
    } else null

    val innerPadding = when (variant) {
        ButtonVariant.Primary -> PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ButtonVariant.Ghost -> PaddingValues(0.dp)
        ButtonVariant.Icon -> PaddingValues(0.dp)
    }

    val finalShape = shape ?: if (variant == ButtonVariant.Icon) CircleShape else RoundedCornerShape(12.dp)

    val sizeModifier = if (variant == ButtonVariant.Icon) {
        Modifier.size(size)
    } else {
        Modifier
            .height(height)
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .then(sizeModifier),
        enabled = enabled,
        shape = finalShape,
        color = if (enabled) finalContainerColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        contentColor = if (enabled) finalContentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        border = if (enabled) borderStroke else null,
        interactionSource = interactionSource,
        shadowElevation = if (variant == ButtonVariant.Primary && !isPressed) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = if (variant == ButtonVariant.Icon) 0.dp else ButtonDefaults.MinWidth,
                    minHeight = if (variant == ButtonVariant.Icon) 0.dp else ButtonDefaults.MinHeight
                )
                .padding(innerPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}