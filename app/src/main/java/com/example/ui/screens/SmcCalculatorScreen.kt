package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SignalDirection
import com.example.data.SmcSignalRead
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmcCalculatorScreen(
    broker: String,
    market: String,
    pair: String,
    expiry: String,
    useHighThinking: Boolean,
    isLoading: Boolean,
    result: SmcSignalRead?,
    error: String?,
    isArabic: Boolean,
    onSelectBroker: (String) -> Unit,
    onSelectMarket: (String) -> Unit,
    onSelectPair: (String) -> Unit,
    onSelectExpiry: (String) -> Unit,
    onToggleHighThinking: (Boolean) -> Unit,
    onRequestSignal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brokers = listOf("Pocket Option", "Quotex", "IQ Option", "Olymp Trade", "Binomo", "Exnova", "Bubinga", "Deriv", "Alpari")
    val markets = listOf("OTC", "Forex", "Crypto", "Stocks", "Commodities", "Indices")
    val pairs = listOf("EUR/USD", "GBP/USD", "USD/JPY", "USD/CAD", "AUD/USD", "BTC/USD", "ETH/USD", "XAU/USD (Gold)")
    val expiries = listOf("5m", "15m", "30m", "1h", "4h", "1d")

    var brokerExpanded by remember { mutableStateOf(false) }
    var marketExpanded by remember { mutableStateOf(false) }
    var pairExpanded by remember { mutableStateOf(false) }
    var showThinkingDetails by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgMidnight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title & Description
        Text(
            text = if (isArabic) "حاسبة إشارات SMC الذكية" else "SMC Signal AI Bot",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = if (isArabic) "بطاقة واحدة · قراءة حاسمة · بدقة الثواني" else "One card · One answer · To the second",
            color = PurpleLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PurplePrimary.copy(alpha = 0.4f), BorderDark)))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: Broker & Market Dropdowns
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Broker
                    ExposedDropdownMenuBox(
                        expanded = brokerExpanded,
                        onExpandedChange = { brokerExpanded = !brokerExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = broker,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isArabic) "الوسيط" else "Broker", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brokerExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = customTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = brokerExpanded,
                            onDismissRequest = { brokerExpanded = false },
                            modifier = Modifier.background(BgCardElevated)
                        ) {
                            brokers.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b, color = TextPrimary) },
                                    onClick = {
                                        onSelectBroker(b)
                                        brokerExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Market
                    ExposedDropdownMenuBox(
                        expanded = marketExpanded,
                        onExpandedChange = { marketExpanded = !marketExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = market,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isArabic) "السوق" else "Market", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marketExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = customTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = marketExpanded,
                            onDismissRequest = { marketExpanded = false },
                            modifier = Modifier.background(BgCardElevated)
                        ) {
                            markets.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m, color = TextPrimary) },
                                    onClick = {
                                        onSelectMarket(m)
                                        marketExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Row 2: Pair Dropdown
                ExposedDropdownMenuBox(
                    expanded = pairExpanded,
                    onExpandedChange = { pairExpanded = !pairExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = pair,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isArabic) "زوج التداول" else "Trading Pair", fontSize = 11.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pairExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = pairExpanded,
                        onDismissRequest = { pairExpanded = false },
                        modifier = Modifier.background(BgCardElevated)
                    ) {
                        pairs.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p, color = TextPrimary) },
                                onClick = {
                                    onSelectPair(p)
                                    pairExpanded = false
                                }
                            )
                        }
                    }
                }

                // Row 3: Expiry timeframe buttons
                Column {
                    Text(
                        text = if (isArabic) "مدة انتهاء الشمعة (Expiry)" else "Candle Expiry Timeframe",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(BgSurface)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        expiries.forEach { exp ->
                            val isSel = expiry.equals(exp, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSel) PurplePrimary else Color.Transparent)
                                    .clickable { onSelectExpiry(exp) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exp,
                                    color = if (isSel) Color.White else TextMuted,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Row 4: High Thinking Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgSurface)
                        .border(1.dp, if (useHighThinking) PurpleLight.copy(alpha = 0.5f) else BorderDark, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (useHighThinking) Icons.Default.Psychology else Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (useHighThinking) PurpleLight else AmberSignal,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = if (useHighThinking) {
                                    if (isArabic) "وضع التفكير المؤسسي العالي (Gemini 4 Argon)" else "High Thinking Mode (Gemini 4 Argon)"
                                } else {
                                    if (isArabic) "استجابة فائقة السرعة (Gemini 3.1 Flash-Lite)" else "Low-Latency Mode (Gemini 3.1 Flash-Lite)"
                                },
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (useHighThinking) {
                                    if (isArabic) "ThinkingLevel.HIGH لتحليل تفصيلي عميق" else "ThinkingLevel.HIGH multi-timeframe reasoning"
                                } else {
                                    if (isArabic) "سرعة فائقة للصفقات الخاطفة والنبض اللحظي" else "Sub-second instant candle pulse read"
                                },
                                color = TextMuted,
                                fontSize = 9.5.sp
                            )
                        }
                    }

                    Switch(
                        checked = useHighThinking,
                        onCheckedChange = onToggleHighThinking,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurplePrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = BgCard
                        ),
                        modifier = Modifier.testTag("switch_high_thinking")
                    )
                }

                // Get Signal Action Button
                Button(
                    onClick = onRequestSignal,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_get_smc_signal"),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        disabledContainerColor = PurplePrimary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "جارٍ قراءة الشمعة والسيولة..." else "Reading candle & liquidity...",
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "الحصول على التوصية الآن" else "Get Signal",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = error, color = RedSignal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // THE SIGNATURE ONE-CARD SMC RESULT
        val displayResult = result ?: SmcSignalRead(
            broker = broker,
            market = market,
            pair = pair,
            expiry = expiry,
            decision = SignalDirection.WAIT,
            agreementPercent = 42,
            entryTime = "—:—:—",
            closeTime = "—:—:—",
            rationale = if (isArabic) "اضغط على زر (الحصول على التوصية) لبدء قراءة الشمعة القادمة." else "Tap Get Signal to analyze the upcoming candle.",
            modelUsed = if (useHighThinking) "gemini-4-argon (ThinkingLevel.HIGH)" else "gemini-3.1-flash-lite"
        )

        SmcCardDisplay(
            read = displayResult,
            isArabic = isArabic,
            showThinkingDetails = showThinkingDetails,
            onToggleThinking = { showThinkingDetails = !showThinkingDetails }
        )
    }
}

@Composable
fun SmcCardDisplay(
    read: SmcSignalRead,
    isArabic: Boolean,
    showThinkingDetails: Boolean,
    onToggleThinking: () -> Unit
) {
    val (answerColor, answerBg, answerText, icon) = when (read.decision) {
        SignalDirection.UP -> Quadruple(
            GreenSignal,
            GreenSignal.copy(alpha = 0.15f),
            if (isArabic) "صعود (CALL)" else "CALL UP",
            Icons.Default.ArrowUpward
        )
        SignalDirection.DOWN -> Quadruple(
            RedSignal,
            RedSignal.copy(alpha = 0.15f),
            if (isArabic) "هبوط (PUT)" else "PUT DOWN",
            Icons.Default.ArrowDownward
        )
        SignalDirection.WAIT -> Quadruple(
            AmberSignal,
            AmberSignal.copy(alpha = 0.15f),
            if (isArabic) "انتظار (WAIT)" else "WAIT",
            Icons.Default.HourglassEmpty
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("smc_result_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(answerColor.copy(alpha = 0.7f), BorderDark)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Signal pill & Context
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isArabic) "بطاقة التوصية الذكية" else "SIGNAL CARD",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgSurface)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${read.broker} · ${read.market} · ${read.pair}",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Decision Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(answerBg)
                    .border(1.dp, answerColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(answerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = answerText,
                        color = answerColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = read.rationale,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Agreement Meter
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isArabic) "توافق المؤشرات والسيولة" else "Agreement Meter",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${read.agreementPercent}%",
                        color = answerColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { read.agreementPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = answerColor,
                    trackColor = BgSurface
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Exact Entry & Close times
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgSurface)
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) "وقت الدخول" else "ENTRY TIME",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = read.entryTime,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(30.dp).background(BorderDark))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) "وقت الإغلاق" else "CLOSE TIME",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = read.closeTime,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // High Thinking Chain (when available)
            if (!read.thoughts.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleThinking() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isArabic) "سلسلة التفكير المؤسسي الذكي (Thinking Trace)" else "High Thinking Reason Trace",
                            color = PurpleLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Default.ExpandMore, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(16.dp))
                }

                AnimatedVisibility(visible = showThinkingDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgSurface)
                            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = read.thoughts,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer disclaimer
            Text(
                text = if (isArabic) "النموذج النشط: ${read.modelUsed} · التوصيات للإرشاد وقرار التداول يعود لك." else "Model: ${read.modelUsed} · Signals are non-custodial reads.",
                color = TextMuted,
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PurpleLight,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = PurpleLight,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = BgSurface,
    unfocusedContainerColor = BgSurface
)
