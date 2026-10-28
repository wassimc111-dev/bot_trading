package com.example.data

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class TradingRepository(
    private val aiService: AiService = AiService()
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    val pairs = listOf(
        // OTC Pairs (Pocket Option / Quotex favorite markets)
        "USDBDT-OTC", "USDEGP-OTC", "EURNZD-OTC", "EURUSD-OTC", "GBPUSD-OTC",
        "USDJPY-OTC", "AUDUSD-OTC", "USDCAD-OTC", "USDCHF-OTC", "EURGBP-OTC",
        "GBPJPY-OTC", "EURJPY-OTC", "AUDJPY-OTC", "NZDUSD-OTC", "USDZAR-OTC",
        "USDMXN-OTC", "USDINR-OTC", "USDPHP-OTC", "USDTHB-OTC", "USDPKR-OTC",
        // Forex Major & Cross Pairs
        "EUR/USD", "GBP/USD", "USD/JPY", "USD/CHF", "AUD/USD", "USD/CAD", "NZD/USD",
        "EUR/GBP", "EUR/JPY", "GBP/JPY", "EUR/AUD", "EUR/NZD", "AUD/CAD", "AUD/NZD",
        "CAD/JPY", "CHF/JPY", "GBP/AUD", "GBP/CAD", "GBP/NZD", "NZD/JPY",
        // Commodities & Crypto
        "XAU/USD (Gold)", "XAG/USD (Silver)", "BTC/USD", "ETH/USD", "SOL/USD"
    )

    val strategies = listOf(
        "Smart Money Concepts (SMC)", "Liquidity Sweep Grab", "Fair Value Gap (FVG)",
        "Break of Structure (BOS)", "Change of Character (CHOCH)", "Order Flow Footprint",
        "RSI Momentum Divergence", "Bollinger Bands Squeeze", "MACD Multi-Cross",
        "EMA 9/21 Dynamic Trend", "VWAP Institutional Bounce", "Wyckoff Accumulation",
        "Elliott Wave Impulse", "Harmonic Gartley Pattern", "Volume Delta Acceleration",
        "SuperTrend Micro-Breakout", "Stochastic Momentum Surge", "Price Action Hammer Reversal",
        "Support & Resistance Flip", "Heiken Ashi Trend Wave", "Donchian Breakout 5m",
        "Institutional Block Absorption", "High-Volume Pulse"
    )

    val brokers = listOf(
        "Pocket Option", "Quotex", "IQ Option", "Olymp Trade",
        "Binomo", "Exnova", "Bubinga", "Deriv", "Alpari"
    )

    val markets = listOf(
        "OTC", "Forex", "Crypto", "Stocks", "Commodities", "Indices"
    )

    val timeframes = listOf("30s", "1m", "2m", "3m", "5m", "10m", "15m", "30m")

    private val _signals = MutableStateFlow<List<SignalItem>>(emptyList())
    val signals: StateFlow<List<SignalItem>> = _signals.asStateFlow()

    private val _stats = MutableStateFlow(TradingStats())
    val stats: StateFlow<TradingStats> = _stats.asStateFlow()

    private val _isEngineRunning = MutableStateFlow(true)
    val isEngineRunning: StateFlow<Boolean> = _isEngineRunning.asStateFlow()

    private var signalCounter = 100L

    init {
        // Generate initial signals batch
        generateInitialBatch()

        // Background loop to tick anticipation and spawn live signals
        scope.launch {
            while (true) {
                delay(1000L)
                tickAnticipation()
            }
        }

        // Periodic new signal arrival
        scope.launch {
            while (true) {
                delay(7000L)
                if (_isEngineRunning.value) {
                    spawnRandomSignal()
                }
            }
        }
    }

    private fun generateInitialBatch() {
        val initialList = mutableListOf<SignalItem>()
        for (i in 1..14) {
            val tf = timeframes.random()
            initialList.add(createGeneratedSignal(tf))
        }
        _signals.value = initialList
        updateStats()
    }

    private fun createGeneratedSignal(tf: String): SignalItem {
        signalCounter++
        val pair = pairs.random()
        val isOTC = pair.contains("OTC")
        val dir = if (Random.nextBoolean()) SignalDirection.UP else SignalDirection.DOWN
        val channel = "CH" + Random.nextInt(1, 9)
        val strat = strategies.random()
        val conf = Random.nextInt(74, 98)

        // Status based on confidence
        val status = if (conf >= 80) {
            if (Random.nextDouble() > 0.28) SignalStatus.WIN else SignalStatus.PENDING
        } else {
            SignalStatus.LOSS
        }

        val now = System.currentTimeMillis()
        val anticipateSec = Random.nextInt(5, 75)
        val entryTime = now + (anticipateSec * 1000L)

        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val shortTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return SignalItem(
            id = signalCounter,
            channel = "Vixunity $channel",
            pair = pair,
            direction = dir,
            timeframe = tf,
            status = status,
            confidence = conf,
            strategy = strat,
            entryTime = shortTimeFormat.format(Date(entryTime)),
            anticipateSec = anticipateSec,
            signalTime = timeFormat.format(Date(now)),
            isOTC = isOTC,
            timestamp = now
        )
    }

    private fun spawnRandomSignal() {
        val newSig = createGeneratedSignal(timeframes.random())
        val current = _signals.value.toMutableList()
        current.add(0, newSig)
        if (current.size > 50) {
            current.removeAt(current.size - 1)
        }
        _signals.value = current
        updateStats()
    }

    private fun tickAnticipation() {
        val current = _signals.value.map { item ->
            if (item.anticipateSec > 0) {
                item.copy(anticipateSec = item.anticipateSec - 1)
            } else {
                item
            }
        }
        _signals.value = current
    }

    private fun updateStats() {
        val list = _signals.value
        val total = 1000 + list.size
        val wins = list.count { it.status == SignalStatus.WIN }
        val losses = list.count { it.status == SignalStatus.LOSS }
        val accuracy = if (list.isNotEmpty()) {
            Math.round((wins.toFloat() / list.size.toFloat()) * 100)
        } else {
            87
        }
        _stats.value = TradingStats(
            totalSignals24h = total,
            winCount = 850 + wins,
            filteredLosses = 130 + losses,
            accuracyPercent = maxOf(82, minOf(94, accuracy))
        )
    }

    fun toggleEngineRunning() {
        _isEngineRunning.value = !_isEngineRunning.value
    }

    fun forceRefresh() {
        val fresh = mutableListOf<SignalItem>()
        for (i in 1..12) {
            fresh.add(createGeneratedSignal(timeframes.random()))
        }
        _signals.value = fresh
        updateStats()
    }

    // AI Integrations
    suspend fun analyzeChartImage(
        bitmap: Bitmap,
        market: String,
        timeframe: String,
        languageAr: Boolean
    ): ChartAnalysisResult {
        return aiService.analyzeChartImage(bitmap, market, timeframe, languageAr)
    }

    suspend fun analyzeChartVideoFrames(
        frames: List<Bitmap>,
        market: String,
        timeframe: String,
        languageAr: Boolean
    ): ChartAnalysisResult {
        return aiService.analyzeChartVideoFrames(frames, market, timeframe, languageAr)
    }

    suspend fun generateSmcSignal(
        broker: String,
        market: String,
        pair: String,
        expiry: String,
        useHighThinking: Boolean,
        languageAr: Boolean
    ): SmcSignalRead {
        return if (useHighThinking) {
            aiService.generateHighThinkingSmcSignal(broker, market, pair, expiry, languageAr)
        } else {
            aiService.generateLowLatencySmcSignal(broker, market, pair, expiry, languageAr)
        }
    }

    suspend fun generateBioscienceAnalysis(
        symbolOrTopic: String,
        languageAr: Boolean
    ): BioscienceReport {
        return aiService.generateBioscienceAnalysis(symbolOrTopic, languageAr)
    }
}
