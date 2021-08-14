package com.openavt.core.utils

import org.junit.Assert

class OAVTAssert {
    companion object {
        fun assertEquals(expected: Long, actual: Long, delta: Long) {
            Assert.assertEquals(expected.toDouble(), actual.toDouble(), delta.toDouble())
        }
    }
}