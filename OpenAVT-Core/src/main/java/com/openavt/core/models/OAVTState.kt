package com.openavt.core.models

/**
 * An OpenAVT state.
 */
open class OAVTState() {

    /** Media did request flag. */
    var didMediaRequest = false
    /** Player set flag. */
    var didPlayerSet = false
    /** Stream did load flag. */
    var didStreamLoad = false
    /** Stream did start flag. */
    var didStart = false
    /** Player in buffer state flag. */
    var isBuffering = false
    /** Player in pause state flag. */
    var isPaused = false
    /** Player in seek state flag. */
    var isSeeking = false
    /** Playback finished flag. */
    var didFinish = false
    /** Player in Ad break flag. */
    var inAdBreak = false
    /** Player playing an Ad flag. */
    var inAd = false

    /**
     * Reset the state.
     */
    fun reset() {
        didMediaRequest = false
        didPlayerSet = false
        didStreamLoad = false
        didStart = false
        isBuffering = false
        isPaused = false
        isSeeking = false
        didFinish = false
        inAdBreak = false
        inAd = false
    }
}