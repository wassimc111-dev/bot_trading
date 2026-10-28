package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.TradingViewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.screens.BioscienceLabScreen
import com.example.ui.screens.ChartAnalyzerScreen
import com.example.ui.screens.JsonSettingsScreen
import com.example.ui.screens.LiveSignalsScreen
import com.example.ui.screens.SmcCalculatorScreen
import com.example.ui.theme.BgMidnight
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                TradingAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TradingAppRoot(viewModel: TradingViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()

    // Handle back button: if not on Home tab (0), return to Home tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.selectTab(0)
    }

    // Set layout direction according to chosen language (RTL for Arabic, LTR for English)
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BgMidnight,
            topBar = {
                val isRunning by viewModel.isEngineRunning.collectAsState()
                AppHeader(
                    isArabic = isArabic,
                    isEngineRunning = isRunning,
                    onToggleEngine = { viewModel.toggleEngine() },
                    onRefresh = { viewModel.refreshSignals() },
                    onToggleLanguage = { viewModel.toggleLanguage() }
                )
            },
            bottomBar = {
                AppBottomNav(
                    currentTab = currentTab,
                    isArabic = isArabic,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BgMidnight)
            ) {
                when (currentTab) {
                    0 -> {
                        val signals by viewModel.filteredSignals.collectAsState()
                        val stats by viewModel.stats.collectAsState()
                        val selectedTf by viewModel.selectedTimeframe.collectAsState()
                        val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

                        LiveSignalsScreen(
                            signals = signals,
                            stats = stats,
                            selectedTimeframe = selectedTf,
                            selectedStatusFilter = selectedStatus,
                            isArabic = isArabic,
                            onSelectTimeframe = { viewModel.setTimeframeFilter(it) },
                            onSelectStatus = { viewModel.setStatusFilter(it) }
                        )
                    }
                    1 -> {
                        val broker by viewModel.smcBroker.collectAsState()
                        val market by viewModel.smcMarket.collectAsState()
                        val pair by viewModel.smcPair.collectAsState()
                        val expiry by viewModel.smcExpiry.collectAsState()
                        val useHighThinking by viewModel.smcUseHighThinking.collectAsState()
                        val isLoading by viewModel.smcLoading.collectAsState()
                        val result by viewModel.smcResult.collectAsState()
                        val error by viewModel.smcError.collectAsState()

                        SmcCalculatorScreen(
                            broker = broker,
                            market = market,
                            pair = pair,
                            expiry = expiry,
                            useHighThinking = useHighThinking,
                            isLoading = isLoading,
                            result = result,
                            error = error,
                            isArabic = isArabic,
                            onSelectBroker = { viewModel.setSmcBroker(it) },
                            onSelectMarket = { viewModel.setSmcMarket(it) },
                            onSelectPair = { viewModel.setSmcPair(it) },
                            onSelectExpiry = { viewModel.setSmcExpiry(it) },
                            onToggleHighThinking = { viewModel.setSmcUseHighThinking(it) },
                            onRequestSignal = { viewModel.requestSmcSignal() }
                        )
                    }
                    2 -> {
                        val phase by viewModel.analyzerPhase.collectAsState()
                        val market by viewModel.analyzerMarket.collectAsState()
                        val timeframe by viewModel.analyzerTimeframe.collectAsState()
                        val chartBitmap by viewModel.chartBitmap.collectAsState()
                        val videoFrames by viewModel.videoFrames.collectAsState()
                        val isAnalyzing by viewModel.isAnalyzingMedia.collectAsState()
                        val countdown by viewModel.scanCountdown.collectAsState()
                        val result by viewModel.chartResult.collectAsState()
                        val error by viewModel.analyzerError.collectAsState()

                        ChartAnalyzerScreen(
                            phase = phase,
                            market = market,
                            timeframe = timeframe,
                            chartBitmap = chartBitmap,
                            videoFrames = videoFrames,
                            isAnalyzing = isAnalyzing,
                            countdown = countdown,
                            result = result,
                            error = error,
                            isArabic = isArabic,
                            onSelectMarket = { viewModel.setAnalyzerMarket(it) },
                            onSelectTimeframe = { viewModel.setAnalyzerTimeframe(it) },
                            onChartImageSelected = { viewModel.onChartImageSelected(it) },
                            onVideoFramesSelected = { viewModel.onVideoFramesSelected(it) },
                            onStartAnalysis = { viewModel.startChartAnalysis() },
                            onReset = { viewModel.resetAnalyzer() }
                        )
                    }
                    3 -> {
                        val query by viewModel.bioQuery.collectAsState()
                        val isLoading by viewModel.bioLoading.collectAsState()
                        val report by viewModel.bioReport.collectAsState()
                        val error by viewModel.bioError.collectAsState()

                        BioscienceLabScreen(
                            query = query,
                            isLoading = isLoading,
                            report = report,
                            error = error,
                            isArabic = isArabic,
                            onQueryChange = { viewModel.setBioQuery(it) },
                            onRequestAnalysis = { viewModel.requestBioscienceAnalysis() }
                        )
                    }
                    4 -> {
                        val useGemini by viewModel.useGeminiAi.collectAsState()
                        val useCloud by viewModel.useCloudAi.collectAsState()
                        val useTv by viewModel.useTradingView.collectAsState()
                        val useOanda by viewModel.useOanda.collectAsState()
                        val lossFilter by viewModel.lossFilterEnabled.collectAsState()
                        val jsonOutput = viewModel.generateJsonExport()

                        JsonSettingsScreen(
                            jsonOutput = jsonOutput,
                            isArabic = isArabic,
                            useGeminiAi = useGemini,
                            useCloudAi = useCloud,
                            useTradingView = useTv,
                            useOanda = useOanda,
                            lossFilterEnabled = lossFilter,
                            onToggleGemini = { viewModel.useGeminiAi.value = it },
                            onToggleCloud = { viewModel.useCloudAi.value = it },
                            onToggleTradingView = { viewModel.useTradingView.value = it },
                            onToggleOanda = { viewModel.useOanda.value = it },
                            onToggleLossFilter = { viewModel.lossFilterEnabled.value = it },
                            onToggleLanguage = { viewModel.toggleLanguage() }
                        )
                    }
                }
            }
        }
    }
}
