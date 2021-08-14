package com.openavt.core.assets

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState

class DummyTracker(): OAVTTrackerInterface {
    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        return event
    }

    override var trackerId: Int? = null
    override var state = OAVTState()

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {}
}