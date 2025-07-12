package com.nnamo;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.ServiceModel;
import com.nnamo.models.StopModel;
import com.nnamo.models.TripModel;

public class DatabaseService {

    private JdbcConnectionSource connection;

    public enum Tables {
        STOPS,
        ROUTES,
        AGENCIES,
        SERVICES,
        TRIPS
    }

    private Dao<StopModel, String> stopDao;
    private Dao<RouteModel, String> routeDao;
    private Dao<AgencyModel, String> agencyDao;
    private Dao<TripModel, String> tripDao;
    private Dao<ServiceModel, String> serviceDao;

    public DatabaseService() throws SQLException {
        this.connection = new JdbcConnectionSource("jdbc:sqlite:data.db");

        TableUtils.createTableIfNotExists(connection, StopModel.class);
        TableUtils.createTableIfNotExists(connection, RouteModel.class);
        TableUtils.createTableIfNotExists(connection, AgencyModel.class);
        TableUtils.createTableIfNotExists(connection, TripModel.class);
        TableUtils.createTableIfNotExists(connection, ServiceModel.class);

        this.stopDao =  DaoManager.createDao(connection, StopModel.class);
        this.routeDao =  DaoManager.createDao(connection, RouteModel.class);
        this.agencyDao = DaoManager.createDao(connection, AgencyModel.class);
        this.tripDao = DaoManager.createDao(connection, TripModel.class);
        this.serviceDao = DaoManager.createDao(connection, ServiceModel.class);
    }

    // Checks if any of the tables is empty and needs to be populated
    public boolean needsCaching() throws SQLException {
        return stopDao.countOf() == 0 || routeDao.countOf() == 0 || agencyDao.countOf() == 0;
    }
}
