package org.TransportModel.network.io;

import org.TransportModel.Config;
import org.TransportModel.io.TabularFileUtil;
import org.TransportModel.network.Link;
import org.TransportModel.network.Link.ROUTE_TYPE;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.network.io.GTFS_CONTAINERS.Route;
import org.TransportModel.network.io.GTFS_CONTAINERS.Section;
import org.TransportModel.network.io.GTFS_CONTAINERS.Trip;
import org.TransportModel.network.io.GTFS_CONTAINERS.TripStop;
import org.TransportModel.utils.CoordinateUtils;
import org.locationtech.jts.geom.Coordinate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Reads GTFS files to create a network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public final class NetworkReaderGTFS
{
    private NetworkReaderGTFS(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Create a network with GTFS files*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Network readFiles()
    {
        createMissingFiles();
        Network network = new Network();
        HashMap<String,String> stopParent = readStopFile(network);
        readSectionsFile(network, stopParent);
        readTransfersFile(network, stopParent);
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads stop file from a GTFS  and for each stop:
     * If the stop does not have a parent station, a new Node object representing the stop is created and added to the network
     * If the stop has a parent station, a mapping of the stop's ID to its parent station's ID is added to the HashMap
     * @param network The network to which the Node objects will be added
     * @return A Map<stopId,stopParentId> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static HashMap<String,String> readStopFile(Network network)
    {
        final String ID = "stop_id", NAME = "stop_name", LON = "stop_lon", LAT = "stop_lat", PARENT_ID = "parent_station";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.stops);
        HashMap<String,String> stopParent = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String nodeName = val.get(NAME), nodeId = val.get(ID), parentNodeId = val.get(PARENT_ID);
            double lon = Double.parseDouble(val.get(LON)), lat = Double.parseDouble(val.get(LAT));
            //If stop don't have parent, had node to the network else add to map
            if(parentNodeId.isEmpty())
                network.addNode(new Node(nodeId, nodeName, new Coordinate(lon,lat)));
            else
                stopParent.put(nodeId,parentNodeId);
        });
        return stopParent;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a route section file (previously created), for each section creates and adds a link to the network
     * If either of the stops within a section has a parent station, the link is established with the parent station
     * @param network The network to which the links will be added
     * @param stopParent A Map<stopId,stopParentId> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void readSectionsFile(Network network, HashMap<String, String> stopParent)
    {
        final String AVG_TIME = "avg_time", TO_ID = "to_stop_id", FREQUENCY = "frequency", TYPE = "route_type";
        final String FROM_ID = "from_stop_id", FIRST = "first_traversal" , LAST = "last_traversal", NAME = "route_name";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.routeSections);
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String linkName = val.get(NAME), fromId = val.get(FROM_ID), toId = val.get(TO_ID);
            double timeInS = Double.parseDouble(val.get(AVG_TIME)), frequency = Double.parseDouble(val.get(FREQUENCY));
            int firstTraversalInS = Integer.parseInt(val.get(FIRST)), lastTraversalInS = Integer.parseInt(val.get(LAST));
            //Create and add Link (if in period set in config)
            if(Config.getTransportValues().areHoursValid(firstTraversalInS, lastTraversalInS)) {
                Node fromNode = stopParent.containsKey(fromId)?network.getNode(stopParent.get(fromId)):network.getNode(fromId);
                Node toNode = stopParent.containsKey(toId)?network.getNode(stopParent.get(toId)):network.getNode(toId);
                ROUTE_TYPE routeType = getRouteType(Integer.parseInt(val.get(TYPE)));
                double capacityPerHour = Config.getTransportCapacity(routeType)/frequency/3600;
                double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(),toNode.getCoordinate());
                double speedInMS = lengthInM / timeInS;
                Link link = new Link(fromNode, toNode, speedInMS, capacityPerHour,lengthInM, routeType, linkName);
                    network.addLink(link);}
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a transfer file from a GTFS, for each transfer creates and adds a link to the network
     * If either of the stops within a section has a parent station, the link is established with the parent station
     * @param network The network to which the links will be added
     * @param stopParent A Map<stopId,stopParentId> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void readTransfersFile(Network network, HashMap<String, String> stopParent)
    {
        final String FROM_ID = "from_stop_id", TO_ID = "to_stop_id", TIME = "min_transfer_time";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.transfers);
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String fromId = val.get(FROM_ID), toId = val.get(TO_ID);
            double timeInS = Double.parseDouble(val.get(TIME));
            //Create and add Link
            String linkName = "Transfer";
            Node fromNode = stopParent.containsKey(fromId)?network.getNode(stopParent.get(fromId)):network.getNode(fromId);
            Node toNode = stopParent.containsKey(toId)?network.getNode(stopParent.get(toId)):network.getNode(toId);
            ROUTE_TYPE routeType = ROUTE_TYPE.FOOT;
            double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(), toNode.getCoordinate());
            double speedInMS = lengthInM / timeInS;
            double capacityPerHour = Double.MAX_VALUE;
            network.addLink(new Link(fromNode, toNode, speedInMS, capacityPerHour, lengthInM, routeType, linkName));
            network.addLink(new Link(toNode, fromNode, speedInMS, capacityPerHour, lengthInM, routeType, linkName));
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads the route file from a GTFS and for each route stores the data in a route object
     * containing the route names and types
     * @return A Map<routeId,Route> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static HashMap<String,Route> readRouteFile()
    {
        final String ID = "route_id",TYPE = "route_type", NAME = "route_long_name";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.routes);
        HashMap<String,Route> routes = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String id = val.get(ID), name = val.get(NAME);
            int type = Integer.parseInt(val.get(TYPE));
            //Add to map
            routes.put(id,new Route(type,name));
        });
        return routes;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads the trip file from a GTFS and for each trip stores the data in a trip object
     * containing for all the trip's stop the arrival time the stop id and the stop sequence
     * @return all the trips <tripId,trip> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static HashMap<String,Trip> readTimeFile()
    {
        final  String TRIP_ID = "trip_id", ARRIVAL_TIME = "arrival_time", STOP_ID = "stop_id",SEQUENCE = "stop_sequence";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.stopTimes);
        HashMap<String,Trip> trips = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String stopId = val.get(STOP_ID), tripId = val.get(TRIP_ID);
            int sequence = Integer.parseInt(val.get(SEQUENCE));
            int[] arrivalTime = Arrays.stream(val.get(ARRIVAL_TIME).split(":")).mapToInt(Integer::parseInt).toArray();
            //Create a TripStop object to contain data and add it to the associated Trip object
            int arrivalTimeInS = arrivalTime[0] * 3600 + arrivalTime[1] * 60 + arrivalTime[2];
            TripStop tripStop = new TripStop(stopId,sequence,arrivalTimeInS);
            trips.putIfAbsent(tripId, new Trip());
            trips.get(tripId).addStop(tripStop);
        });
        return trips;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Associate each route with all its trips
     * @param routes all the routes <routeId,route>
     * @param trips all the trips <tripId,trip>
     * @return route-trips map <route,List<trip>> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static HashMap<Route,List<Trip>> readTripFile(HashMap<String,Route> routes, HashMap<String,Trip> trips)
    {
        final String ROUTE_ID = "route_id", ID = "trip_id";
        Path filePath = Paths.get(Config.getNetworkFiles().gtfs.trips);
        HashMap<Route,List<Trip>> routeTrips = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(val) -> {
            //File values
            String routeId = val.get(ROUTE_ID), tripId = val.get(ID);
            //Add the trip to the route's list
            Route route = routes.get(routeId);
            Trip trip = trips.get(tripId);
            routeTrips.putIfAbsent(route,new ArrayList<>());
            routeTrips.get(route).add(trip);
        });
        return routeTrips;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Create missing files (not obligatory files) toDO create transfers.txt if missing*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createMissingFiles()
    {
        Path sectionsPath = Paths.get(Config.getNetworkFiles().gtfs.routeSections);
        if(!Files.exists(sectionsPath))
            createSectionsFile();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Create the route sections file based on stopTimes, routes and trips files
     * Route section = fragment of a route connecting two stops */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createSectionsFile()
    {
        Path routeSectionsPath = Paths.get(Config.getNetworkFiles().gtfs.routeSections);
        //Extract data from existing files
        HashMap<String, Route> routes = readRouteFile();
        HashMap<String, Trip> trips = readTimeFile();
        HashMap<Route,List<Trip>> routeTripsMap = readTripFile(routes,trips);
        //1 Map = 1 line of the file, key = header, value = value
        List<HashMap<String,String>> fileLines = createSectionFileLines(routeTripsMap);
        TabularFileUtil.writeFile(",", routeSectionsPath, fileLines);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates a file line for each section of each Route for the route section file
     * @param routeTripsMap the map of all the route associated with all their trips
     * @return a list of map, each map corresponding to a line of the file (keys = headers, values = values) */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<HashMap<String,String>> createSectionFileLines(HashMap<Route,List<Trip>> routeTripsMap)
    {
        final String AVG_TIME="avg_time",TO_ID="to_stop_id",FREQUENCY="frequency",ROUTE_TYPE="route_type";
        final String FROM_ID="from_stop_id",FIRST="first_traversal",LAST="last_traversal",ROUTE_NAME="route_name";
        List<HashMap<String,String>> fileLines = new ArrayList<>();
        for (HashMap.Entry<Route,List<Trip>> entry : routeTripsMap.entrySet()) {
            Route route = entry.getKey();
            List<Trip> tripsOfTheRoute = entry.getValue();
            List<Section> sectionsOfTheRoute = createSections(tripsOfTheRoute);
            for(Section section:sectionsOfTheRoute) {
                fileLines.add(new HashMap<String,String>() {{
                    put(ROUTE_TYPE,""+route.getType());
                    put(ROUTE_NAME,route.getName());
                    put(FROM_ID,section.getFromId());
                    put(TO_ID,section.getToId());
                    put(FREQUENCY,""+section.getAverageFrequency());
                    put(AVG_TIME,""+section.getAverageTraversalTime());
                    put(FIRST,""+section.getFirstTraversal());
                    put(LAST,""+section.getLastTraversal());}});}}
        return fileLines;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each consecutive two stops, create a Section object which contains the two stops id,
     * the first and last traversal schedule and the average traversal time
     * @param trips the list of trip of a route
     * @return the list of sections created */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<Section> createSections(List<Trip> trips)
    {
        HashMap<String, Section> sectionsMap = new HashMap<>();
        for(Trip trip:trips) {
            List<TripStop> stops =  new ArrayList<>(trip.getStops().values());
            //Sort stops by sequence and create a section for each two consecutive stops
            stops.sort(Comparator.comparingInt(TripStop::getSequence));
            for (int i = 0; i < stops.size()-1;i++) {
                TripStop fromStop = stops.get(i);
                TripStop toStop = stops.get(i+1);
                String sectionId = fromStop.getId()+":"+toStop.getId();
                sectionsMap.putIfAbsent(sectionId,new Section(fromStop.getId(),toStop.getId()));
                sectionsMap.get(sectionId).addTraversal(fromStop.getArrivalTime(),toStop.getArrivalTime());}}
        return new ArrayList<>(sectionsMap.values());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Returns the route type corresponding to the given gtfs route_type code
     * @param gtfsRouteType the gtfs route_type code
     * @return the route type corresponding to the given route_type code */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static ROUTE_TYPE getRouteType(int gtfsRouteType)
    {
        switch(gtfsRouteType){
            case 0:return ROUTE_TYPE.TRAM_OR_LIGHT_SUBWAY;
            case 1:return ROUTE_TYPE.SUBWAY;
            case 2:return ROUTE_TYPE.TRAIN;
            case 3:return ROUTE_TYPE.BUS;
            default:return ROUTE_TYPE.UNDEFINED;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Splits a string based on comma delimiters (Comma between quote are ignored)
     * @param input the string to split
     * @return an array of strings obtained by splitting the input string */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static String[] split(String input)
    {
        List<String> strings = new ArrayList<>();
        StringBuilder currentString = new StringBuilder();
        boolean insideQuotes = false;
        for (char c : input.toCharArray()) {
            if (c == '"')
                insideQuotes = !insideQuotes;
            else if (c == ',' && !insideQuotes) {
                strings.add(currentString.toString().trim());
                currentString = new StringBuilder();}
            else
                currentString.append(c);}
        strings.add(currentString.toString().trim());
        return strings.toArray(new String[0]);
    }
}