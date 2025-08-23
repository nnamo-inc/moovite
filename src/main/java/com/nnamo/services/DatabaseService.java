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
import com.nnamo.utils.Utils;

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

/**
 * Service class for managing database operations
 * related to GTFS data, users, favorites, routes,
 * stops, and metrics.
 * <ul>
 * <li>Initializes DAOs and tables</li>
 * <li>Imports and caches GTFS data</li>
 * <li>Provides query methods for models</li>
 * <li>Handles favorite stops and routes</li>
 * <li>Supports fuzzy search for stops and routes</li>
 * <li>Manages realtime metrics and trip updates</li>
 * </ul>
 *
 * @author Samuele Lombardi, Davide Galilei
 */
public class DatabaseService {

    private final JdbcConnectionSource connection;
    private final HashMap<Class<?>, Dao<?, ?>> daos = new HashMap<>();

    /**
     * Creates the DatabaseService by creating a connection to the SQLite database
     * and by initializing tables and DAOs
     */
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

    /**
     * Loads static GTFS data in the database
     * 
     * @param gtfs StaticGtfsService the GTFS service
     * @throws SQLException       if query to the connection goes wrong
     * @throws IOException        if static gtfs data can be loaded
     * @throws URISyntaxException if static gtfs data can be loaded
     */
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

    /**
     * Checks if database needs caching
     * 
     * @throws SQLException if query to the connection goes wrong
     */
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

    /**
     * Returns a Model's DAO (Database Access Object) by providing the Class of the
     * model
     * 
     * @author Samuele Lombardi
     * @param modelClass the Class of the model to return
     * @return Dao if the model's dao exists, null otherwise
     */
    @SuppressWarnings("unchecked")
    public <T, ID> Dao<T, ID> getDao(Class<T> modelClass) {
        return (Dao<T, ID>) daos.get(modelClass);
    }

    /**
     * Returns every stop instance from the database
     * 
     * @author Samuele Lombardi
     * @return stop models (empty list if no stop found)
     */
    public List<StopModel> getAllStops() throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        return stopDao.queryForAll();
    }

    /**
     * Returns a stop by its ID
     * 
     * @param id String
     * @author Samuele Lombardi
     * @return StopModel
     */
    public StopModel getStopById(String id) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        return stopDao.queryForId(id);
    }

    /**
     * Fuzzy search stops by its name. Fuzzy finding means finding strings that
     * match a pattern approximately
     * 
     * @param searchTerm String
     * @author Davide Galilei
     * @throws SQLException if query fails
     * @return the list of models with the approximate match
     */
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

    /**
     * Fuzzy search routes by its name. Fuzzy finding means finding strings that
     * match a pattern approximately
     * 
     * @param searchTerm The search term for the route
     * @param routeType  The type of the route (bus, tram, etc.)
     * @author Davide Galilei
     * @throws SQLException if query fails
     * @return the list of routes with the approximate match
     */
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

//        if (routeType != RouteType.ALL) {
//            where.and().eq("type", new SelectArg(SqlType.UNKNOWN, routeType));


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

    /**
     * For each route in the provided list, this method creates a directioned route
     * for each available direction. Which means that the method returns all the
     * provided routes but for each directions. It is pretty useful in order to
     * display
     * routes' trips based on the direction (OUTGOING, INGOING)
     * 
     * @param routes The routes which we want for each direction
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of directioned routes
     */
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

    /**
     * Gets the unique routes that go through a specific stop
     * 
     * @param stopId The ID of the stop
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of routes that go through that stop
     */
    // Gets the routes that go through a stop
    public List<RouteModel> getStopRoutes(String stopId) throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        String rawQuery = "SELECT DISTINCT r.* FROM routes r " +
                "JOIN trips t ON r.id = t.route_id " +
                "JOIN stop_times st ON t.id = st.trip_id " +
                "WHERE st.stop_id = ?";
        return routeDao.queryRaw(rawQuery, routeDao.getRawRowMapper(), stopId).getResults();
    }

    /**
     * Gets the stop times for a specific stop
     * 
     * @param stopId The ID of the stop
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of ALL stoptimes for that stop
     */
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

    /**
     * Gets the next 6 hours stop times for a specific stop based on the time
     * provided
     * 
     * @param stopId The ID of the stop
     * @param time   the time from when we want the stop times to start
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of next 6 hours (based on the time provided) stoptimes for
     *         that stop
     */
    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time) throws SQLException {
        return getNextStopTimes(stopId, time, 6);
    }

    /**
     * Gets the next X hours stop times for a specific stop based on the time
     * provided
     * 
     * @param stopId The ID of the stop
     * @param time   the time from when we want the stop times to start
     * @param hours  the hours range from the time provided
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of next X hours (based on the time provided) stoptimes for
     *         that stop
     */
    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time, int hours) throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        int hoursToMillisec = hours * 3600;
        int nextHoursDate = time.toSecondOfDay() + hoursToMillisec;

        boolean ascending = true;
        return stopTimeDao
                .queryBuilder()
                .orderBy("arrival_time", ascending)
                .where()
                .eq("stop_id", stopId)
                .and()
                .between("arrival_time", time.toSecondOfDay(), nextHoursDate)
                .query();
    }

    /**
     * Gets the next X hours stop times for a specific stop based on the time
     * provided and the date provided. This returns only the stop times for trips
     * that run on the provided day.
     * 
     * @param stopId The ID of the stop
     * @param time   the time from when we want the stop times to start
     * @param hours  the hours range from the time provided
     * @param date   the date of the returned stop times
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of next X hours (based on the time and date provided)
     *         stoptimes for
     *         that stop
     */
    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time, Date date, int hours)
            throws SQLException {
        List<StopTimeModel> stopTimes = getNextStopTimes(stopId, time, hours);
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

    /**
     * Gets the next X hours stop times for a specific stop based on the time
     * provided and the date provided. This returns only the stop times for trips
     * that run on the provided day. It also provides the static stop times of
     * realtime trips
     * 
     * @param stopId      The ID of the stop
     * @param time        the time from when we want the stop times to start
     * @param hours       the hours range from the time provided
     * @param date        the date of the returned stop times
     * @param tripUpdates the trip updates for that stop
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the list of next X hours (based on the time and date provided + the
     *         realtime trips) stoptimes for
     *         that stop
     */
    public List<StopTimeModel> getNextStopTimes(String stopId, LocalTime time, int hours, Date date,
            List<RealtimeStopUpdate> tripUpdates)
            throws SQLException {
        Dao<StopTimeModel, String> stopTimeDao = getDao(StopTimeModel.class);
        List<StopTimeModel> filteredStopTimes = getNextStopTimes(stopId, time, date, hours);

        HashMap<String, StopTimeModel> stopTimesMap = new HashMap<>();
        for (StopTimeModel stopTime : filteredStopTimes) {
            stopTimesMap.put(stopTime.getTrip().getId(), stopTime);
        }

        List<String> tripIds = new ArrayList<>();
        for (RealtimeStopUpdate update : tripUpdates) {
            tripIds.add(update.getTripId());
        }

        // Realtime trips are not filtered since some of those trips do not respect
        // service days
        System.out.println(stopId);
        List<StopTimeModel> realtimeStopModels = stopTimeDao
                .queryBuilder()
                .where()
                .eq("stop_id", stopId)
                .and()
                .in("trip_id", tripIds)
                .query();

        // Adds stop times of valid realtime stops
        for (StopTimeModel stopTime : realtimeStopModels) {
            if (stopTimesMap.get(stopTime.getTrip().getId()) == null) { // Checks if the realtime stoptime is already
                                                                        // present
                System.out.println("");
                filteredStopTimes.add(stopTime);
            }
        }

        return filteredStopTimes;
    }

    /**
     * Get services by ID
     * 
     * @param serviceId The id of the services to get
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return services with that ID
     */
    public List<ServiceModel> getServicesById(String serviceId) throws SQLException {
        Dao<ServiceModel, String> serviceDao = getDao(ServiceModel.class);
        return serviceDao
                .queryBuilder()
                .where()
                .eq("service_id", serviceId)
                .query();
    }

    /**
     * Get services of a specific trip by model
     * 
     * @param trip The model trip with the services
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return trip's services
     */
    public List<ServiceModel> getTripServices(TripModel trip) throws SQLException {
        if (trip == null) {
            return new ArrayList<>();
        }
        return getServicesById(trip.getServiceId());
    }

    /**
     * Get services of a specific trip by ID
     * 
     * @param tripId The ID of the trip with the services
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return trip's services
     */
    public List<ServiceModel> getTripServices(String tripId) throws SQLException {
        Dao<TripModel, String> tripDao = getDao(TripModel.class);
        TripModel trip = tripDao.queryForId(tripId);
        return getTripServices(trip);
    }

    /**
     * Creates user from model
     * 
     * @param user User model
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
    public void addUser(UserModel user) throws SQLException {
        getDao(UserModel.class).createIfNotExists(user);
    }

    /**
     * Creates user from username and password
     * 
     * @param username User's username
     * @param passwordHash User's password
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
    public void addUser(String username, String passwordHash) throws SQLException {
        getDao(UserModel.class)
                .createIfNotExists(new UserModel(username, passwordHash));
    }

    /**
     * Get user by the username
     * 
     * @param username User's username
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the user with the provided name, or null if doesn't exist
     */
    public UserModel getUserByName(String username) throws SQLException {
        var users = getDao(UserModel.class).queryForEq("username", username);
        if (users.size() >= 1) {
            return users.getFirst();
        }
        return null;
    }

    /**
     * Get user by ID
     * 
     * @param id User's id
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return the user with the provided ID, or null if doesn't exist
     */
    public UserModel getUserById(int id) throws SQLException {
        return getDao(UserModel.class).queryForId(id);
    }

    /**
     * Add favorite stop to a user's favorite stops list
     * 
     * @param userId The id of the user adding the favorite stop
     * @param stopId The id of the favorite stop
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
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

    /**
     * Add favorite stop to a user's favorite stops list
     * 
     * @param user User model
     * @param stop Stop model
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
    public void addFavStop(UserModel user, StopModel stop) throws SQLException {
        Dao<FavoriteStopModel, String> favoriteStopDao = getDao(FavoriteStopModel.class);
        if (user == null || stop == null) {
            return;
        }
        favoriteStopDao.create(new FavoriteStopModel(user, stop));
    }

    /**
     * Remove favorite stop from a user's favorite stops list
     * 
     * @param userId The id of the user adding the favorite stop
     * @param stopId The id of the favorite stop
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
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

    /**
     * Get user's favorite stops
     * 
     * @param userId The id of the user who has the favorite stops
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return list of user's favorite stops
     */
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

    /**
     * Get user's favorite stops
     * 
     * @param user The model of the user who has the favorite stops
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return list of user's favorite stops
     */
    public List<StopModel> getFavoriteStops(UserModel user) throws SQLException {
        return getFavoriteStops(user.getId());
    }

    /**
     * Get user's favorite stops. Fuzzy search
     * 
     * @param userId     The id of the user adding the favorite stop
     * @param searchTerm Fuzzy pattern to search the stops
     * @param routeType  The type of the route (bus, tram, etc.)
     * @author Davide Galilei
     * @throws SQLException if query fails
     */
    public List<StopModel> getFavoriteStopsByName(int userId, String searchTerm)
            throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);
        double scoreThresholdPercentage = 60;

        String rawQuery = "SELECT s.* FROM stops s " +
                "JOIN favorite_stops fs ON s.id = fs.stop_id " +
                "WHERE fs.user_id = ? AND (" +
                "s.id = ? OR " +
                "s.id LIKE ? OR " +
                "s.name LIKE ? OR " +
                "FUZZY_SCORE(s.name, ?) > ?) " +
                "ORDER BY FUZZY_SCORE(s.name, ?) DESC";

        return stopDao.queryRaw(rawQuery, stopDao.getRawRowMapper(),
                String.valueOf(userId), searchTerm, "%" + searchTerm + "%", "%" + searchTerm + "%",
                searchTerm, String.valueOf(scoreThresholdPercentage), searchTerm).getResults();
    }

    /**
     * Get user's favorite routes. Fuzzy search
     * 
     * @param userId     The id of the user adding the favorite stop
     * @param searchTerm Fuzzy pattern to search the stops
     * @param routeType  The type of the stop. "Which routes does it serve?" (bus,
     *                   tram, etc.)
     * @author Davide Galilei
     * @throws SQLException if query fails
     */
    public List<RouteDirection> getFavoriteRoutesByName(int userId, String searchTerm)
            throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        double scoreThresholdPercentage = 60;

        String rawQuery = "SELECT r.* FROM routes r " +
                "JOIN favorite_routes fr ON r.id = fr.route_id " +
                "WHERE fr.user_id = ? AND (" +
                "r.id = ? OR " +
                "r.id LIKE ? OR " +
                "r.longname LIKE ? OR " +
                "r.shortname LIKE ? OR " +
                "FUZZY_SCORE(r.longname, ?) > ? OR " +
                "FUZZY_SCORE(r.shortname, ?) > ?)";

        String orderBy = " ORDER BY CASE " +
                "WHEN FUZZY_SCORE(r.shortname, ?) > FUZZY_SCORE(r.longname, ?) THEN FUZZY_SCORE(r.shortname, ?) " +
                "ELSE FUZZY_SCORE(r.longname, ?) END DESC";

//        if (routeType != RouteType.ALL) {
//            rawQuery += " AND r.type = ?" + orderBy;
//            List<RouteModel> routes = routeDao.queryRaw(rawQuery, routeDao.getRawRowMapper(),
//                    String.valueOf(userId), searchTerm, "%" + searchTerm + "%", "%" + searchTerm + "%",
//                    "%" + searchTerm + "%", searchTerm, String.valueOf(scoreThresholdPercentage),
//                    searchTerm, String.valueOf(scoreThresholdPercentage), routeType.name(),
//                    searchTerm, searchTerm, searchTerm, searchTerm).getResults();
//            return getDirectionedRoutes(routes);
//        } else {
            rawQuery += orderBy;
            List<RouteModel> routes = routeDao.queryRaw(rawQuery, routeDao.getRawRowMapper(),
                    String.valueOf(userId), searchTerm, "%" + searchTerm + "%", "%" + searchTerm + "%",
                    "%" + searchTerm + "%", searchTerm, String.valueOf(scoreThresholdPercentage),
                    searchTerm, String.valueOf(scoreThresholdPercentage),
                    searchTerm, searchTerm, searchTerm, searchTerm).getResults();
            return getDirectionedRoutes(routes);
//        }
    }

    /**
     * Get route by ID
     * 
     * @param id route ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return RouteModel with the specified ID, or null if not found
     */
    public RouteModel getRouteById(String id) throws SQLException {
        Dao<RouteModel, String> routeDao = getDao(RouteModel.class);
        return routeDao.queryForId(id);
    }

    /**
     * Get a trip for a specific route and direction
     * 
     * @param routeId   String route ID
     * @param direction Direction enum (INBOUND/OUTBOUND)
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return TripModel for the route in the specified direction, or null if not
     *         found
     */
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

    /**
     * Get ordered stops for a route in a specific direction
     * 
     * @param routeId   String route ID
     * @param direction Direction enum (INBOUND/OUTBOUND)
     * @author Davide Galilei
     * @throws SQLException if query fails
     * @return List of stops ordered by arrival time for the route
     */
    public List<StopModel> getOrderedStopsForRoute(String routeId, Direction direction) throws SQLException {
        Dao<StopModel, String> stopDao = getDao(StopModel.class);

        String tripId = getDirectionTrip(routeId, direction).getId();

        String rawQuery = "SELECT s.* FROM stops s " +
                "JOIN stop_times st ON s.id = st.stop_id " +
                "WHERE st.trip_id = ? " +
                "ORDER BY st.arrival_time";

        return stopDao.queryRaw(rawQuery, stopDao.getRawRowMapper(), tripId).getResults();
    }

    /**
     * Calculate average delay for a route based on trip updates
     * 
     * @param routeId String route ID
     * @author Davide Galilei
     * @throws SQLException if query fails
     * @return average delay in seconds, or 0 if no delays found
     */
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

    /**
     * Get trip updates for a specific route
     * 
     * @param routeId String route ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return List of trip updates for the route
     */
    public List<TripUpdateModel> getRouteTripUpdates(String routeId) throws SQLException {
        Dao<TripUpdateModel, String> tripUpdateDao = getDao(TripUpdateModel.class);

        String rawQuery = "SELECT t.* FROM trip_updates t " +
                "JOIN trips tr ON tr.id = t.trip_id " +
                "JOIN routes r ON r.id = tr.route_id " +
                "WHERE r.id = ?";
        return tripUpdateDao.queryRaw(rawQuery, tripUpdateDao.getRawRowMapper(), routeId).getResults();
    }

    /**
     * Create trip update delays from GTFS realtime feed entities
     * 
     * @param tripEntities List of GTFS realtime feed entities
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
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

    /**
     * Save a realtime metric to the database
     * 
     * @param type  RealtimeMetricType the type of metric
     * @param value int the metric value
     * @author Davide Galilei
     * @throws SQLException if query fails
     */
    public void saveMetric(RealtimeMetricType type, int value) throws SQLException {
        Dao<RealtimeMetricModel, String> metricDao = getDao(RealtimeMetricModel.class);
        RealtimeMetricModel metric = new RealtimeMetricModel(type, value, LocalDateTime.now());
        metricDao.create(metric);
    }

    /**
     * Get metrics of a specific type
     * 
     * @param type RealtimeMetricType the type of metric to retrieve
     * @author Davide Galilei
     * @throws SQLException if query fails
     * @return List of metrics ordered by creation date (newest first)
     */
    public List<RealtimeMetricModel> getMetrics(RealtimeMetricType type) throws SQLException {
        Dao<RealtimeMetricModel, String> metricDao = getDao(RealtimeMetricModel.class);
        return metricDao.queryBuilder()
                .orderBy("createdAt", false) // Order by created_at descending
                .where()
                .eq("type", type)
                .query();
    }

    /**
     * Batch create or update multiple models
     * 
     * @param <ID>       ID type
     * @param <MODEL>    Model type
     * @param modelClass Class of the model
     * @param data       List of models to create or update
     * @author Samuele Lombardi
     */
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

    /**
     * Batch create or update multiple models
     * 
     * @param <ID>       ID type
     * @param <MODEL>    Model type
     * @param modelClass Class of the model
     * @param data       List of models to create or update
     * @author Davide Galilei
     */
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

    /**
     * Check if a stop is in user's favorites
     * 
     * @param userId int user ID
     * @param stopId String stop ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return true if stop is favorite, false otherwise
     */
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

    /**
     * Add route to user's favorite routes
     * 
     * @param userId  int user ID
     * @param routeId String route ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
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

    /**
     * Remove route from user's favorite routes
     * 
     * @param userId  int user ID
     * @param routeId String route ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     */
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

    /**
     * Get user's favorite routes
     * 
     * @param userId int user ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return List of user's favorite routes
     */
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

    /**
     * Get user's favorite routes with direction information
     * 
     * @param userId int user ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return List of directioned routes from user's favorites
     */
    public List<RouteDirection> getFavoriteDirectionRoutes(int userId) throws SQLException {
        List<RouteModel> routes = getFavoriteRoutes(userId);
        return getDirectionedRoutes(routes);
    }

    /**
     * Check if a route is in user's favorites
     * 
     * @param userId  int user ID
     * @param routeId String route ID
     * @author Samuele Lombardi
     * @throws SQLException if query fails
     * @return true if route is favorite, false otherwise
     */
    public boolean isFavouriteRoute(int userId, String routeId) throws SQLException {
        Dao<FavoriteRouteModel, String> favoriteRouteDao = getDao(FavoriteRouteModel.class);
        var favorites = favoriteRouteDao
                .queryBuilder()
                .where()
                .eq("user_id", userId)
                .and()
                .eq("route_id", routeId)
                .query();

        return !favorites.isEmpty();
    }
}
