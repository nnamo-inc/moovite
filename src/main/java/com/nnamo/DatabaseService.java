package com.nnamo;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.*;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DatabaseService {

    private final JdbcConnectionSource connection;

    HashMap<Class<?>, Dao<?, ?>> daos = new HashMap<Class<?>, Dao<?, ?>>();
    List<Class> gtfs_models;

    public DatabaseService() throws SQLException {
        this.connection = new JdbcConnectionSource("jdbc:sqlite:data.db");
        initDaos();
        initTables();
    }

    private void initDaos() throws SQLException {
        Class[] models = {
                StopModel.class,
                RouteModel.class,
                AgencyModel.class,
                TripModel.class,
                // ServiceModel.class,
                StopTimeModel.class
        };
        this.gtfs_models = Arrays.asList(models);

        this.daos.put(StopModel.class, DaoManager.createDao(connection, StopModel.class));
        this.daos.put(RouteModel.class, DaoManager.createDao(connection, RouteModel.class));
        this.daos.put(AgencyModel.class, DaoManager.createDao(connection, AgencyModel.class));
        this.daos.put(TripModel.class, DaoManager.createDao(connection, TripModel.class));
        this.daos.put(ServiceModel.class, DaoManager.createDao(connection, ServiceModel.class));
        this.daos.put(StopTimeModel.class, DaoManager.createDao(connection, StopTimeModel.class));

        this.daos.put(UserModel.class, DaoManager.createDao(connection, UserModel.class));
        this.daos.put(FavoriteLineModel.class, DaoManager.createDao(connection, FavoriteLineModel.class));
        this.daos.put(FavoriteStopModel.class, DaoManager.createDao(connection, FavoriteStopModel.class));
    }

    private void initTables() throws SQLException {
        TableUtils.createTableIfNotExists(connection, StopModel.class);
        TableUtils.createTableIfNotExists(connection, RouteModel.class);
        TableUtils.createTableIfNotExists(connection, AgencyModel.class);
        TableUtils.createTableIfNotExists(connection, TripModel.class);
        TableUtils.createTableIfNotExists(connection, ServiceModel.class);
        TableUtils.createTableIfNotExists(connection, UserModel.class);
        TableUtils.createTableIfNotExists(connection, StopTimeModel.class);
        TableUtils.createTableIfNotExists(connection, FavoriteStopModel.class);
        TableUtils.createTableIfNotExists(connection, FavoriteLineModel.class);
    }

    // Checks if any of the tables is empty and needs to be populated
    public boolean needsCaching() throws SQLException {
        for (Class modelClass : gtfs_models) {
            if (daos.get(modelClass).countOf() == 0) {
                return true;
            }
        }
        return false;
    }

    public void importStopsFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<StopModel, String> stopDao = this.getDao(StopModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (stopDao.countOf() != 0)
            return;

        ArrayList<StopModel> stops = new ArrayList<>();
        for (Stop stop : store.getAllStops()) {
            StopModel instance = new StopModel(
                    stop.getId().getId(),
                    stop.getName(),
                    stop.getLat(),
                    stop.getLon());
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
        System.out.println("Stops imported successfully in the database");
    }

    public void importTripsFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<TripModel, String> tripDao = this.getDao(TripModel.class);
        Dao<AgencyModel, String> agencyDao = this.getDao(AgencyModel.class);
        Dao<RouteModel, String> routeDao = this.getDao(RouteModel.class);

        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (tripDao.countOf() != 0)
            return;

        ArrayList<TripModel> trips = new ArrayList<TripModel>();
        for (Trip trip : store.getAllTrips()) {
            Route route = trip.getRoute();
            Agency agency = route.getAgency();
            AgencyModel agencyModel = new AgencyModel(
                    agency.getId(),
                    agency.getName(),
                    agency.getTimezone(),
                    agency.getUrl());
            agencyDao.createIfNotExists(agencyModel);

            RouteModel routeModel = new RouteModel(
                    route.getId().getId(),
                    agencyModel,
                    route.getShortName(),
                    route.getLongName());
            routeDao.createIfNotExists(routeModel);

            /*
             * ServiceModel serviceModel = serviceDao.queryBuilder()
             * .where()
             * .eq("service_id", trip.getServiceId().getId())
             * .and()
             * .eq("date", trip.getServiceId().)
             * .query();
             */
            TripModel tripModel = new TripModel(
                    trip.getId().getId(),
                    // serviceModel,
                    routeModel,
                    trip.getTripHeadsign(),
                    trip.getDirectionId());

            trips.add(tripModel);
            if (trips.size() >= 200) {
                tripDao.create(trips);
                trips.clear();
            }
        }
        if (!trips.isEmpty()) {
            tripDao.create(trips);
            trips.clear();
        }
        System.out.println("Trips imported successfully in the database");
    }

    /*
     * public void importServicesFromGtfs(StaticGtfsService gtfs) throws
     * SQLException {
     * System.out.println("Loading services");
     * Dao<ServiceModel, String> serviceDao = this.getServiceDao();
     * GtfsRelationalDaoImpl store = gtfs.getStore();
     * 
     * if (serviceDao.countOf() != 0)
     * return;
     * 
     * ArrayList<ServiceModel> services = new ArrayList<>();
     * for (ServiceCalendarDate date : store.getAllCalendarDates()) {
     * if (date.getServiceId() == null || date.getDate() == null) {
     * continue;
     * }
     * 
     * ServiceModel serviceModel = new ServiceModel(
     * date.getServiceId().getId(),
     * date.getDate().getAsDate(),
     * date.getExceptionType());
     * services.add(serviceModel);
     * 
     * if (services.size() >= 200) {
     * serviceDao.create(services);
     * services.clear();
     * }
     * }
     * 
     * if (!services.isEmpty()) {
     * serviceDao.create(services);
     * services.clear();
     * }
     * }
     */

    public void importStopTimesFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<TripModel, String> tripDao = this.getDao(TripModel.class);
        Dao<StopTimeModel, String> stopTimeDao = this.getDao(StopTimeModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (stopTimeDao.countOf() != 0)
            return;

        if (tripDao.countOf() == 0) {
            System.out.println("Can't cache stop times: there are no trips cached.\n" +
                    "Reminder to devs: import trips before import stop times");
            return;
        }

        ArrayList<StopTimeModel> stopTimes = new ArrayList<>();
        for (StopTime stopTime : store.getAllStopTimes()) {
            TripModel tripModel = tripDao.queryForId(stopTime.getTrip().getId().getId());
            if (tripModel == null) {
                continue; // Skip if the stop is not found
            }

            Date arrivalTime = new Date(stopTime.getArrivalTime() * 1000L);
            Date departureTime = new Date(stopTime.getDepartureTime() * 1000L);

            StopTimeModel stopTimeModel = new StopTimeModel(
                    tripModel,
                    arrivalTime,
                    departureTime);
            stopTimes.add(stopTimeModel);

            if (stopTimes.size() >= 1000000) {
                stopTimeDao.create(stopTimes);
                stopTimes.clear();
            }
        }

        if (!stopTimes.isEmpty()) {
            stopTimeDao.create(stopTimes);
            stopTimes.clear();
        }
        System.out.println("Stop Times imported successfully in the database");
    }

    public void importRoutesFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<RouteModel, String> routeDao = this.getDao(RouteModel.class);
        Dao<AgencyModel, String> agencyDao = this.getDao(AgencyModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (routeDao.countOf() != 0)
            return;

        for (Route route : store.getAllRoutes()) {
            AgencyModel agencyModel = new AgencyModel(
                    route.getAgency().getId(),
                    route.getAgency().getName(),
                    route.getAgency().getTimezone(),
                    route.getAgency().getUrl());
            agencyDao.createIfNotExists(agencyModel);

            RouteModel routeModel = new RouteModel(
                    route.getId().getId(),
                    agencyModel,
                    route.getShortName(),
                    route.getLongName());
            routeDao.createIfNotExists(routeModel);
        }
        System.out.println("Routes imported successfully in the database");
    }

    public void preloadGtfsData(StaticGtfsService gtfs) throws SQLException, IOException {
        if (needsCaching()) {
            System.out.println("Preloading GTFS data into the database...");
            gtfs.load(); // Load GTFS data from the static file

            // Important! Order Matters, do not touch
            // importServicesFromGtfs(gtfs);
            importStopsFromGtfs(gtfs);
            importRoutesFromGtfs(gtfs);
            importTripsFromGtfs(gtfs); // Requires routes to be imported first
            importStopTimesFromGtfs(gtfs); // Requires trips and stops to be imported
            // first

            System.out.println("GTFS data preloaded successfully.");
        } else {
            System.out.println("GTFS data already cached in the database.");
        }
    }

    @SuppressWarnings("unchecked")
    public <T, ID> Dao<T, ID> getDao(Class<T> modelClass) {
        return (Dao<T, ID>) daos.get(modelClass);
    }

    public List<StopModel> getAllStops() throws SQLException {
        Dao<StopModel, String> stopDao = this.getDao(StopModel.class);
        return stopDao.queryForAll();
    }
}
