package com.example

import com.example.data.SignalDirection
import com.example.data.SignalStatus
import com.example.data.SmcSignalRead
import com.example.data.TradingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun repository_initializesWithSignalsAndStats() {
        val repo = TradingRepository()
        val signals = repo.signals.value
        val stats = repo.stats.value

        assertTrue("Initial signals list should not be empty", signals.isNotEmpty())
        assertTrue("Stats total signals should be positive", stats.totalSignals24h > 1000)
        assertTrue("Accuracy should be between 70 and 100", stats.accuracyPercent in 70..100)
    }

    @Test
    fun smcSignalRead_modelProperties() {
        val read = SmcSignalRead(
            broker = "Pocket Option",
            market = "OTC",
            pair = "EUR/USD",
            expiry = "30m",
            decision = SignalDirection.UP,
            agreementPercent = 89,
            entryTime = "14:30:00",
            closeTime = "15:00:00",
            rationale = "Test rationale",
            modelUsed = "gemini-4-argon (ThinkingLevel.HIGH)"
        )

        assertEquals("Pocket Option", read.broker)
        assertEquals("OTC", read.market)
        assertEquals(SignalDirection.UP, read.decision)
        assertEquals(89, read.agreementPercent)
        assertTrue(read.modelUsed.contains("gemini-4-argon"))
    }

    @Test
    fun signalStatus_enums() {
        assertEquals(3, SignalStatus.values().size)
        assertNotNull(SignalStatus.valueOf("WIN"))
        assertNotNull(SignalStatus.valueOf("LOSS"))
        assertNotNull(SignalStatus.valueOf("PENDING"))
    }
}
