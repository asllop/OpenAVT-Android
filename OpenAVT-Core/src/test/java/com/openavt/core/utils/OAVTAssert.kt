package com.openavt.core.utils

import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTState
import org.junit.Assert

class OAVTAssert {
    companion object {
        fun assertEquals(expected: Long, actual: Long, delta: Long) {
            Assert.assertEquals(expected.toDouble(), actual.toDouble(), delta.toDouble())
        }

        fun assertStates(tracker: OAVTTrackerInterface, compareState: OAVTState) {
            Assert.assertEquals(tracker.state.didMediaRequest, compareState.didMediaRequest)
            Assert.assertEquals(tracker.state.didPlayerSet, compareState.didPlayerSet)
            Assert.assertEquals(tracker.state.didStreamLoad, compareState.didStreamLoad)
            Assert.assertEquals(tracker.state.didStart, compareState.didStart)
            Assert.assertEquals(tracker.state.isBuffering, compareState.isBuffering)
            Assert.assertEquals(tracker.state.isPaused, compareState.isPaused)
            Assert.assertEquals(tracker.state.isSeeking, compareState.isSeeking)
            Assert.assertEquals(tracker.state.didFinish, compareState.didFinish)
            Assert.assertEquals(tracker.state.inAdBreak, compareState.inAdBreak)
            Assert.assertEquals(tracker.state.inAd, compareState.inAd)
        }
    }
}