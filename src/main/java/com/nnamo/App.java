package com.nnamo;

import java.io.IOException;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws InterruptedException {
        Thread preloadThread = new Thread(() -> {
            try {
                DatabaseService db = new DatabaseService();
                StaticGtfsService gtfs = new StaticGtfsService();
                db.preloadGtfsData(gtfs);
            } catch (SQLException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.err.println("Error loading GTFS data: " + e.getMessage());
                return;
            }

        });
        preloadThread.start();

        // Wait for the preload thread to finish before proceeding
        preloadThread.join();
    }
}
