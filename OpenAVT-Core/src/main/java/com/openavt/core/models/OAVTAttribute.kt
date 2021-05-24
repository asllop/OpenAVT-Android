package com.openavt.core.models

import java.util.*

/**
 * An OpenAVT Attribute.
 *
 * @param name Attribute name.
 */
class OAVTAttribute(name: String) {

    /**
     * Get attribute name.
     */
    val attributeName = name

    /**
     * Custom equality comparator.
     *
     * @param other The object to compare.
     * @return True if are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        return attributeName == (other as OAVTAttribute).attributeName
    }

    /**
     * Convert object to string.
     *
     * @return Object representation.
     */
    override fun toString(): String {
        return attributeName
    }

    /**
     * Provide custom hash code.
     *
     * @return Object hash code.
     */
    override fun hashCode(): Int {
        return attributeName.hashCode()
    }

    companion object {
        /** Tracker Target attribute. The target of the tracker (i.e.: AVPlayer, IMA, ...). */
        val TRACKER_TARGET = OAVTAttribute("trackerTarget")
        /** Stream Id attribute. Identificator of the stream being played. */
        val STREAM_ID = OAVTAttribute("streamId")
        /** Playback ID attribute. Identificator of the current playback. */
        val PLAYBACK_ID = OAVTAttribute("playbackId")
        /** Sender ID attribute. Identificator of the sender (the instrument-tracker). */
        val SENDER_ID = OAVTAttribute("senderId")
        /** Count Errors attribute. Number of errors. */
        val COUNT_ERRORS = OAVTAttribute("countErrors")
        /** Count Starts attribute. Number of starts. */
        val COUNT_STARTS = OAVTAttribute("countStarts")
        /** Accumulated Pause Time attribute. Total amount of time in paused state. */
        val ACCUM_PAUSE_TIME = OAVTAttribute("accumPauseTime")
        /** Accumulated Buffer Time attribute. Total amount of time buffering. */
        val ACCUM_BUFFER_TIME = OAVTAttribute("accumBufferTime")
        /** Accumulated Seek Time attribute. Total amount of time seeking. */
        val ACCUM_SEEK_TIME = OAVTAttribute("accumSeekTime")
        /** Accumulated Play Time attribute. Total amount of time playing. */
        val ACCUM_PLAY_TIME = OAVTAttribute("accumPlayTime")
        /** Delta Play Time attribute. Time playing since last event. */
        val DELTA_PLAY_TIME = OAVTAttribute("deltaPlayTime")
        /** In Pause Block attribute. Player is paused. */
        val IN_PAUSE_BLOCK = OAVTAttribute("inPauseBlock")
        /** In Seek Block attribute. Player is seeking. */
        val IN_SEEK_BLOCK = OAVTAttribute("inSeekBlock")
        /** In Buffer Block attribute. Player is buffering. */
        val IN_BUFFER_BLOCK = OAVTAttribute("inBufferBlock")
        /** In Playback Block attribute. Player is playing. */
        val IN_PLAYBACK_BLOCK = OAVTAttribute("inPlaybackBlock")
        /** Error Description attribute. Error message. */
        val ERROR_DESCRIPTION = OAVTAttribute("errorDescription")
        /** Error Type attribute. Error message. */
        val ERROR_TYPE = OAVTAttribute("errorType")
        /** Position attribute. Current stream position. */
        val POSITION = OAVTAttribute("position")
        /** Duration attribute. Stream duration. */
        val DURATION = OAVTAttribute("duration")
        /** Resolution Height attribute. In video streams, vertical resolution. */
        val RESOLUTION_HEIGHT = OAVTAttribute("resolutionHeight")
        /** Resolution Width attribute. In video streams, horizontal resolution. */
        val RESOLUTION_WIDTH = OAVTAttribute("resolutionWidth")
        /** Is Muted attribute. Playback is muted. */
        val IS_MUTED = OAVTAttribute("isMuted")
        /** Volume attribute. Current volume. */
        val VOLUME = OAVTAttribute("volume")
        /** FPS attribute. Frames per second. */
        val FPS = OAVTAttribute("fps")
        /** Source attribute. Stream source, usually an URL. */
        val SOURCE = OAVTAttribute("source")
        /** Bitrate attribute. Stream bitrate. */
        val BITRATE = OAVTAttribute("bitrate")
        /** Language attribute. Stream language. */
        val LANGUAGE = OAVTAttribute("language")
        /** Subtitles attribute. Subtitles language. */
        val SUBTITLES = OAVTAttribute("subtitles")
        /** Title attribute. Stream title. */
        val TITLE = OAVTAttribute("title")
        /** Is Ads Tracker attribute. Tracker is generating Ad events. */
        val IS_ADS_TRACKER = OAVTAttribute("isAdsTracker")
        /** Count Ads attribute. Number of ads. */
        val COUNT_ADS = OAVTAttribute("countAds")
        /** In Ad Break Block attribute. An Ad break has started. */
        val IN_AD_BREAK_BLOCK = OAVTAttribute("inAdBreakBlock")
        /** In Ad Block attribute. Currently playing an Ad. */
        val IN_AD_BLOCK = OAVTAttribute("inAdBlock")
        /** Ad Position attribute. Current Ad stream position. */
        val AD_POSITION = OAVTAttribute("adPosition")
        /** Ad Duration attribute. Ad stream duration. */
        val AD_DURATION = OAVTAttribute("adDuration")
        /** Ad Buffered Time attribute. Amount of Ad stream buffered. */
        val AD_BUFFERED_TIME = OAVTAttribute("adBufferedTime")
        /** Ad Volume attribute. Current Ad volume. */
        val AD_VOLUME = OAVTAttribute("adVolume")
        /** Ad Roll attribute. Ad position within the main stream (pre, mid, post). */
        val AD_ROLL = OAVTAttribute("adRoll")
        /** Ad Description attribute. Ad description. */
        val AD_DESCRIPTION = OAVTAttribute("adDescription")
        /** Ad ID attribute. Ad ID. */
        val AD_ID = OAVTAttribute("adId")
        /** Ad Title attribute. Ad Title. */
        val AD_TITLE = OAVTAttribute("adTitle")
        /** Ad Advertiser Name attribute. Ad advertiser name. */
        val AD_ADVERTISER_NAME = OAVTAttribute("adAdvertiserName")
        /** Ad Creative ID attribute. Ad creative ID. */
        val AD_CREATIVE_ID = OAVTAttribute("adCreativeId")
        /** Ad Bitrate attribute. Ad stream bitrate. */
        val AD_BITRATE = OAVTAttribute("adBitrate")
        /** Ad Resolution Height attribute. Ad vertical resolution. */
        val AD_RESOLUTION_HEIGHT = OAVTAttribute("adResolutionHeight")
        /** Ad Resolution Width attribute. Ad horizontal resolution. */
        val AD_RESOLUTION_WIDTH = OAVTAttribute("adResolutionWidth")
        /** Ad System attribute. Ad system. */
        val AD_SYSTEM = OAVTAttribute("adSystem")
    }
}