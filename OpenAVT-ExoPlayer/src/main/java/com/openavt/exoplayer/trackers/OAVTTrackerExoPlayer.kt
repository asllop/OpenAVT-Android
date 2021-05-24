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

class OAVTTrackerExoPlayer() : OAVTTrackerInterface, Player.EventListener, AnalyticsListener {
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
    fun setPlayer(player: SimpleExoPlayer) {
        if (this.player != null) {
            unregisterListeners()
        }
        this.player = player
        this.instrument!!.emit(OAVTAction.PLAYER_SET, this)
        registerListeners()
    }

    /**
     * Unegister player listeners.
     */
    fun unregisterListeners() {
        player?.removeListener(this)
        player?.removeAnalyticsListener(this)
        player = null
    }

    /**
     * Register player listeners.
     */
    fun registerListeners() {
        player?.addListener(this)
        player?.addAnalyticsListener(this)
        this.instrument?.emit(OAVTAction.PLAYER_READY, this)
    }

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        if (event.action == OAVTAction.ERROR) {
            lastError?.let {
                val type : String?
                when (it.type) {
                    ExoPlaybackException.TYPE_REMOTE -> type = "remote"
                    ExoPlaybackException.TYPE_RENDERER -> type = "renderer"
                    ExoPlaybackException.TYPE_SOURCE -> type = "source"
                    ExoPlaybackException.TYPE_UNEXPECTED -> type = "unexpected"
                    else -> type = null
                }

                event.attributes[OAVTAttribute.ERROR_DESCRIPTION] = it.message ?: ""
                event.attributes[OAVTAttribute.ERROR_TYPE] = type ?: ""
                lastError = null
            }
        }

        // Set attributes from getters
        instrument?.useGetter(OAVTAttribute.TRACKER_TARGET, event, this)
        instrument?.useGetter(OAVTAttribute.POSITION, event, this)
        instrument?.useGetter(OAVTAttribute.DURATION, event, this)
        instrument?.useGetter(OAVTAttribute.RESOLUTION_HEIGHT, event, this)
        instrument?.useGetter(OAVTAttribute.RESOLUTION_WIDTH, event, this)
        instrument?.useGetter(OAVTAttribute.IS_MUTED, event, this)
        instrument?.useGetter(OAVTAttribute.VOLUME, event, this)
        instrument?.useGetter(OAVTAttribute.FPS, event, this)
        instrument?.useGetter(OAVTAttribute.SOURCE, event, this)
        instrument?.useGetter(OAVTAttribute.BITRATE, event, this)
        instrument?.useGetter(OAVTAttribute.LANGUAGE, event, this)
        instrument?.useGetter(OAVTAttribute.SUBTITLES, event, this)
        instrument?.useGetter(OAVTAttribute.IS_ADS_TRACKER, event, this)

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            registerGetters()
            this.instrument?.emit(OAVTAction.TRACKER_INIT, this)
        }
    }

    override fun endOfService() {
        unregisterListeners()
    }

    /**
     * Register attributes getters.
     */
    fun registerGetters() {
        this.instrument?.registerGetter(OAVTAttribute.TRACKER_TARGET, ::getTrackerTarget, this)
        this.instrument?.registerGetter(OAVTAttribute.POSITION, ::getPosition, this)
        this.instrument?.registerGetter(OAVTAttribute.DURATION, ::getDuration, this)
        this.instrument?.registerGetter(OAVTAttribute.RESOLUTION_HEIGHT, ::getResolutionHeight, this)
        this.instrument?.registerGetter(OAVTAttribute.RESOLUTION_WIDTH, ::getResolutionWidth, this)
        this.instrument?.registerGetter(OAVTAttribute.IS_MUTED, ::getIsMuted, this)
        this.instrument?.registerGetter(OAVTAttribute.VOLUME, ::getVolume, this)
        this.instrument?.registerGetter(OAVTAttribute.FPS, ::getFps, this)
        this.instrument?.registerGetter(OAVTAttribute.SOURCE, ::getSource, this)
        this.instrument?.registerGetter(OAVTAttribute.BITRATE, ::getBitrate, this)
        this.instrument?.registerGetter(OAVTAttribute.LANGUAGE, ::getLanguage, this)
        this.instrument?.registerGetter(OAVTAttribute.SUBTITLES, ::getSubtitles, this)
        this.instrument?.registerGetter(OAVTAttribute.IS_ADS_TRACKER, ::getIsAdsTracker, this)
    }

    // Attribute getters

    /**
     * Get tracker target attribute.
     *
     * @return attribute.
     */
    fun getTrackerTarget() : String {
        return "ExoPlayer"
    }

    /**
     * Get position attribute.
     *
     * @return attribute.
     */
    fun getPosition() : Long? {
        return player?.contentPosition
    }

    /**
     * Get duration attribute.
     *
     * @return attribute.
     */
    fun getDuration() : Long? {
        return player?.duration
    }

    /**
     * Get resolution height attribute.
     *
     * @return attribute.
     */
    fun getResolutionHeight() : Int? {
        return player?.videoFormat?.height
    }

    /**
     * Get resolution width attribute.
     *
     * @return attribute.
     */
    fun getResolutionWidth() : Int? {
        return player?.videoFormat?.width
    }

    /**
     * Get volume attribute.
     *
     * @return attribute.
     */
    fun getVolume() : Float? {
        return player?.volume
    }

    /**
     * Get is muted attribute.
     *
     * @return attribute.
     */
    fun getIsMuted() : Boolean? {
        return if (getVolume() != null) getVolume() == 0f else null
    }

    /**
     * Get fps attribute.
     *
     * @return attribute.
     */
    fun getFps() : Float? {
        return player?.videoFormat?.frameRate
    }

    /**
     * Get source attribute.
     *
     * @return attribute.
     */
    fun getSource() : String? {
        return null
    }

    /**
     * Get bitrate attribute.
     *
     * @return attribute.
     */
    fun getBitrate() : Long? {
        return bitrateEstimate
    }

    /**
     * Get language attribute.
     *
     * @return attribute.
     */
    fun getLanguage() : String? {
        return null
    }

    /**
     * Get subtitles attribute.
     *
     * @return attribute.
     */
    fun getSubtitles() : String? {
        return null
    }

    /**
     * Get is ads tracker attribute.
     *
     * @return attribute.
     */
    fun getIsAdsTracker() : Boolean {
        return false
    }

    //TODO: check resolution change

    // ExoPlayer event listener methods

    override fun onPlaybackStateChanged(state: Int) {
        OAVTLog.verbose("onPlaybackStateChanged, payback state = $state")

        when (state) {
            Player.STATE_BUFFERING -> {
                OAVTLog.verbose("STATE: STATE_BUFFERING")
                instrument?.emit(OAVTAction.STREAM_LOAD, this)
                instrument?.emit(OAVTAction.BUFFER_BEGIN, this)

            }
            Player.STATE_READY -> {
                OAVTLog.verbose("STATE: STATE_READY")
                instrument?.emit(OAVTAction.BUFFER_FINISH, this)
                if (userRequested) {
                    instrument?.emit(OAVTAction.START, this)
                }
                if (this.state.isSeeking) {
                    instrument?.emit(OAVTAction.SEEK_FINISH, this)
                }
            }
            Player.STATE_ENDED -> {
                OAVTLog.verbose("STATE: STATE_ENDED")
                instrument?.emit(OAVTAction.END, this)
                userRequested = false
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
                instrument?.emit(OAVTAction.START, this)
            }
            instrument?.emit(OAVTAction.PAUSE_FINISH, this)
        }
        else {
            instrument?.emit(OAVTAction.PAUSE_BEGIN, this)
        }
    }

    override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {
        super.onSeekStarted(eventTime)
        instrument?.emit(OAVTAction.SEEK_BEGIN, this)
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
        instrument?.emit(OAVTAction.ERROR, this)
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

    fun checkResolutionChange() {
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
                            instrument?.emit(OAVTAction.QUALITY_CHANGE_DOWN, this)
                        }
                        else if (lastMul < currMul) {
                            instrument?.emit(OAVTAction.QUALITY_CHANGE_UP, this)
                        }

                lastResolutionHeight = currH
                lastResolutionWidth = currW
            }
        }
    }
}