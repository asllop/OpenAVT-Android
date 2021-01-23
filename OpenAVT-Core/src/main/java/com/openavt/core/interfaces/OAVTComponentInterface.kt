package com.openavt.core.interfaces

import com.openavt.core.OAVTInstrument

/**
 * OpenAVT base interface for instrument components.
 */
interface OAVTComponentInterface {
    /**
     * Instrument is ready. Called when the user executes `OAVTInstrument.ready()`.
     * @param instrument Instrument instance.
     */
    fun instrumentReady(instrument: OAVTInstrument)

    /**
     * End of service. Called when a component is removed from the instrument or when `OAVTInstrument.shutdown()` is called.
     */
    fun endOfService()
}