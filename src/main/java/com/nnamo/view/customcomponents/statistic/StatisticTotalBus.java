package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;

import java.awt.*;
import java.util.List;

public class StatisticTotalBus extends StatisticUnit {
    public StatisticTotalBus() {
        super("Total Bus", "Buses", new Color(33, 150, 243)); // Material Blue 500
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
        saveMetricToDatabase(busCount);
        notifyStatisticUpdateListeners(busCount);
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.TOTAL_BUS;
    }
}
