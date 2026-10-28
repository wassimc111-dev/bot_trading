package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.BioscienceReport
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgCardElevated
import com.example.ui.theme.BgMidnight
import com.example.ui.theme.BgSurface
import com.example.ui.theme.BioCyan
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
fun BioscienceLabScreen(
    query: String,
    isLoading: Boolean,
    report: BioscienceReport?,
    error: String?,
    isArabic: Boolean,
    onQueryChange: (String) -> Unit,
    onRequestAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickTopics = listOf(
        "XBI Biotech ETF (FDA Catalysts)",
        "Moderna mRNA Phase 3 Topline Data",
        "CRISPR Therapeutics Gene-Editing Trial",
        "Pfizer Oncology PDUFA Approval Date",
        "Eli Lilly Weight-Loss Pipeline Impact"
    )

    var showThoughts by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgMidnight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with Science Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Biotech, contentDescription = null, tint = BioCyan, modifier = Modifier.size(24.dp))
            Text(
                text = if (isArabic) "مختبر العلوم والتفكير العميق" else "Bioscience & High Thinking Lab",
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = if (isArabic) "تحليل المحفزات البيولوجية والطبية ونموذج التفكير عالي المستوى (Gemini 4 Argon)"
            else "Biomedical trial catalysts & High Thinking Mode (ThinkingLevel.HIGH)",
            color = BioCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Query Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BioCyan.copy(alpha = 0.5f), BorderDark)))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isArabic) "الأصل أو الحدث الحيوي المستهدف" else "Target Asset or Biomedical Catalyst",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth().testTag("input_bioscience_query"),
                    colors = customTextFieldColors(),
                    placeholder = { Text("e.g. XBI Biotech ETF, FDA approval, Phase 3 trial...", color = TextMuted, fontSize = 12.sp) }
                )

                // Quick chips
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTopics.forEach { topic ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(BgSurface)
                                .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                                .clickable { onQueryChange(topic) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = topic, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Analyze button
                Button(
                    onClick = onRequestAnalysis,
                    enabled = !isLoading && query.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_run_bioscience_thinking"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BioCyan,
                        disabledContainerColor = BioCyan.copy(alpha = 0.3f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isArabic) "نموذج Gemini 4 Argon يمارس التفكير العميق..." else "Gemini 4 Argon deep thinking in progress...", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "بدء التفكير العلمي العميق" else "Run Deep Thinking Analysis", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = error, color = RedSignal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Display Bioscience Report
        if (report != null) {
            BioscienceReportCard(
                report = report,
                isArabic = isArabic,
                showThoughts = showThoughts,
                onToggleThoughts = { showThoughts = !showThoughts }
            )
        }
    }
}

@Composable
fun BioscienceReportCard(
    report: BioscienceReport,
    isArabic: Boolean,
    showThoughts: Boolean,
    onToggleThoughts: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("bioscience_report_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(BioCyan, BorderDark)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.assetSymbol,
                    color = BioCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (report.impactRating.lowercase()) {
                                "extreme" -> RedSignal.copy(alpha = 0.2f)
                                "high" -> AmberSignal.copy(alpha = 0.2f)
                                else -> GreenSignal.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Impact: ${report.impactRating}",
                        color = when (report.impactRating.lowercase()) {
                            "extreme" -> RedSignal
                            "high" -> AmberSignal
                            else -> GreenSignal
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = report.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            // Catalyst & Stage
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgSurface)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Text(if (isArabic) "نوع المحفز العلمي" else "Catalyst Type", color = TextMuted, fontSize = 9.sp)
                    Text(report.catalystType, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BgSurface)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Text(if (isArabic) "المرحلة السريرية" else "Clinical Phase", color = TextMuted, fontSize = 9.sp)
                    Text(report.phaseOrStage, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Market Correlations
            Column {
                Text(
                    text = if (isArabic) "الارتباطات السوقية وحركة السيولة" else "Market Correlations",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    report.marketCorrelations.forEach { corr ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PurplePrimary.copy(alpha = 0.2f))
                                .border(1.dp, PurpleLight.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(corr, color = PurpleLight, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Order Flow Prediction
            Column {
                Text(
                    text = if (isArabic) "توقعات تدفق الأوامر والتقلبات" else "Order Flow Prediction",
                    color = BioCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.orderFlowPrediction,
                    color = TextPrimary,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Scientific Reasoning
            Column {
                Text(
                    text = if (isArabic) "التحليل العلمي والبيولوجي المتكامل" else "Scientific & Quantitative Breakdown",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = report.reasoningChain,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Thinking Chain
            if (!report.thoughts.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleThoughts() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = BioCyan, modifier = Modifier.size(16.dp))
                        Text(
                            text = if (isArabic) "سلسلة التفكير عالي المستوى (ThinkingLevel.HIGH)" else "High Thinking Reason Trace",
                            color = BioCyan,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Icons.Default.ExpandMore, contentDescription = null, tint = BioCyan, modifier = Modifier.size(16.dp))
                }

                AnimatedVisibility(visible = showThoughts) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BgSurface)
                            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = report.thoughts,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
