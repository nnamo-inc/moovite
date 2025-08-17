package com.nnamo.controllers;

import com.nnamo.enums.DataType;
import com.nnamo.interfaces.WaypointBehaviour;
import com.nnamo.models.*;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.utils.Utils;
import com.nnamo.view.frame.MainFrame;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.jxmapviewer.viewer.GeoPosition;

public class MapController {
    private final DatabaseService db;
    private final MainFrame mainFrame;
    private UserModel sessionUser;
    private final RealtimeGtfsService realtimeService;

    public MapController(DatabaseService db, MainFrame mainFrame, RealtimeGtfsService realtimeService) {
        this.db = db;
        this.mainFrame = mainFrame;
        this.realtimeService = realtimeService;
    }

    public void run() {
        setupWaypointBehavior();
    }

    public void setSessionUser(UserModel sessionUser) {
        this.sessionUser = sessionUser;
    }

    public void setupWaypointBehavior() {
        mainFrame.setClickWaypointBehaviour((geo, b) -> {
            if (geo != null) {
                handleStopClick(geo);
            } else {
                mainFrame.updateStopPanelVisibility(false);
            }
        });
    }

    public static GeoPosition calculateBaricentro(List<StopModel> stopList) {
        double latStop = 0;
        double lonStop = 0;
        int divider = 0;

        for (StopModel stop : stopList) {
            divider++;
            latStop += stop.getLatitude();
            lonStop += stop.getLongitude();
        }
        return new GeoPosition(latStop / divider, lonStop / divider);
    }

    public static int calculateZoomLevel(List<StopModel> stopList) {
        int zoomLevel;
        double topPosition = Double.MIN_VALUE;
        double bottomPosition = Double.MAX_VALUE;
        double leftPosition = Double.MAX_VALUE;
        double rightPosition = Double.MIN_VALUE;

        for (StopModel stop : stopList) {
            topPosition = Math.max(topPosition, stop.getLatitude());
            bottomPosition = Math.min(bottomPosition, stop.getLatitude());
            leftPosition = Math.min(leftPosition, stop.getLongitude());
            rightPosition = Math.max(rightPosition, stop.getLongitude());
        }

        double latDiff = topPosition - bottomPosition;
        double lonDiff = rightPosition - leftPosition;
        double absoluteDiff = Math.max(latDiff, lonDiff);

        if (absoluteDiff > 0.12) {
            zoomLevel = 7; // zoom molto lontano per route molto estese
        } else if (absoluteDiff > 0.050) {
            zoomLevel = 6; // zoom molto lontano per route molto estese
        } else if (absoluteDiff > 0.030) {
            zoomLevel = 5; // zoom intermedio
        } else if (absoluteDiff > 0.013) {
            zoomLevel = 4; // zoom più vicino per route medie
        } else if (absoluteDiff > 0.010) {
            zoomLevel = 3; // zoom più vicino per route piccole
        } else if (absoluteDiff > 0.009) {
            zoomLevel = 2; // zoom molto vicino per route piccole
        } else {
            zoomLevel = 1; // zoom molto vicino per route piccolissime
        }
        return zoomLevel;
    }

    private void updateStopPanel(StopModel stop, List<StopTimeModel> stopTimes,
            List<RealtimeStopUpdate> realtimeUpdates) throws SQLException, IOException {
        UIController.updateStopPanel(stop, stopTimes, realtimeUpdates, this.mainFrame, this.db);
    }

    private void updatePreferButton(String itemId, boolean isFav, DataType dataType) throws SQLException, IOException {
        UIController.updatePreferButton(itemId, isFav, dataType, this.mainFrame);
    }

    private void handleStopClick(GeoPosition geo) {
        WaypointBehaviour clickWaypointBehaviour = new WaypointBehaviour() {
            @Override
            public void onWaypointClick(GeoPosition geo, boolean b) throws SQLException, IOException {
                // Convert the GeoPosition of the click to pixel coordinates
                // then get the current icon from the StopPainter
                if (geo != null) {
                    Point2D clickPixel = mainFrame.getMapPanel().getMap().convertGeoPositionToPoint(geo);
                    BufferedImage currentIcon = mainFrame.getCurrentStopIcon();
                    if (currentIcon == null) {
                        return;
                    }
                    // For each stop in the database, create and then convert the GeoPosition to
                    // pixel coordinates
                    // then check if the click position is inside the icon bounds
                    for (StopModel stop : db.getAllStops()) {
                        GeoPosition stopGeo = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                        Point2D stopPixel = mainFrame.getMapPanel().getMap().convertGeoPositionToPoint(stopGeo);
                        // Get icon width and height
                        int iconWidth = currentIcon.getWidth();
                        int iconImgHeight = currentIcon.getHeight();
                        // Get icon pixel pointer position
                        int iconPointerWidth = iconWidth / 2;
                        int iconPointerHeight = iconImgHeight;
                        // Calculate the click position relative to the icon
                        int clickXIcon = (int) (clickPixel.getX() - (stopPixel.getX() - iconPointerWidth));
                        int clickYIcon = (int) (clickPixel.getY() - (stopPixel.getY() - iconImgHeight));
                        // Check if the click is inside the icon bounds and find witch stop was clicked
                        if (clickXIcon >= 0 && clickXIcon < iconWidth && clickYIcon >= 0
                                && clickYIcon < iconImgHeight) {
                            // Get the pixel color at the click position
                            int argb = currentIcon.getRGB(clickXIcon, clickYIcon);
                            // Create a Color object to check the alpha(transparency) value
                            int alpha = new Color(argb, true).getAlpha();
                            // Check alpha
                            if (alpha > 0) {
                                UIController.handleStopSelection(stop, sessionUser, realtimeService, mainFrame, db);
                                return;
                            }
                        }
                    }

                    mainFrame.updateStopPanelVisibility(false);
                    mainFrame.updatePreferBarVisibility(false);
                    mainFrame.getMapPanel().repaintView();
                } else {
                    mainFrame.updateStopPanelVisibility(false);
                }
            }
        };
        mainFrame.setClickWaypointBehaviour(clickWaypointBehaviour);
    }
}
