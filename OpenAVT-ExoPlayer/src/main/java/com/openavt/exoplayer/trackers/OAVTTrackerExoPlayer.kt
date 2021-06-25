package com.openavt.exoplayer.trackers

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

open class OAVTTrackerExoPlayer() : OAVTTrackerInterface, Player.EventListener, AnalyticsListener {
    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    private var instrument: OAVTInstrument? = null
    private var player: SimpleExoPlayer? = null
    private var userRequested = false
    private var bitrateEstimate: Long? = null
    private var lastError: ExoPlaybackException? = null
    private var lastResolutionHeight: Int = 0
    private var lastResolutionWidth: Int = 0

    /**
     * Init a new OAVTTrackerExoPlayer.
     */
    init {
        OAVTLog.verbose("OAVTTrackerExoPlayer init")
    }

    /**
     * Init a new OAVTTrackerExoPlayer, providing player.
     *
     * @param player SimpleExoPlayer instance.
     */
    constructor(player: SimpleExoPlayer): this() {
        setPlayer(player)
    }

    /**
     * Set player.
     *
     * @param player SimpleExoPlayer instance.
     */
    open fun setPlayer(player: SimpleExoPlayer) {
        if (this.player != null) {
            unregisterListeners()
        }
        this.player = player
        this.instrument!!.emit(OAVTAction.PlayerSet, this)
        registerListeners()
    }

    /**
     * Unegister player listeners.
     */
    open fun unregisterListeners() {
        player?.removeListener(this)
        player?.removeAnalyticsListener(this)
        player = null
    }

    /**
     * Register player listeners.
     */
    open fun registerListeners() {
        player?.addListener(this)
        player?.addAnalyticsListener(this)
        this.instrument?.emit(OAVTAction.PlayerReady, this)
    }

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        if (event.action == OAVTAction.Error) {
            lastError?.let {
                val type : String?
                when (it.type) {
                    ExoPlaybackException.TYPE_REMOTE -> type = "remote"
                    ExoPlaybackException.TYPE_RENDERER -> type = "renderer"
                    ExoPlaybackException.TYPE_SOURCE -> type = "source"
                    ExoPlaybackException.TYPE_UNEXPECTED -> type = "unexpected"
                    else -> type = null
                }

                event.attributes[OAVTAttribute.errorDescription] = it.message ?: ""
                event.attributes[OAVTAttribute.errorType] = type ?: ""
                lastError = null
            }
        }

        // Set attributes from getters
        instrument?.useGetter(OAVTAttribute.trackerTarget, event, this)
        instrument?.useGetter(OAVTAttribute.position, event, this)
        instrument?.useGetter(OAVTAttribute.duration, event, this)
        instrument?.useGetter(OAVTAttribute.resolutionHeight, event, this)
        instrument?.useGetter(OAVTAttribute.resolutionWidth, event, this)
        instrument?.useGetter(OAVTAttribute.isMuted, event, this)
        instrument?.useGetter(OAVTAttribute.volume, event, this)
        instrument?.useGetter(OAVTAttribute.fps, event, this)
        instrument?.useGetter(OAVTAttribute.source, event, this)
        instrument?.useGetter(OAVTAttribute.bitrate, event, this)
        instrument?.useGetter(OAVTAttribute.language, event, this)
        instrument?.useGetter(OAVTAttribute.subtitles, event, this)
        instrument?.useGetter(OAVTAttribute.isAdsTracker, event, this)

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            registerGetters()
            this.instrument?.emit(OAVTAction.TrackerInit, this)
        }
    }

    override fun endOfService() {
        unregisterListeners()
    }

    /**
     * Register attributes getters.
     */
    open fun registerGetters() {
        this.instrument?.registerGetter(OAVTAttribute.trackerTarget, ::getTrackerTarget, this)
        this.instrument?.registerGetter(OAVTAttribute.position, ::getPosition, this)
        this.instrument?.registerGetter(OAVTAttribute.duration, ::getDuration, this)
        this.instrument?.registerGetter(OAVTAttribute.resolutionHeight, ::getResolutionHeight, this)
        this.instrument?.registerGetter(OAVTAttribute.resolutionWidth, ::getResolutionWidth, this)
        this.instrument?.registerGetter(OAVTAttribute.isMuted, ::getIsMuted, this)
        this.instrument?.registerGetter(OAVTAttribute.volume, ::getVolume, this)
        this.instrument?.registerGetter(OAVTAttribute.fps, ::getFps, this)
        this.instrument?.registerGetter(OAVTAttribute.source, ::getSource, this)
        this.instrument?.registerGetter(OAVTAttribute.bitrate, ::getBitrate, this)
        this.instrument?.registerGetter(OAVTAttribute.language, ::getLanguage, this)
        this.instrument?.registerGetter(OAVTAttribute.subtitles, ::getSubtitles, this)
        this.instrument?.registerGetter(OAVTAttribute.isAdsTracker, ::getIsAdsTracker, this)
    }

    // Attribute getters

    /**
     * Get tracker target attribute.
     *
     * @return attribute.
     */
    open fun getTrackerTarget() : String {
        return "ExoPlayer"
    }

    /**
     * Get position attribute.
     *
     * @return attribute.
     */
    open fun getPosition() : Long? {
        return player?.contentPosition
    }

    /**
     * Get duration attribute.
     *
     * @return attribute.
     */
    open fun getDuration() : Long? {
        return player?.duration
    }

    /**
     * Get resolution height attribute.
     *
     * @return attribute.
     */
    open fun getResolutionHeight() : Int? {
        return player?.videoFormat?.height
    }

    /**
     * Get resolution width attribute.
     *
     * @return attribute.
     */
    open fun getResolutionWidth() : Int? {
        return player?.videoFormat?.width
    }

    /**
     * Get volume attribute.
     *
     * @return attribute.
     */
    open fun getVolume() : Float? {
        return player?.volume
    }

    /**
     * Get is muted attribute.
     *
     * @return attribute.
     */
    open fun getIsMuted() : Boolean? {
        return if (getVolume() != null) getVolume() == 0f else null
    }

    /**
     * Get fps attribute.
     *
     * @return attribute.
     */
    open fun getFps() : Float? {
        return player?.videoFormat?.frameRate
    }

    /**
     * Get source attribute.
     *
     * @return attribute.
     */
    open fun getSource() : String? {
        return null
    }

    /**
     * Get bitrate attribute.
     *
     * @return attribute.
     */
    open fun getBitrate() : Long? {
        return bitrateEstimate
    }

    /**
     * Get language attribute.
     *
     * @return attribute.
     */
    open fun getLanguage() : String? {
        return null
    }

    /**
     * Get subtitles attribute.
     *
     * @return attribute.
     */
    open fun getSubtitles() : String? {
        return null
    }

    /**
     * Get is ads tracker attribute.
     *
     * @return attribute.
     */
    open fun getIsAdsTracker() : Boolean {
        return false
    }

    // ExoPlayer event listener methods

    override fun onPlaybackStateChanged(state: Int) {
        OAVTLog.verbose("onPlaybackStateChanged, payback state = $state")

        when (state) {
            Player.STATE_BUFFERING -> {
                OAVTLog.verbose("STATE: STATE_BUFFERING")
                instrument?.emit(OAVTAction.StreamLoad, this)
                instrument?.emit(OAVTAction.BufferBegin, this)

            }
            Player.STATE_READY -> {
                OAVTLog.verbose("STATE: STATE_READY")
                instrument?.emit(OAVTAction.BufferFinish, this)
                if (userRequested) {
                    instrument?.emit(OAVTAction.Start, this)
                }
                if (this.state.isSeeking) {
                    instrument?.emit(OAVTAction.SeekFinish, this)
                }
            }
            Player.STATE_ENDED -> {
                OAVTLog.verbose("STATE: STATE_ENDED")
                instrument?.emit(OAVTAction.End, this)
                userRequested = false
            }
            Player.STATE_IDLE -> {
                OAVTLog.verbose("STATE: STATE_IDLE")
            }
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        OAVTLog.verbose("onPlayWhenReadyChanged playWhenReady = $playWhenReady , reason = $reason")

        when (reason) {
            Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> {
                OAVTLog.verbose("REASON: USER_REQUEST")
                userRequested = true
            }
            Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> OAVTLog.verbose("REASON: REMOTE")
            Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM -> OAVTLog.verbose("REASON: END_OF_MEDIA_ITEM")
            Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> OAVTLog.verbose("REASON: AUDIO_BECOMING_NOISY")
            Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> OAVTLog.verbose("REASON: AUDIO_FOCUS_LOSS")
            else -> OAVTLog.verbose("REASON: OTHER")
        }

        if (playWhenReady) {
            if (this.state.didStreamLoad) {
                instrument?.emit(OAVTAction.Start, this)
            }
            instrument?.emit(OAVTAction.PauseFinish, this)
        }
        else {
            instrument?.emit(OAVTAction.PauseBegin, this)
        }
    }

    override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
        super.onSeekStarted(eventTime)
        instrument?.emit(OAVTAction.SeekBegin, this)
    }

    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
    ) {
        super.onBandwidthEstimate(eventTime, totalLoadTimeMs, totalBytesLoaded, bitrateEstimate)
        this.bitrateEstimate = bitrateEstimate;
    }

    override fun onPlayerError(
        eventTime: AnalyticsListener.EventTime,
        error: ExoPlaybackException
    ) {
        super<AnalyticsListener>.onPlayerError(eventTime, error)
        this.lastError = error;
        instrument?.emit(OAVTAction.Error, this)
    }

    override fun onVideoSizeChanged(
        eventTime: AnalyticsListener.EventTime,
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {
        super.onVideoSizeChanged(
            eventTime,
            width,
            height,
            unappliedRotationDegrees,
            pixelWidthHeightRatio
        )
        OAVTLog.verbose("-----> onVideoSizeChanged")
        checkResolutionChange()
    }

    private fun checkResolutionChange() {
        val currH = getResolutionHeight()
        val currW = getResolutionWidth()
        if (currH != null && currW != null) {
            if (lastResolutionWidth == 0 || lastResolutionHeight == 0) {
                lastResolutionHeight = currH
                lastResolutionWidth = currW
            }
            else {
                val lastMul = lastResolutionHeight * lastResolutionWidth
                        val currMul = currH * currW

                        if (lastMul > currMul) {
                            instrument?.emit(OAVTAction.QualityChangeDown, this)
                        }
                        else if (lastMul < currMul) {
                            instrument?.emit(OAVTAction.QualityChangeUp, this)
                        }

                lastResolutionHeight = currH
                lastResolutionWidth = currW
            }
        }
    }
}