package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;

import java.util.List;

public interface StatisticsBehaviour {
    void updateStatistics(List<FeedEntity> tripEntities);
}
