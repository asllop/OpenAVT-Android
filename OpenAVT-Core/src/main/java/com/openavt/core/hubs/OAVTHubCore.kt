package com.openavt.core.hubs

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import java.util.*

/**
 * OAVT hub for generic content players.
 */
open class OAVTHubCore : OAVTHubInterface {
    private var countErrors = 0
    private var countStarts = 0
    private var accumPauseTime = 0L
    private var accumSeekTime = 0L
    private var accumBufferTime = 0L
    private var lastBufferBeginInPauseBlock = false
    private var lastBufferBeginInSeekBlock = false
    private var streamId : String? = null
    private var playbackId : String? = null
    private var timestampOfLastEventOnPlayback : Long = 0
    protected var instrument: OAVTInstrument? = null

    override fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent? {
        if (tracker.state.didFinish) {
            tracker.state.didStart = false
            tracker.state.isPaused = false
            tracker.state.isBuffering = false
            tracker.state.isSeeking = false
            tracker.state.didFinish = false
        }

        initPlaybackId(event)

        if (tracker.state.didStart &&!tracker.state.isPaused && !tracker.state.isSeeking && !tracker.state.isBuffering) {
            event.attributes[OAVTAttribute.deltaPlayTime] = System.currentTimeMillis() - timestampOfLastEventOnPlayback
        }

        if (!acceptOrRejectEvent(event, tracker)) {
            return null
        }

        // Once we get here, the event has been accepted by the Hub

        timestampOfLastEventOnPlayback = System.currentTimeMillis()

        event.attributes[OAVTAttribute.countErrors] = countErrors
        event.attributes[OAVTAttribute.countStarts] = countStarts
        event.attributes[OAVTAttribute.accumPauseTime] = accumPauseTime
        event.attributes[OAVTAttribute.accumBufferTime] = accumBufferTime
        event.attributes[OAVTAttribute.accumSeekTime] = accumSeekTime
        // In case the BUFFER_BEGIN happens inside a block, we want the BUFFER_FINISH be flagged as belonging to the same block, even if it happened outside of it
        if (event.action == OAVTAction.BufferFinish) {
            event.attributes[OAVTAttribute.inPauseBlock] = lastBufferBeginInPauseBlock
            event.attributes[OAVTAttribute.inSeekBlock] = lastBufferBeginInSeekBlock
        }
        else {
            event.attributes[OAVTAttribute.inPauseBlock] = tracker.state.isPaused
            event.attributes[OAVTAttribute.inSeekBlock] = tracker.state.isSeeking
        }
        event.attributes[OAVTAttribute.inBufferBlock] = tracker.state.isBuffering
        event.attributes[OAVTAttribute.inPlaybackBlock] = tracker.state.didStart && !tracker.state.didFinish

        streamId?.let {
            event.attributes[OAVTAttribute.streamId] = it
        }

        playbackId?.let {
            event.attributes[OAVTAttribute.playbackId] = it
        }

        updatePlaybackId(event)

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        this.instrument = instrument
    }

    override fun endOfService() {}

    /**
     * Setup ping timer.
     *
     * @param tracker Tracker instance.
     */
    open fun startPing(tracker: OAVTTrackerInterface) {
        this.instrument?.startPing(tracker.trackerId!!, 30)
    }

    /**
     * Process event, accepting or rejecting, and mutate states if necessary.
     *
     * @param event Event object.
     * @param tracker Tracker instance.
     * @return True if accept, false if reject
     */
    open fun acceptOrRejectEvent(event: OAVTEvent, tracker: OAVTTrackerInterface) : Boolean {
        when (event.action) {
            OAVTAction.MediaRequest -> {
                if (!tracker.state.didMediaRequest) {
                    tracker.state.didMediaRequest = true
                }
                else {
                    return false
                }
            }
            OAVTAction.PlayerSet -> {
                if (!tracker.state.didPlayerSet) {
                    tracker.state.didPlayerSet = true
                }
                else {
                    return false
                }
            }
            OAVTAction.StreamLoad -> {
                if (!tracker.state.didStreamLoad) {
                    tracker.state.didStreamLoad = true
                    streamId = UUID.randomUUID().toString()
                }
                else {
                    return false
                }
            }
            OAVTAction.Start -> {
                if (!tracker.state.didStart) {
                    startPing(tracker)
                    tracker.state.didStart = true
                    countStarts = countStarts + 1
                }
                else {
                    return false
                }
            }
            OAVTAction.PauseBegin -> {
                if (tracker.state.didStart && !tracker.state.isPaused) {
                    tracker.state.isPaused = true
                }
                else {
                    return false
                }
            }
            OAVTAction.PauseFinish -> {
                if (tracker.state.didStart && tracker.state.isPaused) {
                    tracker.state.isPaused = false
                    val timeSincePauseBegin = event.attributes[OAVTAction.PauseBegin.timeAttribute]
                    accumPauseTime = accumPauseTime + (timeSincePauseBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.BufferBegin -> {
                if (!tracker.state.isBuffering) {
                    tracker.state.isBuffering = true
                    lastBufferBeginInPauseBlock = tracker.state.isPaused
                    lastBufferBeginInSeekBlock = tracker.state.isSeeking
                }
                else {
                    return false
                }
            }
            OAVTAction.BufferFinish -> {
                if (tracker.state.isBuffering) {
                    tracker.state.isBuffering = false
                    val timeSinceBufferBegin = event.attributes[OAVTAction.BufferBegin.timeAttribute]
                    accumBufferTime = accumBufferTime + (timeSinceBufferBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.SeekBegin -> {
                if (!tracker.state.isSeeking) {
                    tracker.state.isSeeking = true
                }
                else {
                    return false
                }
            }
            OAVTAction.SeekFinish -> {
                if (tracker.state.isSeeking) {
                    tracker.state.isSeeking = false
                    val timeSinceSeekBegin = event.attributes[OAVTAction.SeekBegin.timeAttribute]
                    accumSeekTime = accumSeekTime + (timeSinceSeekBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.End, OAVTAction.Stop, OAVTAction.Next -> {
                if (tracker.state.didStart && !tracker.state.didFinish) {
                    this.instrument?.stopPing(tracker.trackerId!!)
                    tracker.state.didFinish = true
                }
                else {
                    return false
                }
            }
            OAVTAction.Error -> {
                countErrors = countErrors + 1
            }
        }

        return true
    }

    private fun initPlaybackId(event: OAVTEvent) {
        if (event.action == OAVTAction.MediaRequest || event.action == OAVTAction.StreamLoad) {
            if (playbackId == null) {
                playbackId = UUID.randomUUID().toString()
            }
        }
    }

    private fun updatePlaybackId(event: OAVTEvent) {
        if (event.action == OAVTAction.End || event.action == OAVTAction.Stop || event.action == OAVTAction.Next) {
            playbackId = UUID.randomUUID().toString()
        }
    }
}