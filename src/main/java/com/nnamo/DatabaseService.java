package com.nnamo;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.*;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Stop;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseService {

    private final JdbcConnectionSource connection;

    public enum Tables {
        STOPS,
        ROUTES,
        AGENCIES,
        SERVICES,
        TRIPS
    }

    private final Dao<StopModel, String> stopDao;
    private final Dao<RouteModel, String> routeDao;
    private final Dao<AgencyModel, String> agencyDao;
    private final Dao<TripModel, String> tripDao;
    private final Dao<ServiceModel, String> serviceDao;

    public DatabaseService() throws SQLException {
        this.connection = new JdbcConnectionSource("jdbc:sqlite:data.db");

        TableUtils.createTableIfNotExists(connection, StopModel.class);
        TableUtils.createTableIfNotExists(connection, RouteModel.class);
        TableUtils.createTableIfNotExists(connection, AgencyModel.class);
        TableUtils.createTableIfNotExists(connection, TripModel.class);
        TableUtils.createTableIfNotExists(connection, ServiceModel.class);

        this.stopDao = DaoManager.createDao(connection, StopModel.class);
        this.routeDao = DaoManager.createDao(connection, RouteModel.class);
        this.agencyDao = DaoManager.createDao(connection, AgencyModel.class);
        this.tripDao = DaoManager.createDao(connection, TripModel.class);
        this.serviceDao = DaoManager.createDao(connection, ServiceModel.class);
    }

    // Checks if any of the tables is empty and needs to be populated
    public boolean needsCaching() throws SQLException {
        return stopDao.countOf() == 0 || routeDao.countOf() == 0 || agencyDao.countOf() == 0;
    }

    public void importStopsFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<StopModel, String> stopDao = this.getStopDao();
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (stopDao.countOf() != 0) return;

        ArrayList<StopModel> stops = new ArrayList<>();
        for (Stop stop : store.getAllStops()) {
            StopModel instance = new StopModel(
                    stop.getId().getId(),
                    stop.getName(),
                    stop.getLat(),
                    stop.getLon()
            );
            stops.add(instance);

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

    public void importRoutesFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<RouteModel, String> routeDao = this.getRouteDao();
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (routeDao.countOf() != 0) return;

        for (org.onebusaway.gtfs.model.Route route : store.getAllRoutes()) {
            AgencyModel agencyModel = new AgencyModel(
                    route.getAgency().getId(),
                    route.getAgency().getName(),
                    route.getAgency().getTimezone(),
                    route.getAgency().getUrl()
            );
            agencyDao.createIfNotExists(agencyModel);

            RouteModel routeModel = new RouteModel(
                    route.getId().getId(),
                    agencyModel,
                    route.getShortName(),
                    route.getLongName()
            );
            routeDao.createIfNotExists(routeModel);
        }
    }

    public void preloadGtfsData(StaticGtfsService gtfs) throws SQLException {
        if (needsCaching()) {
            System.out.println("Preloading GTFS data into the database...");
            try {
                gtfs.load(); // Load GTFS data from the static file
            } catch (IOException e) {
                System.err.println("Error loading GTFS data: " + e.getMessage());
                return;
            }

            importStopsFromGtfs(gtfs);
            importRoutesFromGtfs(gtfs);

            System.out.println("GTFS data preloaded successfully.");
        } else {
            System.out.println("GTFS data already cached in the database.");
        }
    }

    public Dao<StopModel, String> getStopDao() {
        return stopDao;
    }

    public Dao<RouteModel, String> getRouteDao() {
        return routeDao;
    }

    public Dao<AgencyModel, String> getAgencyDao() {
        return agencyDao;
    }

    public Dao<TripModel, String> getTripDao() {
        return tripDao;
    }

    public Dao<ServiceModel, String> getServiceDao() {
        return serviceDao;
    }
}
