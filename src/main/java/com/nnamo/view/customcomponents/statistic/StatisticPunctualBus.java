package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class StatisticPunctualBus extends StatisticUnit {
    private final static Duration PUNCTUAL_THRESHOLD_DURATION = Duration.ofMinutes(2);

    public StatisticPunctualBus() {
        super("Punctual Bus", "Buses", new Color(76, 175, 80)); // Material Green 500
    }

    @Override
    public void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (entity.hasTripUpdate() && entity.getTripUpdate().getVehicle() != null) {
                GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                if (tripUpdate.hasDelay()) {
                    Duration delayDuration = Duration.ofSeconds(tripUpdate.getDelay());
                    // if between -2 and 2 minutes, we count it as punctual
                    if (delayDuration.compareTo(PUNCTUAL_THRESHOLD_DURATION.negated()) >= 0 &&
                        delayDuration.compareTo(PUNCTUAL_THRESHOLD_DURATION) <= 0) {
                        busCount++;
                    }
                }
            }
        }
        setValue(String.valueOf(busCount));
        saveMetricToDatabase(busCount);
        repaint(); // Refresh the display
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.PUNCTUAL_BUS;
    }
}
