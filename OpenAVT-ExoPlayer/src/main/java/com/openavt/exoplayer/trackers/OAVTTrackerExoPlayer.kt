package com.openavt.exoplayer.trackers

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

class OAVTTrackerExoPlayer() : OAVTTrackerInterface {
    private var instrument: OAVTInstrument? = null

    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        this.instrument = instrument
        OAVTLog.verbose(  "OAVTTrackerExoPlayer instrument ready")
    }

    override fun endOfService() {

    }
}