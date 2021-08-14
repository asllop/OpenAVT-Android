package com.openavt.core

import com.openavt.core.assets.DummyBackend
import com.openavt.core.assets.DummyTracker
import com.openavt.core.hubs.OAVTHubCoreAds
import org.junit.Assert
import org.junit.Test

class AdsUnitTest {
    private var instrument: OAVTInstrument
    private var trackerId: Int

    //TODO: add a dummy Ads tracker

    init {
        instrument = OAVTInstrument(OAVTHubCoreAds(), DummyBackend())
        trackerId = instrument.addTracker(DummyTracker())
    }

    @Test
    fun trackedId_integrity() {
        Assert.assertEquals(trackerId, instrument.getTracker(trackerId)!!.trackerId)
        //TODO: check dummy ads tracker ID
    }

    @Test
    fun basic_ads_workflow() {
        //instrument.emit(OAVTAction.Ping, trackerId)
    }
}