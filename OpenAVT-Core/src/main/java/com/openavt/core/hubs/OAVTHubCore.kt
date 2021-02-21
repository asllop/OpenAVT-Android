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
class OAVTHubCore : OAVTHubInterface {
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
        if (tracker.getState().didFinish) {
            tracker.getState().didStart = false
            tracker.getState().isPaused = false
            tracker.getState().isBuffering = false
            tracker.getState().isSeeking = false
            tracker.getState().didFinish = false
        }

        initPlaybackId(event)

        if (tracker.getState().didStart &&!tracker.getState().isPaused && !tracker.getState().isSeeking && !tracker.getState().isBuffering) {
            event.attributes[OAVTAttribute.DELTA_PLAY_TIME] = System.currentTimeMillis() - timestampOfLastEventOnPlayback
        }

        if (event.action == OAVTAction.MEDIA_REQUEST) {
            if (!tracker.getState().didMediaRequest) {
                tracker.getState().didMediaRequest = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.PLAYER_SET) {
            if (!tracker.getState().didPlayerSet) {
                tracker.getState().didPlayerSet = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.STREAM_LOAD) {
            if (!tracker.getState().didStreamLoad) {
                tracker.getState().didStreamLoad = true
                streamId = UUID.randomUUID().toString()
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.START) {
            if (!tracker.getState().didStart) {
                this.instrument?.startPing(tracker.trackerId!!, 30)
                tracker.getState().didStart = true
                countStarts = countStarts + 1
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.PAUSE_BEGIN) {
            if (tracker.getState().didStart && !tracker.getState().isPaused) {
                tracker.getState().isPaused = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.PAUSE_FINISH) {
            if (tracker.getState().didStart && tracker.getState().isPaused) {
                tracker.getState().isPaused = false
                val timeSincePauseBegin = event.attributes[OAVTAction.PAUSE_BEGIN.timeAttribute]
                accumPauseTime = accumPauseTime + (timeSincePauseBegin as Long)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.BUFFER_BEGIN) {
            if (!tracker.getState().isBuffering) {
                tracker.getState().isBuffering = true
                lastBufferBeginInPauseBlock = tracker.getState().isPaused
                lastBufferBeginInSeekBlock = tracker.getState().isSeeking
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.BUFFER_FINISH) {
            if (tracker.getState().isBuffering) {
                tracker.getState().isBuffering = false
                val timeSinceBufferBegin = event.attributes[OAVTAction.BUFFER_BEGIN.timeAttribute]
                accumBufferTime = accumBufferTime + (timeSinceBufferBegin as Long)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.SEEK_BEGIN) {
            if (!tracker.getState().isSeeking) {
                tracker.getState().isSeeking = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.SEEK_FINISH) {
            if (tracker.getState().isSeeking) {
                tracker.getState().isSeeking = false
                val timeSinceSeekBegin = event.attributes[OAVTAction.SEEK_BEGIN.timeAttribute]
                accumSeekTime = accumSeekTime + (timeSinceSeekBegin as Long)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.END || event.action == OAVTAction.STOP || event.action == OAVTAction.NEXT) {
            if (tracker.getState().didStart && !tracker.getState().didFinish) {
                this.instrument?.stopPing(tracker.trackerId!!)
                tracker.getState().didFinish = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.ERROR) {
            countErrors = countErrors + 1
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
            event.attributes[OAVTAttribute.IN_PAUSE_BLOCK] = tracker.getState().isPaused
            event.attributes[OAVTAttribute.IN_SEEK_BLOCK] = tracker.getState().isSeeking
        }
        event.attributes[OAVTAttribute.IN_BUFFER_BLOCK] = tracker.getState().isBuffering
        event.attributes[OAVTAttribute.IN_PLAYBACK_BLOCK] = tracker.getState().didStart && !tracker.getState().didFinish

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