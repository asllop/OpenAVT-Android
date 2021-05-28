package com.openavt.ima.trackers

import com.google.ads.interactivemedia.v3.api.AdErrorEvent
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

open class OAVTTrackerIMA(): OAVTTrackerInterface, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {
    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    private var instrument: OAVTInstrument? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        //TODO: call getters and other stuff
        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            this.instrument?.emit(OAVTAction.TRACKER_INIT, this)
        }
    }

    override fun endOfService() {
    }

    // AdErrorEvent and AdEvent listeners

    override fun onAdError(adError: AdErrorEvent?) {
        //TODO: error attributes
        OAVTLog.verbose("----> IMA Tracker onAdError " + adError)
        instrument!!.emit(OAVTAction.AD_ERROR, this)
    }

    override fun onAdEvent(adEvent: AdEvent?) {
        if (adEvent == null) return

        if (adEvent.getType() !== AdEvent.AdEventType.AD_PROGRESS) {
            OAVTLog.verbose("----> IMA Tracker onAdEvent " + adEvent)
        }

        //TODO: attributes

        when (adEvent.getType()) {
            AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED -> {
                instrument!!.emit(OAVTAction.AD_BREAK_BEGIN, this)
            }
            AdEvent.AdEventType.CONTENT_RESUME_REQUESTED -> {
                instrument!!.emit(OAVTAction.AD_BREAK_FINISH, this)
            }
            AdEvent.AdEventType.STARTED -> {
                instrument!!.emit(OAVTAction.AD_BEGIN, this)
            }
            AdEvent.AdEventType.COMPLETED -> {
                instrument!!.emit(OAVTAction.AD_FINISH, this)
            }
            AdEvent.AdEventType.TAPPED, AdEvent.AdEventType.CLICKED -> {
                instrument!!.emit(OAVTAction.AD_CLICK, this)
            }
            AdEvent.AdEventType.SKIPPED -> {
                instrument!!.emit(OAVTAction.AD_SKIP, this)
            }
            AdEvent.AdEventType.FIRST_QUARTILE -> {
                instrument!!.emit(OAVTAction.AD_FIRST_QUARTILE, this)
            }
            AdEvent.AdEventType.MIDPOINT -> {
                instrument!!.emit(OAVTAction.AD_SECOND_QUARTILE, this)
            }
            AdEvent.AdEventType.THIRD_QUARTILE -> {
                instrument!!.emit(OAVTAction.AD_THIRD_QUARTILE, this)
            }
            AdEvent.AdEventType.PAUSED -> {
                instrument!!.emit(OAVTAction.AD_PAUSE_BEGIN, this)
            }
            AdEvent.AdEventType.RESUMED -> {
                instrument!!.emit(OAVTAction.AD_PAUSE_FINISH, this)
            }
        }
    }

    //TODO: attribute getters
}