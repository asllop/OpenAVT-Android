package com.openavt.core

import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTState
import org.junit.Test

import org.junit.Assert.*

class CoreUnitTest {
    private var instrument: OAVTInstrument = OAVTInstrument(OAVTHubCore(), DummyBackend())
    private var trackerId: Int = instrument.addTracker(DummyTracker())

    @Test
    fun trackedId_integrity() {
        assertEquals(trackerId, instrument.getTracker(trackerId)!!.trackerId)
    }

    @Test
    fun player_state() {
        val tracker = instrument.getTracker(trackerId)!!
        var compareState = OAVTState()

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        compareState.didMediaRequest = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.Start, trackerId)
        compareState.didStart = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        compareState.isPaused = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        compareState.isSeeking = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        compareState.isSeeking = false
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        compareState.isPaused = false
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.End, trackerId)
        compareState.didFinish = true
        check_states(tracker, compareState)

        // Reset for next test
        tracker.state.reset()
    }

    @Test
    fun event_workflow() {
        val tracker = instrument.getTracker(trackerId)!!
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.MediaRequest)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.PlayerSet)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.StreamLoad)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.Start, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.Start)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.PauseBegin)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.SeekBegin)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.SeekFinish)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.PauseFinish)

        instrument.emit(OAVTAction.End, trackerId)
        assertEquals(backend.latestEvent!!.action, OAVTAction.End)

        // Reset for next test
        tracker.state.reset()
    }

    //TODO: check states and event workflow sending repeated events and in wrong order

    private fun check_states(tracker: OAVTTrackerInterface, compareState: OAVTState) {
        assertEquals(tracker.state.didMediaRequest, compareState.didMediaRequest)
        assertEquals(tracker.state.didPlayerSet, compareState.didPlayerSet)
        assertEquals(tracker.state.didStreamLoad, compareState.didStreamLoad)
        assertEquals(tracker.state.didStart, compareState.didStart)
        assertEquals(tracker.state.isBuffering, compareState.isBuffering)
        assertEquals(tracker.state.isPaused, compareState.isPaused)
        assertEquals(tracker.state.isSeeking, compareState.isSeeking)
        assertEquals(tracker.state.didFinish, compareState.didFinish)
        assertEquals(tracker.state.inAdBreak, compareState.inAdBreak)
        assertEquals(tracker.state.inAd, compareState.inAd)
    }
}