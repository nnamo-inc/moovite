package com.nnamo.view.customcomponents;

import com.google.transit.realtime.GtfsRealtime;

import java.util.List;

public class StatisticLateBus extends StatisticUnit {
    public StatisticLateBus() {
        super("Late Bus", "Buses");
    }

    @Override
    public void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (entity.hasTripUpdate() && entity.getTripUpdate().getVehicle() != null) {
                busCount++;
            }
        }
        setValue(String.valueOf(busCount));
        repaint(); // Refresh the display
    }
}
