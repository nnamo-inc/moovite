package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

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
    public static final String FEED_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_trip_updates_feed.pb";
    public static final Duration POLLING_INTERVAL = Duration.ofSeconds(30);

    private final URL feedUrl;
    private final List<FeedUpdateListener> feedUpdateListeners = new ArrayList<>();
    private final List<FeedStopLinesListener> feedStopLinesListeners = new ArrayList<>();
    private List<FeedEntity> entityList;
    private HashMap<String, FeedEntity> tripsMap = new HashMap<>();
    private HashMap<String, List<RealtimeStopUpdate>> stopsMap = new HashMap<>();

    public RealtimeGtfsService() throws URISyntaxException, IOException {
        this.feedUrl = new URI(FEED_URL).toURL();
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
            listener.onFeedUpdated(entityList);
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
        if (entityList != null) {
            for (FeedEntity entity : entityList) {
                previousEntityIds.add(entity.getId());
            }
        }

        InputStream stream = feedUrl.openStream();
        FeedMessage feed = FeedMessage.parseFrom(stream);
        this.entityList = feed.getEntityList();
        this.tripsMap = new HashMap<>();
        this.stopsMap = new HashMap<>();

        for (FeedEntity entity : entityList) {
            TripUpdate tripUpdate = entity.getTripUpdate();
            String tripId = tripUpdate.getTrip().getTripId();
            tripsMap.put(tripId, entity);

            for (StopTimeUpdate stopTime : tripUpdate.getStopTimeUpdateList()) {
                String stopId = stopTime.getStopId();
                String routeId = tripUpdate.getTrip().getRouteId();
                System.out.println("Adding stop time for stop " + stopId + ": trip " + tripId);
                RealtimeStopUpdate stopUpdate = new RealtimeStopUpdate(tripId, stopTime);

                // Creates ArrayList if it does not exist for the stopId, and adds the stop
                // update
                stopsMap.computeIfAbsent(stopId, k -> new ArrayList<>()).add(stopUpdate);
            }

            // TODO: logic to notify listeners about new or updated or removed stop lines
        }
        System.out.println(entityList.size() + " entities");

        notifyFeedUpdateListeners();
    }

    public void startBackgroundThread() {
        Thread backgroundThread = new Thread(() -> {
            try {
                while (true) {
                    try {
                        System.out.println("Updating feed from " + feedUrl);
                        updateFeed();
                        System.out.println("Feed updated successfully. Waiting 30s for the next update...");
                    } catch (IOException e) {
                        System.err.println("Error updating feed: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        Thread.sleep(POLLING_INTERVAL);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        });
        backgroundThread.setName("RealtimeGtfsService-BackgroundThread");
        backgroundThread.setDaemon(true); // Set as a daemon thread to not block JVM exit
        System.out.println("Starting background thread for RealtimeGtfsService");
        backgroundThread.start();
    }

    public synchronized FeedEntity getEntityByTripId(String tripId) {
        return tripsMap.get(tripId);
    }

    public synchronized List<RealtimeStopUpdate> getStopUpdatesById(String stopId) {
        return stopsMap.getOrDefault(stopId, new ArrayList<>());
    }
}
