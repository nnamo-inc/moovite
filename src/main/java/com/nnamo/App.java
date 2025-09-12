package com.nnamo;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.nnamo.controllers.MainController;
import com.nnamo.services.DatabaseService;
import com.nnamo.services.RealtimeGtfsService;
import com.nnamo.services.StaticGtfsService;
import com.nnamo.utils.Log;
import com.nnamo.utils.UserDataUtils;
import com.nnamo.view.frame.StartupLoadingFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * Program entry point. Initializes the database, GTFS services, and main
 * controller.
 *
 * @author Samuele Lombardi
 * @author Riccardo Finocchiaro
 * @author Davide Galilei
 * @see DatabaseService
 * @see StaticGtfsService
 * @see RealtimeGtfsService
 * @see MainController
 */
public class App {
    /**
     * Starts the main application, by initializing the available services
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            DatabaseService db = new DatabaseService();
            StaticGtfsService staticGtfs = new StaticGtfsService();
            RealtimeGtfsService realtimeGtfs = new RealtimeGtfsService();

            // Processing... window
            StartupLoadingFrame loadingFrame = new StartupLoadingFrame();
            loadingFrame.setVisible(true);

            db.preloadGtfsData(staticGtfs);

            // Close processing frame after loading GTFS data
            loadingFrame.close();

            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                // https://www.formdev.com/flatlaf/customizing/
                UIManager.put("Button.arc", 15);
                UIManager.put("Component.arc", 15);
                UIManager.put("ProgressBar.arc", 15);
                UIManager.put("TextComponent.arc", 15);
            } catch (Exception ex) {
                Log.error("Failed to initialize LaF");
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
            Log.error("Error loading GTFS data");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            Log.error("Error loading realtime GTFS data");
            e.printStackTrace();
        }
    }
}
