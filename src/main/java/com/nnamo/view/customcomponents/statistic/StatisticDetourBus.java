package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.services.FeedUpdateListener;

import java.awt.*;
import java.util.List;

/**
 * Statistic unit that counts the number of buses with detour alerts.
 * Extends {@link StatisticUnit} to display the count of detour buses.
 * Implements {@link FeedUpdateListener} to update the count based on feed data.
 *
 * @author Davide Galilei
 * @see StatisticUnit
 * @see FeedUpdateListener
 */
public class StatisticDetourBus extends StatisticUnit {
    public StatisticDetourBus() {
        super("Detour Bus", "Buses", new Color(255, 193, 7)); // Material Yellow 500
    }

    @Override
    public int computeMetric(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (!entity.hasAlert()) continue;
            GtfsRealtime.Alert alert = entity.getAlert();
            if (!alert.hasEffect()) continue;
            GtfsRealtime.Alert.Effect effect = alert.getEffect();
            // Count the bus if it has a detour alert
            if (effect == GtfsRealtime.Alert.Effect.DETOUR) {
                busCount++;
            }
        }
        return busCount;
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.DETOUR_BUS;
    }
}
