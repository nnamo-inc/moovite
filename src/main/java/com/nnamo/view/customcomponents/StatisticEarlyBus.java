package com.nnamo.view.customcomponents;

import com.google.transit.realtime.GtfsRealtime;

import java.awt.*;
import java.util.List;

public class StatisticEarlyBus extends StatisticUnit {
    public StatisticEarlyBus() {
        super("Early Bus", "Buses", new Color(139, 195, 74)); // Material Light Green 500
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
