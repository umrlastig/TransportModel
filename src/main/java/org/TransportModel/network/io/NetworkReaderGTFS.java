package org.TransportModel.network.io;

import org.TransportModel.Config;
import org.TransportModel.io.TabularFileUtil;
import org.TransportModel.network.io.GTFS_CONTAINERS.*;
import org.TransportModel.network.Link.ROUTE_TYPE;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.utils.CoordinateUtils;
import org.locationtech.jts.geom.Coordinate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Reads GTFS folder to fill a network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public final class NetworkReaderGTFS
{
    private NetworkReaderGTFS(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a GTFS folder and fill the network with data */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Network readFiles()
    {
        createMissingFiles();
        Network network = readStopFile();
        readSectionsFile(network);
        readTransfersFile(network);
        readPathwaysFile(network);
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network readStopFile()
    {
        final String ID = "stop_id", NAME = "stop_name", LON = "stop_lon", LAT = "stop_lat", PARENT_ID = "parent_station";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.stops);
        Network network = new Network();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String stopId = values[headers.indexOf(ID)];
            String name = values[headers.indexOf(NAME)];
            String parentStopId = values[headers.indexOf(PARENT_ID)];
            double lon = Double.parseDouble(values[headers.indexOf(LON)]);
            double lat = Double.parseDouble(values[headers.indexOf(LAT)]);
            //Add node to network
            network.addNode(new Node(stopId,name,new Coordinate(lon,lat)));
            //Link to parent if has parent node
            if(!parentStopId.isEmpty()){
                if(!network.containsNode(parentStopId))//(if parent not already added, create a Node to represents it)
                    network.addNode(new Node(parentStopId,new Coordinate(lon,lat)));
                network.addLink(new Link(network.getNode(parentStopId),network.getNode(stopId),"parentChild"));
                network.addLink(new Link(network.getNode(stopId),network.getNode(parentStopId),"parentChild"));}
        });
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void readSectionsFile(Network network)
    {
        final String AVG_TIME="avg_time",TO_ID="to_stop_id",FREQUENCY="frequency",TYPE="route_type";
        final String FROM_ID="from_stop_id",FIRST="first_traversal",LAST="last_traversal",ROUTE_NAME="route_name";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.routeSections);
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            String name = values[headers.indexOf(ROUTE_NAME)];
            int firstTraversal = Integer.parseInt(values[headers.indexOf(FIRST)]);
            int lastTraversal = Integer.parseInt(values[headers.indexOf(LAST)]);
            int type = Integer.parseInt(values[headers.indexOf(TYPE)]);
            double timeInS = Double.parseDouble(values[headers.indexOf(AVG_TIME)]);
            double frequency = Double.parseDouble(values[headers.indexOf(FREQUENCY)]);
            if(!Config.getInstance().transportValues.validHours.isValid(firstTraversal,lastTraversal)){return;}
            //Create and add Link
            Node fromNode = network.getNode(fromId);
            Node toNode = network.getNode(toId);
            ROUTE_TYPE routeType = getRouteType(type);
            double capacityPerHour = Config.getInstance().transportValues.getCapacity(routeType) / frequency / 3600;
            double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(),toNode.getCoordinate());
            double speedInMS = lengthInM / timeInS;
            Link link = new Link(fromNode, toNode, speedInMS, capacityPerHour,lengthInM,routeType,name);
            network.addLink(link);
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void readTransfersFile(Network network)
    {
        final String FROM_ID = "from_stop_id", TO_ID = "to_stop_id", TIME = "min_transfer_time";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.transfers);
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            double timeInS = Double.parseDouble(values[headers.indexOf(TIME)]);
            //Create and add Link
            String name = "Transfer";
            Node fromNode = network.getNode(fromId);
            Node toNode = network.getNode(toId);
            Link.ROUTE_TYPE routeType = Link.ROUTE_TYPE.FOOT;
            double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(), toNode.getCoordinate());
            double speedInMS = lengthInM / timeInS;
            double capacityPerHour = 999999;
            network.addLink(new Link(fromNode, toNode, speedInMS, capacityPerHour, lengthInM, routeType, name));
            network.addLink(new Link(toNode, fromNode, speedInMS, capacityPerHour, lengthInM, routeType, name));
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void readPathwaysFile(Network network)
    {
        final String LENGTH="length",FROM_ID="from_stop_id",TO_ID="to_stop_id",BI="is_bidirectional",TIME="traversal_time";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.pathways);
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            boolean bidirectional = values[headers.indexOf(BI)].equals("1");
            double lengthInM = Double.parseDouble(values[headers.indexOf(LENGTH)]);
            double timeInS = Double.parseDouble(values[headers.indexOf(TIME)]);
            //Create and add Link
            String name = "Pathway";
            Node fromNode = network.getNode(fromId);
            Node toNode = network.getNode(toId);
            Link.ROUTE_TYPE routeType = Link.ROUTE_TYPE.FOOT;
            double speedInMS = lengthInM / timeInS;
            double capacityPerHour = 999999;
            network.addLink(new Link(fromNode,toNode,speedInMS,capacityPerHour,lengthInM,routeType,name));
            if(bidirectional)
                network.addLink(new Link(toNode, fromNode, speedInMS, capacityPerHour, lengthInM, routeType,name));
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static HashMap<String, Route> readRouteFile()
    {
        final String ID = "route_id",TYPE = "route_type", NAME = "route_long_name";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.routes);
        HashMap<String, Route> routes = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String routeName = values[headers.indexOf(NAME)];
            String routeId = values[headers.indexOf(ID)];
            int routeType = Integer.parseInt(values[headers.indexOf(TYPE)]);
            //Create a TripStop object to contain data and add it to the associated Trip object
            routes.put(routeId,new Route(routeId,routeType,routeName));
        });
        return routes;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static HashMap<String, Trip> readTimeFile()
    {
        final  String TRIP_ID = "trip_id", ARRIVAL_TIME = "arrival_time", STOP_ID = "stop_id",SEQUENCE = "stop_sequence";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.stopTimes);
        HashMap<String, Trip> trips = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String[] t = values[headers.indexOf(ARRIVAL_TIME)].split(":");
            String tripId = values[headers.indexOf(TRIP_ID)];
            String stopId = values[headers.indexOf(STOP_ID)];
            int stopSequence = Integer.parseInt(values[headers.indexOf(SEQUENCE)]);
            //Create a TripStop object to contain data and add it to the associated Trip object
            int arrivalTimeInS = Integer.parseInt(t[0]) * 3600 + Integer.parseInt(t[1]) * 60 + Integer.parseInt(t[2]);
            TripStop tripStop = new TripStop(stopId,stopSequence,arrivalTimeInS);
            trips.computeIfAbsent(tripId, k -> new Trip(tripId)).addStop(tripStop);
        });
        return trips;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static HashMap<Route,List<Trip>> readTripFile(HashMap<String, Route> routes, HashMap<String, Trip> trips)
    {
        final String ROUTE_ID = "route_id", ID = "trip_id";
        Path filePath = Paths.get(Config.getInstance().networkFiles.gtfs.trips);
        HashMap<Route,List<Trip>> routeTrips = new HashMap<>();
        TabularFileUtil.readFile(filePath,NetworkReaderGTFS::split,(headers, values) -> {
            //File values
            String tripId = values[headers.indexOf(ID)];
            String routeId = values[headers.indexOf(ROUTE_ID)];
            routeTrips.computeIfAbsent(routes.get(routeId), k -> new ArrayList<>()).add(trips.get(tripId));
        });
        return routeTrips;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createMissingFiles()
    {
        Path sectionsPath = Paths.get(Config.getInstance().networkFiles.gtfs.routeSections);
        if(!Files.exists(sectionsPath))
            createSectionsFile();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Create the custom route sections file to the specified folder path with trips, routes and stop_times data
     * Route section = fragment of a route connecting two stops
     * The frequency correspond to the average frequency of passages of every trip of the route
     * The time correspond to the average travel time between the two section points of every trip of the route */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createSectionsFile()
    {
        Path routeSectionsPath = Paths.get(Config.getInstance().networkFiles.gtfs.routeSections);
        //Extract data from existing files
        HashMap<String, Route> routes = readRouteFile();
        HashMap<String, Trip> trips = readTimeFile();
        HashMap<Route,List<Trip>> routeTripsMap = readTripFile(routes,trips);
        //1 Map = 1 line of the file, key = header, value = value
        List<HashMap<String,String>> fileLines = createSectionLines(routeTripsMap);
        TabularFileUtil.writeFile(",", routeSectionsPath, fileLines);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<HashMap<String,String>> createSectionLines(HashMap<Route,List<Trip>> routeTripsMap)
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
    /** For each two stops linked of a route, create a Section object
     * @param trips all the trips of the route
     * @return A RouteSection HashMap <routeSectionId,RouteSection> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<Section> createSections(List<Trip> trips)
    {
        HashMap<String, Section> sectionsMap = new HashMap<>();
        for(Trip trip:trips) {
            List<TripStop> stops =  new ArrayList<>(trip.getStops().values());
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