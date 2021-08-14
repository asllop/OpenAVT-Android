package com.openavt.core.hubs

import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent

/**
 * OAVT hub for generic content players with ads.
 */
open class OAVTHubCoreAds: OAVTHubCore() {
    private var countAds = 0

    override fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent? {
        if (event.action == OAVTAction.AdBreakBegin) {
            if (!tracker.state.inAdBreak) {
                setInAdBreakState(true)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.AdBreakFinish) {
            if (tracker.state.inAdBreak) {
                setInAdBreakState(false)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.AdBegin) {
            if (!tracker.state.inAd) {
                instrument?.startPing(tracker.trackerId!!, 30L)
                setInAdState(true)
                countAds++
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.AdFinish) {
            if (tracker.state.inAd) {
                instrument?.stopPing(tracker.trackerId!!)
                setInAdState(false)
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.End) {
            // To avoid content end when an ad break happens
            if (tracker.state.inAdBreak) {
                return null
            }
        }
        else if (event.action == OAVTAction.AdPauseBegin) {
            if (!tracker.state.isPaused) {
                tracker.state.isPaused = true
            }
            else {
                return null
            }
        }
        else if (event.action == OAVTAction.AdPauseFinish) {
            if (tracker.state.isPaused) {
                tracker.state.isPaused = false
            }
            else {
                return null
            }
        }

        event.attributes[OAVTAttribute.inAdBreakBlock] = tracker.state.inAdBreak
        event.attributes[OAVTAttribute.inAdBlock] = tracker.state.inAd
        event.attributes[OAVTAttribute.countAds] = countAds

        // Get current content video position
        instrument?.getTrackers()?.let {
            for ((_, tracker) in it) {
                instrument?.callGetter(OAVTAttribute.isAdsTracker, tracker)?.let {
                    if (it as Boolean) {
                        instrument?.useGetter(OAVTAttribute.position, event, tracker)
                    }
                }
            }
        }

        return super.processEvent(event, tracker)
    }

    /// Set inAd state for all trackers of the instrument
    open fun setInAdState(state: Boolean) {
        instrument?.getTrackers()?.let {
            for ((_, tracker) in it) {
                tracker.state.inAd = state
            }
        }
    }

    /// Set inAdBreak state for all trackers of the instrument
    open fun setInAdBreakState(state: Boolean) {
        instrument?.getTrackers()?.let {
            for ((_, tracker) in it) {
                tracker.state.inAdBreak = state
            }
        }
    }
}