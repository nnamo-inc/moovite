package com.nnamo;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.IOException;

public class StaticGtfsService {

    private final GtfsRelationalDaoImpl store;

    public StaticGtfsService() {
        GtfsReader reader = new GtfsReader(); // Lettore del buffer
        GtfsRelationalDaoImpl store = new GtfsRelationalDaoImpl(); // API per interagire con i dati letti

        System.out.println("Caricamento dati GTFS statici..."); // Lento, caricarli solo quando è necessario

        this.store = store;
    }

    public void load() throws IOException {
        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(this.store);
        reader.setInputLocation(new File("assets/gtfs/rome_static_gtfs.zip"));
        reader.run();
        System.out.println("Dati GTFS statici caricati con successo.");
    }

    public GtfsRelationalDaoImpl getStore() {
        return this.store;
    }
}
