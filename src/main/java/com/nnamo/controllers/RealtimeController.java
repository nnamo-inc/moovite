package com.nnamo.controllers;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.RealtimeStatusChangeListener;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.models.*;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.FeedUpdateListener;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.services.StatisticsBehaviour;
import com.nnamo.utils.Utils;
import com.nnamo.view.frame.MainFrame;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

public class RealtimeController {
    private final DatabaseService db;
    private final MainFrame mainFrame;
    private RealtimeGtfsService realtimeService;

    public RealtimeController(DatabaseService db, MainFrame mainFrame, RealtimeGtfsService realtimeService) {
        this.db = db;
        this.realtimeService = realtimeService;
        this.mainFrame = mainFrame;
    }

    public void run() {
        setupRealtimeBehaviour();
    }

    // BEHAVIOUR //
    private void setupRealtimeBehaviour() {
        // Listener for when Realtime Service changes status
        realtimeService.setRealtimeChangeListener(new RealtimeStatusChangeListener() {
            @Override
            public void onChange(RealtimeStatus newStatus) {
                mainFrame.setRealtimeStatus(newStatus);
            }
        });

        realtimeService.addListener(new FeedUpdateListener() {
            @Override
            public void onFeedUpdated(List<FeedEntity> entities) {
                try {

                    // Updates stop panel details
                    String stopId = mainFrame.getCurrentStopId();
                    if (stopId != null && !stopId.isEmpty()) {
                        List<StopTimeModel> stopTimes = db.getNextStopTimes(stopId, Utils.getCurrentTime(),
                                Utils.getCurrentDate());
                        List<RealtimeStopUpdate> realtimeUpdates = realtimeService.getStopUpdatesById(stopId);
                        mainFrame.updateStopPanelTimes(stopTimes, realtimeUpdates);
                        System.out.println("Updated realtime details on feed update on stop " + stopId);
                    }

                    // Updates route vehicle positions
                    String routeId = mainFrame.getCurrentRouteId();
                    if (routeId != null && !routeId.isEmpty()) {
                        var positions = realtimeService.getRoutesVehiclePositions(routeId);
                        mainFrame.renderVehiclePositions(positions); // Update vehicle positions
                        System.out.println("Updated realtime vehicle positions on feed update on route " + routeId);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        realtimeService.setStatisticsBehavior(new StatisticsBehaviour() {
            @Override
            public void updateStatistics(List<FeedEntity> tripEntities) {
                try {
                    db.createTripUpdateDelays(tripEntities);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });

        // Listener for switch button in MainFrame
        mainFrame.setRealtimeSwitchListener(new SwitchBarListener() {
            @Override
            public void onSwitch(RealtimeStatus newStatus) {
                realtimeService.setRealtimeStatus(newStatus);
                if (newStatus == RealtimeStatus.OFFLINE) {
                    JOptionPane.showMessageDialog(null, "Couldn't connect to realtime feed. Switching to offline...",
                            "Switching to offline...",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
