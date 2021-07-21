package com.openavt

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.openavt.core.OAVTInstrument
import com.openavt.core.buffers.OAVTBuffer
import com.openavt.core.buffers.OAVTReservoirBuffer
import com.openavt.core.hubs.OAVTHubCore
import com.openavt.core.hubs.OAVTHubCoreAds
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.metrics.OAVTMetricalcCore
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.models.OAVTSample
import com.openavt.core.utils.OAVTLog
import com.openavt.exoplayer.trackers.OAVTTrackerExoPlayer
import com.openavt.graphite.backends.OAVTBackendGraphite
import com.openavt.ima.trackers.OAVTTrackerIMA
import com.openavt.influxdb.backends.OAVTBackendInfluxdb
import java.net.URL

class AnyBackend : OAVTBackendInterface {
    private val buffer = OAVTReservoirBuffer(5)

    override fun sendEvent(event: OAVTEvent) {
        OAVTLog.verbose(  "AnyBackend sendEvent = " + event)
        if (buffer.put(event)) {
            OAVTLog.verbose(  "Sample inserted into buffer")
        }
        else {
            OAVTLog.verbose(  "Sample NOT inserted into buffer")
        }
        OAVTLog.verbose(  "AnyBackend buffer remaining = " + buffer.remaining())
    }

    override fun sendMetric(metric: OAVTMetric) {
        OAVTLog.verbose(  "AnyBackend sendMetric = " + metric)
        if (buffer.put(metric)) {
            OAVTLog.verbose(  "Sample inserted into buffer")
        }
        else {
            OAVTLog.verbose(  "Sample NOT inserted into buffer")
        }
        OAVTLog.verbose(  "AnyBackend buffer remaining = " + buffer.remaining())
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        OAVTLog.verbose(  "AnyBackend instrumentReady")
    }

    override fun endOfService() {
        OAVTLog.verbose(  "AnyBackend endOfService")
    }
}

class MyGraphiteBackend(
    buffer: OAVTBuffer = OAVTReservoirBuffer(500),
    time: Long = 30,
    host: String,
    port: Int = 2003
) : OAVTBackendGraphite(buffer, time, host, port) {
    override fun sendEvent(event: OAVTEvent) {
        OAVTLog.verbose(  "MyGraphiteBackend sendEvent = " + event)
    }
}

class MyTracker : OAVTTrackerExoPlayer() {
    private var source : String? = null

    fun setSource(source: String) {
        this.source = source
    }

    override fun getSource(): String? {
        return this.source
    }
}

class MainActivity : AppCompatActivity() {
    var player: SimpleExoPlayer? = null
    var adsLoader: ImaAdsLoader? = null

    lateinit var instrument : OAVTInstrument
    var trackerId : Int = -1
    var adTrackerId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OAVTLog.setLogLevel(OAVTLog.LogLevel.Verbose)

        OAVTLog.verbose("----------- START HERE -----------")

        //playVideo("https://demos.transloadit.com/dashtest/my_playlist.mpd")
        //playVideo("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
        playVideoWithAds("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd")
    }

    private fun playVideo(videoUrl: String) {
        instrument = OAVTInstrument(hub = OAVTHubCore(), backend = AnyBackend())
        trackerId = instrument.addTracker(MyTracker())
        instrument.ready()

        player = SimpleExoPlayer.Builder(this).build()

        // Set player into tracker
        (instrument.getTracker(trackerId) as OAVTTrackerExoPlayer).setPlayer(player!!)

        // Set video source
        (instrument.getTracker(trackerId) as? MyTracker)?.setSource(videoUrl)

        val playerView = findViewById<PlayerView>(R.id.player)
        playerView.player = player
        player!!.setMediaItem(MediaItem.fromUri(videoUrl))

        // Prepare the player.
        player!!.playWhenReady = true
        player!!.prepare()
    }

    private fun playVideoWithAds(videoUrl: String) {
        val hub = OAVTHubCoreAds()
        val metricalc = OAVTMetricalcCore()
        //val backend = OAVTBackendInfluxdb(time = 5, url = URL("http://192.168.99.100:8086/write?db=test"))
        val backend = MyGraphiteBackend(time = 5, host = "192.168.99.100")
        instrument = OAVTInstrument(hub = hub, metricalc = metricalc, backend = backend)
        trackerId = instrument.addTracker(OAVTTrackerExoPlayer())
        adTrackerId = instrument.addTracker(OAVTTrackerIMA())
        instrument.ready()

        val builder = ImaAdsLoader.Builder(this)
        builder.setAdErrorListener(instrument.getTracker(adTrackerId) as OAVTTrackerIMA)
        builder.setAdEventListener(instrument.getTracker(adTrackerId) as OAVTTrackerIMA)
        adsLoader = builder.build()

        val playerView = findViewById<PlayerView>(R.id.player)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        mediaSourceFactory.setAdsLoaderProvider { adsLoader }
        mediaSourceFactory.setAdViewProvider(playerView)

        player = SimpleExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()

        // Set player into tracker
        (instrument.getTracker(trackerId) as OAVTTrackerExoPlayer).setPlayer(player!!)

        playerView.player = player
        adsLoader!!.setPlayer(player)

        val contentUri = Uri.parse(videoUrl)
        val adTagUri = Uri.parse("http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=xml_vmap1&unviewed_position_start=1&cust_params=sample_ar%3Dpremidpostpod%26deployment%3Dgmf-js&cmsid=496&vid=short_onecue&correlator=")
        val mediaItem = MediaItem.Builder().setUri(contentUri).setAdTagUri(adTagUri).build()

        player!!.setMediaItem(mediaItem)
        player!!.playWhenReady = true
        player!!.prepare()
    }
}