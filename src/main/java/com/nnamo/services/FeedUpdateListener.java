package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime;

import java.util.List;

public interface FeedUpdateListener {
    void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities);
}
