package com.nnamo;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;

public class DatabaseService {

    private JdbcConnectionSource source;

    // Needs refactoring
    private Dao<StopModel, String> stopDao;
    private Dao<RouteModel, String> routeDao;
    private Dao<AgencyModel, String> agencyDao;

    public DatabaseService() throws SQLException {
        this.source = new JdbcConnectionSource("jdbc:sqlite:data.db");
        this.stopDao =  DaoManager.createDao(source, StopModel.class);
        this.routeDao =  DaoManager.createDao(source, RouteModel.class);
        this.agencyDao = DaoManager.createDao(source, AgencyModel.class);

        TableUtils.createTableIfNotExists(source, StopModel.class);
        TableUtils.createTableIfNotExists(source, RouteModel.class);
        TableUtils.createTableIfNotExists(source, AgencyModel.class);
    }
}
