package com.openavt.ima.trackers

import com.google.ads.interactivemedia.v3.api.Ad
import com.google.ads.interactivemedia.v3.api.AdError
import com.google.ads.interactivemedia.v3.api.AdErrorEvent
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTState
import com.openavt.core.utils.OAVTLog

/**
 * OpenAVT tracker for Google IMA Ads.
 */
open class OAVTTrackerIMA(): OAVTTrackerInterface, AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {
    override var state: OAVTState = OAVTState()
    override var trackerId: Int? = null

    private var instrument: OAVTInstrument? = null
    private var lastAdError: AdError? = null
    private var adRoll: String? = null
    private var creativeId: String? = null
    private var title: String? = null
    private var bitrate: Long? = null
    private var resolutionHeight: Long? = null
    private var resolutionWidth: Long? = null
    private var duration: Long? = null

    override fun initEvent(event: OAVTEvent): OAVTEvent? {
        if (event.action == OAVTAction.AdError) {
            lastAdError?.let {
                val type : String
                when (it.errorType) {
                    AdError.AdErrorType.LOAD -> type = "load"
                    AdError.AdErrorType.PLAY -> type = "play"
                    else -> type = ""
                }

                event.attributes[OAVTAttribute.errorDescription] = it.message as String
                event.attributes[OAVTAttribute.errorType] = type
                event.attributes[OAVTAttribute.errorCode] = it.errorCodeNumber
                lastAdError = null
            }
        }

        return event
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
        if (this.instrument == null) {
            this.instrument = instrument
            registerGetters()
            this.instrument?.emit(OAVTAction.TrackerInit, this)
        }
    }

    override fun endOfService() {
    }

    /**
     * Register attributes getters.
     */
    open fun registerGetters() {
        this.instrument?.registerGetter(OAVTAttribute.trackerTarget, ::getTrackerTarget, this)
        this.instrument?.registerGetter(OAVTAttribute.adRoll, ::getAdRoll, this)
        this.instrument?.registerGetter(OAVTAttribute.adDuration, ::getAdDuration, this)
        this.instrument?.registerGetter(OAVTAttribute.adResolutionHeight, ::getAdResolutionHeight, this)
        this.instrument?.registerGetter(OAVTAttribute.adResolutionWidth, ::getAdResolutionWidth, this)
        this.instrument?.registerGetter(OAVTAttribute.adBitrate, ::getAdBitrate, this)
        this.instrument?.registerGetter(OAVTAttribute.adCreativeId, ::getAdCreativeId, this)
        this.instrument?.registerGetter(OAVTAttribute.adTitle, ::getAdTitle, this)
        this.instrument?.registerGetter(OAVTAttribute.isAdsTracker, ::getIsAdsTracker, this)
    }

    // AdErrorEvent and AdEvent listeners

    override fun onAdError(adError: AdErrorEvent?) {
        OAVTLog.error("IMA Tracker onAdError " + adError)
        lastAdError = adError?.error
        instrument!!.emit(OAVTAction.AdError, this)
    }

    override fun onAdEvent(adEvent: AdEvent?) {
        if (adEvent == null || adEvent.type == AdEvent.AdEventType.AD_PROGRESS) {
            return
        }

        OAVTLog.verbose("IMA Tracker onAdEvent " + adEvent)

        generateAdAttributes(adEvent.ad)

        when (adEvent.getType()) {
            AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED -> {
                instrument!!.emit(OAVTAction.AdBreakBegin, this)
            }
            AdEvent.AdEventType.CONTENT_RESUME_REQUESTED -> {
                instrument!!.emit(OAVTAction.AdBreakFinish, this)
            }
            AdEvent.AdEventType.STARTED -> {
                instrument!!.emit(OAVTAction.AdBegin, this)
            }
            AdEvent.AdEventType.COMPLETED -> {
                instrument!!.emit(OAVTAction.AdFinish, this)
            }
            AdEvent.AdEventType.TAPPED, AdEvent.AdEventType.CLICKED -> {
                instrument!!.emit(OAVTAction.AdClick, this)
            }
            AdEvent.AdEventType.SKIPPED -> {
                instrument!!.emit(OAVTAction.AdSkip, this)
            }
            AdEvent.AdEventType.FIRST_QUARTILE -> {
                instrument!!.emit(OAVTAction.AdFirstQuartile, this)
            }
            AdEvent.AdEventType.MIDPOINT -> {
                instrument!!.emit(OAVTAction.AdSecondQuartile, this)
            }
            AdEvent.AdEventType.THIRD_QUARTILE -> {
                instrument!!.emit(OAVTAction.AdThirdQuartile, this)
            }
            AdEvent.AdEventType.PAUSED -> {
                instrument!!.emit(OAVTAction.AdPauseBegin, this)
            }
            AdEvent.AdEventType.RESUMED -> {
                instrument!!.emit(OAVTAction.AdPauseFinish, this)
            }
            else -> {}
        }
    }

    // Attribute getters

    private fun generateAdAttributes(ad: Ad?) {
        if (ad == null) return
        adRoll = when (ad.adPodInfo.podIndex) {
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
     * Get Ad Roll.
     *
     * @return Attribute.
     */
    open fun getAdRoll(): String? {
        return adRoll
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