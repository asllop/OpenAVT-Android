package com.openavt.exoplayer.trackers

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

    // Attributes

    fun getTrackerTarget() : String {
        return "ExoPlayer"
    }

    fun getPosition() : Long? {
        return player?.contentPosition
    }

    fun getDuration() : Long? {
        return player?.duration
    }

    fun getResolutionHeight() : Int? {
        return player?.videoFormat?.height
    }

    fun getResolutionWidth() : Int? {
        return player?.videoFormat?.width
    }

    fun getVolume() : Float? {
        return player?.volume
    }

    fun getIsMuted() : Boolean? {
        return if (getVolume() != null) getVolume() == 0f else null
    }

    fun getFps() : Float? {
        return player?.videoFormat?.frameRate
    }

    fun getSource() : String? {
        return null
    }

    //TODO: estimated bitrate
    fun getBitrate() : Int? {
        return null
    }

    fun getLanguage() : String? {
        return null
    }

    fun getSubtitles() : String? {
        return null
    }

    fun getIsAdsTracker() : Boolean {
        return false
    }

    //TODO: errors
    //TODO: seeking
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
}