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
        val TRACKER_INIT = OAVTAction("TrackerInit")
        /** Media Request action. Sent when an audio/video stream is requested. */
        val MEDIA_REQUEST = OAVTAction("MediaRequest")
        /** Player Set action. Sent when a player instance is sent to the tracker. */
        val PLAYER_SET = OAVTAction("PlayerSet")
        /** Player Ready action. Sent when the player is ready to receive commands. */
        val PLAYER_READY = OAVTAction("PlayerReady")
        /** Prepare Item action. Sent when an audio/video item is prepared. */
        val PREPARE_ITEM = OAVTAction("PrepareItem")
        /** Manifest Load action. Sent when the stream manifest is loaded. */
        val MANIFEST_LOAD = OAVTAction("ManifestLoad")
        /** Stream Load action. Sent when an audio/video stream is loaded. */
        val STREAM_LOAD = OAVTAction("StreamLoad")
        /** Start action. Sent when an stram starts playing. */
        val START = OAVTAction("Start")
        /** Buffer Begin action. Sent when the player starts buffering. */
        val BUFFER_BEGIN = OAVTAction("BufferBegin")
        /** Buffer Finish action. Sent when the player ends buffering. */
        val BUFFER_FINISH = OAVTAction("BufferFinish")
        /** Seek Begin action. Sent when the player starts seeking. */
        val SEEK_BEGIN = OAVTAction("SeekBegin")
        /** Seek Finish action. Sent when the player ends seeking. */
        val SEEK_FINISH = OAVTAction("SeekFinish")
        /** Pause Begin action. Sent when the playback is paused. */
        val PAUSE_BEGIN = OAVTAction("PauseBegin")
        /** Pause Finish action. Sent when the playback is resumed. */
        val PAUSE_FINISH = OAVTAction("PauseFinish")
        /** Forward Begin action. Sent when the player starts fast forward. */
        val FORWARD_BEGIN = OAVTAction("ForwardBegin")
        /** Forward Finish action. Sent when the player ends fast forward. */
        val FORWARD_FINISH = OAVTAction("ForwardFinish")
        /** Rewind Begin action. Sent when the player starts rewind. */
        val REWIND_BEGIN = OAVTAction("RewindBegin")
        /** Rewind Finish action. Sent when the player ends rewind. */
        val REWIND_FINISH = OAVTAction("RewindFinish")
        /** Quality Change Up action. Sent when the stream quality goes up. */
        val QUALITY_CHANGE_UP = OAVTAction("QualityChangeUp")
        /** Quality Change Down action. Sent when the stream quality goes down. */
        val QUALITY_CHANGE_DOWN = OAVTAction("QualityChangeDown")
        /** Stop action. Sent when the stream is stoped by the user. */
        val STOP = OAVTAction("Stop")
        /** End action. Sent when the stream ends. */
        val END = OAVTAction("End")
        /** Next action. Sent when a playlist moves to the next stream in the list. */
        val NEXT = OAVTAction("Next")
        /** Error action. Sent when an error happens. */
        val ERROR = OAVTAction("Error")
        /** Ping action. Sent periodically when the ping timer is enabled. */
        val PING = OAVTAction("Ping")
        /** Ad Break Begin action. Sent when an ad block starts. */
        val AD_BREAK_BEGIN = OAVTAction("AdBreakBegin")
        /** Ad Break Finish action. Sent when an ad block ends. */
        val AD_BREAK_FINISH = OAVTAction("AdBreakFinish")
        /** Ad Begin action. Sent when an ad starts playing. */
        val AD_BEGIN = OAVTAction("AdBegin")
        /** Ad Finish action. Sent when an ad ends playing. */
        val AD_FINISH = OAVTAction("AdFinish")
        /** Ad Pause Begin action. Sent when the an ad is paused. */
        val AD_PAUSE_BEGIN = OAVTAction("AdPauseBegin")
        /** Ad Pause Finish action. Sent when the an ad is resumed. */
        val AD_PAUSE_FINISH = OAVTAction("AdPauseFinish")
        /** Ad Buffer Begin action. Sent when the ad starts buffering. */
        val AD_BUFFER_BEGIN = OAVTAction("AdBufferBegin")
        /** Ad Buffer Finish action. Sent when the ad ends buffering. */
        val AD_BUFFER_FINISH = OAVTAction("AdBufferFinish")
        /** Ad Skip action. Sent when the an ad is skipped. */
        val AD_SKIP = OAVTAction("AdSkip")
        /** Ad Click action. Sent when the an ad is clicked. */
        val AD_CLICK = OAVTAction("AdClick")
        /** Ad First Quartile action. Sent when the an ad reaches the first quartiles. */
        val AD_FIRST_QUARTILE = OAVTAction("AdFirstQuartile")
        /** Ad Second Quartile action. Sent when the an ad reaches the second quartiles. */
        val AD_SECOND_QUARTILE = OAVTAction("AdSecondQuartile")
        /** Ad Third Quartile action. Sent when the an ad reaches the third quartiles. */
        val AD_THIRD_QUARTILE = OAVTAction("AdThirdQuartile")
        /** Ad Error action. Sent when an error happens during an ad. */
        val AD_ERROR = OAVTAction("AdError")
    }
}