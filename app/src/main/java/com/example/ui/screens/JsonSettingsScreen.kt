package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JsonSettingsScreen(
    jsonOutput: String,
    isArabic: Boolean,
    useGeminiAi: Boolean,
    useCloudAi: Boolean,
    useTradingView: Boolean,
    useOanda: Boolean,
    lossFilterEnabled: Boolean,
    onToggleGemini: (Boolean) -> Unit,
    onToggleCloud: (Boolean) -> Unit,
    onToggleTradingView: (Boolean) -> Unit,
    onToggleOanda: (Boolean) -> Unit,
    onToggleLossFilter: (Boolean) -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgMidnight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(24.dp))
            Text(
                text = if (isArabic) "بيانات JSON وإعدادات المحرك" else "Live JSON & Engine Settings",
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Live JSON Viewer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PurplePrimary.copy(alpha = 0.5f), BorderDark)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isArabic) "📄 بيانات الإشارات المباشرة (JSON)" else "📄 Live Signal Stream (JSON)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Vixunity JSON", jsonOutput)
                            clipboard.setPrimaryClip(clip)
                            isCopied = true
                            Toast.makeText(context, if (isArabic) "تم نسخ JSON إلى الحافظة بنجاح" else "JSON copied to clipboard", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                delay(2000L)
                                isCopied = false
                            }
                        },
                        modifier = Modifier.height(34.dp).testTag("btn_copy_json"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isCopied) GreenSignal else PurplePrimary)
                    ) {
                        Icon(if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCopied) (if (isArabic) "تم النسخ" else "Copied") else (if (isArabic) "نسخ JSON" else "Copy JSON"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A0C14))
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                ) {
                    Text(
                        text = jsonOutput,
                        color = Color(0xFF86EFAC),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Engine Integration Toggles Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isArabic) "🤖 تكامل محركات الذكاء الاصطناعي والبيانات" else "🤖 AI Engine & Data Sources",
                    color = PurpleLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                SettingToggleRow(
                    title = "Google Gemini AI (Argon & Flash-Lite)",
                    subtitle = if (isArabic) "تحليل الشارت والرؤية والتفكير المؤسسي" else "Chart vision & High thinking",
                    checked = useGeminiAi,
                    onCheckedChange = onToggleGemini
                )

                SettingToggleRow(
                    title = "Cloud AI High-Frequency Engine",
                    subtitle = if (isArabic) "حسابات السيولة السحابية السريعة" else "Cloud liquidity and fast calculations",
                    checked = useCloudAi,
                    onCheckedChange = onToggleCloud
                )

                SettingToggleRow(
                    title = "TradingView Live Feeds",
                    subtitle = if (isArabic) "مؤشرات وتدفقات الشموع اللحظية" else "Technical indicators & candle flow",
                    checked = useTradingView,
                    onCheckedChange = onToggleTradingView
                )

                SettingToggleRow(
                    title = "OANDA Global Liquidity Pool",
                    subtitle = if (isArabic) "أسعار الفوركس المباشرة" else "Live FX prices and institutional spreads",
                    checked = useOanda,
                    onCheckedChange = onToggleOanda
                )

                SettingToggleRow(
                    title = if (isArabic) "تصفية الإشارات الخاسرة (Loss Filter)" else "Low-Confidence Loss Filter",
                    subtitle = if (isArabic) "إخفاء الإشارات ذات الدقة المنخفضة تلقائياً" else "Automatically purge signals below 68% confidence",
                    checked = lossFilterEnabled,
                    onCheckedChange = onToggleLossFilter
                )
            }
        }

        // Language & Localization Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isArabic) "اللغة / Language" else "Language / اللغة",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "الحالية: العربية 🇸🇦" else "Current: English 🇺🇸",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onToggleLanguage,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text(if (isArabic) "English 🇺🇸" else "العربية 🇸🇦", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Risk Warning Card (Mandatory compliance)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BgCardElevated),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AmberSignal.copy(alpha = 0.5f), BorderDark)))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AmberSignal, modifier = Modifier.size(20.dp))
                Column {
                    Text(
                        text = if (isArabic) "تحذير المخاطر وإخلاء المسؤولية" else "Risk Warning & Non-Custodial Disclaimer",
                        color = AmberSignal,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isArabic) {
                            "تداول الخيارات الثنائية والفوركس ينطوي على مخاطر مالية عالية. هذا التطبيق أداة تحليل ذكية ومولد إشارات استرشادية، ولا يحتفظ بأموالك أو يحل محل إدارة المخاطر المستقلة. تداول فقط بالأموال التي يمكنك تحمل خسارتها."
                        } else {
                            "Binary options and OTC markets carry high financial risk. This app is an intelligent signal generator and does not hold user funds or execute automatic broker deposits. Always trade responsibly."
                        },
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(BgSurface)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 10.sp)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PurplePrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = BgCard
            )
        )
    }
}
