package com.openavt.influxdb.backends

import android.os.Handler
import android.os.Looper
import com.github.kittinunf.fuel.httpPost
import com.openavt.core.OAVTInstrument
import com.openavt.core.buffers.OAVTBuffer
import com.openavt.core.buffers.OAVTReservoirBuffer
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.models.OAVTSample
import com.openavt.core.utils.OAVTLog
import java.net.URL
import java.util.*

/**
 * OpenAVT backend for InfluxDB.
 */
open class OAVTBackendInfluxdb(buffer: OAVTBuffer = OAVTReservoirBuffer(500), time: Long = 30, url: URL): OAVTBackendInterface  {
    private val buffer = buffer
    private val time = time
    private val url = url
    private var timer: Timer? = null

    init {
        setupTimer(this.time)
    }

    override fun sendEvent(event: OAVTEvent) {
        if (buffer.put(event)) {
            OAVTLog.verbose("---> OAVTBackendInfluxdb SEND EVENT = $event")
        }
    }

    override fun sendMetric(metric: OAVTMetric) {
        if (buffer.put(metric)) {
            OAVTLog.verbose("---> OAVTBackendInfluxdb SEND METRIC = $metric")
        }
    }

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {
        stopTimer()
        OAVTLog.verbose("Final sync")
        pushMetrics()
    }

    fun buildLineMetrics(samples: Array<OAVTSample>): String {
        var metrics = ""
        for (sample in samples) {
            if (sample is OAVTMetric) {
                metrics += buildMetric(sample) + "\n"
            }
            else if (sample is OAVTEvent) {
                metrics += buildEventMetric(sample) + "\n"
            }
        }
        return metrics
    }

    fun putBackMetrics(samples: Array<OAVTSample>) {
        for (sample in samples) {
            buffer.put(sample)
        }
    }

    open fun buildMetric(metric: OAVTMetric): String {
        return getInfluxDBPath(metric) + " " + metric.name + "=" + metric.value.toString() + " " + (metric.timestamp * 1000000L).toString()
    }

    open fun buildEventMetric(event: OAVTEvent): String {
        // Header
        var line = getInfluxDBPath(event) + " action=\"" + event.action.actionName + "\","
        // Body
        for ((key, value) in event.getDictionary()) {
            if (value is String) {
                line += "$key=\"$value\","
            }
            else {
                //TODO: check if default float representation is OK for InfluxDB
                line += "$key=$value,"
            }
        }
        line = line.dropLast(1)
        // Tail
        line += " " + (event.timestamp * 1000000L).toString()
        return line
    }

    open fun getInfluxDBPath(sample: OAVTSample): String {
        return when (sample) {
            is OAVTMetric -> {
                "OAVT_METRICS"
            }
            is OAVTEvent -> {
                "OAVT_EVENTS"
            }
            else -> {
                "OAVT"
            }
        }
    }

    fun setupTimer(interval: Long) {
        stopTimer()
        this.timer = Timer()
        this.timer!!.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                val handler = Handler(Looper.getMainLooper())
                handler.post(Runnable {
                    pushMetrics()
                })
            }
        }, interval * 1000, interval * 1000)
    }

    fun stopTimer() {
        this.timer?.let {
            it.cancel()
        }
        this.timer = null
    }

    open fun pushMetrics() {
        OAVTLog.verbose("Push Metrics! buffer remaining = " + buffer.remaining())

        val samples = buffer.retrieveInOrder()
        val postString = buildLineMetrics(samples)

        //TODO: if postString is empty, do not make request

        OAVTLog.verbose("---> OAVTBackendInfluxdb PUSH METRICS:")
        OAVTLog.verbose(postString)

        url.toString().httpPost().body(postString).response { request, response, result ->
            OAVTLog.verbose("HTTP RESPONSE:")
            OAVTLog.verbose("   Request " + request)
            OAVTLog.verbose("   Response " + response)
            OAVTLog.verbose("   Result " + result)

            //TODO: in case of error, put back metrics
        }
    }
}