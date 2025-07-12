package com.nnamo;

import com.j256.ormlite.dao.Dao;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws InterruptedException {
        // TODO need to extract this logic somewhere else, maybe DatabaseService or StaticGtfsService
        Thread preloadThread = new Thread(() -> {
            try {
                DatabaseService db = new DatabaseService();
                StaticGtfsService gtfs = new StaticGtfsService();
                db.preloadGtfsData(gtfs);
            } catch (SQLException e) {
                System.out.println(e);
            }
        });
        preloadThread.start();

        // Wait for the preload thread to finish before proceeding
        preloadThread.join();
    }
}
