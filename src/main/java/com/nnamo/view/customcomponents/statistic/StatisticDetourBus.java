package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;

import java.awt.*;
import java.util.List;

public class StatisticDetourBus extends StatisticUnit {
    public StatisticDetourBus() {
        super("Detour Bus", "Buses", new Color(255, 193, 7)); // Material Yellow 500
    }

    @Override
    public void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
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
        setValue(String.valueOf(busCount));
        repaint(); // Refresh the display
    }
}
