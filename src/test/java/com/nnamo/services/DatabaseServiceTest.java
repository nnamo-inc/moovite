package com.nnamo.services;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RouteType;
import com.nnamo.models.*;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

public class DatabaseServiceTest extends TestCase {

    private final DatabaseService service = new DatabaseService(new JdbcConnectionSource("jdbc:sqlite::memory:"));

    public DatabaseServiceTest() throws SQLException {
        var agencyDao = this.service.getDao(AgencyModel.class);
        var stopDao = this.service.getDao(StopModel.class);
        var routeDao = this.service.getDao(RouteModel.class);
        var tripDao = this.service.getDao(TripModel.class);
        var serviceDao = this.service.getDao(ServiceModel.class);
        var stopTimeDao = this.service.getDao(StopTimeModel.class);
        var userDao = this.service.getDao(UserModel.class);
        service.wipeTables(); // Resets database (also AUTO INCREMENT counter)
        service.initTables();

        // Populating database with Mock Data
        UserModel user = new UserModel("user", "abcdefc");
        userDao.create(user);

        AgencyModel agency = new AgencyModel("0", "Atac", "", "");
        StopModel[] stops = new StopModel[]{
                new StopModel("0", "Verano", 100.0, 100.0),
                new StopModel("1", "Rebibbia", 130.0, 100.0),
                new StopModel("2", "Policlinico", 170.0, 100.0),
        };

        RouteModel[] routes = new RouteModel[]{
                new RouteModel("0", agency, "163", "163", RouteType.BUS),
                new RouteModel("1", agency, "71", "71", RouteType.BUS),
        };

        var currentTime = LocalTime.now().toSecondOfDay();
        ServiceModel serviceModel = new ServiceModel("0", new Date(), 1);
        TripModel trip1 = new TripModel("0", routes[0], "0", "Verano", Direction.INBOUND);
        StopTimeModel stopTime = new StopTimeModel(trip1, stops[0], currentTime + 1000, currentTime + 1000);

        // Rows Creation
        serviceDao.create(serviceModel);
        agencyDao.create(agency);
        stopDao.create(Arrays.asList(stops));
        routeDao.create(Arrays.asList(routes));
        tripDao.create(trip1);
        stopTimeDao.create(stopTime);

        service.addFavStop(user, stops[0]);
        service.addFavRoute(user.getId(), routes[0].getId());
    }

    public void testStopFetching() throws SQLException {
        var stop = service.getStopById("0");
        assertEquals("0", stop.getId());
        assertEquals("Verano", stop.getName());
    }

    public void testStopRoutes() throws SQLException {
        var routes = service.getStopRoutes("0");
        assertEquals("163", routes.getFirst().getShortName());
    }

    public void testUserFavStop() throws SQLException {
        var user = service.getUserByName("user");
        assertNotNull(user);

        var stops = service.getFavoriteStops(user);
        var firstStop = stops.getFirst();
        assertNotNull(firstStop);
        assertEquals("Verano", firstStop.getName());
    }

    public void testUserFavRoute() throws SQLException {
        var user = service.getUserByName("user");
        assertNotNull(user);

        var routes = service.getFavoriteRoutes(user.getId());
        var firstRoute = routes.getFirst();
        assertNotNull(firstRoute);
        assertEquals("163", firstRoute.getShortName());
    }

    public void testNextStopTimes() throws SQLException {
        var currentTime = LocalTime.now();
        var stopTimes = service.getNextStopTimes("0", currentTime);
        var stopTime = stopTimes.getFirst();
        assertNotNull(stopTime);
        assertEquals("0", stopTime.getTrip().getId());
    }
}
