package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.services.FeedUpdateListener;

import java.awt.*;
import java.util.List;

/**
 * Statistic unit that counts the number of the real time buses.
 * Extends {@link StatisticUnit} to display the count of detour buses.
 * Implements {@link FeedUpdateListener} to update the count based on feed data.
 *
 * @see StatisticUnit
 * @see FeedUpdateListener
 *
 * @author Davide Galilei
 */
public class StatisticTotalBus extends StatisticUnit {
    public StatisticTotalBus() {
        super("Total Bus", "Buses", new Color(33, 150, 243)); // Material Blue 500
    }

    @Override
    public int computeMetric(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (entity.hasTripUpdate() && entity.getTripUpdate().getVehicle() != null) {
                busCount++;
            }
        }
        return busCount;
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.TOTAL_BUS;
    }
}
