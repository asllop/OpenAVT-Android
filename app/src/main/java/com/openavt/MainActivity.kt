package com.openavt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.openavt.core.*
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.models.OAVTState

class AnyHub : OAVTHubInterface {
    override fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent? {
        TODO("Not yet implemented")
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyHub instrumentReady")
    }

    override fun endOfService() {
        TODO("Not yet implemented")
    }

}

class AnyBackend : OAVTBackendInterface {
    override fun sendEvent(event: OAVTEvent) {
        TODO("Not yet implemented")
    }

    override fun sendMetric(metric: OAVTMetric) {
        TODO("Not yet implemented")
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyBackend instrumentReady")
    }

    override fun endOfService() {
        TODO("Not yet implemented")
    }
}

class AnyMetricalc : OAVTMetricalcInterface {
    override fun processMetric(event: OAVTEvent, tracker: OAVTTrackerInterface): Array<OAVTMetric> {
        TODO("Not yet implemented")
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyMetricalc instrumentReady")
    }

    override fun endOfService() {
        TODO("Not yet implemented")
    }

}

class AnyTracker : OAVTTrackerInterface {

    override var trackerId: Int? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        TODO("Not yet implemented")
    }

    override fun getState(): OAVTState {
        TODO("Not yet implemented")
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        Log.d("OAVT",  "AnyTracker instrumentReady from Id = " + trackerId)
    }

    override fun endOfService() {
        TODO("Not yet implemented")
    }

}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val instrument = OAVTInstrument(hub = AnyHub(), metricalc = AnyMetricalc(), backend = AnyBackend())
        instrument.addTracker(AnyTracker())
        instrument.addTracker(AnyTracker())
        instrument.ready()
    }
}