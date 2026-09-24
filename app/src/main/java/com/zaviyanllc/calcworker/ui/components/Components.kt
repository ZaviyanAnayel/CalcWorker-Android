package com.zaviyanllc.calcworker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaviyanllc.calcworker.data.CalcSpec
import com.zaviyanllc.calcworker.ui.theme.Brand
import com.zaviyanllc.calcworker.ui.theme.categoryColor

/** Springy press-scale wrapper for cards and buttons. */
@Composable
fun Pressable(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "press"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clip(MaterialTheme.shapes.medium)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) { content() }
}

/** Gradient monogram tile for a calculator (initials on brand gradient). */
@Composable
fun CalcTile(spec: CalcSpec, size: Int = 52, modifier: Modifier = Modifier) {
    val c = categoryColor(spec.category)
    val initials = spec.title.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(c.copy(alpha = 0.85f), c.copy(alpha = 0.45f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials.ifEmpty { "C" },
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF06080D)
        )
    }
}

/** Full-width calculator row card. */
@Composable
fun CalcRowCard(
    spec: CalcSpec,
    isFavorite: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Pressable(onClick = onOpen, modifier = modifier.fillMaxWidth()) {
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalcTile(spec)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(spec.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(spec.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(48.dp)) {
                    Icon(
                        if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Brand.Gold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Compact horizontal-scroll card for Popular. */
@Composable
fun CalcMiniCard(spec: CalcSpec, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Pressable(onClick = onOpen, modifier = modifier.width(168.dp)) {
        Surface(
            tonalElevation = 2.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(Modifier.padding(14.dp)) {
                CalcTile(spec, size = 44)
                Spacer(Modifier.height(10.dp))
                Text(spec.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(spec.category, style = MaterialTheme.typography.bodyMedium, color = categoryColor(spec.category), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        action?.invoke()
    }
}

@Composable
fun EmptyHint(icon: ImageVector, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
