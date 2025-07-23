package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
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
    public static final String FEED_URL = "https://dati.comune.roma.it/catalog/dataset/a7dadb4a-66ae-4eff-8ded-a102064702ba/resource/bf7577b5-ed26-4f50-a590-38b8ed4d2827/download/rome_trip_updates.pb";
    public static final Duration POLLING_INTERVAL = Duration.ofSeconds(35);

    private final URL feedUrl;
    private final List<FeedUpdateListener> feedUpdateListeners = new ArrayList<>();
    private final List<FeedStopLinesListener> feedStopLinesListeners = new ArrayList<>();
    private List<FeedEntity> entityList;
    private HashMap<String, FeedEntity> tripsMap;
    private HashMap<String, StopTimeUpdate> stopsMap;

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

        try (InputStream stream = feedUrl.openStream()) {
            FeedMessage feed = FeedMessage.parseFrom(stream);
            this.entityList = feed.getEntityList();
            this.tripsMap = new HashMap<>();

            for (FeedEntity entity : entityList) {
                String tripId = entity.getTripUpdate().getTrip().getTripId();
                tripsMap.put(tripId, entity);

                // TODO: logic to notify listeners about new or updated or removed stop lines
            }
            System.out.println(entityList.size() + " entities");
        }

        notifyFeedUpdateListeners();
    }

    public void startBackgroundThread() throws IOException {
        Thread backgroundThread = new Thread(() -> {
            try {
                while (true) {
                    try {
                        System.out.println("Updating feed from " + feedUrl);
                        updateFeed();
                    } catch (IOException e) {
                        System.err.println("Error updating feed: " + e.getMessage());
                    } finally {
                        Thread.sleep(POLLING_INTERVAL);
                    }
                }
            } catch (InterruptedException e) {
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
}
