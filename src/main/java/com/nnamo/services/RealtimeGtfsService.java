package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.RealtimeStatusChangeListener;
import com.nnamo.models.RealtimeStopUpdate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RealtimeGtfsService {
    public static final String TRIP_FEED_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    public static final String POSITIONS_FEED_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";
    public static final Duration POLLING_INTERVAL = Duration.ofSeconds(30);

    private final Thread backgroundThread;
    private RealtimeStatus realtimeStatus = RealtimeStatus.ONLINE;
    private RealtimeStatusChangeListener statusChangeListener; // Listener for when status changes automatically (and
                                                               // not by pressing button in mainframe)

    private final URL tripFeedUrl;
    private final URL positionsFeedUrl;
    private final List<FeedUpdateListener> feedUpdateListeners = new ArrayList<>();
    private final List<FeedStopLinesListener> feedStopLinesListeners = new ArrayList<>();
    private List<FeedEntity> tripEntityList;
    private List<FeedEntity> positionEntityList;
    private HashMap<String, FeedEntity> tripsMap = new HashMap<>();
    private HashMap<String, List<VehiclePosition>> routesPositionsMap = new HashMap<>();
    private HashMap<String, List<RealtimeStopUpdate>> stopsMap = new HashMap<>();

    public RealtimeGtfsService() throws URISyntaxException, IOException {
        this.tripFeedUrl = new URI(TRIP_FEED_URL).toURL();
        this.positionsFeedUrl = new URI(POSITIONS_FEED_URL).toURL();

        this.backgroundThread = new Thread(() -> {
            try {
                while (true) {
                    try {
                        if (realtimeStatus == RealtimeStatus.ONLINE) {
                            System.out.println("Updating trip feed from " + tripFeedUrl);
                            System.out.println("Updating vehicle feed from " + positionsFeedUrl);
                            updateFeed();
                            System.out.println("Feed updated successfully. Waiting 30s for the next update...");
                        }
                    } catch (IOException e) {
                        System.err.println("Error updating feed: " + e.getMessage() + ". Switching to offline");
                        this.setRealtimeStatus(RealtimeStatus.OFFLINE); // If Realtime is unavailable, switch to offline

                        if (statusChangeListener != null) {
                            this.statusChangeListener.onChange(RealtimeStatus.OFFLINE);
                        }
                    } finally {
                        Thread.sleep(POLLING_INTERVAL);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });
    }

    public void startBackgroundThread() {
        if (backgroundThread == null) {
            System.out.println("Couldn't start Realtime Thread");
            return;
        }
        backgroundThread.setName("RealtimeGtfsService-BackgroundThread");
        backgroundThread.setDaemon(true); // Set as a daemon thread to not block JVM exit
        backgroundThread.start();
        this.realtimeStatus = RealtimeStatus.ONLINE;
        System.out.println("Starting background thread for RealtimeGtfsService");
    }

    public synchronized void setRealtimeStatus(RealtimeStatus newStatus) {
        this.realtimeStatus = newStatus;
        switch (newStatus) {
            case ONLINE:
                System.out.println("Realtime status switched to ONLINE");
                break;
            case OFFLINE:
                System.out.println("Realtime status switched to OFFLINE");
                if (this.tripEntityList != null) {
                    this.tripEntityList = new ArrayList<>();
                    this.tripsMap.clear();
                    this.stopsMap.clear();
                    this.routesPositionsMap.clear();
                }
                break;
        }
    }

    public RealtimeStatus getRealtimeStatus() {
        return this.realtimeStatus;
    }

    public synchronized void addListener(FeedUpdateListener listener) {
        feedUpdateListeners.add(listener);
    }

    public synchronized void removeListener(FeedUpdateListener listener) {
        feedUpdateListeners.remove(listener);
    }

    public synchronized void addListener(FeedStopLinesListener listener) {
        feedStopLinesListeners.add(listener);
    }

    public synchronized void removeListener(FeedStopLinesListener listener) {
        feedStopLinesListeners.remove(listener);
    }

    private void notifyFeedUpdateListeners() {
        for (FeedUpdateListener listener : feedUpdateListeners) {
            listener.onFeedUpdated(tripEntityList);
        }
    }

    private void notifyNewStopLineListeners(GtfsRealtime.FeedEntity entity) {
        for (FeedStopLinesListener listener : feedStopLinesListeners) {
            listener.onNewStopLine(entity);
        }
    }

    private void notifyStopLineRemovedListeners(String tripLineId) {
        for (FeedStopLinesListener listener : feedStopLinesListeners) {
            listener.onStopLineRemoved(tripLineId);
        }
    }

    private void notifyStopLineUpdatedListeners(GtfsRealtime.FeedEntity entity) {
        for (FeedStopLinesListener listener : feedStopLinesListeners) {
            listener.onStopLineUpdated(entity);
        }
    }

    public synchronized void updateFeed() throws IOException {
        // all previous entities ids hashset
        HashSet<String> previousEntityIds = new HashSet<>();
        if (tripEntityList != null) {
            for (FeedEntity entity : tripEntityList) {
                previousEntityIds.add(entity.getId());
            }
        }

        InputStream tripStream = tripFeedUrl.openStream();
        FeedMessage tripFeed = FeedMessage.parseFrom(tripStream);

        InputStream positionStream = positionsFeedUrl.openStream();
        FeedMessage positionFeed = FeedMessage.parseFrom(positionStream);

        this.tripEntityList = tripFeed.getEntityList();
        this.tripsMap = new HashMap<>();
        this.stopsMap = new HashMap<>();
        this.routesPositionsMap = new HashMap<>();

        for (FeedEntity entity : tripEntityList) {
            TripUpdate tripUpdate = entity.getTripUpdate();
            String tripId = tripUpdate.getTrip().getTripId();
            String routeId = tripUpdate.getTrip().getRouteId();
            tripsMap.put(tripId, entity);

            for (StopTimeUpdate stopTime : tripUpdate.getStopTimeUpdateList()) {
                String stopId = stopTime.getStopId();
                RealtimeStopUpdate stopUpdate = new RealtimeStopUpdate(tripId, stopTime, entity.getVehicle());

                // Creates ArrayList if it does not exist for the stopId, and adds the stop
                // update
                stopsMap.computeIfAbsent(stopId, k -> new ArrayList<>()).add(stopUpdate);
            }
            // TODO: logic to notify listeners about new or updated or removed stop lines
        }
        System.out.println(tripEntityList.size() + " trip entities");

        this.positionEntityList = positionFeed.getEntityList();
        for (FeedEntity entity : positionEntityList) {
            String routeId = entity.getVehicle().getTrip().getRouteId();
            if (routeId.isEmpty()) {
                continue;
            }
            routesPositionsMap.computeIfAbsent(routeId, x -> new ArrayList<>()).add(entity.getVehicle());
        }
        System.out.println(positionEntityList.size() + " vehicle position entities");

        notifyFeedUpdateListeners();
    }

    public synchronized VehiclePosition getTripVehiclePosition(String tripId) {
        return this.tripsMap.get(tripId).getVehicle();
    }

    public synchronized List<VehiclePosition> getRoutesVehiclePositions(String routeId) {
        List<VehiclePosition> routePositions = new ArrayList<>();
        List<VehiclePosition> routesFeed = this.routesPositionsMap.get(routeId);
        if (routesFeed == null) {
            System.out.println("No feed found for route " + routeId);
            return routePositions;
        }

        for (VehiclePosition vehiclePosition : routesFeed) {
            System.out.println("Found vehicle position for route " + routeId);
            routePositions.add(vehiclePosition);
        }
        return routePositions;
    }

    public synchronized List<VehiclePosition> getAllVehiclePositions() {
        ArrayList<VehiclePosition> positions = new ArrayList<>();
        for (FeedEntity entity : tripEntityList) {
            positions.add(entity.getVehicle());
        }
        return positions;
    }

    public synchronized FeedEntity getEntityByTripId(String tripId) {
        return tripsMap.get(tripId);
    }

    public synchronized List<RealtimeStopUpdate> getStopUpdatesById(String stopId) {
        return stopsMap.getOrDefault(stopId, new ArrayList<>());
    }

    public void setRealtimeChangeListener(RealtimeStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }
}
