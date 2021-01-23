package com.openavt.core

import android.util.Log
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import java.util.*

/**
 * An OpenAVT Instrument.
 */
class OAVTInstrument() {

    private val instrumentId : String = UUID.randomUUID().toString()
    private var hub : OAVTHubInterface? = null
    private var metricalc : OAVTMetricalcInterface? = null
    private var backend : OAVTBackendInterface? = null
    private var trackers : MutableMap<Int, OAVTTrackerInterface> = mutableMapOf()
    private var nextTrackerId : Int = 0

    /**
     * Init a new OAVTInstrument.
     */
    init {
        Log.d("OAVT",  "Instrument init")
    }

    /**
     * Init a new OAVTInstrument, providing hub and backend.
     *
     * @param hub Hub. An object conforming to `OAVTHubInterface`
     * @param backend Backend. An object conforming to `OAVTBackendInterface`
     */
    constructor(hub: OAVTHubInterface, backend: OAVTBackendInterface): this() {
        setHub(hub)
        setBackend(backend)
        Log.d("OAVT", "Instrument init with hub and backend")
    }

    /**
     * Init a new OAVTInstrument, providing hub, metricalc and backend.
     *
     * @param hub Hub. An object conforming to `OAVTHubInterface`
     * @param metricalc Metricalc. An object conforming to `OAVTMetricalcInterface`
     * @param backend Backend. An object conforming to `OAVTBackendInterface`
     */
    constructor(hub: OAVTHubInterface, metricalc: OAVTMetricalcInterface, backend: OAVTBackendInterface): this(hub, backend) {
        setMetricalc(metricalc)
        Log.d("OAVT", "Instrument init with hub, metricalc and backend")
    }

    /**
     * Set the hub instance.
     *
     * @param hub An object conforming to OAVTHubProtocol.
     */
    fun setHub(hub: OAVTHubInterface) {
        this.hub?.let { it.endOfService() }
        this.hub = hub
    }

    /**
     * Set the metricalc instance.
     *
     * @param metricalc An object conforming to OAVTMetricalcProtocol.
     */
    fun setMetricalc(metricalc: OAVTMetricalcInterface) {
        this.metricalc?.let { it.endOfService() }
        this.metricalc = metricalc
    }

    /**
     * Set the backend instance.
     *
     * @param backend An object conforming to OAVTBackendProtocol.
     */
    fun setBackend(backend: OAVTBackendInterface) {
        this.backend?.let { it.endOfService() }
        this.backend = backend
    }

    /**
     * Add a tracker instance.
     *
     * @param tracker An object conforming to OAVTTrackerProtocol.
     * @return The Tracker ID.
     */
    fun addTracker(tracker: OAVTTrackerInterface): Int {
        val trackerId = this.nextTrackerId++
        tracker.trackerId = trackerId
        this.trackers[trackerId] = tracker
        return trackerId
    }

    /**
     * Remove a tracker.
     *
     * @param trackerId: Tracker ID.
     * @param True if removed, False otherwise.
     */
    fun removeTracker(trackerId: Int): Boolean {
        if (this.trackers[trackerId] != null) {
            val tracker = this.trackers[trackerId]
            tracker!!.endOfService()
            this.trackers.remove(trackerId)
            return true
        }
        else {
            return false
        }
    }

    /**
     * Get the list of trackers.
     *
     * @return Map of trackers, using tracker ID as a key.
     */
    fun getTrackers(): Map<Int, OAVTTrackerInterface> {
        return this.trackers
    }

    /**
     * Get one specific tracker.
     *
     * @param trackerId Tracker ID
     * @return A tracker.
     */
    fun getTracker(trackerId: Int): OAVTTrackerInterface? {
        return trackers[trackerId]
    }

    /**
     * Get the hub.
     *
     * @return A hub.
     */
    fun getHub(): OAVTHubInterface? {
        return this.hub
    }

    /**
     * Get the metricalc.
     *
     * @return A metricalc.
     */
    fun getMetricalc(): OAVTMetricalcInterface? {
        return this.metricalc
    }

    /**
     * Get the backend.
     *
     * @return A backend.
     */
    fun getBackend(): OAVTBackendInterface? {
        return this.backend
    }

    /**
     * Tell the instrument chain everything is ready to start.
     * It calls the `instrumentReady` method of all chain components (trackers, hub, metricalc and backend).
     */
    fun ready() {
        this.backend?.let { it.instrumentReady(this) }
        this.metricalc?.let { it.instrumentReady(this) }
        this.hub?.let { it.instrumentReady(this) }
        for ((_, tracker) in this.trackers) {
            tracker.instrumentReady(this)
        }
    }

    /**
     * Tell the instrument chain the job is done and we are shutting down.
     * It calls the `endOfService` method of all chain components (trackers, hub, metricalc and backend).
     */
    fun shutdown() {
        for ((_, tracker) in this.trackers) {
            tracker.endOfService()
        }
        this.hub?.let { it.endOfService() }
        this.metricalc?.let { it.endOfService() }
        this.backend?.let { it.endOfService() }
    }
}