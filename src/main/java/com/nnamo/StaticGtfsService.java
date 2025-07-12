package com.nnamo;

import java.io.File;
import java.io.IOException;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

public class StaticGtfsService {

    private GtfsRelationalDaoImpl store;

    public StaticGtfsService() throws IOException {
        GtfsReader reader = new GtfsReader(); // Lettore del buffer
        GtfsRelationalDaoImpl store = new GtfsRelationalDaoImpl(); // API per interagire con i dati letti

        reader.setEntityStore(store);
        reader.setInputLocation(new File("assets/gtfs/rome_static_gtfs.zip"));

        System.out.println("Caricamento dati GTFS statici..."); // Lento, caricarli solo quando è necessario

        reader.run(); // Lettura del buffer e salvataggio dei dati in store

        this.store = store;
    }

    public GtfsRelationalDaoImpl getStore() {
        return this.store;
    }
}
