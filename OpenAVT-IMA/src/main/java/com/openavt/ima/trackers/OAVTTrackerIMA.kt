package com.openavt.ima.trackers

import com.google.ads.interactivemedia.v3.api.Ad
import com.google.ads.interactivemedia.v3.api.AdErrorEvent
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

open class OAVTTrackerIMA(): OAVTTrackerInterface, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {
    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    private var instrument: OAVTInstrument? = null

    private var adPosition: String? = null
    private var creativeId: String? = null
    private var title: String? = null
    private var bitrate: Long? = null
    private var resolutionHeight: Long? = null
    private var resolutionWidth: Long? = null
    private var duration: Long? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        //TODO: set error attributes

        // Set attributes from getters
        this.instrument?.useGetter(OAVTAttribute.TRACKER_TARGET, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_POSITION, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_DURATION, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_RESOLUTION_HEIGHT, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_RESOLUTION_WIDTH, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_BITRATE, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_CREATIVE_ID, event, this)
        this.instrument?.useGetter(OAVTAttribute.AD_TITLE, event, this)
        this.instrument?.useGetter(OAVTAttribute.IS_ADS_TRACKER, event, this)

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            registerGetters()
            this.instrument?.emit(OAVTAction.TRACKER_INIT, this)
        }
    }

    override fun endOfService() {
    }

    /**
     * Register attributes getters.
     */
    open fun registerGetters() {
        this.instrument?.registerGetter(OAVTAttribute.TRACKER_TARGET, ::getTrackerTarget, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_POSITION, ::getAdPosition, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_DURATION, ::getAdDuration, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_RESOLUTION_HEIGHT, ::getAdResolutionHeight, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_RESOLUTION_WIDTH, ::getAdResolutionWidth, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_BITRATE, ::getAdBitrate, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_CREATIVE_ID, ::getAdCreativeId, this)
        this.instrument?.registerGetter(OAVTAttribute.AD_TITLE, ::getAdTitle, this)
        this.instrument?.registerGetter(OAVTAttribute.IS_ADS_TRACKER, ::getIsAdsTracker, this)
    }

    // AdErrorEvent and AdEvent listeners

    override fun onAdError(adError: AdErrorEvent?) {
        //TODO: error attributes
        OAVTLog.verbose("----> IMA Tracker onAdError " + adError)
        instrument!!.emit(OAVTAction.AD_ERROR, this)
    }

    override fun onAdEvent(adEvent: AdEvent?) {
        if (adEvent == null) return

        if (adEvent.getType() !== AdEvent.AdEventType.AD_PROGRESS) {
            OAVTLog.verbose("----> IMA Tracker onAdEvent " + adEvent)
        }

        generateAdAttributes(adEvent.ad)

        when (adEvent.getType()) {
            AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED -> {
                instrument!!.emit(OAVTAction.AD_BREAK_BEGIN, this)
            }
            AdEvent.AdEventType.CONTENT_RESUME_REQUESTED -> {
                instrument!!.emit(OAVTAction.AD_BREAK_FINISH, this)
            }
            AdEvent.AdEventType.STARTED -> {
                instrument!!.emit(OAVTAction.AD_BEGIN, this)
            }
            AdEvent.AdEventType.COMPLETED -> {
                instrument!!.emit(OAVTAction.AD_FINISH, this)
            }
            AdEvent.AdEventType.TAPPED, AdEvent.AdEventType.CLICKED -> {
                instrument!!.emit(OAVTAction.AD_CLICK, this)
            }
            AdEvent.AdEventType.SKIPPED -> {
                instrument!!.emit(OAVTAction.AD_SKIP, this)
            }
            AdEvent.AdEventType.FIRST_QUARTILE -> {
                instrument!!.emit(OAVTAction.AD_FIRST_QUARTILE, this)
            }
            AdEvent.AdEventType.MIDPOINT -> {
                instrument!!.emit(OAVTAction.AD_SECOND_QUARTILE, this)
            }
            AdEvent.AdEventType.THIRD_QUARTILE -> {
                instrument!!.emit(OAVTAction.AD_THIRD_QUARTILE, this)
            }
            AdEvent.AdEventType.PAUSED -> {
                instrument!!.emit(OAVTAction.AD_PAUSE_BEGIN, this)
            }
            AdEvent.AdEventType.RESUMED -> {
                instrument!!.emit(OAVTAction.AD_PAUSE_FINISH, this)
            }
        }
    }

    // Attribute getters

    private fun generateAdAttributes(ad: Ad?) {
        if (ad == null) return
        adPosition = when (ad.adPodInfo.podIndex) {
            0 -> "pre"
            -1 -> "post"
            else -> "mid"
        }
        creativeId = ad.creativeId
        title = ad.title
        bitrate = ad.vastMediaBitrate.toLong()
        resolutionHeight = ad.vastMediaHeight.toLong()
        resolutionWidth = ad.vastMediaWidth.toLong()
        duration = ad.duration.toLong()
    }

    /**
     * Get tracker target.
     *
     * @return Atribute.
     */
    open fun getTrackerTarget(): String? {
        return "IMA"
    }

    /**
     * Get Ad Position.
     *
     * @return Attribute.
     */
    open fun getAdPosition(): String? {
        return adPosition
    }

    /**
     * Get Ad Creative ID
     *
     * @return Attribute.
     */
    open fun getAdCreativeId(): String? {
        return creativeId
    }

    /**
     * Get title.
     *
     * @return Attribute.
     */
    open fun getAdTitle(): String? {
        return title
    }

    /**
     * Get bitrate.
     *
     * @return Attribute.
     */
    open fun getAdBitrate(): Long? {
        return bitrate
    }

    /**
     * Get rendition height.
     *
     * @return Attribute.
     */
    open fun getAdResolutionHeight(): Long? {
        return resolutionHeight
    }

    /**
     * Get rendition width.
     *
     * @return Attribute.
     */
    open fun getAdResolutionWidth(): Long? {
        return resolutionWidth
    }

    /**
     * Get Ad duration.
     *
     * @return Attribute.
     */
    open fun getAdDuration(): Long? {
        return duration
    }

    /**
     * Get is ads tracker attribute.
     *
     * @return attribute.
     */
    open fun getIsAdsTracker() : Boolean {
        return true
    }
}