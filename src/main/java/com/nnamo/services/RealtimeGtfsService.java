package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.RouteQuality;
import com.nnamo.interfaces.RealtimeStatusChangeListener;
import com.nnamo.models.RealtimeStopUpdate;
import com.nnamo.models.StopTimeModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RealtimeGtfsService {
    public static final String ROME_TRIP_FEED_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    public static final String ROME_POSITIONS_FEED_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";
    public static final Duration FEED_INTERVAL = Duration.ofSeconds(30);
    public static final Duration STATISTICS_INTERVAL = Duration.ofMinutes(5);
    public static final int ALLOWED_DELAY = 5 * 60; // 5 minutes
    public static final int ALLOWED_ADVANCE = 5 * 60; // 5 minutes

    private final Thread feedThread;
    private final Thread statisticsThread;
    private RealtimeStatus realtimeStatus = RealtimeStatus.ONLINE;
    private RealtimeStatusChangeListener statusChangeListener; // Listener for when status changes automatically (and
    // not by pressing button in mainframe)
    private final URL tripFeedUrl;
    private final URL positionsFeedUrl;

    private List<FeedEntity> positionEntityList;
    private HashMap<String, List<VehiclePosition>> routesPositionsMap = new HashMap<>(); // Maps Route ID with all its
    // vehicle positions
    private final HashMap<String, VehiclePosition> tripsPositionMap = new HashMap<>(); // Maps a Trip ID with its vehicle
    // position

    private List<FeedEntity> tripEntityList;
    private HashMap<String, FeedEntity> tripsMap = new HashMap<>();
    private HashMap<String, List<RealtimeStopUpdate>> stopsMap = new HashMap<>();

    private final List<FeedUpdateListener> feedUpdateListeners = new ArrayList<>();
    private StatisticsBehaviour statisticsBehaviour;

    public RealtimeGtfsService() throws URISyntaxException, IOException {
        this.tripFeedUrl = new URI(ROME_TRIP_FEED_URL).toURL();
        this.positionsFeedUrl = new URI(ROME_POSITIONS_FEED_URL).toURL();

        this.feedThread = new Thread(() -> {
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
                        Thread.sleep(FEED_INTERVAL);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });
        feedThread.setName("RealtimeGtfsService-BackgroundThread");
        feedThread.setDaemon(true); // Set as a daemon thread to not block JVM exit

        // Thread that updates every STATISTICS_INTERVAL the history of trips in the
        // database
        this.statisticsThread = new Thread(() -> {
            try {
                while (true) {
                    if (realtimeStatus == RealtimeStatus.ONLINE && statisticsBehaviour != null
                            && tripEntityList != null) {
                        statisticsBehaviour.updateStatistics(tripEntityList);
                        System.out.println("Updated trips' history");
                    }
                    Thread.sleep(STATISTICS_INTERVAL);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });
        statisticsThread.setName("Statistics-BackgroundThread");
        statisticsThread.setDaemon(true); // Set as a daemon thread to not block JVM exit
    }

    public void startFeedThread() {
        if (feedThread == null) {
            System.out.println("Couldn't start Realtime Thread");
            return;
        }
        feedThread.start();
        this.realtimeStatus = RealtimeStatus.ONLINE;
        System.out.println("Starting background thread for RealtimeGtfsService");
    }

    public void startStatisticsThread() {
        if (statisticsThread.isAlive()) {
            return;
        }

        if (statisticsThread == null) {
            System.out.println("Couldn't start Statistics Thread");
            return;
        }
        statisticsThread.start();
        System.out.println("Starting background thread for updating statistics in database");
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
                RealtimeStopUpdate stopUpdate = new RealtimeStopUpdate(tripId, routeId, stopTime, entity.getVehicle());

                // Creates ArrayList if it does not exist for the stopId, and adds the stop
                // update
                stopsMap.computeIfAbsent(stopId, k -> new ArrayList<>()).add(stopUpdate);
            }
        }
        System.out.println(tripEntityList.size() + " trip entities");

        this.positionEntityList = positionFeed.getEntityList();
        for (FeedEntity entity : positionEntityList) {
            String tripId = entity.getVehicle().getTrip().getTripId();
            String routeId = entity.getVehicle().getTrip().getRouteId();
            if (routeId.isEmpty()) {
                continue;
            }
            tripsPositionMap.put(tripId, entity.getVehicle());
            // System.out.println(getTripVehiclePosition(tripId) + " vehicle position for
            // trip " + tripId);
            routesPositionsMap.computeIfAbsent(routeId, x -> new ArrayList<>()).add(entity.getVehicle());
        }
        System.out.println(positionEntityList.size() + " vehicle position entities");

        startStatisticsThread(); // Statistics thread starts after first feed update
        notifyFeedUpdateListeners();
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

    public static RouteQuality getRouteQuality(int delay) {
        boolean onTime = (delay >= 0 && delay <= ALLOWED_DELAY) || (delay <= 0 && -delay <= ALLOWED_ADVANCE);
        boolean delayed = delay > 0 && delay >= ALLOWED_DELAY;
        boolean advance = delay < 0 && -delay >= ALLOWED_DELAY;

        if (onTime) {
            return RouteQuality.OFTEN_ON_TIME;
        } else if (delayed) {
            return RouteQuality.OFTEN_DELAYED;
        } else if (advance) {
            return RouteQuality.OFTEN_EARLY;
        }

        return RouteQuality.INVALID;
    }

    public RealtimeStatus getRealtimeStatus() {
        return this.realtimeStatus;
    }

    public void setStatisticsBehavior(StatisticsBehaviour behavior) {
        this.statisticsBehaviour = behavior;
    }

    public synchronized void addListener(FeedUpdateListener listener) {
        feedUpdateListeners.add(listener);
    }

    public synchronized void removeListener(FeedUpdateListener listener) {
        feedUpdateListeners.remove(listener);
    }

    private void notifyFeedUpdateListeners() {
        for (FeedUpdateListener listener : feedUpdateListeners) {
            listener.onFeedUpdated(tripEntityList);
        }
    }

    // Not needed, Realtime GTFS has already a delay method
    public void updateTripMonitoring(List<StopTimeModel> stopTimes) {
        HashMap<String, List<StopTimeModel>> stopTimesMap = new HashMap<>();
        for (StopTimeModel stopTime : stopTimes) {
            stopTimesMap.computeIfAbsent(
                    stopTime.getTrip().getId(),
                    k -> new ArrayList<>()).add(stopTime);
        }

        for (FeedEntity entity : tripEntityList) {
            TripUpdate tripUpdate = entity.getTripUpdate();
            TripDescriptor trip = tripUpdate.getTrip();
            String tripId = trip.getTripId();

            List<StopTimeModel> tripStopTimes = stopTimesMap.get(tripId);
            if (tripStopTimes == null) {
                continue;
            }

            for (StopTimeModel stopTime : tripStopTimes) {
            }
        }
    }

    public synchronized VehiclePosition getTripVehiclePosition(String tripId) {
        return this.tripsPositionMap.get(tripId);
    }

    public synchronized List<VehiclePosition> getRoutesVehiclePositions(String routeId) {
        List<VehiclePosition> routePositions = this.routesPositionsMap.get(routeId);
        if (routePositions == null) {
            System.out.println("No feed found for route " + routeId);
            return new ArrayList<>();
        }
        return routePositions;
    }

    public synchronized List<VehiclePosition> getRoutesVehiclePositions(String routeId, Direction requestedDirection) {
        List<VehiclePosition> positions = getRoutesVehiclePositions(routeId);

        List<VehiclePosition> directedRoutePositions = new ArrayList<>();
        for (VehiclePosition position : positions) {
            String tripId = position.getTrip().getTripId();
            FeedEntity tripEntity = tripsMap.get(tripId);
            TripUpdate tripUpdate = tripEntity.getTripUpdate();
            TripDescriptor trip = tripUpdate.getTrip();

            Direction tripDirection = Direction.getDirection(trip.getDirectionId());
            if (tripDirection == requestedDirection) {
                directedRoutePositions.add(position);
            }
        }

        return directedRoutePositions;
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
