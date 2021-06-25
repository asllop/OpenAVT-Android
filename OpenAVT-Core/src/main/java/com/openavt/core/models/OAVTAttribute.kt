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
        val trackerTarget = OAVTAttribute("trackerTarget")
        /** Stream Id attribute. Identificator of the stream being played. */
        val streamId = OAVTAttribute("streamId")
        /** Playback ID attribute. Identificator of the current playback. */
        val playbackId = OAVTAttribute("playbackId")
        /** Sender ID attribute. Identificator of the sender (the instrument-tracker). */
        val senderId = OAVTAttribute("senderId")
        /** Count Errors attribute. Number of errors. */
        val countErrors = OAVTAttribute("countErrors")
        /** Count Starts attribute. Number of starts. */
        val countStarts = OAVTAttribute("countStarts")
        /** Accumulated Pause Time attribute. Total amount of time in paused state. */
        val accumPauseTime = OAVTAttribute("accumPauseTime")
        /** Accumulated Buffer Time attribute. Total amount of time buffering. */
        val accumBufferTime = OAVTAttribute("accumBufferTime")
        /** Accumulated Seek Time attribute. Total amount of time seeking. */
        val accumSeekTime = OAVTAttribute("accumSeekTime")
        /** Accumulated Play Time attribute. Total amount of time playing. */
        val accumPlayTime = OAVTAttribute("accumPlayTime")
        /** Delta Play Time attribute. Time playing since last event. */
        val deltaPlayTime = OAVTAttribute("deltaPlayTime")
        /** In Pause Block attribute. Player is paused. */
        val inPauseBlock = OAVTAttribute("inPauseBlock")
        /** In Seek Block attribute. Player is seeking. */
        val inSeekBlock = OAVTAttribute("inSeekBlock")
        /** In Buffer Block attribute. Player is buffering. */
        val inBufferBlock = OAVTAttribute("inBufferBlock")
        /** In Playback Block attribute. Player is playing. */
        val inPlaybackBlock = OAVTAttribute("inPlaybackBlock")
        /** Error Description attribute. Error message. */
        val errorDescription = OAVTAttribute("errorDescription")
        /** Error Type attribute. Error type. */
        val errorType = OAVTAttribute("errorType")
        /** Error Code attribute. Error code. */
        val errorCode = OAVTAttribute("errorCode")
        /** Position attribute. Current stream position. */
        val position = OAVTAttribute("position")
        /** Duration attribute. Stream duration. */
        val duration = OAVTAttribute("duration")
        /** Resolution Height attribute. In video streams, vertical resolution. */
        val resolutionHeight = OAVTAttribute("resolutionHeight")
        /** Resolution Width attribute. In video streams, horizontal resolution. */
        val resolutionWidth = OAVTAttribute("resolutionWidth")
        /** Is Muted attribute. Playback is muted. */
        val isMuted = OAVTAttribute("isMuted")
        /** Volume attribute. Current volume. */
        val volume = OAVTAttribute("volume")
        /** FPS attribute. Frames per second. */
        val fps = OAVTAttribute("fps")
        /** Source attribute. Stream source, usually an URL. */
        val source = OAVTAttribute("source")
        /** Bitrate attribute. Stream bitrate. */
        val bitrate = OAVTAttribute("bitrate")
        /** Language attribute. Stream language. */
        val language = OAVTAttribute("language")
        /** Subtitles attribute. Subtitles language. */
        val subtitles = OAVTAttribute("subtitles")
        /** Title attribute. Stream title. */
        val title = OAVTAttribute("title")
        /** Is Ads Tracker attribute. Tracker is generating Ad events. */
        val isAdsTracker = OAVTAttribute("isAdsTracker")
        /** Count Ads attribute. Number of ads. */
        val countAds = OAVTAttribute("countAds")
        /** In Ad Break Block attribute. An Ad break has started. */
        val inAdBreakBlock = OAVTAttribute("inAdBreakBlock")
        /** In Ad Block attribute. Currently playing an Ad. */
        val inAdBlock = OAVTAttribute("inAdBlock")
        /** Ad Position attribute. Current Ad stream position. */
        val adPosition = OAVTAttribute("adPosition")
        /** Ad Duration attribute. Ad stream duration. */
        val adDuration = OAVTAttribute("adDuration")
        /** Ad Buffered Time attribute. Amount of Ad stream buffered. */
        val adBufferedTime = OAVTAttribute("adBufferedTime")
        /** Ad Volume attribute. Current Ad volume. */
        val adVolume = OAVTAttribute("adVolume")
        /** Ad Roll attribute. Ad position within the main stream (pre, mid, post). */
        val adRoll = OAVTAttribute("adRoll")
        /** Ad Description attribute. Ad description. */
        val adDescription = OAVTAttribute("adDescription")
        /** Ad ID attribute. Ad ID. */
        val adId = OAVTAttribute("adId")
        /** Ad Title attribute. Ad Title. */
        val adTitle = OAVTAttribute("adTitle")
        /** Ad Advertiser Name attribute. Ad advertiser name. */
        val adAdvertiserName = OAVTAttribute("adAdvertiserName")
        /** Ad Creative ID attribute. Ad creative ID. */
        val adCreativeId = OAVTAttribute("adCreativeId")
        /** Ad Bitrate attribute. Ad stream bitrate. */
        val adBitrate = OAVTAttribute("adBitrate")
        /** Ad Resolution Height attribute. Ad vertical resolution. */
        val adResolutionHeight = OAVTAttribute("adResolutionHeight")
        /** Ad Resolution Width attribute. Ad horizontal resolution. */
        val adResolutionWidth = OAVTAttribute("adResolutionWidth")
        /** Ad System attribute. Ad system. */
        val adSystem = OAVTAttribute("adSystem")
    }
}