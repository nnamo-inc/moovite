package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime;

public interface FeedStopLinesListener {
    void onNewStopLine(GtfsRealtime.FeedEntity entity);
    void onStopLineRemoved(String tripLineId);
    void onStopLineUpdated(GtfsRealtime.FeedEntity entity);
}
