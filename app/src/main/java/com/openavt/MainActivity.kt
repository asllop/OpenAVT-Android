package com.openavt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.openavt.core.*
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val instrument = OAVTInstrument(hub = AnyHub() , backend = AnyBackend())
        instrument.ready()
    }
}