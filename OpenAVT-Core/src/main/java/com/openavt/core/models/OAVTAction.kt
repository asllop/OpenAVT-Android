package com.openavt.core.models

/**
 * An OpenAVT Action.
 *
 * @param name Action name.
 * @param timeAttribute Action time attribute.
 */
class OAVTAction(name: String, timeAttribute: OAVTAttribute = OAVTAttribute("timeSince" + name)) {

    /**
    * Get the action name of an action.
    */
    val actionName = name

    /**
     * Get the time attribute of an action.
     */
    val timeAttribute = timeAttribute

    /**
     * Custom equality comparator.
     *
     * @param other The object to compare.
     * @return True if are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        return actionName == (other as OAVTAction).actionName
    }

    /**
     * Convert object to string.
     *
     * @return Object representation.
     */
    override fun toString(): String {
        return actionName
    }

    companion object {
        /** Tracker Init action. Sent when a tracker is started. */
        val TrackerInit = OAVTAction("TrackerInit")
        /** Media Request action. Sent when an audio/video stream is requested. */
        val MediaRequest = OAVTAction("MediaRequest")
        /** Player Set action. Sent when a player instance is sent to the tracker. */
        val PlayerSet = OAVTAction("PlayerSet")
        /** Player Ready action. Sent when the player is ready to receive commands. */
        val PlayerReady = OAVTAction("PlayerReady")
        /** Prepare Item action. Sent when an audio/video item is prepared. */
        val PrepareItem = OAVTAction("PrepareItem")
        /** Manifest Load action. Sent when the stream manifest is loaded. */
        val ManifestLoad = OAVTAction("ManifestLoad")
        /** Stream Load action. Sent when an audio/video stream is loaded. */
        val StreamLoad = OAVTAction("StreamLoad")
        /** Start action. Sent when an stram starts playing. */
        val Start = OAVTAction("Start")
        /** Buffer Begin action. Sent when the player starts buffering. */
        val BufferBegin = OAVTAction("BufferBegin")
        /** Buffer Finish action. Sent when the player ends buffering. */
        val BufferFinish = OAVTAction("BufferFinish")
        /** Seek Begin action. Sent when the player starts seeking. */
        val SeekBegin = OAVTAction("SeekBegin")
        /** Seek Finish action. Sent when the player ends seeking. */
        val SeekFinish = OAVTAction("SeekFinish")
        /** Pause Begin action. Sent when the playback is paused. */
        val PauseBegin = OAVTAction("PauseBegin")
        /** Pause Finish action. Sent when the playback is resumed. */
        val PauseFinish = OAVTAction("PauseFinish")
        /** Forward Begin action. Sent when the player starts fast forward. */
        val ForwardBegin = OAVTAction("ForwardBegin")
        /** Forward Finish action. Sent when the player ends fast forward. */
        val ForwardFinish = OAVTAction("ForwardFinish")
        /** Rewind Begin action. Sent when the player starts rewind. */
        val RewindBegin = OAVTAction("RewindBegin")
        /** Rewind Finish action. Sent when the player ends rewind. */
        val RewindFinish = OAVTAction("RewindFinish")
        /** Quality Change Up action. Sent when the stream quality goes up. */
        val QualityChangeUp = OAVTAction("QualityChangeUp")
        /** Quality Change Down action. Sent when the stream quality goes down. */
        val QualityChangeDown = OAVTAction("QualityChangeDown")
        /** Stop action. Sent when the stream is stoped by the user. */
        val Stop = OAVTAction("Stop")
        /** End action. Sent when the stream ends. */
        val End = OAVTAction("End")
        /** Next action. Sent when a playlist moves to the next stream in the list. */
        val Next = OAVTAction("Next")
        /** Error action. Sent when an error happens. */
        val Error = OAVTAction("Error")
        /** Ping action. Sent periodically when the ping timer is enabled. */
        val Ping = OAVTAction("Ping")
        /** Ad Break Begin action. Sent when an ad block starts. */
        val AdBreakBegin = OAVTAction("AdBreakBegin")
        /** Ad Break Finish action. Sent when an ad block ends. */
        val AdBreakFinish = OAVTAction("AdBreakFinish")
        /** Ad Begin action. Sent when an ad starts playing. */
        val AdBegin = OAVTAction("AdBegin")
        /** Ad Finish action. Sent when an ad ends playing. */
        val AdFinish = OAVTAction("AdFinish")
        /** Ad Pause Begin action. Sent when the an ad is paused. */
        val AdPauseBegin = OAVTAction("AdPauseBegin")
        /** Ad Pause Finish action. Sent when the an ad is resumed. */
        val AdPauseFinish = OAVTAction("AdPauseFinish")
        /** Ad Buffer Begin action. Sent when the ad starts buffering. */
        val AdBufferBegin = OAVTAction("AdBufferBegin")
        /** Ad Buffer Finish action. Sent when the ad ends buffering. */
        val AdBufferFinish = OAVTAction("AdBufferFinish")
        /** Ad Skip action. Sent when the an ad is skipped. */
        val AdSkip = OAVTAction("AdSkip")
        /** Ad Click action. Sent when the an ad is clicked. */
        val AdClick = OAVTAction("AdClick")
        /** Ad First Quartile action. Sent when the an ad reaches the first quartiles. */
        val AdFirstQuartile = OAVTAction("AdFirstQuartile")
        /** Ad Second Quartile action. Sent when the an ad reaches the second quartiles. */
        val AdSecondQuartile = OAVTAction("AdSecondQuartile")
        /** Ad Third Quartile action. Sent when the an ad reaches the third quartiles. */
        val AdThirdQuartile = OAVTAction("AdThirdQuartile")
        /** Ad Error action. Sent when an error happens during an ad. */
        val AdError = OAVTAction("AdError")
    }
}