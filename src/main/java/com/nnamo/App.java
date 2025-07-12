package com.nnamo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.DatabaseService.Tables;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;

public class App 
{
    public static void main( String[] args ) {
        // TODO need to extract this logic somewhere else, maybe DatabaseService or StaticGtfsService
        try {
            DatabaseService db = new DatabaseService();
            Dao<StopModel, String> stopDao = db.getDao(Tables.STOPS);
            Dao<RouteModel, String> routeDao = db.getDao(Tables.ROUTES);
            Dao<AgencyModel, String> agencyDao = db.getDao(Tables.AGENCIES);

            if (db.needsCaching()) {
                StaticGtfsService gtfs = null;
                try {
                    gtfs = new StaticGtfsService();
                } catch (IOException e) {
                    System.out.println("IO error:\n\t" + e.toString());
                    System.exit(1);
                }

                GtfsRelationalDaoImpl store = gtfs.getStore();
                if (stopDao.countOf() == 0) {
                    for (Stop stop : store.getAllStops()) {
                        System.out.println("Caching " + stop.getName() + " ...");
                        StopModel instance = new StopModel(
                                stop.getId().getId(),
                                stop.getName(),
                                stop.getLat(),
                                stop.getLon()
                                );
                        stopDao.createIfNotExists(instance);
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
            System.out.println(e.toString());
        }
    }
}

