package com.nnamo.services;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.nnamo.enums.RouteType;
import com.nnamo.models.AgencyModel;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;

import java.sql.SQLException;
import java.util.Arrays;

public class DatabaseServiceTest extends TestCase {

    private final DatabaseService service = new DatabaseService(new JdbcConnectionSource("jdbc:sqlite:test.db"));
    private StopModel testStop;

    public DatabaseServiceTest() throws SQLException {
        var agencyDao = this.service.getDao(AgencyModel.class);
        var stopDao = this.service.getDao(StopModel.class);
        var routeDao = this.service.getDao(RouteModel.class);
        service.wipeTables(); // Resets database (also AUTO INCREMENT counter)
        service.initTables();

        AgencyModel agency = new AgencyModel("0", "Atac", "", "");
        StopModel[] stops = new StopModel[] {
                new StopModel("0", "Verano", 100.0, 100.0),
                new StopModel("1", "Rebibbia", 130.0, 100.0),
                new StopModel("2", "Policlinico", 170.0, 100.0),
        };

        RouteModel[] routes = new RouteModel[] {
                new RouteModel("0", agency, "163", "163", RouteType.BUS),
                new RouteModel("1", agency, "71", "71", RouteType.BUS),
        };

        agencyDao.create(agency);
        stopDao.create(Arrays.asList(stops));
        routeDao.create(Arrays.asList(routes));
    }

    public void testStopFetching() throws SQLException {
        var stop = service.getStopById("0");
        assertEquals(true, (stop.getId().equalsIgnoreCase("0") && stop.getName().equalsIgnoreCase("Verano")));
    }
}
