package com.openavt.core

import com.openavt.core.assets.DummyAdsTracker
import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.hubs.OAVTHubCoreAds
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTAssert.Companion.assertStates
import org.junit.Test
import org.junit.Assert.*

class AdsUnitTest {
    /**
     * Check tracker Id of a new tracker.
     */
    @Test
    fun trackedId_integrity() {
        val (instrument, trackerId, adTrackerId) = createInstrument()
        assertEquals(trackerId, instrument.getTracker(trackerId)!!.trackerId)
        assertEquals(adTrackerId, instrument.getTracker(adTrackerId)!!.trackerId)
    }

    /**
     * Test tracker state with correct events.
     */
    @Test
    fun ads_state() {
        val (instrument, trackerId, adTrackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val adTracker = instrument.getTracker(adTrackerId)!!
        val compareState = OAVTState()
        val compareAdState = OAVTState()

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        compareState.didMediaRequest = true

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true

        // Pre-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false

        instrument.emit(OAVTAction.Start, trackerId)
        compareState.didStart = true

        // Mid-roll ad break (2 ads)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.End, trackerId)
        compareState.didFinish = true

        // Post-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)
    }

    /**
     * Test a correct event workflow.
     */
    @Test
    fun ad_event_workflow() {
        val (instrument, trackerId, adTrackerId) = createInstrument()
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.StreamLoad, trackerId)

        instrument.emit(OAVTAction.BufferBegin, trackerId)

        // Pre-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakBegin)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakFinish)

        instrument.emit(OAVTAction.BufferFinish, trackerId)

        instrument.emit(OAVTAction.Start, trackerId)

        // Mid-roll ad break (2 ads)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakBegin)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdPauseBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdPauseBegin)

        instrument.emit(OAVTAction.AdPauseFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdPauseFinish)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakFinish)

        instrument.emit(OAVTAction.End, trackerId)

        // Post-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakBegin)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakFinish)
    }

    /**
     * Test tracker states with wrong events.
     */
    @Test
    fun ad_state_mistakes() {
        val (instrument, trackerId, adTrackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val adTracker = instrument.getTracker(adTrackerId)!!
        val compareState = OAVTState()
        val compareAdState = OAVTState()

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        compareState.didMediaRequest = true

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true

        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        // Pre-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId) // Repeated event
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        instrument.emit(OAVTAction.AdFinish, adTrackerId) // Repeated event
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false

        instrument.emit(OAVTAction.Start, trackerId)
        compareState.didStart = true

        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        // Mid-roll ad break (2 ads)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        compareState.inAd = false
        compareAdState.inAd = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.End, trackerId)
        compareState.didFinish = true

        // Post-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        compareState.inAdBreak = true
        compareAdState.inAdBreak = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        compareState.inAd = true
        compareAdState.inAd = true
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)

        // missing ad end
        // Note: Ad trackers should call AdFinish before AdBreakFinish to avoid keeping inAd state true.

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        compareState.inAdBreak = false
        compareAdState.inAdBreak = false
        assertStates(tracker, compareState)
        assertStates(adTracker, compareAdState)
    }

    /**
     * Test a wrong event workflow.
     */
    @Test
    fun ad_event_workflow_mistake() {
        val (instrument, trackerId, adTrackerId) = createInstrument()
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.StreamLoad, trackerId)

        instrument.emit(OAVTAction.BufferBegin, trackerId)

        // Pre-roll ad break (1 ad)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakBegin)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId) // Event at wrong position
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdFinish, adTrackerId) // Repeated event
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakFinish)

        instrument.emit(OAVTAction.BufferFinish, trackerId)

        instrument.emit(OAVTAction.Start, trackerId)

        // Mid-roll ad break (2 ads)
        instrument.emit(OAVTAction.AdBreakBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakBegin)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBegin)

        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdFinish)

        // Ad Pause block outside of an Ad block
        instrument.emit(OAVTAction.AdPauseBegin, adTrackerId)
        assertNull(backend.getLastEvent())
        instrument.emit(OAVTAction.AdPauseFinish, adTrackerId)
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.AdBreakFinish)

        instrument.emit(OAVTAction.End, trackerId)
        backend.getLastEvent()

        // Post-roll ad break (1 ad)

        // Missing AdBreakBegin
        instrument.emit(OAVTAction.AdBegin, adTrackerId)
        assertNull(backend.getLastEvent())
        instrument.emit(OAVTAction.AdFinish, adTrackerId)
        assertNull(backend.getLastEvent())
        instrument.emit(OAVTAction.AdBreakFinish, adTrackerId)
        assertNull(backend.getLastEvent())
    }

    private fun createInstrument(): Triple<OAVTInstrument, Int, Int> {
        val instrument = OAVTInstrument(OAVTHubCoreAds(), DummyBackend())
        val trackerId = instrument.addTracker(DummyTracker())
        val adTrackerId = instrument.addTracker(DummyAdsTracker())
        instrument.ready()
        return Triple(instrument, trackerId, adTrackerId)
    }
}