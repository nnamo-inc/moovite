package com.nnamo.services;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Service to load and provide access to static GTFS data.
 * It reads GTFS data from a zip file located in the resources folder
 * and stores it in a GtfsRelationalDaoImpl for easy querying.
 *
 * <ul>
 *   <li>Use {@link #load()} to load the GTFS data from the zip file.</li>
 *   <li>Use {@link #getStore()} to access the loaded GTFS data through the {@link GtfsRelationalDaoImpl} instance.</li>
 * </ul>
 *
 * @see GtfsRelationalDaoImpl
 * @see GtfsReader
 *
 * @author Samuele Lombardi
 */
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

        URL staticFeedURL = getClass().getResource("/rome_static_gtfs.zip");
        if (staticFeedURL == null) {
            throw new FileNotFoundException("GTFS zip not found in resources.");
        }

        // Reads the zip file from the Jar Resource and creates a temporary file
        // (This fixes FileSystemNotFoundException on some systems)
        InputStream in = staticFeedURL.openStream();
        File tempFile = File.createTempFile("rome_static_gtfs", ".zip");
        tempFile.deleteOnExit();
        try (OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        reader.setInputLocation(tempFile);
        reader.run();
        System.out.println("Dati GTFS statici caricati con successo.");
    }

    public GtfsRelationalDaoImpl getStore() {
        return this.store;
    }
}
