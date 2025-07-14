package com.nnamo;

import java.io.IOException;
import java.sql.SQLException;

import com.nnamo.models.StopModel;

public class App {
    public static void main(String[] args) throws InterruptedException {
        Thread preloadThread = new Thread(() -> {
            try {
                DatabaseService db = new DatabaseService();
                StaticGtfsService gtfs = new StaticGtfsService();
                MapService map = new MapService(db);
                db.preloadGtfsData(gtfs);
                map.run();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                System.err.println("Error loading GTFS data");
                e.printStackTrace();
                return;
            }

        });
        preloadThread.start();

        // Wait for the preload thread to finish before proceeding
        preloadThread.join();
    }
}
