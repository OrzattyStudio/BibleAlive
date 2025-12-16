package com.bible.alive.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    showLabel: Boolean = true,
    size: StreakBadgeSize = StreakBadgeSize.MEDIUM
) {
    val fireColor by animateColorAsState(
        targetValue = if (isActive && streakDays > 0) {
            Color(0xFFFF6B35)
        } else {
            Color.Gray
        },
        animationSpec = tween(durationMillis = 300),
        label = "fire_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "streak_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive && streakDays > 0) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val (iconSize, textStyle, padding) = when (size) {
        StreakBadgeSize.SMALL -> Triple(16.dp, MaterialTheme.typography.labelSmall, 4.dp)
        StreakBadgeSize.MEDIUM -> Triple(24.dp, MaterialTheme.typography.labelLarge, 8.dp)
        StreakBadgeSize.LARGE -> Triple(32.dp, MaterialTheme.typography.titleMedium, 12.dp)
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(size.cornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = padding * 2, vertical = padding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = "Racha",
                tint = fireColor,
                modifier = Modifier
                    .size(iconSize)
                    .scale(if (isActive && streakDays > 0) scale else 1f)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$streakDays",
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (showLabel) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (streakDays == 1) "día" else "días",
                    style = when (size) {
                        StreakBadgeSize.SMALL -> MaterialTheme.typography.labelSmall
                        StreakBadgeSize.MEDIUM -> MaterialTheme.typography.labelMedium
                        StreakBadgeSize.LARGE -> MaterialTheme.typography.labelLarge
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun StreakBadgeCompact(
    streakDays: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val fireColor = if (isActive && streakDays > 0) {
        Color(0xFFFF6B35)
    } else {
        Color.Gray
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = "Racha",
            tint = fireColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "$streakDays",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    val gradientColors = if (isActive && currentStreak > 0) {
        listOf(
            Color(0xFFFF6B35),
            Color(0xFFFF8C00),
            Color(0xFFFFD700)
        )
    } else {
        listOf(
            Color.Gray.copy(alpha = 0.5f),
            Color.Gray.copy(alpha = 0.3f)
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(gradientColors),
                    alpha = 0.15f
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (isActive && currentStreak > 0) Color(0xFFFF6B35) else Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "$currentStreak",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "días de racha",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (longestStreak > currentStreak) {
                    Spacer(modifier = Modifier.padding(top = 12.dp))
                    Text(
                        text = "Mejor racha: $longestStreak días",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StreakMilestoneIndicator(
    currentStreak: Int,
    nextMilestone: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentStreak.toFloat() / nextMilestone).coerceIn(0f, 1f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFFF6B35),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$currentStreak / $nextMilestone",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "${nextMilestone - currentStreak} días para el siguiente hito",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

enum class StreakBadgeSize(val cornerRadius: Dp) {
    SMALL(8.dp),
    MEDIUM(12.dp),
    LARGE(16.dp)
}
