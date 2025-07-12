package com.nnamo;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.*;

import java.sql.SQLException;

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
