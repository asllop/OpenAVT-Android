package com.openavt.core

import android.util.Log
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.interfaces.OAVTHubInterface
import com.openavt.core.interfaces.OAVTMetricalcInterface
import java.util.*

/**
 * An OpenAVT Instrument.
 */
class OAVTInstrument() {

    private val instrumentId : String = UUID.randomUUID().toString()
    private var hub : OAVTHubInterface? = null
    private var metricalc : OAVTMetricalcInterface? = null
    private var backend : OAVTBackendInterface? = null

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
    constructor(hub: OAVTHubInterface, metricalc: OAVTMetricalcInterface, backend: OAVTBackendInterface): this() {
        setHub(hub)
        setMetricalc(metricalc)
        setBackend(backend)
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

    // TODO: Tracker stuff

    /**
     * Tell the instrument chain everything is ready to start.
     * It calls the `instrumentReady` method of all chain components (trackers, hub, metricalc and backend).
     */
    fun ready() {
        this.backend?.let { it.instrumentReady(this) }
        this.metricalc?.let { it.instrumentReady(this) }
        this.hub?.let { it.instrumentReady(this) }
        //TODO: init trackers
    }
}