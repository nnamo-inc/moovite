package com.nnamo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;

public class DatabaseService {

    private JdbcConnectionSource connection;

    public enum Tables {
        STOPS,
        ROUTES,
        AGENCIES
    }

    // TODO See if this is worth it
    private HashMap<String, Dao> daos;

    private Dao<StopModel, String> stopDao;
    private Dao<RouteModel, String> routeDao;
    private Dao<AgencyModel, String> agencyDao;

    public DatabaseService() throws SQLException {
        this.connection = new JdbcConnectionSource("jdbc:sqlite:data.db");

        TableUtils.createTableIfNotExists(connection, StopModel.class);
        TableUtils.createTableIfNotExists(connection, RouteModel.class);
        TableUtils.createTableIfNotExists(connection, AgencyModel.class);

        this.stopDao =  DaoManager.createDao(connection, StopModel.class);
        this.routeDao =  DaoManager.createDao(connection, RouteModel.class);
        this.agencyDao = DaoManager.createDao(connection, AgencyModel.class);

        this.daos = new HashMap(Map.of(
            Tables.STOPS, this.stopDao,
            Tables.ROUTES, this.routeDao,
            Tables.AGENCIES, this.agencyDao
        ));
    }

    // Checks if any of the tables is empty and needs to be populated
    public boolean needsCaching() throws SQLException {
        return stopDao.countOf() == 0 || routeDao.countOf() == 0 || agencyDao.countOf() == 0;
    }

    public Dao getDao(Tables table) {
        return daos.get(table);
    }
}
