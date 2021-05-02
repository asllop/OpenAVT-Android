package com.openavt.exoplayer.trackers

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

class OAVTTrackerExoPlayer() : OAVTTrackerInterface, Player.EventListener, AnalyticsListener {
    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    private var instrument: OAVTInstrument? = null
    private var player: SimpleExoPlayer? = null

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
        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            //TODO: registerGetters()
            this.instrument?.emit(OAVTAction.TRACKER_INIT, this)
        }
    }

    override fun endOfService() {
        unregisterListeners()
    }

    // ExoPlayer event listener methods

    override fun onPlaybackStateChanged(state: Int) {
        OAVTLog.verbose("onPlaybackStateChanged, payback state = $state")

        when (state) {
            Player.STATE_BUFFERING -> {
                instrument?.emit(OAVTAction.STREAM_LOAD, this)
                instrument?.emit(OAVTAction.BUFFER_BEGIN, this)
            }
            Player.STATE_READY -> instrument?.emit(OAVTAction.BUFFER_FINISH, this)
            Player.STATE_ENDED -> instrument?.emit(OAVTAction.END, this)
        }
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        OAVTLog.verbose("onPlayWhenReadyChanged playWhenReady = $playWhenReady , reason = $reason")

        if (playWhenReady) {
            instrument?.emit(OAVTAction.START, this)
            instrument?.emit(OAVTAction.PAUSE_FINISH, this)
        }
        else {
            instrument?.emit(OAVTAction.PAUSE_BEGIN, this)
        }
    }
}