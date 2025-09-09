package com.nnamo.controllers;

import com.google.transit.realtime.GtfsRealtime;
import com.nnamo.enums.Direction;
import com.nnamo.interfaces.WaypointBehaviour;
import com.nnamo.models.StaticVehiclePosition;
import com.nnamo.models.StopModel;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.view.frame.MainFrame;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for managing map-related functionalities,
 * including waypoint interactions, route rendering, and vehicle position updates.
 * It interacts with the DatabaseService, MainFrame, and RealtimeGtfsService
 * to provide a seamless user experience on the map interface.
 *
 * @author Samuele Lombardi
 * @author Riccardo Finocchiaro
 * @see DatabaseService
 * @see MainFrame
 * @see RealtimeGtfsService
 */
public class MapController {
    private final DatabaseService db;
    private final MainFrame mainFrame;
    private UserModel sessionUser;
    private final RealtimeGtfsService realtimeService;

    // CONSTRUCTORS //
    /**
     * Creates a {@link MapController} with the specified {@link DatabaseService}, {@link MainFrame}, and {@link RealtimeGtfsService}.
     *
     * @param db              the database service for data access
     * @param mainFrame       the main application frame for UI interactions
     * @param realtimeService the real-time GTFS service for fetching live data
     * @see DatabaseService
     * @see MainFrame
     * @see RealtimeGtfsService
     */
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

    /**
     * Calculate the geographical barycenter of a list of stops.
     *
     * @param stopList List of StopModel objects representing the stops.
     * @return GeoPosition representing the calculated barycenter.
     * @throws ArithmeticException if the stopList is empty.
     */
    public static GeoPosition calculateBarycenter(List<StopModel> stopList) {
        if (stopList.isEmpty()) {
            throw new ArithmeticException("Cannot calculate barycenter for an empty list of stops.");
        }
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

    /**
     * Calculate an appropriate zoom level based on the geographical spread of a list of stops.
     *
     * @param stopList List of StopModel objects representing the stops.
     * @return An integer representing the calculated zoom level.
     */
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

        System.out.println(absoluteDiff);

        if (absoluteDiff > 0.12) {
            zoomLevel = 7;
        } else if (absoluteDiff > 0.050) {
            zoomLevel = 6;
        } else if (absoluteDiff > 0.030) {
            zoomLevel = 5;
        } else if (absoluteDiff > 0.013) {
            zoomLevel = 4;
        } else if (absoluteDiff > 0.010) {
            zoomLevel = 3;
        } else if (absoluteDiff > 0.009) {
            zoomLevel = 2;
        } else {
            zoomLevel = 1;
        }
        return zoomLevel;
    }

    public static void renderRoutesVehicles(List<StopModel> stopModels, RealtimeGtfsService realtimeService,
                                            MainFrame mainFrame, DatabaseService db, String routeId, Direction direction) throws SQLException {
        GeoPosition geoPosition = MapController.calculateBarycenter(stopModels);
        int zoomLevel = MapController.calculateZoomLevel(stopModels);

        List<GtfsRealtime.VehiclePosition> routePositions = realtimeService
                .getRoutesVehiclePositions(routeId, direction);
        List<StaticVehiclePosition> staticPositions = new ArrayList<>();
        staticPositions.add(db.getStaticPosition(routeId, direction, LocalTime.now()));

        // render stops and route lines on the map
        mainFrame.renderRouteLines(stopModels, routePositions, staticPositions, routeId, geoPosition, zoomLevel);
    }

    public static void updateVehiclePositions(String routeId, MainFrame mainFrame,
                                              RealtimeGtfsService realtimeService, DatabaseService db) throws SQLException {
        List<GtfsRealtime.VehiclePosition> realtimePositions = realtimeService.getRoutesVehiclePositions(routeId);
        List<StaticVehiclePosition> staticPositions = new ArrayList<>();
        staticPositions.add(db.getStaticPosition(routeId, Direction.INBOUND, LocalTime.now()));
        mainFrame.renderVehiclePositions(realtimePositions, staticPositions); // Update vehicle positions
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
                        GeoPosition stopPosition = new GeoPosition(stop.getLatitude(), stop.getLongitude());
                        Point2D stopPixel = mainFrame.getMapPanel().getMap().convertGeoPositionToPoint(stopPosition);
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
                                System.out.println("icon clicked");
                                UIController.handleStopSelection(stop, sessionUser, stopPosition, realtimeService,
                                        mainFrame, db);
                                return;
                            }
                        }
                    }

                    mainFrame.updateStopPanelVisibility(false);
                    mainFrame.updatePreferBarVisibility(false);
                    mainFrame.setCurrentStop(null, null); // Current stop id needs to get removed, since no stop is
                    // still
                    // seleced
                    mainFrame.repaintMap();
                } else {
                    mainFrame.updateStopPanelVisibility(false);
                }
            }
        };
        mainFrame.setClickWaypointBehaviour(clickWaypointBehaviour);
    }
}
