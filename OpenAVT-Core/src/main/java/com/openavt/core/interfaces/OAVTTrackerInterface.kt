package com.openavt.core.interfaces

import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState

interface OAVTTrackerInterface : OAVTComponentInterface {
    /**
     * Init an event.
     *
     * @param event Event received.
     * @return Event or null.
     */
    fun initEvent(event: OAVTEvent): OAVTEvent?
    /**
     * Tracked ID.
     */
    var trackerId : Int?
    /**
     * Tracker state.
     */
    var state : OAVTState
}