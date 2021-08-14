package com.openavt.core

import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTAssert.Companion.assertEquals
import org.junit.Test

import org.junit.Assert.*

class CoreUnitTest {
    /**
     * Check tracker Id of a new tracker.
     */
    @Test
    fun trackedId_integrity() {
        val (instrument, trackerId) = createInstrument()
        assertEquals(trackerId, instrument.getTracker(trackerId)!!.trackerId)
    }

    /**
     * Test tracker state with correct events.
     */
    @Test
    fun player_state() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val compareState = OAVTState()

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
    }

    /**
     * Test a correct event workflow.
     */
    @Test
    fun event_workflow() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.MediaRequest)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PlayerSet)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.StreamLoad)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.Start, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.Start)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PauseBegin)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.SeekBegin)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.Error, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.Error)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.SeekFinish)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PauseFinish)

        instrument.emit(OAVTAction.End, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.End)
    }

    /**
     * Test tracker states with wrong events.
     */
    @Test
    fun player_state_mistakes() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val compareState = OAVTState()

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        instrument.emit(OAVTAction.MediaRequest, trackerId) // Repeated event
        compareState.didMediaRequest = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        instrument.emit(OAVTAction.BufferBegin, trackerId) // Repeated event
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

        // Missing SeekFinish
        instrument.emit(OAVTAction.Ping, trackerId)
        check_states(tracker, compareState)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        compareState.isPaused = false
        check_states(tracker, compareState)

        // Missing End
        instrument.emit(OAVTAction.Ping, trackerId)
        check_states(tracker, compareState)
    }

    /**
     * Test a wrong event workflow.
     */
    @Test
    fun event_workflow_mistakes() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.Stop, trackerId) // Event at wrong position
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.MediaRequest)

        instrument.emit(OAVTAction.MediaRequest, trackerId) // Repeated event
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PlayerSet)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.StreamLoad)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.Start, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.Start)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PauseBegin)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.SeekBegin)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.SeekFinish)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PauseFinish)

        instrument.emit(OAVTAction.Ping, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.Ping)

        instrument.emit(OAVTAction.SeekFinish, trackerId) // Finish block without begin
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.End, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.End)

        instrument.emit(OAVTAction.End, trackerId) // End after an End
        assertNull(backend.getLastEvent())

        instrument.emit(OAVTAction.PauseBegin, trackerId) // Block after an End
        assertNull(backend.getLastEvent())
    }

    @Test
    fun time_since_attributes() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.MediaRequest, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.MediaRequest)

        Thread.sleep(300)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PlayerSet)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.StreamLoad)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        Thread.sleep(800)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        val bufferEvent1 = backend.getLastEvent()!!
        assertEquals(bufferEvent1.action, OAVTAction.BufferFinish)
        assertEquals(bufferEvent1.attributes[OAVTAction.BufferBegin.timeAttribute] as Long, 800, 50)

        instrument.emit(OAVTAction.Start, trackerId)
        val startEvent1 = backend.getLastEvent()!!
        assertEquals(startEvent1.action, OAVTAction.Start)
        assertEquals(startEvent1.attributes[OAVTAction.MediaRequest.timeAttribute] as Long, 1100, 50)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.PauseBegin)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.SeekBegin)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferBegin)

        Thread.sleep(600)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.BufferFinish)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        val seekEvent1 = backend.getLastEvent()!!
        assertEquals(seekEvent1.action, OAVTAction.SeekFinish)
        assertEquals(seekEvent1.attributes[OAVTAction.SeekBegin.timeAttribute] as Long, 600, 50)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        val pauseEvent1 = backend.getLastEvent()!!
        assertEquals(pauseEvent1.action, OAVTAction.PauseFinish)
        assertEquals(pauseEvent1.attributes[OAVTAction.PauseBegin.timeAttribute] as Long, 600, 50)

        instrument.emit(OAVTAction.End, trackerId)
        assertEquals(backend.getLastEvent()!!.action, OAVTAction.End)
    }

    @Test
    fun counters() {
        val (instrument, trackerId) = createInstrument()
        val tracker = instrument.getTracker(trackerId)!!
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        val sloadEvent1 = backend.getLastEvent()!!
        assertEquals(sloadEvent1.attributes[OAVTAttribute.countStarts] as Int, 0)
        assertEquals(sloadEvent1.attributes[OAVTAttribute.countErrors] as Int, 0)

        instrument.emit(OAVTAction.Start, trackerId)
        val startEvent1 = backend.getLastEvent()!!
        assertEquals(startEvent1.attributes[OAVTAttribute.countStarts] as Int, 1)
        assertEquals(startEvent1.attributes[OAVTAttribute.countErrors] as Int, 0)

        instrument.emit(OAVTAction.Error, trackerId)
        val errorEvent1 = backend.getLastEvent()!!
        assertEquals(errorEvent1.attributes[OAVTAttribute.countStarts] as Int, 1)
        assertEquals(errorEvent1.attributes[OAVTAttribute.countErrors] as Int, 1)

        instrument.emit(OAVTAction.Ping, trackerId)

        instrument.emit(OAVTAction.Error, trackerId)
        val errorEvent2 = backend.getLastEvent()!!
        assertEquals(errorEvent2.attributes[OAVTAttribute.countStarts] as Int, 1)
        assertEquals(errorEvent2.attributes[OAVTAttribute.countErrors] as Int, 2)

        instrument.emit(OAVTAction.End, trackerId)

        instrument.emit(OAVTAction.Start, trackerId)
        val startEvent2 = backend.getLastEvent()!!
        assertEquals(startEvent2.attributes[OAVTAttribute.countStarts] as Int, 2)
        assertEquals(startEvent2.attributes[OAVTAttribute.countErrors] as Int, 2)

        instrument.emit(OAVTAction.Error, trackerId)
        val errorEvent3 = backend.getLastEvent()!!
        assertEquals(errorEvent3.attributes[OAVTAttribute.countStarts] as Int, 2)
        assertEquals(errorEvent3.attributes[OAVTAttribute.countErrors] as Int, 3)
    }

    //TODO: test accumulated times
    //TODO: test in block attributes
    //TODO: multiple consecutive playbacks

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

    private fun createInstrument(): Pair<OAVTInstrument, Int> {
        val instrument = OAVTInstrument(OAVTHubCore(), DummyBackend())
        val trackerId = instrument.addTracker(DummyTracker())
        return Pair(instrument, trackerId)
    }
}