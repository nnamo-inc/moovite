package com.nnamo.services;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

public class StaticGtfsService {

    private final GtfsRelationalDaoImpl store;

    public StaticGtfsService() {
        GtfsReader reader = new GtfsReader(); // Lettore del buffer
        GtfsRelationalDaoImpl store = new GtfsRelationalDaoImpl(); // API per interagire con i dati letti

        this.store = store;
    }

    public void load() throws IOException, URISyntaxException {
        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(this.store);

        URL staticFeedURL = getClass().getResource("rome_static_gtfs.zip");
        File staticFeedFile = Paths.get(staticFeedURL.toURI()).toFile();
        reader.setInputLocation(staticFeedFile);
        reader.run();
        System.out.println("Dati GTFS statici caricati con successo.");
    }

    public GtfsRelationalDaoImpl getStore() {
        return this.store;
    }
}
