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
 *
 * @param buffer An OAVTBuffer instance. Default OAVTReservoirBuffer with size 500.
 * @param time Push metrics time in seconds. Default 30.
 * @param url InfluxDB URL.
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

    private fun buildLineMetrics(samples: Array<OAVTSample>): String {
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

    /**
     * Build a metric.
     *
     * @param metric An OAVTMetric instance.
     * @return InfluxDB metric.
     */
    open fun buildMetric(metric: OAVTMetric): String {
        return getInfluxDBPath(metric) + " " + metric.name + "=" + metric.value.toString() + " " + (metric.timestamp * 1000000L).toString()
    }

    /**
     * Build an event metric.
     *
     * @param event An OAVTEvent instance.
     * @return InfluxDB metric.
     */
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

    /**
     * Generates the InfluxDB metric path.
     *
     * Overwrite this method in a subclass to provide a custom metric path.
     *
     * @param sample An OAVTSample instance.
     * @return Metric path.
     */
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

    private fun putBackMetrics(samples: Array<OAVTSample>) {
        for (sample in samples) {
            buffer.put(sample)
        }
    }

    private fun setupTimer(interval: Long) {
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

    private fun stopTimer() {
        this.timer?.let {
            it.cancel()
        }
        this.timer = null
    }

    /**
     * Push metrics to InfluxDB server.
     */
    open fun pushMetrics() {
        synchronized(this) {
            OAVTLog.verbose("Push Metrics! buffer remaining = " + buffer.remaining())

            val samples = buffer.retrieveInOrder()
            if (samples.isEmpty()) return
            val postString = buildLineMetrics(samples)

            url.toString().httpPost().body(postString).response { request, response, result ->
                OAVTLog.verbose("HTTP Result " + result)

                val (_, err) = result
                if (err != null) {
                    OAVTLog.error("HTTP Request error, push back metrics")
                    putBackMetrics(samples)
                }
            }
        }
    }
}