package com.example.data

enum class SignalDirection {
    UP,
    DOWN,
    WAIT
}

enum class SignalStatus {
    PENDING,
    WIN,
    LOSS
}

data class SignalItem(
    val id: Long,
    val channel: String,
    val pair: String,
    val direction: SignalDirection,
    val timeframe: String,
    val status: SignalStatus,
    val confidence: Int,
    val strategy: String,
    val entryTime: String,
    val anticipateSec: Int,
    val signalTime: String,
    val isOTC: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class SmcSignalRead(
    val broker: String,
    val market: String,
    val pair: String,
    val expiry: String,
    val decision: SignalDirection,
    val agreementPercent: Int,
    val entryTime: String,
    val closeTime: String,
    val rationale: String,
    val thoughts: String? = null,
    val modelUsed: String = "gemini-3.1-flash-lite",
    val timestamp: Long = System.currentTimeMillis()
)

data class ChartAnalysisResult(
    val signal: SignalDirection,
    val confidence: Int,
    val riskLevel: String, // منخفض / متوسط / مرتفع (Low / Medium / High)
    val takeProfits: List<String>,
    val stopLoss: String,
    val liquidityAnalysis: String,
    val riskAssessment: String,
    val momentumAnalysis: String,
    val institutionalOrders: String,
    val summary: String,
    val thoughts: String? = null,
    val mediaType: String = "image"
)

data class BioscienceReport(
    val title: String,
    val assetSymbol: String,
    val catalystType: String,
    val phaseOrStage: String,
    val impactRating: String, // High / Medium / Extreme
    val marketCorrelations: List<String>,
    val orderFlowPrediction: String,
    val reasoningChain: String,
    val thoughts: String? = null
)

data class TradingStats(
    val totalSignals24h: Int = 1042,
    val winCount: Int = 894,
    val filteredLosses: Int = 148,
    val accuracyPercent: Int = 86
)
