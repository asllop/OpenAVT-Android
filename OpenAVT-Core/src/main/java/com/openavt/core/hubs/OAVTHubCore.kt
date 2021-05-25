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
    private var instrument: OAVTInstrument? = null

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
            event.attributes[OAVTAttribute.DELTA_PLAY_TIME] = System.currentTimeMillis() - timestampOfLastEventOnPlayback
        }

        if (!acceptOrRejectEvent(event, tracker)) {
            return null
        }

        // Once we get here, the event has been accepted by the Hub

        timestampOfLastEventOnPlayback = System.currentTimeMillis()

        event.attributes[OAVTAttribute.COUNT_ERRORS] = countErrors
        event.attributes[OAVTAttribute.COUNT_STARTS] = countStarts
        event.attributes[OAVTAttribute.ACCUM_PAUSE_TIME] = accumPauseTime
        event.attributes[OAVTAttribute.ACCUM_BUFFER_TIME] = accumBufferTime
        event.attributes[OAVTAttribute.ACCUM_SEEK_TIME] = accumSeekTime
        // In case the BUFFER_BEGIN happens inside a block, we want the BUFFER_FINISH be flagged as belonging to the same block, even if it happened outside of it
        if (event.action == OAVTAction.BUFFER_FINISH) {
            event.attributes[OAVTAttribute.IN_PAUSE_BLOCK] = lastBufferBeginInPauseBlock
            event.attributes[OAVTAttribute.IN_SEEK_BLOCK] = lastBufferBeginInSeekBlock
        }
        else {
            event.attributes[OAVTAttribute.IN_PAUSE_BLOCK] = tracker.state.isPaused
            event.attributes[OAVTAttribute.IN_SEEK_BLOCK] = tracker.state.isSeeking
        }
        event.attributes[OAVTAttribute.IN_BUFFER_BLOCK] = tracker.state.isBuffering
        event.attributes[OAVTAttribute.IN_PLAYBACK_BLOCK] = tracker.state.didStart && !tracker.state.didFinish

        streamId?.let {
            event.attributes[OAVTAttribute.STREAM_ID] = it
        }

        playbackId?.let {
            event.attributes[OAVTAttribute.PLAYBACK_ID] = it
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
            OAVTAction.MEDIA_REQUEST -> {
                if (!tracker.state.didMediaRequest) {
                    tracker.state.didMediaRequest = true
                }
                else {
                    return false
                }
            }
            OAVTAction.PLAYER_SET -> {
                if (!tracker.state.didPlayerSet) {
                    tracker.state.didPlayerSet = true
                }
                else {
                    return false
                }
            }
            OAVTAction.STREAM_LOAD -> {
                if (!tracker.state.didStreamLoad) {
                    tracker.state.didStreamLoad = true
                    streamId = UUID.randomUUID().toString()
                }
                else {
                    return false
                }
            }
            OAVTAction.START -> {
                if (!tracker.state.didStart) {
                    startPing(tracker)
                    tracker.state.didStart = true
                    countStarts = countStarts + 1
                }
                else {
                    return false
                }
            }
            OAVTAction.PAUSE_BEGIN -> {
                if (tracker.state.didStart && !tracker.state.isPaused) {
                    tracker.state.isPaused = true
                }
                else {
                    return false
                }
            }
            OAVTAction.PAUSE_FINISH -> {
                if (tracker.state.didStart && tracker.state.isPaused) {
                    tracker.state.isPaused = false
                    val timeSincePauseBegin = event.attributes[OAVTAction.PAUSE_BEGIN.timeAttribute]
                    accumPauseTime = accumPauseTime + (timeSincePauseBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.BUFFER_BEGIN -> {
                if (!tracker.state.isBuffering) {
                    tracker.state.isBuffering = true
                    lastBufferBeginInPauseBlock = tracker.state.isPaused
                    lastBufferBeginInSeekBlock = tracker.state.isSeeking
                }
                else {
                    return false
                }
            }
            OAVTAction.BUFFER_FINISH -> {
                if (tracker.state.isBuffering) {
                    tracker.state.isBuffering = false
                    val timeSinceBufferBegin = event.attributes[OAVTAction.BUFFER_BEGIN.timeAttribute]
                    accumBufferTime = accumBufferTime + (timeSinceBufferBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.SEEK_BEGIN -> {
                if (!tracker.state.isSeeking) {
                    tracker.state.isSeeking = true
                }
                else {
                    return false
                }
            }
            OAVTAction.SEEK_FINISH -> {
                if (tracker.state.isSeeking) {
                    tracker.state.isSeeking = false
                    val timeSinceSeekBegin = event.attributes[OAVTAction.SEEK_BEGIN.timeAttribute]
                    accumSeekTime = accumSeekTime + (timeSinceSeekBegin as Long)
                }
                else {
                    return false
                }
            }
            OAVTAction.END, OAVTAction.STOP, OAVTAction.NEXT -> {
                if (tracker.state.didStart && !tracker.state.didFinish) {
                    this.instrument?.stopPing(tracker.trackerId!!)
                    tracker.state.didFinish = true
                }
                else {
                    return false
                }
            }
            OAVTAction.ERROR -> {
                countErrors = countErrors + 1
            }
        }

        return true
    }

    private fun initPlaybackId(event: OAVTEvent) {
        if (event.action == OAVTAction.MEDIA_REQUEST || event.action == OAVTAction.STREAM_LOAD) {
            if (playbackId == null) {
                playbackId = UUID.randomUUID().toString()
            }
        }
    }

    private fun updatePlaybackId(event: OAVTEvent) {
        if (event.action == OAVTAction.END || event.action == OAVTAction.STOP || event.action == OAVTAction.NEXT) {
            playbackId = UUID.randomUUID().toString()
        }
    }
}