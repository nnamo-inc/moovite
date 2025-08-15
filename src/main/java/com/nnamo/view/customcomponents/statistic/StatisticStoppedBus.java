package com.nnamo.view.customcomponents.statistic;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.RealtimeMetricType;

import java.awt.*;
import java.util.List;

public class StatisticStoppedBus extends StatisticUnit {
    public StatisticStoppedBus() {
        super("Stopped Bus", "Buses", new Color(255, 152, 0)); // Material Orange 500
    }

    @Override
    public void onFeedUpdated(List<GtfsRealtime.FeedEntity> entities) {
        int busCount = 0;
        for (GtfsRealtime.FeedEntity entity : entities) {
            if (!entity.hasVehicle()) continue;
            GtfsRealtime.VehiclePosition vehiclePosition = entity.getVehicle();
            if (!vehiclePosition.hasCurrentStatus()) continue;
            GtfsRealtime.VehiclePosition.VehicleStopStatus status = vehiclePosition.getCurrentStatus();
            // Count the bus if it is stopped
            if (status == GtfsRealtime.VehiclePosition.VehicleStopStatus.STOPPED_AT) {
                busCount++;
            }
        }
        setValue(String.valueOf(busCount));
        saveMetricToDatabase(busCount);
        repaint(); // Refresh the display
    }

    public RealtimeMetricType getMetricType() {
        return RealtimeMetricType.STOPPED_BUS;
    }
}
