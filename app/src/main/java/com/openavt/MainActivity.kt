package com.openavt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.openavt.core.*
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.models.OAVTState

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
        Log.d("OAVT",  "AnyBackend sendEvent")
    }

    override fun sendMetric(metric: OAVTMetric) {
        Log.d("OAVT",  "AnyBackend sendMetric")
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
        return arrayOf(OAVTMetric(), OAVTMetric())
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

        instrument.removeTracker(trackerId1)

        instrument.emit(OAVTAction(), trackerId0)

        instrument.shutdown()
    }
}