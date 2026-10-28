package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChartAnalysisResult
import com.example.data.SignalDirection
import com.example.ui.ChartAnalyzerPhase
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgCardElevated
import com.example.ui.theme.BgMidnight
import com.example.ui.theme.BgSurface
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.GreenSignal
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.RedSignal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.InputStream

@Composable
fun ChartAnalyzerScreen(
    phase: ChartAnalyzerPhase,
    market: String,
    timeframe: String,
    chartBitmap: Bitmap?,
    videoFrames: List<Bitmap>,
    isAnalyzing: Boolean,
    countdown: Int,
    result: ChartAnalysisResult?,
    error: String?,
    isArabic: Boolean,
    onSelectMarket: (String) -> Unit,
    onSelectTimeframe: (String) -> Unit,
    onChartImageSelected: (Bitmap) -> Unit,
    onVideoFramesSelected: (List<Bitmap>) -> Unit,
    onStartAnalysis: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Media Picker for Photo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onChartImageSelected(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Media Picker for Video
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationMs = durationStr?.toLongOrNull() ?: 5000L

                // Extract 3 representative keyframes from the video
                val frames = mutableListOf<Bitmap>()
                val points = listOf(durationMs / 4, durationMs / 2, (durationMs * 3) / 4)
                for (timeMs in points) {
                    val frame = retriever.getFrameAtTime(timeMs * 1000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    if (frame != null) {
                        frames.add(frame)
                    }
                }
                retriever.release()
                if (frames.isNotEmpty()) {
                    onVideoFramesSelected(frames)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgMidnight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = if (isArabic) "أداة تحليل الشارت بالذكاء الاصطناعي" else "AI Chart & Video Scanner",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = if (isArabic) "تحليل متعدد الطبقات للسيولة والأوامر عبر Gemini 4 Argon" else "Multi-Layer Liquidity & Order Flow with Gemini 4 Argon",
            color = PurpleLight,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
        )

        // Phases: SELECT, SCANNING, RESULT
        when (phase) {
            ChartAnalyzerPhase.SELECT -> {
                SelectAndUploadCard(
                    market = market,
                    timeframe = timeframe,
                    chartBitmap = chartBitmap,
                    isArabic = isArabic,
                    isVideoSelected = videoFrames.isNotEmpty(),
                    onSelectMarket = onSelectMarket,
                    onSelectTimeframe = onSelectTimeframe,
                    onPickPhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onPickVideo = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    onSelectSample = {
                        // Generate a synthetic procedural candle chart bitmap for instant testing
                        val sampleBitmap = createSampleChartBitmap()
                        onChartImageSelected(sampleBitmap)
                    },
                    onAnalyze = onStartAnalysis
                )
            }
            ChartAnalyzerPhase.SCANNING -> {
                ScanningHologramCard(countdown = countdown, isArabic = isArabic)
            }
            ChartAnalyzerPhase.RESULT -> {
                if (result != null) {
                    ResultAnalysisCard(
                        result = result,
                        isArabic = isArabic,
                        onReset = onReset
                    )
                }
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error, color = RedSignal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SelectAndUploadCard(
    market: String,
    timeframe: String,
    chartBitmap: Bitmap?,
    isArabic: Boolean,
    isVideoSelected: Boolean,
    onSelectMarket: (String) -> Unit,
    onSelectTimeframe: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onPickVideo: () -> Unit,
    onSelectSample: () -> Unit,
    onAnalyze: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BluePrimary.copy(alpha = 0.5f), BorderDark)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Timeframe Selector: 1m vs 5m
            Column {
                Text(
                    text = if (isArabic) "نوع الفحص والإطار الزمني" else "Scan Mode & Timeframe",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val tf5mSelected = timeframe == "5m"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tf5mSelected) BluePrimary else BgSurface)
                            .border(1.dp, if (tf5mSelected) PurpleLight else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { onSelectTimeframe("5m") }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "📈 فحص الأرباح (5 دقائق)" else "📈 5-Minute Scan",
                            color = if (tf5mSelected) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    val tf1mSelected = timeframe == "1m"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tf1mSelected) BluePrimary else BgSurface)
                            .border(1.dp, if (tf1mSelected) PurpleLight else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { onSelectTimeframe("1m") }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "⚡ فحص الأرباح (1 دقيقة)" else "⚡ 1-Minute Fast",
                            color = if (tf1mSelected) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Market Selector: Forex vs OTC
            Column {
                Text(
                    text = if (isArabic) "اختيار السوق المستهدف" else "Target Market",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isForex = market.equals("forex", ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isForex) BluePrimary else BgSurface)
                            .border(1.dp, if (isForex) PurpleLight else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { onSelectMarket("Forex") }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "📊 سوق الفوركس (Forex)" else "📊 Forex Market",
                            color = if (isForex) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    val isOtc = market.equals("otc", ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isOtc) BluePrimary else BgSurface)
                            .border(1.dp, if (isOtc) PurpleLight else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { onSelectMarket("OTC") }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "🔴 سوق OTC (نهاية الأسبوع)" else "🔴 OTC Market (24/7)",
                            color = if (isOtc) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Media Preview or Placeholder
            if (chartBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderHighlight, RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        bitmap = chartBitmap.asImageBitmap(),
                        contentDescription = "Chart preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    if (isVideoSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PurplePrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("VIDEO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Upload buttons: Photo / Video / Sample
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPickPhoto,
                    modifier = Modifier.weight(1f).height(44.dp).testTag("btn_choose_chart_photo"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "صورة الشارت" else "Upload Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPickVideo,
                    modifier = Modifier.weight(1f).height(44.dp).testTag("btn_choose_chart_video"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isArabic) "فيديو الشارت" else "Upload Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Use Sample Chart action
            OutlinedButton(
                onClick = onSelectSample,
                modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_use_sample_chart"),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderHighlight)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurpleLight, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "تجربة نموذج شارت جاهز (Pocket Option Sample)" else "Load Instant Demo Chart",
                    color = PurpleLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Analyze Action
            Button(
                onClick = onAnalyze,
                enabled = chartBitmap != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_analyze_chart_gemini"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    disabledContainerColor = BluePrimary.copy(alpha = 0.3f)
                )
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isArabic) "🤖 تحليل الشارت بالذكاء الاصطناعي (Gemini 4 Argon)" else "Analyze with Gemini 4 Argon",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ScanningHologramCard(countdown: Int, isArabic: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )
    val rotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rot"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BluePrimary, PurpleLight)))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Glowing Holographic Orb
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Ring
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .rotate(rotate)
                        .border(2.dp, Brush.sweepGradient(listOf(BluePrimary, PurpleLight, Color.Transparent)), CircleShape)
                )
                // Center Sphere
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF5070EE), Color(0xFF1A237E), Color(0xFF0C1440))
                            )
                        )
                )
            }

            Text(
                text = if (isArabic) "جارٍ فحص أزواج الصفقات وهيكل الشارت..." else "Scanning chart structure and institutional orders...",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = if (isArabic) "الرجاء الانتظار $countdown ثانية..." else "Please wait $countdown seconds...",
                color = PurpleLight,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ResultAnalysisCard(
    result: ChartAnalysisResult,
    isArabic: Boolean,
    onReset: () -> Unit
) {
    val isBuy = result.signal == SignalDirection.UP
    val sigColor = if (isBuy) GreenSignal else RedSignal
    val sigBg = if (isBuy) GreenSignal.copy(alpha = 0.15f) else RedSignal.copy(alpha = 0.15f)
    val dirWord = if (isBuy) (if (isArabic) "صعود / شراء" else "CALL / BUY") else (if (isArabic) "هبوط / بيع" else "PUT / SELL")

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Main Recommendation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(sigColor, BorderDark)))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isArabic) "الصفقة المكتملة مع انتهاء هذه الدقيقة" else "Completed Signal with Candle Close",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(sigBg)
                        .border(1.dp, sigColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "● $dirWord",
                        color = sigColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Row: Confidence, Risk Level, Signal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        title = if (isArabic) "نسبة الثقة" else "Confidence",
                        value = "${result.confidence}%",
                        color = PurpleLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = if (isArabic) "مستوى المخاطرة" else "Risk Level",
                        value = result.riskLevel,
                        color = AmberSignal,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = if (isArabic) "الإشارة" else "Signal",
                        value = if (isBuy) "CALL" else "PUT",
                        color = sigColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Take Profits & Stop Loss
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "🎯 أهداف الربح ووقف الخسارة (Targets & SL)" else "🎯 Targets & Stop Loss",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                result.takeProfits.forEachIndexed { idx, tp ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isArabic) "الهدف ${idx + 1}" else "Target ${idx + 1}", color = TextSecondary, fontSize = 11.5.sp)
                        Text(tp, color = GreenSignal, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isArabic) "وقف الخسارة" else "Stop Loss", color = TextSecondary, fontSize = 11.5.sp)
                    Text(result.stopLoss, color = RedSignal, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Multi-Layer Breakdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isArabic) "🔍 تفاصيل التحليل متعدد الطبقات" else "🔍 Multi-Layer Institutional Analysis",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                LayerRow(title = if (isArabic) "💧 السيولة العميقة (Deep Liquidity)" else "Deep Liquidity", content = result.liquidityAnalysis)
                LayerRow(title = if (isArabic) "⚖️ تقييم المخاطر (Risk Assessment)" else "Risk Assessment", content = result.riskAssessment)
                LayerRow(title = if (isArabic) "⚡ الزخم السعري (Price Momentum)" else "Price Momentum", content = result.momentumAnalysis)
                LayerRow(title = if (isArabic) "🏦 أوامر المؤسسات (Institutional Orders)" else "Institutional Footprint", content = result.institutionalOrders)
                LayerRow(title = if (isArabic) "🎯 الخلاصة التنفيذية" else "Executive Summary", content = result.summary)
            }
        }

        // Reset Button
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_reset_chart_analysis"),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isArabic) "← بدء تحليل شارت جديد" else "Start New Chart Analysis", color = TextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LayerRow(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, color = PurpleLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
            text = content,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun StatBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BgSurface)
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        Text(text = title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Creates a synthetic procedural candlestick chart bitmap for instant live demo testing.
 */
fun createSampleChartBitmap(): Bitmap {
    val width = 480
    val height = 280
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val bgPaint = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#0C1020") }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Grid lines
    val gridPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#1C2540")
        strokeWidth = 1f
    }
    for (y in 40 until height step 40) {
        canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
    }
    for (x in 40 until width step 40) {
        canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
    }

    // Draw candlesticks
    val greenPaint = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#22C55E") }
    val redPaint = android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#EF4444") }
    val wickPaint = android.graphics.Paint().apply { strokeWidth = 2f }

    val candles = listOf(
        Triple(180, 160, 200), Triple(165, 140, 175), Triple(145, 155, 160),
        Triple(150, 130, 160), Triple(135, 110, 145), Triple(115, 95, 125),
        Triple(100, 115, 120), Triple(110, 85, 120), Triple(90, 70, 100),
        Triple(75, 55, 85)
    )

    var curX = 30f
    candles.forEach { (open, close, wickHigh) ->
        val isUp = close < open
        val p = if (isUp) greenPaint else redPaint
        wickPaint.color = p.color
        val wickLow = maxOf(open, close) + 15
        canvas.drawLine(curX + 10f, wickHigh.toFloat(), curX + 10f, wickLow.toFloat(), wickPaint)
        canvas.drawRect(curX, minOf(open, close).toFloat(), curX + 20f, maxOf(open, close).toFloat(), p)
        curX += 42f
    }

    return bitmap
}
