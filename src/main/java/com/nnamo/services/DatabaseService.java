package com.nnamo.services;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import com.nnamo.enums.Direction;
import com.nnamo.enums.RealtimeMetricType;
import com.nnamo.enums.RouteType;
import com.nnamo.models.*;
import com.nnamo.utils.FuzzyMatch;

import org.apache.commons.lang3.time.DateUtils;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.sqlite.Function;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        this.daos.put(FavoriteRouteModel.class, DaoManager.createDao(connection, FavoriteRouteModel.class));
        this.daos.put(FavoriteStopModel.class, DaoManager.createDao(connection, FavoriteStopModel.class));
        this.daos.put(TripUpdateModel.class, DaoManager.createDao(connection, TripUpdateModel.class));
        this.daos.put(RealtimeMetricModel.class, DaoManager.createDao(connection, RealtimeMetricModel.class));
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
        TableUtils.createTableIfNotExists(connection, FavoriteRouteModel.class);
        TableUtils.createTableIfNotExists(connection, TripUpdateModel.class);
        TableUtils.createTableIfNotExists(connection, RealtimeMetricModel.class);
    }

    public void preloadGtfsData(StaticGtfsService gtfs) throws SQLException, IOException, URISyntaxException {
        if (needsCaching()) {
            System.out.println("Starting GTFS data import...");
            gtfs.load();

            importServicesFromGtfs(gtfs);
            importStopsFromGtfs(gtfs);
            importTripsFromGtfs(gtfs);
            importStopTimesFromGtfs(gtfs);

            System.out.println("GTFS import completed successfully.");
        } else {
            System.out.println("GTFS data already cached.");
        }
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
        Dao<ServiceModel, String> serviceDao = getDao(ServiceModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (tripDao.countOf() != 0)
            return;

        System.out.println("Starting agencies, routes, and trips import...");

        TransactionManager.callInTransaction(connection, (Callable<Void>) () -> {
            HashMap<String, AgencyModel> agencyMap = new HashMap<>();
            HashMap<String, RouteModel> routeMap = new HashMap<>();
            HashMap<String, ServiceModel> serviceMap = new HashMap<>();
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
                RouteType routeType;
                switch (route.getType()) {
                    case 0:
                        routeType = RouteType.TRAM;
                        break;
                    case 1:
                        routeType = RouteType.METRO;
                        break;
                    case 3:
                    default:
                        routeType = RouteType.BUS;
                        break;
                }

                if (!routeMap.containsKey(route.getId().getId())) {
                    RouteModel routeModel = new RouteModel(
                            route.getId().getId(),
                            agencyMap.get(route.getAgency().getId()),
                            route.getLongName(),
                            route.getShortName(),
                            routeType);
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
                Direction direction = null;
                switch (trip.getDirectionId()) {
                    case "0":
                        direction = Direction.OUTBOUND;
                        break;
                    case "1":
                    default:
                        direction = Direction.INBOUND;
                        break;
                }

                trips.add(new TripModel(
                        trip.getId().getId(),
                        routeMap.get(trip.getRoute().getId().getId()),
                        trip.getServiceId().getId(),
                        trip.getTripHeadsign(),
                        direction));

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

                stopTime.getArrivalTime();
                stopTimes.add(new StopTimeModel(
                        tripModel,
                        stopModel,
                        stopTime.getArrivalTime(),
                        stopTime.getDepartureTime()));

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

    private void importServicesFromGtfs(StaticGtfsService gtfs) throws SQLException {
        Dao<ServiceModel, String> serviceDao = getDao(ServiceModel.class);
        GtfsRelationalDaoImpl store = gtfs.getStore();

        if (serviceDao.countOf() != 0)
            return;

        System.out.println("Starting services import...");

        TransactionManager.callInTransaction(connection, (Callable<Void>) () -> {
            ArrayList<ServiceModel> services = new ArrayList<>(1000);
            int totalProcessed = 0;

            for (ServiceCalendarDate service : store.getAllCalendarDates()) {
                ServiceModel serviceModel = new ServiceModel(
                        service.getServiceId().getId(),
                        service.getDate().getAsDate(),
                        service.getExceptionType());
                System.out.println(serviceModel.getServiceId() + " - " + serviceModel.getDate());
                services.add(serviceModel);

                if (services.size() >= 1000) {
                    serviceDao.create(services);
                    totalProcessed += services.size();
                    services.clear();
                    System.out.println("Processed " + totalProcessed + " services...");
                }
            }

            if (!services.isEmpty()) {
                serviceDao.create(services);
                totalProcessed += services.size();
            }

            System.out.println("Services imported. Total: " + totalProcessed + " records");
            return null;
        });
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

    public List<StopModel> getStopsByName(String searchTerm) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        double scoreThresholdPercentage = 60;

        QueryBuilder<StopModel, String> queryBuilder = stopDao.queryBuilder();
        Where<StopModel, String> where = queryBuilder.where();

        where.eq("id", new SelectArg(SqlType.STRING, searchTerm))
                .or()
                .like("id", new SelectArg(SqlType.STRING, "%" + searchTerm + "%"))
                .or()
                .like("name", new SelectArg(SqlType.STRING, "%" + searchTerm + "%"))
                .or()
                .raw(
                        "FUZZY_SCORE(name, ?) > ?",
                        new SelectArg(SqlType.STRING, searchTerm),
                        new SelectArg(SqlType.DOUBLE, scoreThresholdPercentage));

        queryBuilder.orderByRaw(
                "FUZZY_SCORE(name, ?) DESC",
                new SelectArg(SqlType.STRING, searchTerm));

        return queryBuilder.query();
    }

    public List<RouteDirection> getRoutesByName(String searchTerm) throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        double scoreThresholdPercentage = 60;

        QueryBuilder<RouteModel, String> queryBuilder = routeDao.queryBuilder();
        Where<RouteModel, String> where = queryBuilder.where();

        where.eq("id", new SelectArg(SqlType.STRING, searchTerm))
                .or()
                .like("id", new SelectArg(SqlType.STRING, "%" + searchTerm + "%"))
                .or()
                .like("longname", new SelectArg(SqlType.STRING, "%" + searchTerm + "%"))
                .or()
                .like("shortname", new SelectArg(SqlType.STRING, "%" + searchTerm + "%"))
                .or()
                .raw(
                        "FUZZY_SCORE(longname, ?) > ?",
                        new SelectArg(SqlType.STRING, searchTerm),
                        new SelectArg(SqlType.DOUBLE, scoreThresholdPercentage))
                .or()
                .raw(
                        "FUZZY_SCORE(shortname, ?) > ?",
                        new SelectArg(SqlType.STRING, searchTerm),
                        new SelectArg(SqlType.DOUBLE, scoreThresholdPercentage));

        queryBuilder.orderByRaw(
                "CASE " +
                        "WHEN FUZZY_SCORE(shortname, ?) > FUZZY_SCORE(longname, ?) THEN FUZZY_SCORE(shortname, ?) " +
                        "ELSE FUZZY_SCORE(longname, ?) END DESC",
                new SelectArg(SqlType.STRING, searchTerm),
                new SelectArg(SqlType.STRING, searchTerm),
                new SelectArg(SqlType.STRING, searchTerm),
                new SelectArg(SqlType.STRING, searchTerm));

        List<RouteModel> routes = queryBuilder.query();
        return getDirectionedRoutes(routes);
    }

    // Returns routes for both directions
    public List<RouteDirection> getDirectionedRoutes(List<RouteModel> routes) throws SQLException {
        // Add routes for both directions
        List<RouteDirection> result = new ArrayList<>();
        for (RouteModel route : routes) {
            for (Direction direction : Direction.values()) {
                TripModel trip = getDirectionTrip(route.getId(), direction);
                if (trip != null) {
                    result.add(new RouteDirection(
                            route.getId(),
                            route.getAgency(),
                            route.getLongName(),
                            route.getShortName(),
                            route.getType(),
                            trip.getDirection(),
                            trip.getHeadsign()));
                }
            }
        }
        return result;
    }

    // Gets the routes that go through a stop
    public List<RouteModel> getStopRoutes(String stopId) throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        String rawQuery = "SELECT DISTINCT r.* FROM routes r " +
                "JOIN trips t ON r.id = t.route_id " +
                "JOIN stop_times st ON t.id = st.trip_id " +
                "WHERE st.stop_id = ?";
        return routeDao.queryRaw(rawQuery, routeDao.getRawRowMapper(), stopId).getResults();
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

        int hoursToMillisec = 6 * 3600; // 6 hours
        int currentDateTime = LocalTime.now().toSecondOfDay();
        int nextHoursDate = currentDateTime + hoursToMillisec;

        boolean ascending = true;
        return stopTimeDao
                .queryBuilder()
                .orderBy("arrival_time", ascending)
                .where()
                .eq("stop_id", stopId)
                .and()
                .between("arrival_time", currentDateTime, nextHoursDate)
                .query();
    }

    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time, Date date,
            List<RealtimeStopUpdate> tripUpdates)
            throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        var stopTimes = getNextStopTimes(stopId, time);
        ArrayList<StopTimeModel> filteredStopTimes = new ArrayList<>();

        for (StopTimeModel stopTime : stopTimes) {
            for (ServiceModel service : getTripServices(stopTime.getTrip())) {
                boolean isServiceToday = (service.getExceptionType() == ServiceModel.ExceptionType.ADDED
                        && DateUtils.isSameDay(service.getDate(), date));

                if (isServiceToday) {
                    filteredStopTimes.add(stopTime);
                    break;
                }
            }
        }

        HashMap<String, StopTimeModel> stopTimesMap = new HashMap<>();
        for (StopTimeModel stopTime : stopTimes) {
            stopTimesMap.put(stopTime.getTrip().getId(), stopTime);
        }

        // Realtime trips are not filtered since some of those trips do not respect
        // service days
        for (RealtimeStopUpdate tripUpdate : tripUpdates) {
            String tripId = tripUpdate.getTripId();
            StopTimeModel stopTime = stopTimeDao
                    .queryBuilder()
                    .where()
                    .eq("trip_id", tripId)
                    .queryForFirst();
            if (stopTime != null && stopTimesMap.get(tripId) == null) {
                System.out.println("Adding trip's " + tripId + " stoptime");
                filteredStopTimes.add(stopTime);
            }
        }

        return filteredStopTimes;
    }

    // Filter next stop times by service date
    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time, Date date)
            throws SQLException {
        List<StopTimeModel> stopTimes = getNextStopTimes(stopId, time);
        ArrayList<StopTimeModel> filteredStopTimes = new ArrayList<>();

        for (StopTimeModel stopTime : stopTimes) {
            for (ServiceModel service : getTripServices(stopTime.getTrip())) {
                boolean isServiceToday = (service.getExceptionType() == ServiceModel.ExceptionType.ADDED
                        && DateUtils.isSameDay(service.getDate(), date));

                if (isServiceToday) {
                    filteredStopTimes.add(stopTime);
                    break;
                }
            }
        }
        return filteredStopTimes;
    }

    public List<ServiceModel> getServicesById(String serviceId) throws SQLException {
        Dao<ServiceModel, String> serviceDao = getDao(ServiceModel.class);
        return serviceDao
                .queryBuilder()
                .where()
                .eq("service_id", serviceId)
                .query();
    }

    public List<ServiceModel> getTripServices(TripModel trip) throws SQLException {
        if (trip == null) {
            return new ArrayList<>();
        }
        return getServicesById(trip.getServiceId());
    }

    public List<ServiceModel> getTripServices(String tripId) throws SQLException {
        Dao<TripModel, String> tripDao = getDao(TripModel.class);
        TripModel trip = tripDao.queryForId(tripId);
        return getTripServices(trip);
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

    public void addFavStop(int userId, String stopId) throws SQLException {
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

    public void addFavStop(UserModel user, StopModel stop) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);
        if (user == null || stop == null) {
            return;
        }
        favoriteStopDao.create(new FavoriteStopModel(user, stop));
    }

    public void removeFavStop(int userId, String stopId) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);

        var deleteBuilder = favoriteStopDao.deleteBuilder();
        deleteBuilder
                .where()
                .eq("user_id", userId)
                .and()
                .eq("stop_id", stopId);
        deleteBuilder.delete();
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

    public void addFavRoute(int userId, String routeId) throws SQLException {
        Dao<FavoriteRouteModel, String> favoriteRouteDao = getDao(FavoriteRouteModel.class);
        Dao<UserModel, Integer> userDao = getDao(UserModel.class);
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);

        UserModel user = userDao.queryForId(userId);
        RouteModel route = routeDao.queryForId(routeId);

        if (user == null || route == null) {
            return;
        }

        favoriteRouteDao.create(new FavoriteRouteModel(user, route));
    }

    public void removeFavRoute(int userId, String routeId) throws SQLException {
        Dao<FavoriteRouteModel, String> favoriteRouteDao = getDao(FavoriteRouteModel.class);

        var deleteBuilder = favoriteRouteDao.deleteBuilder();
        deleteBuilder
                .where()
                .eq("user_id", userId)
                .and()
                .eq("route_id", routeId);
        deleteBuilder.delete();
    }

    public List<RouteModel> getFavoriteRoutes(int userId) throws SQLException {
        Dao<FavoriteRouteModel, String> favoriteRouteDao = getDao(FavoriteRouteModel.class);

        var favorites = favoriteRouteDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .query();

        List<RouteModel> routes = new ArrayList<>();
        for (FavoriteRouteModel favorite : favorites) {
            routes.add(favorite.getRoute());
        }
        return routes;
    }

    public List<RouteDirection> getFavoriteDirectionRoutes(int userId) throws SQLException {
        List<RouteModel> routes = getFavoriteRoutes(userId);
        return getDirectionedRoutes(routes);
    }

    public boolean isFavouriteRoute(int userId, String routeId) throws SQLException {
        Dao<FavoriteRouteModel, String> favoriteLineDao = getDao(FavoriteRouteModel.class);
        var favorites = favoriteLineDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .and()
                .eq("route_id", routeId)
                .query();

        return !favorites.isEmpty();
    }

    public RouteModel getRouteById(String id) throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        return routeDao.queryForId(id);
    }

    public TripModel getDirectionTrip(String routeId, Direction direction) throws SQLException {
        Dao<TripModel, String> tripDao = getDao(TripModel.class);
        List<TripModel> trips = tripDao.queryBuilder()
                .distinct()
                .limit(1l)
                .where()
                .eq("route_id", routeId)
                .and()
                .eq("direction", direction)
                .query();

        if (trips.isEmpty()) {
            return null;
        }
        return trips.getFirst();
    }

    public List<StopModel> getOrderedStopsForRoute(String routeId, Direction direction) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        String tripId = getDirectionTrip(routeId, direction).getId();

        String rawQuery = "SELECT s.* FROM stops s " +
                "JOIN stop_times st ON s.id = st.stop_id " +
                "WHERE st.trip_id = ? " +
                "ORDER BY st.arrival_time";

        return stopDao.queryRaw(rawQuery, stopDao.getRawRowMapper(), tripId).getResults();
    }

    public int getAverageDelayForRoute(String routeId) throws SQLException {
        Dao<TripUpdateModel, String> tripUpdateDao = getDao(TripUpdateModel.class);

        // First check if there are any records for this route
        long count = tripUpdateDao.queryRawValue(
                "SELECT COUNT(*) FROM trip_updates_delays WHERE route_id = ? AND delay IS NOT NULL",
                routeId);

        if (count == 0) {
            return 0; // No delays found
        }

        long avgDelay = tripUpdateDao.queryRawValue(
                "SELECT AVG(delay) FROM trip_updates_delays WHERE route_id = ? AND delay IS NOT NULL",
                routeId);

        return (int) avgDelay;
    }

    public List<TripUpdateModel> getRouteTripUpdates(String routeId) throws SQLException {
        Dao<TripUpdateModel, String> tripUpdateDao = getDao(TripUpdateModel.class);

        String rawQuery = "SELECT t.* FROM trip_updates t " +
                "JOIN trips tr ON tr.id = t.trip_id " +
                "JOIN routes r ON r.id = tr.route_id " +
                "WHERE r.id = ?";
        return tripUpdateDao.queryRaw(rawQuery, tripUpdateDao.getRawRowMapper(), routeId).getResults();
    }

    public void createTripUpdateDelays(List<FeedEntity> tripEntities) throws SQLException {
        if (tripEntities == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        List<TripUpdateModel> updates = new ArrayList<>();
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);

        for (FeedEntity entity : tripEntities) {
            TripUpdate tripUpdate = entity.getTripUpdate();
            TripDescriptor trip = tripUpdate.getTrip();
            RouteModel routeModel = routeDao.queryForId(trip.getRouteId());
            int delay = tripUpdate.getDelay();
            boolean isDeleted = entity.getIsDeleted();

            if (routeModel == null) {
                continue;
            }

            updates.add(new TripUpdateModel(routeModel, now, delay));
        }
        this.batchCreate(TripUpdateModel.class, updates);
    }

    public void saveMetric(RealtimeMetricType type, int value) throws SQLException {
        Dao<RealtimeMetricModel, String> metricDao = getDao(RealtimeMetricModel.class);
        RealtimeMetricModel metric = new RealtimeMetricModel(type, value, LocalDateTime.now());
        metricDao.create(metric);
    }

    public List<RealtimeMetricModel> getMetrics(RealtimeMetricType type) throws SQLException {
        Dao<RealtimeMetricModel, String> metricDao = getDao(RealtimeMetricModel.class);
        return metricDao.queryBuilder()
                .orderBy("createdAt", false) // Order by created_at descending
                .where()
                .eq("type", type)
                .query();
    }

    public <ID, MODEL> void batchCreateOrUpdate(Class<MODEL> modelClass, List<MODEL> data) {
        Dao<MODEL, ID> dao = getDao(modelClass);
        if (dao == null) {
            System.out.println("Create or update failed: " + modelClass.getName() + " not found in the DAOs");
            return;
        }

        try {
            dao.callBatchTasks(() -> {
                for (MODEL obj : data) {
                    dao.createOrUpdate(obj);
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <ID, MODEL> void batchCreate(Class<MODEL> modelClass, List<MODEL> data) {
        Dao<MODEL, ID> dao = getDao(modelClass);
        if (dao == null) {
            System.out.println("Create or update failed: " + modelClass.getName() + " not found in the DAOs");
            return;
        }

        try {
            dao.callBatchTasks(() -> {
                for (MODEL obj : data) {
                    dao.create(obj);
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
