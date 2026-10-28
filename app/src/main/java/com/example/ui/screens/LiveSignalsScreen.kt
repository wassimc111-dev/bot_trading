package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.SignalDirection
import com.example.data.SignalItem
import com.example.data.SignalStatus
import com.example.data.TradingStats
import com.example.ui.components.StatsOverviewBanner
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgCardElevated
import com.example.ui.theme.BgMidnight
import com.example.ui.theme.BgSurface
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.OtcOrange
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RedSignal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveSignalsScreen(
    signals: List<SignalItem>,
    stats: TradingStats,
    selectedTimeframe: String,
    selectedStatusFilter: String,
    isArabic: Boolean,
    onSelectTimeframe: (String) -> Unit,
    onSelectStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeframes = listOf("ALL", "30s", "1m", "2m", "3m", "5m", "10m", "15m", "30m")
    val statuses = listOf("ALL", "PENDING", "WIN", "LOSS")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgMidnight),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Engine Headline Banner
        item {
            EngineInfoBanner(isArabic = isArabic)
        }

        // 24h Stats
        item {
            StatsOverviewBanner(stats = stats, isArabic = isArabic)
        }

        // Timeframe selector bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEach { tf ->
                    val isSelected = selectedTimeframe.equals(tf, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) PurplePrimary else BgCard)
                            .border(1.dp, if (isSelected) PurpleLight else BorderDark, RoundedCornerShape(20.dp))
                            .clickable { onSelectTimeframe(tf) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("tf_chip_$tf"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tf == "ALL") (if (isArabic) "كل الأوقات" else "All TFs") else tf,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Status Filter bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { st ->
                    val isSelected = selectedStatusFilter.equals(st, ignoreCase = true)
                    val label = when (st) {
                        "PENDING" -> if (isArabic) "⏳ قيد الانتظار" else "⏳ Pending"
                        "WIN" -> if (isArabic) "✅ ربح" else "✅ Win"
                        "LOSS" -> if (isArabic) "❌ خسارة" else "❌ Loss"
                        else -> if (isArabic) "جميع الحالات" else "All Status"
                    }
                    val activeColor = when (st) {
                        "PENDING" -> PurpleLight
                        "WIN" -> GreenSignal
                        "LOSS" -> RedSignal
                        else -> PurplePrimary
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) activeColor.copy(alpha = 0.25f) else BgCard)
                            .border(1.dp, if (isSelected) activeColor else BorderDark, RoundedCornerShape(20.dp))
                            .clickable { onSelectStatus(st) }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .testTag("status_chip_$st"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) activeColor else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Empty state
        if (signals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد إشارات مطابقة للتصفية حالياً" else "No signals matching active filter",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(signals, key = { it.id }) { signal ->
                SignalCardItem(signal = signal, isArabic = isArabic)
            }
        }
    }
}

@Composable
fun EngineInfoBanner(isArabic: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCardElevated),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PurplePrimary.copy(alpha = 0.5f), Color.Transparent)))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PurpleLight)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isArabic) "محرك موحد — 100+ استراتيجية رقمية" else "Unified Engine — 100+ Quantitative Strategies",
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "TradingView · OANDA · Gemini · Cloud AI · 24/7 OTC",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PurplePrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "LIVE FEED",
                    color = PurpleLight,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun SignalCardItem(
    signal: SignalItem,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val isBuy = signal.direction == SignalDirection.UP
    val dirColor = if (isBuy) GreenSignal else RedSignal
    val statusBg = when (signal.status) {
        SignalStatus.WIN -> GreenSignal.copy(alpha = 0.15f)
        SignalStatus.LOSS -> RedSignal.copy(alpha = 0.15f)
        SignalStatus.PENDING -> PurpleLight.copy(alpha = 0.15f)
    }
    val statusColor = when (signal.status) {
        SignalStatus.WIN -> GreenSignal
        SignalStatus.LOSS -> RedSignal
        SignalStatus.PENDING -> PurpleLight
    }
    val statusLabel = when (signal.status) {
        SignalStatus.WIN -> if (isArabic) "ربح ✓" else "Win ✓"
        SignalStatus.LOSS -> if (isArabic) "خسارة" else "Loss"
        SignalStatus.PENDING -> if (isArabic) "انتظار" else "Pending"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 5.dp)
            .testTag("signal_card_${signal.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(statusColor.copy(alpha = 0.4f), BorderDark)))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Top Row: Channel, Status, OTC badge, Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurplePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = signal.channel,
                            color = PurpleLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (signal.isOTC) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(OtcOrange.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OTC",
                                color = OtcOrange,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Text(
                    text = signal.signalTime,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Pair Name and Direction Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = signal.pair,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isBuy) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = dirColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isBuy) (if (isArabic) "صعود (CALL)" else "CALL UP") else (if (isArabic) "هبوط (PUT)" else "PUT DOWN"),
                        color = dirColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Third Row: Entry Countdown, Timeframe, Strategy Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountdownBadge(anticipateSec = signal.anticipateSec, entryTime = signal.entryTime, isArabic = isArabic)
                    Text(
                        text = "⏱ ${signal.timeframe}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgSurface)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = signal.strategy,
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Confidence Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { signal.confidence / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = if (signal.confidence >= 82) GreenSignal else AmberSignal,
                    trackColor = BorderDark
                )
                Text(
                    text = "${signal.confidence}%",
                    color = if (signal.confidence >= 82) GreenSignal else AmberSignal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun CountdownBadge(
    anticipateSec: Int,
    entryTime: String,
    isArabic: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    when {
        anticipateSec <= 10 -> {
            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RedSignal.copy(alpha = 0.2f))
                    .border(1.dp, RedSignal, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isArabic) "🔴 دخول الآن!" else "🔴 ENTER NOW!",
                    color = RedSignal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        anticipateSec <= 35 -> {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberSignal.copy(alpha = 0.15f))
                    .border(1.dp, AmberSignal.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isArabic) "⚡ دخول خلال ${anticipateSec}ث" else "⚡ Enter in ${anticipateSec}s",
                    color = AmberSignal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else -> {
            Text(
                text = if (isArabic) "🕐 دخول: $entryTime" else "🕐 Entry: $entryTime",
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
