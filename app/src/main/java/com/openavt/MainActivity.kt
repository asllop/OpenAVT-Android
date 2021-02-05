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

class AnyHub : OAVTHubInterface {
    override fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent? {
        Log.d("OAVT",  "AnyHub processEvent")
        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyHub instrumentReady")
    }

    override fun endOfService() {
        Log.d("OAVT",  "AnyHub endOfService")
    }

}

class AnyBackend : OAVTBackendInterface {
    override fun sendEvent(event: OAVTEvent) {
        Log.d("OAVT",  "AnyBackend sendEvent = " + event)
    }

    override fun sendMetric(metric: OAVTMetric) {
        Log.d("OAVT",  "AnyBackend sendMetric = " + metric)
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyBackend instrumentReady")
    }

    override fun endOfService() {
        Log.d("OAVT",  "AnyBackend endOfService")
    }
}

class AnyMetricalc : OAVTMetricalcInterface {
    override fun processMetric(event: OAVTEvent, tracker: OAVTTrackerInterface): Array<OAVTMetric> {
        Log.d("OAVT",  "AnyMetricalc processMetric")
        return arrayOf(
                OAVTMetric("metricX", OAVTMetric.MetricType.Counter, 10),
                OAVTMetric("metricY", OAVTMetric.MetricType.Gauge, 9.9999),
                OAVTMetric.START_TIME(100)
        )
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyMetricalc instrumentReady")
    }

    override fun endOfService() {
        Log.d("OAVT",  "AnyMetricalc endOfService")
    }

}

class AnyTracker : OAVTTrackerInterface {
    private val state = OAVTState()

    override var trackerId: Int? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        Log.d("OAVT",  "AnyTracker initEvent from Id = " + trackerId)
        event.attributes[OAVTAttribute.TITLE] = "Space balls"
        event.attributes[OAVTAttribute.VOLUME] = 100
        return event
    }

    override fun getState(): OAVTState {
        Log.d("OAVT",  "AnyTracker getState from Id = " + trackerId)
        return state
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyTracker instrumentReady from Id = " + trackerId)
    }

    override fun endOfService() {
        Log.d("OAVT",  "AnyTracker endOfService from Id = " + trackerId)
    }

}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        Log.d("OAVT",  "------------------------------------------------------------------------------")
        instrument.emit(OAVTAction("TEST_ACTION_ONE"), trackerId1)
        instrument.emit(OAVTAction("TEST_ACTION_TWO"), trackerId1)

        if (!instrument.removeAttribute(OAVTAttribute("attrForTracker"), trackerId = trackerId1)) {
            Log.d("OAVT",  "Attribute not removed!")
        }

        instrument.emit(OAVTAction("TEST_ACTION_THREE"), trackerId1)

        instrument.shutdown()
    }
}