package com.openavt.graphite.backends

import android.os.Handler
import android.os.Looper
import com.openavt.core.OAVTInstrument
import com.openavt.core.buffers.OAVTBuffer
import com.openavt.core.buffers.OAVTReservoirBuffer
import com.openavt.core.interfaces.OAVTBackendInterface
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric
import com.openavt.core.models.OAVTSample
import com.openavt.core.utils.OAVTLog
import java.io.PrintWriter
import java.net.Socket
import java.util.*

open class OAVTBackendGraphite(buffer: OAVTBuffer = OAVTReservoirBuffer(500), time: Long = 30, host: String, port: Int = 2003): OAVTBackendInterface {
    private val buffer = buffer
    private val time = time
    private val host = host
    private val port = port
    private var timer: Timer? = null

    init {
        setupTimer(this.time)
    }

    override fun sendEvent(event: OAVTEvent) {}

    override fun sendMetric(metric: OAVTMetric) {
        if (buffer.put(metric)) {
            OAVTLog.verbose("---> OAVTBackendGraphite SEND METRIC = $metric")
        }
    }

    override fun instrumentReady(instrument: OAVTInstrument) {}

    override fun endOfService() {
        stopTimer()
        OAVTLog.verbose("Final sync")
        pushMetrics()
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

    private fun putBackMetrics(samples: Array<OAVTSample>) {
        for (sample in samples) {
            buffer.put(sample)
        }
    }

    /**
     * Build a metric name.
     *
     * Overwrite this method in a subclass to provide a custom metric path.
     *
     * @param metric An OAVTMetric instance.
     * @return Metric path.
     */
    open fun buildMetricName(metric: OAVTMetric): String {
        return "OAVT." + metric.name
    }

    /**
     * Build a metric.
     *
     * Overwrite this method in a subclass to provide a custom metric format.
     *
     * @param metric An OAVTMetric instance.
     * @return Metric.
     */
    open fun buildMetric(metric: OAVTMetric): String {
        return buildMetricName(metric) + " " + metric.value + " " + metric.timestamp / 1000
    }

    /**
     * Push metrics to Graphite server.
     */
    open fun pushMetrics() {
        synchronized(this) {
            OAVTLog.verbose("Push Metrics! buffer remaining = " + buffer.remaining())

            val samples = buffer.retrieveInOrder()
            if (samples.isEmpty()) return

            var graphiteMetrics = ""

            // Convert samples to graphite metrics
            for (sample in samples) {
                if (sample is OAVTMetric) {
                    graphiteMetrics += buildMetric(sample) + "\n"
                }
            }

            Thread {
                OAVTLog.verbose("Connect to Graphite server...")

                try {
                    val client = Socket(this.host, this.port)
                    val output = PrintWriter(client.getOutputStream(), true)

                    OAVTLog.verbose("Send graphite metrics =\n" + graphiteMetrics)

                    output.print(graphiteMetrics)
                    output.flush()
                    client.close()

                    OAVTLog.verbose("Close Graphite connection")
                }
                catch(e: Exception) {
                    OAVTLog.error("Error pushing metrics: " + e)
                    putBackMetrics(samples)
                }
            }.start()
        }
    }
}