package com.nnamo.view.customcomponents.statistic;

import com.nnamo.enums.RealtimeMetricType;

public interface MetricCollector {
    void onProducedMetric(RealtimeMetricType type, int value);
}
