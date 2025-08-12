package com.nnamo.view.customcomponents;

import com.google.transit.realtime.GtfsRealtime;

import java.util.List;

public class StatisticStoppedBus extends StatisticUnit {
    public StatisticStoppedBus() {
        super("Stopped Bus", "Buses");
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
