package com.nnamo.view.customcomponents.statistic;

import com.nnamo.enums.RealtimeMetricType;

public interface StatisticUpdateListener {
    void onStatisticUpdated(RealtimeMetricType type, int value);
}
