package com.nnamo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.nnamo.controllers.MainController;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.services.StaticGtfsService;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws InterruptedException {
        try {
            DatabaseService db = new DatabaseService();
            StaticGtfsService staticGtfs = new StaticGtfsService();
            RealtimeGtfsService realtimeGtfs = new RealtimeGtfsService();
            db.preloadGtfsData(staticGtfs);
            realtimeGtfs.startBackgroundThread();

            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }

            MainController controller = new MainController(db, realtimeGtfs);
            controller.run();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            System.err.println("Error loading GTFS data");
            e.printStackTrace();
            return;
        } catch (URISyntaxException e) {
            System.err.println("Error loading realtime GTFS data");
            e.printStackTrace();
            return;
        }
    }
}
