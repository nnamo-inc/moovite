package com.nnamo.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class DatabaseService {

    private final JdbcConnectionSource connection;
    private final HashMap<Class<?>, Dao<?, ?>> daos = new HashMap<>();

    public DatabaseService() throws SQLException {
        this.connection = new JdbcConnectionSource("jdbc:sqlite:data.db");
        initDaos();
        initTables();
    }

    private void initDaos() throws SQLException {
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

    public boolean needsCaching() throws SQLException {
        return daos.get(StopModel.class).countOf() == 0 ||
                daos.get(TripModel.class).countOf() == 0 ||
                daos.get(StopTimeModel.class).countOf() == 0;
    }

    private void importStopsFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (stopDao.countOf() != 0)
            return;

        System.out.println("Starting stops import...");

        TransactionManager.callInTransaction(connection, (Callable<Void>) () -> {
            ArrayList<StopModel> stops = new ArrayList<>(20000);
            int totalProcessed = 0;

            for (Stop stop : store.getAllStops()) {
                stops.add(new StopModel(
                        stop.getId().getId(),
                        stop.getName(),
                        stop.getLat(),
                        stop.getLon()));

                if (stops.size() >= 20000) {
                    stopDao.create(stops);
                    totalProcessed += stops.size();
                    stops.clear();
                    System.out.println("Processed " + totalProcessed + " stops...");
                }
            }

            if (!stops.isEmpty()) {
                stopDao.create(stops);
                totalProcessed += stops.size();
            }

            System.out.println("Stops imported. Total: " + totalProcessed + " records");
            return null;
        });
    }

    private void importTripsFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<TripModel, String> tripDao = getDao(TripModel.class);
        Dao<AgencyModel, String> agencyDao = getDao(AgencyModel.class);
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (tripDao.countOf() != 0)
            return;

        System.out.println("Starting agencies, routes, and trips import...");

        TransactionManager.callInTransaction(connection, (Callable<Void>) () -> {
            HashMap<String, AgencyModel> agencyMap = new HashMap<>();
            HashMap<String, RouteModel> routeMap = new HashMap<>();
            ArrayList<AgencyModel> agencies = new ArrayList<>();
            ArrayList<RouteModel> routes = new ArrayList<>();
            ArrayList<TripModel> trips = new ArrayList<>(20000);

            for (Trip trip : store.getAllTrips()) {
                Agency agency = trip.getRoute().getAgency();
                if (!agencyMap.containsKey(agency.getId())) {
                    AgencyModel agencyModel = new AgencyModel(
                            agency.getId(),
                            agency.getName(),
                            agency.getTimezone(),
                            agency.getUrl());
                    agencies.add(agencyModel);
                    agencyMap.put(agency.getId(), agencyModel);
                }

                Route route = trip.getRoute();
                if (!routeMap.containsKey(route.getId().getId())) {
                    RouteModel routeModel = new RouteModel(
                            route.getId().getId(),
                            agencyMap.get(route.getAgency().getId()),
                            route.getShortName(),
                            route.getLongName());
                    routes.add(routeModel);
                    routeMap.put(route.getId().getId(), routeModel);
                }
            }

            if (!agencies.isEmpty()) {
                agencyDao.create(agencies);
                System.out.println("Agencies imported: " + agencies.size());
            }

            if (!routes.isEmpty()) {
                routeDao.create(routes);
                System.out.println("Routes imported: " + routes.size());
            }

            int totalProcessed = 0;
            for (Trip trip : store.getAllTrips()) {
                trips.add(new TripModel(
                        trip.getId().getId(),
                        routeMap.get(trip.getRoute().getId().getId()),
                        trip.getTripHeadsign(),
                        trip.getDirectionId()));

                if (trips.size() >= 20000) {
                    tripDao.create(trips);
                    totalProcessed += trips.size();
                    trips.clear();
                    System.out.println("Processed " + totalProcessed + " trips...");
                }
            }

            if (!trips.isEmpty()) {
                tripDao.create(trips);
                totalProcessed += trips.size();
            }

            System.out.println("Trips imported. Total: " + totalProcessed + " records");
            return null;
        });
    }

    private void importStopTimesFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<TripModel, String> tripDao = getDao(TripModel.class);
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (stopTimeDao.countOf() != 0)
            return;

        if (tripDao.countOf() == 0) {
            System.out.println("No trips found - skipping stop times");
            return;
        }

        System.out.println("Loading trips into memory...");
        HashMap<String, TripModel> tripMap = new HashMap<>();
        for (TripModel trip : tripDao.queryForAll()) {
            tripMap.put(trip.getId(), trip);
        }
        System.out.println("Loaded " + tripMap.size() + " trips");

        System.out.println("Loading stops into memory...");
        HashMap<String, StopModel> stopMap = new HashMap<>();
        for (StopModel stop : stopDao.queryForAll()) {
            stopMap.put(stop.getId(), stop);
        }
        System.out.println("Loaded " + stopMap.size() + " stops");

        System.out.println("Starting stop times import...");

        TransactionManager.callInTransaction(connection, (Callable<Void>) () -> {
            ArrayList<StopTimeModel> stopTimes = new ArrayList<>(100000);
            int totalProcessed = 0;
            int skipped = 0;

            for (StopTime stopTime : store.getAllStopTimes()) {
                TripModel tripModel = tripMap.get(stopTime.getTrip().getId().getId());
                StopModel stopModel = stopMap.get(stopTime.getStop().getId().getId());

                if (tripModel == null || stopModel == null) {
                    skipped++;
                    continue;
                }

                stopTimes.add(new StopTimeModel(
                        tripModel,
                        stopModel,
                        new Date(stopTime.getArrivalTime() * 1000L),
                        new Date(stopTime.getDepartureTime() * 1000L)));

                if (stopTimes.size() >= 100000) {
                    stopTimeDao.create(stopTimes);
                    totalProcessed += stopTimes.size();
                    stopTimes.clear();
                    System.out.println("Processed " + totalProcessed + " stop times...");
                }
            }

            if (!stopTimes.isEmpty()) {
                stopTimeDao.create(stopTimes);
                totalProcessed += stopTimes.size();
            }

            System.out.println("Stop times imported. Total: " + totalProcessed + " records");
            if (skipped > 0) {
                System.out.println("Skipped " + skipped + " invalid stop times");
            }
            return null;
        });
    }

    public void preloadGtfsData(StaticGtfsService gtfs) throws SQLException, IOException {
        if (needsCaching()) {
            System.out.println("Starting GTFS data import...");
            gtfs.load();

            importStopsFromGtfs(gtfs);
            importTripsFromGtfs(gtfs);
            importStopTimesFromGtfs(gtfs);

            System.out.println("GTFS import completed successfully.");
        } else {
            System.out.println("GTFS data already cached.");
        }
    }

    @SuppressWarnings("unchecked")
    public <T, ID> Dao<T, ID> getDao(Class<T> modelClass) {
        return (Dao<T, ID>) daos.get(modelClass);
    }

    public List<StopModel> getAllStops() throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        return stopDao.queryForAll();
    }

    public StopModel getStopById(String id) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        return stopDao.queryForId(id);
    }

    public List<StopModel> getStopsByName(String stopName) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        return stopDao
                .queryBuilder()
                .where()
                .like("name", "%" + stopName + "%")
                .query();
    }

    public List<StopTimeModel> getStopTimes(String stopId) throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        return stopTimeDao.queryBuilder().where().eq("stop_id", stopId).query();
    }
}
