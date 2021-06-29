package com.openavt.core.buffers

import com.openavt.core.models.OAVTSample

/**
 * Buffer for samples with reservoir sampling, using the Algorithm R. Thread safe.
 */
open class OAVTReservoirBuffer : OAVTBuffer {

    /**
     * Sampling index
     */
    var samplingIndex : Long = 0

    /**
     * Constructor.
     *
     * @param size Buffer size.
     */
    constructor(size: Int): super(size) {
        this.size = size
        this.samplingIndex = size.toLong()
    }

    override fun put(sample: OAVTSample): Boolean {
        synchronized(this) {
            if (remaining() > 0) {
                // Fill the buffer
                buffer.add(sample)
                return true
            }
            else {
                // Buffer is full, start random sampling
                val j = (0..samplingIndex).random()
                samplingIndex +=  1
                return if (j < size) {
                    buffer[j.toInt()] = sample
                    true
                }
                else {
                    false
                }
            }
        }
    }

    override fun retrieve(): Array<OAVTSample> {
        val x = super.retrieve()
        samplingIndex = size.toLong()
        return x
    }
}