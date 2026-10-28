package com.example.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BioscienceReport
import com.example.data.ChartAnalysisResult
import com.example.data.SignalItem
import com.example.data.SignalStatus
import com.example.data.SmcSignalRead
import com.example.data.TradingRepository
import com.example.data.TradingStats
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "TradingViewModel"

enum class ChartAnalyzerPhase {
    SELECT,
    SCANNING,
    RESULT
}

class TradingViewModel(
    private val repository: TradingRepository = TradingRepository()
) : ViewModel() {

    // Tab Navigation: 0 = Live Signals, 1 = SMC Bot, 2 = AI Analyzer, 3 = Bioscience Lab, 4 = JSON Settings
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Language: true = Arabic, false = English
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    // --- TAB 1: Live Signals (Vixunity Pro) ---
    val allSignals: StateFlow<List<SignalItem>> = repository.signals
    val stats: StateFlow<TradingStats> = repository.stats
    val isEngineRunning: StateFlow<Boolean> = repository.isEngineRunning

    private val _selectedTimeframe = MutableStateFlow("ALL")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("ALL")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    val filteredSignals: StateFlow<List<SignalItem>> = combine(
        allSignals,
        selectedTimeframe,
        selectedStatusFilter
    ) { signals, tf, status ->
        signals.filter { item ->
            val matchTf = tf == "ALL" || item.timeframe.equals(tf, ignoreCase = true)
            val matchStatus = when (status) {
                "PENDING" -> item.status == SignalStatus.PENDING
                "WIN" -> item.status == SignalStatus.WIN
                "LOSS" -> item.status == SignalStatus.LOSS
                else -> true
            }
            matchTf && matchStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    // --- TAB 2: SMC Signal AI Bot ---
    private val _smcBroker = MutableStateFlow("Pocket Option")
    val smcBroker: StateFlow<String> = _smcBroker.asStateFlow()

    private val _smcMarket = MutableStateFlow("OTC")
    val smcMarket: StateFlow<String> = _smcMarket.asStateFlow()

    private val _smcPair = MutableStateFlow("EUR/USD")
    val smcPair: StateFlow<String> = _smcPair.asStateFlow()

    private val _smcExpiry = MutableStateFlow("30m")
    val smcExpiry: StateFlow<String> = _smcExpiry.asStateFlow()

    private val _smcUseHighThinking = MutableStateFlow(false)
    val smcUseHighThinking: StateFlow<Boolean> = _smcUseHighThinking.asStateFlow()

    private val _smcLoading = MutableStateFlow(false)
    val smcLoading: StateFlow<Boolean> = _smcLoading.asStateFlow()

    private val _smcResult = MutableStateFlow<SmcSignalRead?>(null)
    val smcResult: StateFlow<SmcSignalRead?> = _smcResult.asStateFlow()

    private val _smcError = MutableStateFlow<String?>(null)
    val smcError: StateFlow<String?> = _smcError.asStateFlow()

    // --- TAB 3: Chart & Video Analyzer (Pocket Option AI) ---
    private val _analyzerPhase = MutableStateFlow(ChartAnalyzerPhase.SELECT)
    val analyzerPhase: StateFlow<ChartAnalyzerPhase> = _analyzerPhase.asStateFlow()

    private val _analyzerMarket = MutableStateFlow("OTC")
    val analyzerMarket: StateFlow<String> = _analyzerMarket.asStateFlow()

    private val _analyzerTimeframe = MutableStateFlow("1m")
    val analyzerTimeframe: StateFlow<String> = _analyzerTimeframe.asStateFlow()

    private val _chartBitmap = MutableStateFlow<Bitmap?>(null)
    val chartBitmap: StateFlow<Bitmap?> = _chartBitmap.asStateFlow()

    private val _videoFrames = MutableStateFlow<List<Bitmap>>(emptyList())
    val videoFrames: StateFlow<List<Bitmap>> = _videoFrames.asStateFlow()

    private val _isAnalyzingMedia = MutableStateFlow(false)
    val isAnalyzingMedia: StateFlow<Boolean> = _isAnalyzingMedia.asStateFlow()

    private val _scanCountdown = MutableStateFlow(4)
    val scanCountdown: StateFlow<Int> = _scanCountdown.asStateFlow()

    private val _chartResult = MutableStateFlow<ChartAnalysisResult?>(null)
    val chartResult: StateFlow<ChartAnalysisResult?> = _chartResult.asStateFlow()

    private val _analyzerError = MutableStateFlow<String?>(null)
    val analyzerError: StateFlow<String?> = _analyzerError.asStateFlow()

    // --- TAB 4: Bioscience & High Thinking Lab ---
    private val _bioQuery = MutableStateFlow("XBI Biotech ETF (FDA Catalysts & Clinical Phase 3 Volatility)")
    val bioQuery: StateFlow<String> = _bioQuery.asStateFlow()

    private val _bioLoading = MutableStateFlow(false)
    val bioLoading: StateFlow<Boolean> = _bioLoading.asStateFlow()

    private val _bioReport = MutableStateFlow<BioscienceReport?>(null)
    val bioReport: StateFlow<BioscienceReport?> = _bioReport.asStateFlow()

    private val _bioError = MutableStateFlow<String?>(null)
    val bioError: StateFlow<String?> = _bioError.asStateFlow()

    // --- Engine Configs (Settings) ---
    val useGeminiAi = MutableStateFlow(true)
    val useCloudAi = MutableStateFlow(true)
    val useTradingView = MutableStateFlow(true)
    val useOanda = MutableStateFlow(true)
    val lossFilterEnabled = MutableStateFlow(true)

    fun selectTab(tab: Int) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun setTimeframeFilter(tf: String) {
        _selectedTimeframe.value = tf
    }

    fun setStatusFilter(status: String) {
        _selectedStatusFilter.value = status
    }

    fun toggleEngine() {
        repository.toggleEngineRunning()
    }

    fun refreshSignals() {
        repository.forceRefresh()
    }

    // --- SMC Bot Actions ---
    fun setSmcBroker(b: String) { _smcBroker.value = b }
    fun setSmcMarket(m: String) { _smcMarket.value = m }
    fun setSmcPair(p: String) { _smcPair.value = p }
    fun setSmcExpiry(e: String) { _smcExpiry.value = e }
    fun setSmcUseHighThinking(enabled: Boolean) { _smcUseHighThinking.value = enabled }

    fun requestSmcSignal() {
        viewModelScope.launch {
            _smcLoading.value = true
            _smcError.value = null
            try {
                val read = repository.generateSmcSignal(
                    broker = _smcBroker.value,
                    market = _smcMarket.value,
                    pair = _smcPair.value,
                    expiry = _smcExpiry.value,
                    useHighThinking = _smcUseHighThinking.value,
                    languageAr = _isArabic.value
                )
                _smcResult.value = read
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "SMC signal generation timed out", e)
                _smcError.value = if (_isArabic.value) {
                    "استغرقت الاستجابة وقتاً أطول من المعتاد، يرجى المحاولة ثانية."
                } else {
                    "Generation timed out. Please try again."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "SMC Signal error", e)
                _smcError.value = e.localizedMessage ?: "Failed to generate SMC signal."
            } finally {
                _smcLoading.value = false
            }
        }
    }

    // --- Chart & Video Analyzer Actions ---
    fun setAnalyzerMarket(m: String) { _analyzerMarket.value = m }
    fun setAnalyzerTimeframe(tf: String) { _analyzerTimeframe.value = tf }

    fun onChartImageSelected(bitmap: Bitmap) {
        _chartBitmap.value = bitmap
        _videoFrames.value = emptyList()
        _analyzerPhase.value = ChartAnalyzerPhase.SELECT
    }

    fun onVideoFramesSelected(frames: List<Bitmap>) {
        _videoFrames.value = frames
        _chartBitmap.value = frames.firstOrNull()
        _analyzerPhase.value = ChartAnalyzerPhase.SELECT
    }

    private var scanJob: Job? = null

    fun startChartAnalysis() {
        val bitmap = _chartBitmap.value
        val frames = _videoFrames.value
        if (bitmap == null && frames.isEmpty()) return

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _analyzerPhase.value = ChartAnalyzerPhase.SCANNING
            _isAnalyzingMedia.value = true
            _analyzerError.value = null

            // 4-second animated scanning phase matching the Pocket Option UX
            for (sec in 4 downTo 1) {
                _scanCountdown.value = sec
                delay(1000L)
            }

            try {
                val result = if (frames.size > 1) {
                    repository.analyzeChartVideoFrames(
                        frames = frames,
                        market = _analyzerMarket.value,
                        timeframe = _analyzerTimeframe.value,
                        languageAr = _isArabic.value
                    )
                } else {
                    repository.analyzeChartImage(
                        bitmap = bitmap!!,
                        market = _analyzerMarket.value,
                        timeframe = _analyzerTimeframe.value,
                        languageAr = _isArabic.value
                    )
                }
                _chartResult.value = result
                _analyzerPhase.value = ChartAnalyzerPhase.RESULT
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Chart analysis timed out", e)
                _analyzerError.value = if (_isArabic.value) {
                    "انتهت مهلة التحليل الذكي للرسم البياني. حاول مرة أخرى."
                } else {
                    "Analysis timed out. Please try again."
                }
                _analyzerPhase.value = ChartAnalyzerPhase.SELECT
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chart analysis error", e)
                _analyzerError.value = e.localizedMessage ?: "Analysis failed."
                _analyzerPhase.value = ChartAnalyzerPhase.SELECT
            } finally {
                _isAnalyzingMedia.value = false
            }
        }
    }

    fun resetAnalyzer() {
        scanJob?.cancel()
        _analyzerPhase.value = ChartAnalyzerPhase.SELECT
        _chartResult.value = null
        _chartBitmap.value = null
        _videoFrames.value = emptyList()
        _analyzerError.value = null
    }

    // --- Bioscience Lab Actions ---
    fun setBioQuery(q: String) { _bioQuery.value = q }

    fun requestBioscienceAnalysis() {
        viewModelScope.launch {
            _bioLoading.value = true
            _bioError.value = null
            try {
                val rep = repository.generateBioscienceAnalysis(
                    symbolOrTopic = _bioQuery.value,
                    languageAr = _isArabic.value
                )
                _bioReport.value = rep
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Bioscience analysis timed out", e)
                _bioError.value = if (_isArabic.value) {
                    "انتهت مهلة استجابة التفكير العميق. حاول مرة ثانية."
                } else {
                    "High thinking analysis timed out. Try again."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Bioscience error", e)
                _bioError.value = e.localizedMessage ?: "Analysis failed."
            } finally {
                _bioLoading.value = false
            }
        }
    }

    // --- JSON Inspector Output ---
    fun generateJsonExport(): String {
        val root = JSONObject()
        root.put("engine", "Vixunity Pro SMC Engine")
        root.put("version", "2.5.0")
        root.put("timestamp", System.currentTimeMillis())

        val cfg = JSONObject()
        cfg.put("google_gemini_ai", useGeminiAi.value)
        cfg.put("cloud_ai", useCloudAi.value)
        cfg.put("tradingview_feed", useTradingView.value)
        cfg.put("oanda_pricing", useOanda.value)
        cfg.put("loss_filter", lossFilterEnabled.value)
        root.put("configuration", cfg)

        val statsObj = JSONObject()
        val s = stats.value
        statsObj.put("total_signals_24h", s.totalSignals24h)
        statsObj.put("win_count", s.winCount)
        statsObj.put("filtered_losses", s.filteredLosses)
        statsObj.put("accuracy", "${s.accuracyPercent}%")
        root.put("stats", statsObj)

        val sigsArray = JSONArray()
        allSignals.value.take(8).forEach { sig ->
            val obj = JSONObject()
            obj.put("id", sig.id)
            obj.put("channel", sig.channel)
            obj.put("pair", sig.pair)
            obj.put("direction", sig.direction.name)
            obj.put("timeframe", sig.timeframe)
            obj.put("confidence", "${sig.confidence}%")
            obj.put("status", sig.status.name)
            obj.put("strategy", sig.strategy)
            obj.put("entry_time", sig.entryTime)
            obj.put("anticipate_sec", sig.anticipateSec)
            obj.put("is_otc", sig.isOTC)
            sigsArray.put(obj)
        }
        root.put("live_signals", sigsArray)

        return root.toString(2)
    }
}
