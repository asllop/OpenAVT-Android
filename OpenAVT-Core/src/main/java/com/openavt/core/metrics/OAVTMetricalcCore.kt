package com.openavt.core.metrics

import com.openavt.core.OAVTInstrument
import com.openavt.core.interfaces.OAVTMetricalcInterface
import com.openavt.core.interfaces.OAVTTrackerInterface
import com.openavt.core.models.OAVTAction
import com.openavt.core.models.OAVTAttribute
import com.openavt.core.models.OAVTEvent
import com.openavt.core.models.OAVTMetric

/**
 * Metricalc core.
 */
class OAVTMetricalcCore: OAVTMetricalcInterface {
    override fun processMetric(event: OAVTEvent, tracker: OAVTTrackerInterface): Array<OAVTMetric> {
        var metricArray : MutableList<OAVTMetric> = mutableListOf()

        if (event.action == OAVTAction.Start) {
            val timeSinceMediaRequest = event.attributes[OAVTAction.MediaRequest.timeAttribute] as? Int
            val timeSinceStreamLoad = event.attributes[OAVTAction.StreamLoad.timeAttribute] as? Int

            if (timeSinceMediaRequest != null) {
                metricArray.add(OAVTMetric.StartTime(timeSinceMediaRequest))
            }
            else if (timeSinceStreamLoad != null) {
                metricArray.add(OAVTMetric.StartTime(timeSinceStreamLoad))
            }
            metricArray.add(OAVTMetric.NumPlays(1))
        }
        else if (event.action == OAVTAction.BufferFinish) {
            (event.attributes[OAVTAttribute.inPlaybackBlock] as? Boolean)?.let { inPlaybackBlock ->
                (event.attributes[OAVTAttribute.inPauseBlock] as? Boolean)?.let { inPauseBlock ->
                    (event.attributes[OAVTAttribute.inSeekBlock] as? Boolean)?.let { inSeekBlock ->
                        if (inPlaybackBlock && !inPauseBlock && !inSeekBlock) {
                            (event.attributes[OAVTAction.BufferBegin.timeAttribute] as? Int)?.let { timeSinceBufferBegin ->
                                metricArray.add(OAVTMetric.RebufferTime(timeSinceBufferBegin))
                                metricArray.add(OAVTMetric.NumRebuffers(1))
                            }
                        }
                    }
                }
            }
        }
        else if (event.action == OAVTAction.MediaRequest) {
            metricArray.add(OAVTMetric.NumRequests(1))
        }
        else if (event.action == OAVTAction.StreamLoad) {
            metricArray.add(OAVTMetric.NumLoads(1))
        }
        else if (event.action == OAVTAction.End || event.action == OAVTAction.Stop || event.action == OAVTAction.Next)  {
            metricArray.add(OAVTMetric.NumEnds(1))
        }

        (event.attributes[OAVTAttribute.deltaPlayTime] as? Int)?.let { deltaPlayTime ->
            metricArray.add(OAVTMetric.PlayTime(deltaPlayTime))
        }

        return metricArray.toTypedArray()
    }

    override fun instrumentReady(instrument: OAVTInstrument) {
    }

    override fun endOfService() {
    }
}