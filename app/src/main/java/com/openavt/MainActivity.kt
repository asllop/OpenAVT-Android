package com.openavt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.openavt.core.*
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.*
import com.openavt.core.utils.OAVTLog
import java.util.*

class AnyHub : OAVTHubInterface {
    override fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent? {
        OAVTLog.verbose(  "AnyHub processEvent")
        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        OAVTLog.verbose(  "AnyHub instrumentReady")
    }

    override fun endOfService() {
        OAVTLog.verbose(  "AnyHub endOfService")
    }

}

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
    private val state = OAVTState()
    private var instrument: OAVTInstrument? = null

    override var trackerId: Int? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        OAVTLog.verbose(  "AnyTracker initEvent from Id = " + trackerId)
        event.attributes[OAVTAttribute.TITLE] = "Space balls"
        event.attributes[OAVTAttribute.VOLUME] = 100
        instrument?.let { it.useGetter(OAVTAttribute.DURATION, event, this) }

        return event
    }

    override fun getState(): OAVTState {
        OAVTLog.verbose(  "AnyTracker getState from Id = " + trackerId)
        return state
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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OAVTLog.setLogLevel(OAVTLog.LogLevel.Verbose)

        val instrument = OAVTInstrument(hub = AnyHub(), metricalc = AnyMetricalc(), backend = AnyBackend())
        val trackerId0 = instrument.addTracker(AnyTracker())
        val trackerId1 = instrument.addTracker(AnyTracker())
        instrument.ready()

        instrument.addAttribute(OAVTAttribute("attrAll"), 1000)
        instrument.addAttribute(OAVTAttribute("attrForAction"), 1000, OAVTAction("TEST_ACTION_ONE"))
        instrument.addAttribute(OAVTAttribute("attrForTracker"), 1000, trackerId = trackerId1)

        instrument.emit(OAVTAction.PING, trackerId0)
        instrument.emit(OAVTAction("TEST_ACTION_ONE"), trackerId0)
        instrument.emit(OAVTAction("TEST_ACTION_TWO", OAVTAttribute("timeSinceTestTwo")), trackerId0)
        instrument.emit(OAVTAction("TEST_ACTION_THREE"), trackerId0)
        OAVTLog.verbose(  "------------------------------------------------------------------------------")
        instrument.emit(OAVTAction("TEST_ACTION_ONE"), trackerId1)
        instrument.emit(OAVTAction("TEST_ACTION_TWO"), trackerId1)

        if (!instrument.removeAttribute(OAVTAttribute("attrForTracker"), trackerId = trackerId1)) {
            OAVTLog.verbose(  "Attribute not removed!")
        }

        instrument.emit(OAVTAction("TEST_ACTION_THREE"), trackerId1)

        var ret: Int = 0
        ret = instrument.callGetter(OAVTAttribute.DURATION, instrument.getTracker(trackerId0)!!) as Int
        OAVTLog.verbose(  "Call getter DURATION on tracker0 = " + ret)
        ret = instrument.callGetter(OAVTAttribute.DURATION, instrument.getTracker(trackerId1)!!) as Int
        OAVTLog.verbose(  "Call getter DURATION on tracker1 = " + ret)

        instrument.startPing(trackerId1, 5)

        Timer().schedule(object: TimerTask() {
            override fun run() {
                instrument.stopPing(trackerId1)

                instrument.shutdown()
            }
        }, 12000)
    }
}