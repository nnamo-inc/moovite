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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import org.jxmapviewer.viewer.GeoPosition;

/**
 * Controller responsible for managing real-time functionalities,
 * including handling real-time data updates, user interactions with
 * the real-time switch, and updating the UI based on real-time status changes.
 * It interacts with the DatabaseService, MainFrame, and RealtimeGtfsService
 * to provide a seamless user experience with real-time data.
 *
 * @see DatabaseService
 * @see MainFrame
 * @see RealtimeGtfsService
 *
 * @author Samuele Lombardi
 */
public class RealtimeController {
    private final DatabaseService db;
    private final MainFrame mainFrame;
    private RealtimeGtfsService realtimeService;
    private UserModel sessionUser;

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
                        GeoPosition stopPosition = mainFrame.getCurrentStopPosition();
                        int nextHoursRange = 6;
                        StopModel stop = db.getStopById(stopId);
                        UIController.handleStopSelection(stop, sessionUser, stopPosition, realtimeService, mainFrame,
                                db);
                        System.out.println("Updated realtime details on feed update on stop " + stopId);
                    }

                    // Updates route vehicle positions
                    String routeId = mainFrame.getCurrentRouteId();
                    if (routeId != null && !routeId.isEmpty()) {
                        MapController.updateVehiclePositions(routeId, mainFrame, realtimeService, db);
                        System.out.println("Updated realtime vehicle positions on feed update on route " + routeId);
                    }
                } catch (SQLException | IOException e) {
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

    public void setUser(UserModel sessionUser) {
        this.sessionUser = sessionUser;
    }
}
