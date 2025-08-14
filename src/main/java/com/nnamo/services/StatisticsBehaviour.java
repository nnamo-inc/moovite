package com.nnamo.services;

import java.util.List;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;

public interface StatisticsBehaviour {
    void updateStatistics(List<FeedEntity> tripEntities);
}
