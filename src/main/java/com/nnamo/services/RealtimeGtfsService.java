package com.nnamo.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;

public class RealtimeGtfsService {

    private URL feedUrl;
    private InputStream urlStream;
    private FeedMessage feed;
    private List<FeedEntity> entityList;
    private HashMap<String, FeedEntity> tripsMap;
    private HashMap<String, StopTimeUpdate> stopsMap;

    public RealtimeGtfsService() throws URISyntaxException, MalformedURLException, IOException {
        this.feedUrl = new URI(
                "https://dati.comune.roma.it/catalog/dataset/a7dadb4a-66ae-4eff-8ded-a102064702ba/resource/bf7577b5-ed26-4f50-a590-38b8ed4d2827/download/rome_trip_updates.pb")
                .toURL();
        this.urlStream = feedUrl.openStream();
    }

    public void updateFeed() throws IOException {
        this.feed = FeedMessage.parseFrom(urlStream);
        this.entityList = feed.getEntityList();
        this.tripsMap = new HashMap<>();

        for (FeedEntity entity : entityList) {
            tripsMap.put(entity.getTripUpdate().getTrip().getTripId(), entity);
        }
        System.out.println(entityList.size() + " entities");

        // TODO notify observers (we need to see how to architecture this part: idk if
        // its
        // better to do observer pattern or something else)
    }

    public void load() throws IOException {
        updateFeed();
    }

    public FeedEntity getEntityByTripId(String tripId) {
        return tripsMap.get(tripId);
    }
}
