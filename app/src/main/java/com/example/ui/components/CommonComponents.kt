package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingStats
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgCardElevated
import com.example.ui.theme.BgMidnight
import com.example.ui.theme.BgSurface
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RedSignal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppHeader(
    isArabic: Boolean,
    isEngineRunning: Boolean,
    onToggleEngine: () -> Unit,
    onRefresh: () -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(BgCardElevated, BgMidnight)
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand & Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(PurplePrimary, Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Vixunity SMC",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    PulsingLiveDot()
                }
                Text(
                    text = if (isArabic) "محرك الذكاء الاصطناعي والتوصيات" else "AI Signal & Market Engine",
                    color = PurpleLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Language Toggle
            IconButton(
                onClick = onToggleLanguage,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BgCard)
                    .border(1.dp, BorderDark, CircleShape)
                    .testTag("btn_language_toggle")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Language",
                    tint = PurpleLight,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Refresh
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BgCard)
                    .border(1.dp, BorderDark, CircleShape)
                    .testTag("btn_refresh_signals")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Pause/Play
            IconButton(
                onClick = onToggleEngine,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isEngineRunning) GreenSignal.copy(alpha = 0.15f) else AmberSignal.copy(alpha = 0.15f))
                    .border(1.dp, if (isEngineRunning) GreenSignal.copy(alpha = 0.4f) else AmberSignal.copy(alpha = 0.4f), CircleShape)
                    .testTag("btn_toggle_engine")
            ) {
                Icon(
                    imageVector = if (isEngineRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Toggle Engine",
                    tint = if (isEngineRunning) GreenSignal else AmberSignal,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PulsingLiveDot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(GreenSignal)
    )
}

@Composable
fun StatsOverviewBanner(
    stats: TradingStats,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = if (isArabic) "صفقة/24h" else "Trades/24h",
            value = "${stats.totalSignals24h}",
            valueColor = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = if (isArabic) "الدقة الإجمالية" else "Accuracy",
            value = "${stats.accuracyPercent}%",
            valueColor = PurpleLight,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = if (isArabic) "الصفقات الرابحة" else "Wins",
            value = "${stats.winCount}",
            valueColor = GreenSignal,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = if (isArabic) "خسائر مُصفّاة" else "Filtered",
            value = "${stats.filteredLosses}",
            valueColor = RedSignal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun AppBottomNav(
    currentTab: Int,
    isArabic: Boolean,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.border(1.dp, BorderDark),
        containerColor = BgSurface,
        contentColor = TextPrimary
    ) {
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
            label = { Text(if (isArabic) "المباشر" else "Live", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = navBarColors(),
            modifier = Modifier.testTag("nav_tab_live")
        )
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
            label = { Text(if (isArabic) "حاسبة SMC" else "SMC Bot", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = navBarColors(),
            modifier = Modifier.testTag("nav_tab_smc")
        )
        NavigationBarItem(
            selected = currentTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.DocumentScanner, contentDescription = null) },
            label = { Text(if (isArabic) "تحليل الشارت" else "AI Chart", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = navBarColors(),
            modifier = Modifier.testTag("nav_tab_analyzer")
        )
        NavigationBarItem(
            selected = currentTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Science, contentDescription = null) },
            label = { Text(if (isArabic) "تفكير علمي" else "Bioscience", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = navBarColors(),
            modifier = Modifier.testTag("nav_tab_bioscience")
        )
        NavigationBarItem(
            selected = currentTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Code, contentDescription = null) },
            label = { Text(if (isArabic) "البيانات" else "Data", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            colors = navBarColors(),
            modifier = Modifier.testTag("nav_tab_data")
        )
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = PurpleLight,
    selectedTextColor = PurpleLight,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted,
    indicatorColor = PurplePrimary.copy(alpha = 0.2f)
)
