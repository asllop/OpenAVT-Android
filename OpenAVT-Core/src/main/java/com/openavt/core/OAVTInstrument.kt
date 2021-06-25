package com.openavt.core

import android.os.Handler
import android.os.Looper
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import com.openavt.core.utils.OAVTLog
import java.util.*


/**
 * An OpenAVT Instrument.
 */
class OAVTInstrument() {

    private val instrumentId: String = UUID.randomUUID().toString()
    private var hub: OAVTHubInterface? = null
    private var metricalc: OAVTMetricalcInterface? = null
    private var backend: OAVTBackendInterface? = null
    private val trackers: MutableMap<Int, OAVTTrackerInterface> = mutableMapOf()
    private var nextTrackerId: Int = 0
    private val timeSince: MutableMap<OAVTAttribute, Long> = mutableMapOf()
    private var pingTrackerTimers: MutableMap<Int, Timer> = mutableMapOf()
    private val trackerGetters : MutableMap<Int, MutableMap<OAVTAttribute, () -> Any?>> = mutableMapOf()

    /**
     * Init a new OAVTInstrument.
     */
    init {
        OAVTLog.verbose("Instrument init")
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
        OAVTLog.verbose("Instrument init with hub and backend")
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
        OAVTLog.verbose("Instrument init with hub, metricalc and backend")
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

    /**
     * Start PING timer.
     *
     * Once called it will start sending PING events every `interval` using the tracker specified in `trackerId`.
     *
     * @param trackerId Tracker ID.
     * @param interval The timer interval of the timer in seconds.
     */
    fun startPing(trackerId: Int, interval: Long) {
        stopPing(trackerId)
        val timer = Timer()
        pingTrackerTimers[trackerId] = timer
        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    OAVTLog.verbose("Send PING from trackerdId = " + trackerId)
                    this@OAVTInstrument.emit(OAVTAction.Ping, trackerId)
                })
            }
        }, interval * 1000, interval * 1000)
    }

    /**
     * Stop PING timer.
     *
     * @param trackerId Tracker ID.
     */
    fun stopPing(trackerId: Int) {
        pingTrackerTimers[trackerId]?.let {
            OAVTLog.verbose("Cancel PING from trackerdId = " + trackerId)
            it.cancel()
            pingTrackerTimers.remove(trackerId)
        }
    }

    /**
     * Emit an event.
     * It generates an `OAVTEvent` using the specified action and emits it using the specified tracker.
     *
     * @param action Action.
     * @param trackerId Tracker ID.
     */
    fun emit(action: OAVTAction, trackerId: Int) {
        getTracker(trackerId)?.let { emit(action, it) }
    }

    /**
     * Emit an event.
     * It generates an `OAVTEvent` using the specified action and emits it using the specified tracker.
     *
     * @param action Action.
     * @param tracker Tracker.
     */
    fun emit(action: OAVTAction, tracker: OAVTTrackerInterface) {
        val event = generateEvent(action, tracker)

        var trackerEvent = tracker.initEvent(event)
        if (trackerEvent != null && this.hub != null) {
            trackerEvent = this.hub!!.processEvent(trackerEvent, tracker)
            if (trackerEvent != null && this.backend != null) {
                if (this.metricalc != null) {
                    for (metric in this.metricalc!!.processMetric(trackerEvent!!, tracker)) {
                        this.backend!!.sendMetric(metric)
                    }
                }

                this.backend!!.sendEvent(trackerEvent)

                // Save action timeSince, only when the event reached the end of the instrument chain
                timeSince[action.timeAttribute] = System.currentTimeMillis()
            }
        }
    }

    /**
     * Register an attribute getter for a tracker.
     *
     * @param attribute An OAVTAttribute.
     * @param getter Code block. It must return the attribute value.
     * @param tracker Tracker.
     */
    fun registerGetter(attribute: OAVTAttribute, getter: () -> Any?, tracker: OAVTTrackerInterface) {
        tracker.trackerId?.let {
            if (trackerGetters[it] == null) {
                trackerGetters[it] = mutableMapOf()
            }
            trackerGetters[it]!![attribute] = getter
        }
    }

    /**
     * Call an attribute getter.
     *
     * @param attribute An OAVTAttribute.
     * @param tracker Tracker.
     *
     * @return Attribute value returned by the getter code block.
     */
    fun callGetter(attribute: OAVTAttribute, tracker: OAVTTrackerInterface): Any? {
        tracker.trackerId?.let {
            trackerGetters[it]?.let {
                it[attribute]?.let {
                    return it()
                }
            }
        }
        return null
    }

    /**
     * Call an attribute getter and put the resulting attribute into an event.
     *
     * @param attribute: An OAVTAttribute.
     * @param event An OAVTEvent.
     * @param tracker Tracker.
     */
    fun useGetter(attribute: OAVTAttribute, event: OAVTEvent, tracker: OAVTTrackerInterface) {
        callGetter(attribute, tracker)?.let {
            event.attributes[attribute] = it
        }
    }

    private fun generateCustomAttributeId(action: OAVTAction? = null, trackerId: Int? = null): String {
        if (action == null && trackerId == null) {
            // For all
            return "5fb1f955b45e38e31789286a1790398d"  // MD5 of string "ALL"
        }
        else if (action == null && trackerId != null) {
            // For specific tracker
            return trackerId.toString()
        }
        else if (trackerId == null && action != null) {
            // For specific action
            return action.actionName
        }
        else if (trackerId != null && action != null) {
            // For specific action and tracker
            return action.actionName + "-" + trackerId.toString()
        }
        else {
            // This case is not possible, but without it Android Studio complains
            return ""
        }
    }

    private fun generateEvent(action: OAVTAction, tracker: OAVTTrackerInterface): OAVTEvent {
        val event = OAVTEvent(action)

        // Generate attributes
        generateSenderId(tracker, event)
        generateTimeSince(event)

        return event
    }

    private fun generateSenderId(tracker: OAVTTrackerInterface, event: OAVTEvent) {
        event.attributes[OAVTAttribute.senderId] = instrumentId + "-" + (tracker.trackerId ?: "?")
    }

    private fun generateTimeSince(event: OAVTEvent) {
        for ((attr, ts) in timeSince) {
            val delta = System.currentTimeMillis() - ts
            event.attributes[attr] = delta
        }
    }
}