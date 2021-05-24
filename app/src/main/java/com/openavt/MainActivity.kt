package com.openavt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.openavt.core.OAVTInstrument
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.utils.OAVTLog
import com.openavt.exoplayer.trackers.OAVTTrackerExoPlayer

class AnyBackend : OAVTBackendInterface {
    override fun sendEvent(event: OAVTEvent) {
        OAVTLog.verbose(  "AnyBackend sendEvent = " + event)
    }

    override fun sendMetric(metric: OAVTMetric) {
        OAVTLog.verbose(  "AnyBackend sendMetric = " + metric)
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        OAVTLog.verbose(  "AnyBackend instrumentReady")
    }

    override fun endOfService() {
        OAVTLog.verbose(  "AnyBackend endOfService")
    }
}
/*
class AnyMetricalc : OAVTMetricalcInterface {
    override fun processMetric(event: OAVTEvent, tracker: OAVTTrackerInterface): Array<OAVTMetric> {
        OAVTLog.verbose(  "AnyMetricalc processMetric")
        return arrayOf(
                OAVTMetric("metricX", OAVTMetric.MetricType.Counter, 10),
                OAVTMetric("metricY", OAVTMetric.MetricType.Gauge, 9.9999),
                OAVTMetric.START_TIME(100)
        )
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        OAVTLog.verbose(  "AnyMetricalc instrumentReady")
    }

    override fun endOfService() {
        OAVTLog.verbose(  "AnyMetricalc endOfService")
    }

}

class AnyTracker : OAVTTrackerInterface {
    private var instrument: OAVTInstrument? = null

    override var state = OAVTState()
    override var trackerId: Int? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        OAVTLog.verbose(  "AnyTracker initEvent from Id = " + trackerId)
        event.attributes[OAVTAttribute.TITLE] = "Space balls"
        event.attributes[OAVTAttribute.VOLUME] = 100
        instrument?.let { it.useGetter(OAVTAttribute.DURATION, event, this) }

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        OAVTLog.verbose(  "AnyTracker instrumentReady from Id = " + trackerId)
        this.instrument = instrument
        instrument.registerGetter(OAVTAttribute.DURATION, ::getAttrDuration, this)
    }

    override fun endOfService() {
        OAVTLog.verbose(  "AnyTracker endOfService from Id = " + trackerId)
    }

    fun getAttrDuration(): Int {
        OAVTLog.verbose(  "AnyTracker getAttrDuration, this = " + this)
        return 1234
    }
}
 */

lateinit var instrument : OAVTInstrument
var trackerId : Int = 0

class MainActivity : AppCompatActivity() {
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OAVTLog.setLogLevel(OAVTLog.LogLevel.Verbose)

        OAVTLog.verbose("----------- START HERE -----------")

        instrument = OAVTInstrument(hub = OAVTHubCore(), backend = AnyBackend())
        trackerId = instrument.addTracker(OAVTTrackerExoPlayer())
        instrument.ready()

        //playVideo("https://demos.transloadit.com/dashtest/my_playlist.mpd")
        playVideo("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
    }

    private fun playVideo(videoUrl: String) {
        player = SimpleExoPlayer.Builder(this).build()

        // Set player into tracker
        (instrument.getTracker(trackerId) as OAVTTrackerExoPlayer).setPlayer(player!!)

        val playerView = findViewById<PlayerView>(R.id.player)
        playerView.player = player
        player!!.setMediaItem(MediaItem.fromUri(videoUrl))

        // Prepare the player.
        player!!.setPlayWhenReady(true)
        player!!.prepare()
    }
}