package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.services.FeedUpdateListener;

import java.awt.*;
import java.time.Duration;
import java.util.List;

/**
 * Statistic unit that counts the number of early buses with a tolerance threshold.
 * Extends {@link StatisticUnit} to display the count of detour buses.
 * Implements {@link FeedUpdateListener} to update the count based on feed data.
 *
 * @author Davide Galilei
 * @see StatisticUnit
 * @see FeedUpdateListener
 */
public class StatisticEarlyBus extends StatisticUnit {
    private final static Duration EARLY_THRESHOLD_DURATION = Duration.ofMinutes(2);

    public StatisticEarlyBus() {
        super("Early Bus", "Buses", new Color(139, 195, 74)); // Material Light Green 500
    }

    @Override
    public int computeMetric(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (entity.hasTripUpdate() && entity.getTripUpdate().getVehicle() != null) {
                GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                if (tripUpdate.hasDelay()) {
                    Duration delayDuration = Duration.ofSeconds(tripUpdate.getDelay());
                    // If the bus is early (delay < -2 minutes), we count it
                    if (delayDuration.compareTo(EARLY_THRESHOLD_DURATION.negated()) < 0) {
                        busCount++;
                    }
                }
            }
        }
        return busCount;
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.EARLY_BUS;
    }
}
