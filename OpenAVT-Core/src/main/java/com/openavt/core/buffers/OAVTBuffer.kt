package com.openavt.core.buffers

import com.openavt.core.models.OAVTSample

/**
 * Sample buffer.
 *
 * @param size Buffer size.
 */
class OAVTBuffer(size: Int) {
    /**
     * Samples buffer
     */
    var buffer : MutableList<OAVTSample> = mutableListOf()
    /**
     * Buffer size
     */
    var size = size

    /**
     * Put sample.
     *
     * @param sample An OAVTSample instance.
     * @return True if added, false otherwise.
     */
    open fun put(sample: OAVTSample): Boolean {
        synchronized(this) {
            if (remaining() > 0) {
                buffer.add(sample)
                return true
            }
            else {
                return false
            }
        }
    }

    /**
     * Set sample at position.
     *
     * @param at Position.
     * @param sample An OAVTSample instance.
     * @return True if set, false otherwise.
     */
    open fun set(at: Int, sample: OAVTSample): Boolean {
        synchronized(this) {
            if (at < buffer.size) {
                buffer[at] = sample
                return true
            }
            else {
                return false
            }
        }
    }

    /**
     * Get sample.
     *
     * @param at Position.
     * @return An OAVTSample instance.
     */
    open fun get(at: Int): OAVTSample? {
        synchronized(this) {
            if (at < buffer.size) {
                return buffer[at]
            }
            else {
                return null
            }
        }
    }

    /**
     * Obtain remaining space in the buffer.
     *
     * @return Remaining space.
     */
    open fun remaining(): Int {
        synchronized(this) {
            return size - buffer.size
        }
    }

    /**
     * Obtain a copy of the buffer and flush.
     *
     * @return Buffer.
     */
    open fun retrieve(): Array<OAVTSample> {
        synchronized(this) {
            val tmp = buffer
            buffer = mutableListOf()
            return tmp.toTypedArray()
        }
    }

    /**
     * Obtain a copy of the buffer, ordered by timestamp, and flush.
     *
     * @return Buffer.
     */
    open fun retrieveInOrder(): Array<OAVTSample> {
        synchronized(this) {
            var tmp = retrieve()
            val timestampComparator = Comparator { sample1: OAVTSample, sample2: OAVTSample ->
                if (sample1.timestamp > sample2.timestamp) { 1 }
                else if (sample1.timestamp < sample2.timestamp) { -1 }
                else { 0 }
            }
            return tmp.sortedWith(timestampComparator).toTypedArray()
        }
    }
}