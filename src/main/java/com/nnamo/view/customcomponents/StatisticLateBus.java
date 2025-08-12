package com.nnamo.view.customcomponents;

import com.google.transit.realtime.GtfsRealtime;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class StatisticLateBus extends StatisticUnit {
    private final static Duration LATE_THRESHOLD_DURATION = Duration.ofMinutes(2);

    public StatisticLateBus() {
        super("Late Bus", "Buses", new Color(244, 67, 54)); // Material Red 500
    }

    @Override
    public void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (entity.hasTripUpdate() && entity.getTripUpdate().getVehicle() != null) {
                GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                if (tripUpdate.hasDelay()) {
                    Duration delayDuration = Duration.ofSeconds(tripUpdate.getDelay());
                    // If the bus is late (delay > 2 minutes), we count it
                    if (delayDuration.compareTo(LATE_THRESHOLD_DURATION) > 0) {
                        busCount++;
                    }
                }
            }
        }
        setValue(String.valueOf(busCount));
        repaint(); // Refresh the display
    }
}
