package com.openavt.core.interfaces

import com.openavt.core.models.OAVTEvent

interface OAVTHubInterface: OAVTComponentInterface {
    /**
     * Process an event.
     *
     * @param event Event received.
     * @param tracker Tracker that generated the event.
     * @return Event or null.
     */
    fun processEvent(event: OAVTEvent, tracker: OAVTTrackerInterface): OAVTEvent?
}