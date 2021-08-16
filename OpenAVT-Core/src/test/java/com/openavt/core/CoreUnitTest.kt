package com.openavt.core

import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.buffers.OAVTBuffer
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.models.*
import com.openavt.core.utils.OAVTAssert.Companion.assertEquals
import com.openavt.core.utils.OAVTAssert.Companion.assertStates
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
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.Start, trackerId)
        compareState.didStart = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        compareState.isPaused = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        compareState.isSeeking = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.SeekFinish, trackerId)
        compareState.isSeeking = false
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        compareState.isPaused = false
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.End, trackerId)
        compareState.didFinish = true
        assertStates(tracker, compareState)
    }

    /**
     * Test a correct event workflow.
     */
    @Test
    fun event_workflow() {
        val (instrument, trackerId) = createInstrument()
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
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PlayerSet, trackerId)
        compareState.didPlayerSet = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        compareState.didStreamLoad = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        instrument.emit(OAVTAction.BufferBegin, trackerId) // Repeated event
        compareState.isBuffering = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.Start, trackerId)
        compareState.didStart = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        compareState.isPaused = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.SeekBegin, trackerId)
        compareState.isSeeking = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        compareState.isBuffering = true
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.BufferFinish, trackerId)
        compareState.isBuffering = false
        assertStates(tracker, compareState)

        // Missing SeekFinish
        instrument.emit(OAVTAction.Ping, trackerId)
        assertStates(tracker, compareState)

        instrument.emit(OAVTAction.PauseFinish, trackerId)
        compareState.isPaused = false
        assertStates(tracker, compareState)

        // Missing End
        instrument.emit(OAVTAction.Ping, trackerId)
        assertStates(tracker, compareState)
    }

    /**
     * Test a wrong event workflow.
     */
    @Test
    fun event_workflow_mistakes() {
        val (instrument, trackerId) = createInstrument()
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

    /**
     * Test Time Since attributes.
     */
    @Test
    fun time_since_attributes() {
        val (instrument, trackerId) = createInstrument()
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

    /**
     * Test counter attributes (countStarts and countErrors).
     */
    @Test
    fun counters() {
        val (instrument, trackerId) = createInstrument()
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

        instrument.emit(OAVTAction.Start, trackerId)
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

    /**
     * Test accumulated time attributes (accumBufferTime, accumPauseTime and accumSeekTime).
     */
    @Test
    fun accumulated_times() {
        val (instrument, trackerId) = createInstrument()
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!

        instrument.emit(OAVTAction.StreamLoad, trackerId)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        Thread.sleep(800)
        instrument.emit(OAVTAction.BufferFinish, trackerId)

        instrument.emit(OAVTAction.Start, trackerId)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        Thread.sleep(1500)
        instrument.emit(OAVTAction.PauseFinish, trackerId)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        instrument.emit(OAVTAction.SeekBegin, trackerId)
        instrument.emit(OAVTAction.BufferBegin, trackerId)
        Thread.sleep(1000)
        instrument.emit(OAVTAction.BufferFinish, trackerId)
        instrument.emit(OAVTAction.SeekFinish, trackerId)
        instrument.emit(OAVTAction.PauseFinish, trackerId)

        instrument.emit(OAVTAction.End, trackerId)
        val endEvent1 = backend.getLastEvent()!!
        assertEquals(endEvent1.attributes[OAVTAttribute.accumBufferTime] as Long, 1800, 50)
        assertEquals(endEvent1.attributes[OAVTAttribute.accumPauseTime] as Long, 2500, 50)
        assertEquals(endEvent1.attributes[OAVTAttribute.accumSeekTime] as Long, 1000, 50)
    }

    /**
     * Test block flag attributes (inPlaybackBlock, inPauseBlock, inBufferBlock and inSeekBlock).
     */
    @Test
    fun in_blocks() {
        val (instrument, trackerId) = createInstrument()
        val backend: DummyBackend = (instrument.getBackend() as DummyBackend?)!!
        var event: OAVTEvent

        instrument.emit(OAVTAction.StreamLoad, trackerId)
        event = backend.getLastEvent()!!
        assertFalse(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)

        instrument.emit(OAVTAction.BufferBegin, trackerId)
        event = backend.getLastEvent()!!
        assertFalse(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertTrue(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)
        instrument.emit(OAVTAction.BufferFinish, trackerId)

        instrument.emit(OAVTAction.Start, trackerId)
        event = backend.getLastEvent()!!
        assertTrue(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        event = backend.getLastEvent()!!
        assertTrue(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertTrue(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)
        instrument.emit(OAVTAction.PauseFinish, trackerId)

        instrument.emit(OAVTAction.PauseBegin, trackerId)
        instrument.emit(OAVTAction.SeekBegin, trackerId)
        instrument.emit(OAVTAction.BufferBegin, trackerId)
        event = backend.getLastEvent()!!
        assertTrue(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertTrue(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertTrue(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertTrue(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)
        instrument.emit(OAVTAction.BufferFinish, trackerId)
        instrument.emit(OAVTAction.SeekFinish, trackerId)
        instrument.emit(OAVTAction.PauseFinish, trackerId)
        event = backend.getLastEvent()!!
        assertTrue(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)

        instrument.emit(OAVTAction.End, trackerId)
        event = backend.getLastEvent()!!
        assertFalse(event.attributes[OAVTAttribute.inPlaybackBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inPauseBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inBufferBlock] as Boolean)
        assertFalse(event.attributes[OAVTAttribute.inSeekBlock] as Boolean)
    }

    /**
     * Test sample buffers.
     */
    @Test
    fun buffers() {
        val simpleBuffer = OAVTBuffer(4)

        assertTrue(simpleBuffer.put(OAVTEvent(OAVTAction.Start)))

        Thread.sleep(50)

        assertTrue(simpleBuffer.put(OAVTEvent(OAVTAction.BufferBegin)))

        Thread.sleep(50)

        assertTrue(simpleBuffer.put(OAVTEvent(OAVTAction.BufferFinish)))

        Thread.sleep(50)

        assertTrue(simpleBuffer.put(OAVTEvent(OAVTAction.End)))

        Thread.sleep(50)

        assertFalse(simpleBuffer.put(OAVTEvent(OAVTAction.Ping)))

        Thread.sleep(50)

        assertTrue(simpleBuffer.set(0, OAVTEvent(OAVTAction.Error)))

        val samples = simpleBuffer.retrieveInOrder()
        assertEquals((samples[0] as OAVTEvent).action, OAVTAction.BufferBegin)
        assertEquals((samples[1] as OAVTEvent).action, OAVTAction.BufferFinish)
        assertEquals((samples[2] as OAVTEvent).action, OAVTAction.End)
        assertEquals((samples[3] as OAVTEvent).action, OAVTAction.Error)

        //NOTE: Can't test reservoir buffer because is not predictable, due to it's random nature.
    }

    private fun createInstrument(): Pair<OAVTInstrument, Int> {
        val instrument = OAVTInstrument(OAVTHubCore(), DummyBackend())
        val trackerId = instrument.addTracker(DummyTracker())
        instrument.ready()
        return Pair(instrument, trackerId)
    }
}