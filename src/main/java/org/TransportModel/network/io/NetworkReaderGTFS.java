package org.TransportModel.network.io;

import org.TransportModel.Config;
import org.TransportModel.io.TabularFileReader;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.utils.CoordinateUtils;
import org.locationtech.jts.geom.Coordinate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
        Network network = new Network();
        //Files paths
        Path sectionsPath = Paths.get(Config.getInstance().networkFiles.gtfs.routeSections);
        Path stopsPath = Paths.get(Config.getInstance().networkFiles.gtfs.stops);
        Path transfersPath = Paths.get(Config.getInstance().networkFiles.gtfs.transfers);
        Path pathwaysPath = Paths.get(Config.getInstance().networkFiles.gtfs.pathways);
        //Create missing files
        if(!Files.exists(sectionsPath))
            createSectionsFile();
        //Read files
        TabularFileReader.readFile(stopsPath, new StopsProcessor(network));
        TabularFileReader.readFile(sectionsPath, new SectionsProcessor(network));
        TabularFileReader.readFile(transfersPath,new TransfersProcessor(network));
        TabularFileReader.readFile(pathwaysPath,new PathwaysProcessor(network));
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a line of stops.txt file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class StopsProcessor implements TabularFileReader.LineProcessor
    {
        public static final String ID = "stop_id", NAME = "stop_name", LON = "stop_lon", LAT = "stop_lat";
        public static final String PARENT_ID = "parent_station";
        private final Network network;
        private final HashMap<String,List<Node>> stopsChilds;
        public StopsProcessor(Network network){this.network = network;this.stopsChilds = new HashMap<>();}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Creates a Node object and adds it to the Network */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String stopId = values[headers.indexOf(ID)];
            String name = values[headers.indexOf(NAME)];
            String parentStopId = values[headers.indexOf(PARENT_ID)];
            double lon = Double.parseDouble(values[headers.indexOf(LON)]);
            double lat = Double.parseDouble(values[headers.indexOf(LAT)]);
            //Create and add Node
            Node node = new Node(stopId,name,new Coordinate(lon,lat));
            network.addNode(node);
            //If node has parent, link to parent
            if(!parentStopId.isEmpty())
                if(network.containsNode(parentStopId))
                    this.addParentChildLink(network.getNode(parentStopId),node);
                else
                    this.stopsChilds.computeIfAbsent(parentStopId, k -> new ArrayList<>()).add(node);
            //If node has childs, link to childs
            else if(this.stopsChilds.containsKey(stopId))
                for(Node childNode:this.stopsChilds.remove(stopId))
                    this.addParentChildLink(node,childNode);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Adds a parent-child link between the given parent node and child node
         * @param parentNode The parent node in the parent-child relationship
         * @param childNode  The child node in the parent-child relationship */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        private void addParentChildLink(Node parentNode, Node childNode)
        {
            network.addLink(new Link(childNode,parentNode,"parentChild"));
            network.addLink(new Link(parentNode,childNode,"parentChild"));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a line of route_sections.txt file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class SectionsProcessor implements TabularFileReader.LineProcessor
    {
        public static final String AVG_TIME = "avg_time", TO_ID = "to_stop_id", FREQUENCY = "frequency";
        public static final String ROUTE_TYPE = "route_type", ROUTE_NAME = "route_name", ROUTE_ID = "route_id";
        public static final String  FROM_ID = "from_stop_id", FIRST = "first_traversal", LAST = "last_traversal";
        private final Network network;
        public SectionsProcessor(Network network){this.network = network;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ////////////////////////////////////////////////////////////////////////////////////////////////
        /** Creates a Link object and adds it to the Network */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            String name = values[headers.indexOf(ROUTE_NAME)];
            int firstTraversal = Integer.parseInt(values[headers.indexOf(FIRST)]);
            int lastTraversal = Integer.parseInt(values[headers.indexOf(LAST)]);
            int type = Integer.parseInt(values[headers.indexOf(ROUTE_TYPE)]);
            double timeInS = Double.parseDouble(values[headers.indexOf(AVG_TIME)]);
            double frequency = Double.parseDouble(values[headers.indexOf(FREQUENCY)]);
            //Create and add Link
            int minHour =  Config.getInstance().transportValues.validHours.min;
            int maxHour = Config.getInstance().transportValues.validHours.max;
            if(!network.containsNode(fromId) || !network.containsNode(toId)) {throw new RuntimeException("Node missing");}
            if(firstTraversal/3600 <= maxHour && lastTraversal/3600 >= minHour) {
                Node fromNode = network.getNode(fromId);
                Node toNode = network.getNode(toId);
                Link.ROUTE_TYPE routeType = getRouteType(type);
                int capacity = Config.getInstance().transportValues.capacities.getCapacity(routeType);
                double capacityPerHour = capacity / frequency / 3600;
                double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(),toNode.getCoordinate());
                double speedInMS = lengthInM / timeInS;
                Link link = new Link(fromNode, toNode, speedInMS, capacityPerHour,lengthInM,routeType,name);
                network.addLink(link);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Creates a list of lines for the route_sections.txt for each section of each Route
         * @param routes The list of Route for which to generate the lines of the sections file */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public static List<HashMap<String,String>> createSectionsFileLines(ArrayList<GTFS_CONTAINERS.Route> routes)
        {
            List<HashMap<String,String>> fileLines = new ArrayList<>();
            for(GTFS_CONTAINERS.Route route:routes)
                for(GTFS_CONTAINERS.Section section:getSections(route.getTrips()))
                    fileLines.add(new HashMap<String,String>() {{
                        put(ROUTE_ID,route.getId());
                        put(ROUTE_TYPE,""+route.getType());
                        put(ROUTE_NAME,route.getName());
                        put(FROM_ID,section.getFromId());
                        put(TO_ID,section.getToId());
                        put(FREQUENCY,""+section.getAverageFrequency());
                        put(AVG_TIME,""+section.getAverageTraversalTime());
                        put(FIRST,""+section.getFirstTraversal());
                        put(LAST,""+section.getLastTraversal());
                    }});
            return fileLines;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a line of transfers.txt file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TransfersProcessor implements TabularFileReader.LineProcessor
    {
        public final static String FROM_ID = "from_stop_id", TO_ID = "to_stop_id", TIME = "min_transfer_time";
        private final Network network;
        public TransfersProcessor(Network network){this.network = network;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Creates a Link object and adds it to the Network */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            double timeInS = Double.parseDouble(values[headers.indexOf(TIME)]);
            //Create and add Link
            if(!network.containsNode(fromId) || !network.containsNode(toId)) {throw new RuntimeException("Node missing");}
            String name = "Transfer";
            Node fromNode = network.getNode(fromId);
            Node toNode = network.getNode(toId);
            Link.ROUTE_TYPE routeType = Link.ROUTE_TYPE.FOOT;
            double lengthInM = CoordinateUtils.calculateWSG84Distance(fromNode.getCoordinate(),toNode.getCoordinate());
            double speedInMS = lengthInM / timeInS;
            double capacityPerHour = 999999;
            network.addLink(new Link(fromNode, toNode, speedInMS, capacityPerHour, lengthInM, routeType,name));
            network.addLink(new Link(toNode, fromNode, speedInMS, capacityPerHour, lengthInM, routeType,name));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a line of pathways.txt file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class PathwaysProcessor implements TabularFileReader.LineProcessor
    {
        public static final String LENGTH = "length", FROM_ID = "from_stop_id", TO_ID = "to_stop_id";
        public static final String  BIDIRECTIONAL = "is_bidirectional",TIME = "traversal_time";
        private final Network network;
        public PathwaysProcessor(Network network){this.network = network;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Creates a Link object and adds it to the Network */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String fromId = values[headers.indexOf(FROM_ID)];
            String toId = values[headers.indexOf(TO_ID)];
            boolean bidirectional = values[headers.indexOf(BIDIRECTIONAL)].equals("1");
            double lengthInM = Double.parseDouble(values[headers.indexOf(LENGTH)]);
            double timeInS = Double.parseDouble(values[headers.indexOf(TIME)]);
            //Create and add Link
            if(!network.containsNode(fromId) || !network.containsNode(toId)) {throw new RuntimeException("Node missing");}
            String name = "Pathway";
            Node fromNode = network.getNode(fromId);
            Node toNode = network.getNode(toId);
            Link.ROUTE_TYPE routeType = Link.ROUTE_TYPE.FOOT;
            double speedInMS = lengthInM / timeInS;
            double capacityPerHour = 999999;
            network.addLink(new Link(fromNode,toNode,speedInMS,capacityPerHour,lengthInM,routeType,name));
            if(bidirectional)
                network.addLink(new Link(toNode, fromNode, speedInMS, capacityPerHour, lengthInM, routeType,name));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** stop_times.txt: 1 line contains schedule information for a specific stop of a given trip */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TimesProcessor implements TabularFileReader.LineProcessor
    {
        public final static String TRIP_ID = "trip_id", ARRIVAL_TIME = "arrival_time", STOP_ID = "stop_id";
        public final static String SEQUENCE = "stop_sequence";
        HashMap<String,GTFS_CONTAINERS.Trip> trips;
        public TimesProcessor(HashMap<String,GTFS_CONTAINERS.Trip> trips){this.trips = trips;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Create a TripStop and add it to the associated Trip (if Trip don't exist, create it) */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String[] time_strings = values[headers.indexOf(ARRIVAL_TIME)].split(":");
            String tripId = values[headers.indexOf(TRIP_ID)];
            String stopId = values[headers.indexOf(STOP_ID)];
            int stopSequence = Integer.parseInt(values[headers.indexOf(SEQUENCE)]);
            //Create a TripStop object to contain data and add it to the associated Trip object
            int hours = Integer.parseInt(time_strings[0]);
            int minutes = Integer.parseInt(time_strings[1]);
            int seconds = Integer.parseInt(time_strings[2]);
            int arrivalTimeInS = (hours * 3600) + (minutes * 60) + seconds;
            GTFS_CONTAINERS.TripStop tripStop = new GTFS_CONTAINERS.TripStop(stopId,stopSequence,arrivalTimeInS);
            trips.computeIfAbsent(tripId, k -> new GTFS_CONTAINERS.Trip(tripId)).addStop(tripStop);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** routes.txt: 1 line contains information for a specific route */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class RoutesProcessor implements TabularFileReader.LineProcessor
    {
        public final static String ID = "route_id",TYPE = "route_type", NAME = "route_long_name";
        HashMap<String,GTFS_CONTAINERS.Route> routes;
        public RoutesProcessor(HashMap<String,GTFS_CONTAINERS.Route> routes){this.routes = routes;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Create a Route object */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String routeName = values[headers.indexOf(NAME)];
            String routeId = values[headers.indexOf(ID)];
            int routeType = Integer.parseInt(values[headers.indexOf(TYPE)]);
            //Create a TripStop object to contain data and add it to the associated Trip object
            routes.put(routeId,new GTFS_CONTAINERS.Route(routeId,routeType,routeName));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** trips.txt: 1 line contains the route id for a specific trip */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TripsProcessor implements TabularFileReader.LineProcessor
    {
        public final static String ROUTE_ID = "route_id", ID = "trip_id";
        HashMap<String,GTFS_CONTAINERS.Route> routes;
        HashMap<String,GTFS_CONTAINERS.Trip> trips;
        public TripsProcessor(HashMap<String,GTFS_CONTAINERS.Route> routes, HashMap<String,GTFS_CONTAINERS.Trip> trips)
        {this.routes = routes; this.trips = trips;}
        @Override public String[] split(String line){return NetworkReaderGTFS.split(line);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Add the Trip object to the associated Route object */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String tripId = values[headers.indexOf(ID)];
            String routeId = values[headers.indexOf(ROUTE_ID)];
            //Create a TripStop object to contain data and add it to the associated Trip object
            routes.get(routeId).addTrip(trips.get(tripId));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Create the custom route sections file to the specified folder path with trips, routes and stop_times data
     * Route section = fragment of a route connecting two stops
     * The frequency correspond to the average frequency of passages of every trip of the route
     * The time correspond to the average travel time between the two section points of every trip of the route */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createSectionsFile()
    {
        //Files paths
        Path routesPath = Paths.get(Config.getInstance().networkFiles.gtfs.routes);
        Path stopTimesPath = Paths.get(Config.getInstance().networkFiles.gtfs.stopTimes);
        Path tripsPath = Paths.get(Config.getInstance().networkFiles.gtfs.trips);
        Path routeSectionsPath = Paths.get(Config.getInstance().networkFiles.gtfs.routeSections);
        //Extract data from existing files
        HashMap<String,GTFS_CONTAINERS.Route> routes = new HashMap<>();
        HashMap<String,GTFS_CONTAINERS.Trip> trips = new HashMap<>();
        //Read files
        TabularFileReader.readFile(routesPath,new RoutesProcessor(routes));
        TabularFileReader.readFile(stopTimesPath,new TimesProcessor(trips));
        TabularFileReader.readFile(tripsPath,new TripsProcessor(routes,trips));
        //Write custom file
        List<HashMap<String,String>> lines = SectionsProcessor.createSectionsFileLines(new ArrayList<>(routes.values()));
        TabularFileReader.writeFile(",", routeSectionsPath, lines);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each two stops linked of a route, create a Section object
     * @param trips all the trips of the route
     * @return A RouteSection HashMap <routeSectionId,RouteSection> */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<GTFS_CONTAINERS.Section> getSections(List<GTFS_CONTAINERS.Trip> trips)
    {
        HashMap<String,GTFS_CONTAINERS.Section> sectionsMap = new HashMap<>();
        for(GTFS_CONTAINERS.Trip trip:trips)
        {
            List<GTFS_CONTAINERS.TripStop> stops =  new ArrayList<>(trip.getStops().values());
            stops.sort(Comparator.comparingInt(GTFS_CONTAINERS.TripStop::getSequence));
            for (int i = 0; i < stops.size()-1;i++) {
                GTFS_CONTAINERS.TripStop fromStop = stops.get(i);
                GTFS_CONTAINERS.TripStop toStop = stops.get(i+1);
                String sectionId = fromStop.getId()+":"+toStop.getId();
                sectionsMap.putIfAbsent(sectionId,new GTFS_CONTAINERS.Section(fromStop.getId(),toStop.getId()));
                sectionsMap.get(sectionId).addTraversal(fromStop.getArrivalTime(),toStop.getArrivalTime());
            }
        }
        return new ArrayList<>(sectionsMap.values());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Returns the route type corresponding to the given gtfs route_type code
     * @param gtfsRouteType the gtfs route_type code
     * @return the route type corresponding to the given route_type code */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Link.ROUTE_TYPE getRouteType(int gtfsRouteType)
    {
        switch(gtfsRouteType) {
            case 0:return Link.ROUTE_TYPE.TRAM_OR_LIGHT_SUBWAY;
            case 1:return Link.ROUTE_TYPE.SUBWAY;
            case 2:return Link.ROUTE_TYPE.TRAIN;
            case 3:return Link.ROUTE_TYPE.BUS;
            default:return Link.ROUTE_TYPE.UNDEFINED;
        }
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
                currentString.append(c);
        }
        strings.add(currentString.toString().trim());
        return strings.toArray(new String[0]);
    }
}