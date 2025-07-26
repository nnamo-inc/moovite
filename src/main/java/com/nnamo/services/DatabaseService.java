package com.nnamo.services;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.models.*;
import com.nnamo.utils.FuzzyMatch;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.sqlite.Function;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

        Function.create(this.connection.getReadWriteConnection(null).getUnderlyingConnection(), "FUZZY_SCORE",
                new FuzzyMatch());
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
                            route.getLongName(),
                            route.getShortName());
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
        double scoreThreshold = 0.6;

        return stopDao
                .queryBuilder()
                .where()
                .raw(
                        "FUZZY_SCORE(name, ?) > ?",
                        new SelectArg(SqlType.STRING, stopName),
                        new SelectArg(SqlType.DOUBLE, scoreThreshold))
                .query();
    }

    public List<StopTimeModel> getStopTimes(String stopId) throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        boolean ascending = true;

        return stopTimeDao
                .queryBuilder()
                .orderBy("arrival_time", ascending)
                .where()
                .eq("stop_id", stopId)
                .query();
    }

    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time) throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        // Conversion from LocalTime to Date, since the time is stored in the db at the
        // EPOCH Date (1970-01-01)
        Instant epochDateTime = time.atDate(LocalDate.EPOCH).atZone(ZoneId.systemDefault()).toInstant();
        Date timedate = Date.from(epochDateTime);

        boolean ascending = true;
        return stopTimeDao
                .queryBuilder()
                .orderBy("arrival_time", ascending)
                .where()
                .eq("stop_id", stopId)
                .and()
                .gt("arrival_time", timedate)
                .query();
    }

    public void addUser(UserModel user) throws SQLException {
        getDao(UserModel.class).createIfNotExists(user);
    }

    public void addUser(String username, String passwordHash) throws SQLException {
        getDao(UserModel.class)
                .createIfNotExists(new UserModel(username, passwordHash));
    }

    public UserModel getUserByName(String username) throws SQLException {
        var users = getDao(UserModel.class).queryForEq("username", username);
        if (users.size() >= 1) {
            return users.getFirst();
        }
        return null;
    }

    public UserModel getUserById(int id) throws SQLException {
        return getDao(UserModel.class).queryForId(id);
    }

    // TODO: extract user and favorites logic in a separate UserService
    public void addFavoriteStop(int userId, String stopId) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);
        Dao<UserModel, Integer> userDao = getDao(UserModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        UserModel user = userDao.queryForId(userId);
        StopModel stop = stopDao.queryForId(stopId);

        if (user == null || stop == null) {
            return;
        }

        favoriteStopDao.create(new FavoriteStopModel(user, stop));
    }

    public void addFavoriteStop(UserModel user, StopModel stop) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);
        if (user == null || stop == null) {
            return;
        }
        favoriteStopDao.create(new FavoriteStopModel(user, stop));
    }

    // TODO: possibile optimization with raw query and join to fix N+1 problem
    public List<StopModel> getFavoriteStops(int userId) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);

        var favorites = favoriteStopDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .query();

        List<StopModel> stops = new ArrayList<>();
        for (FavoriteStopModel favorite : favorites) {
            stops.add(favorite.getStop());
        }
        return stops;
    }

    public List<StopModel> getFavoriteStops(UserModel user) throws SQLException {
        return getFavoriteStops(user.getId());
    }

    public boolean hasFavoriteStop(int userId) throws SQLException {
        return getFavoriteStops(userId).size() >= 1;
    }

    public boolean hasFavoriteStop(UserModel user) throws SQLException {
        return hasFavoriteStop(user.getId());
    }

    public boolean isFavoriteStop(int userId, String stopId) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);
        var favorites = favoriteStopDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .and()
                .eq("stop_id", stopId)
                .query();

        return !favorites.isEmpty();
    }

    public boolean isFavouriteRoute(int userId, String routeId) throws SQLException {
        Dao<FavoriteLineModel, String> favoriteLineDao = getDao(FavoriteLineModel.class);
        var favorites = favoriteLineDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .and()
                .eq("route_id", routeId)
                .query();

        return !favorites.isEmpty();
    }
}
