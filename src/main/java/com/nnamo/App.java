package com.nnamo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.nnamo.controllers.MainController;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.services.StaticGtfsService;
import com.nnamo.utils.UserDataUtils;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws InterruptedException {
        try {
            DatabaseService db = new DatabaseService();
            StaticGtfsService staticGtfs = new StaticGtfsService();
            RealtimeGtfsService realtimeGtfs = new RealtimeGtfsService();

            // Processing... window
            JFrame processingFrame = new JFrame("Processing...");
            processingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            processingFrame.setSize(300, 100);
            processingFrame.setLocationRelativeTo(null);
            JLabel processingLabel = new JLabel("Loading GTFS data, please wait...", SwingConstants.CENTER);
            processingFrame.add(processingLabel);
            processingFrame.setVisible(true);

            db.preloadGtfsData(staticGtfs);

            // Close processing frame after loading GTFS data
            processingFrame.dispose();

            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                // https://www.formdev.com/flatlaf/customizing/
                UIManager.put("Button.arc", 999);
                UIManager.put("Component.arc", 999);
                UIManager.put("ProgressBar.arc", 999);
                UIManager.put("TextComponent.arc", 999);
            } catch (Exception ex) {
                System.err.println("Failed to initialize LaF");
            }

            // Create user directory if it doesn't exist
            Files.createDirectories(Path.of(UserDataUtils.getDataDir()));
            File mapCacheDir = new File(UserDataUtils.getMapCachePath());

            MainController controller = new MainController(db, realtimeGtfs);
            controller.setLocalMapCache(mapCacheDir);
            controller.run();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading GTFS data");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Error loading realtime GTFS data");
            e.printStackTrace();
        }
    }
}
