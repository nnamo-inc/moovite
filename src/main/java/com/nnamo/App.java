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
    public static void main(String[] args) {
        // TODO need to extract this logic somewhere else, maybe DatabaseService or StaticGtfsService
        try {
            DatabaseService db = new DatabaseService();
            Dao<StopModel, String> stopDao = db.getStopDao();
            Dao<RouteModel, String> routeDao = db.getRouteDao();
            Dao<AgencyModel, String> agencyDao = db.getAgencyDao();

            if (db.needsCaching()) {
                StaticGtfsService gtfs = null;
                try {
                    gtfs = new StaticGtfsService();
                } catch (IOException e) {
                    System.out.println("IO error:\n\t" + e);
                    System.exit(1);
                }

                GtfsRelationalDaoImpl store = gtfs.getStore();
                if (stopDao.countOf() == 0) {
                    ArrayList<StopModel> stops = new ArrayList<>();
                    for (Stop stop : store.getAllStops()) {
                        System.out.println("Caching " + stop.getName() + " ...");
                        StopModel instance = new StopModel(
                                stop.getId().getId(),
                                stop.getName(),
                                stop.getLat(),
                                stop.getLon()
                        );
                        stops.add(instance);
                        // stopDao.createIfNotExists(instance);

                        if (stops.size() >= 200) {
                            stopDao.create(stops);
                            stops.clear();
                        }
                    }

                    if (!stops.isEmpty()) {
                        stopDao.create(stops);
                        stops.clear();
                    }
                }

                if (routeDao.countOf() == 0) {
                    for (Route route : store.getAllRoutes()) {
                        System.out.println(route.getShortName());
                        Agency agency = route.getAgency();
                        AgencyModel agencyModel = new AgencyModel(
                                agency.getId(),
                                agency.getName(),
                                agency.getTimezone(),
                                agency.getUrl()
                        );
                        agencyDao.createIfNotExists(agencyModel);

                        System.out.println("Caching " + route.getShortName() + " ...");
                        RouteModel routeModel = new RouteModel(
                                route.getId().getId(),
                                agencyModel,
                                route.getShortName(),
                                route.getLongName()
                        );
                        routeDao.createIfNotExists(routeModel);
                    }

                    /* for (Trip trip : store.getAllTrips()) {
                    // trip.getServiceId().getId()
                    } */
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

