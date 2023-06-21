package org.TransportModel.network.io;

public class GTFS_FILE
{
    static class TRIPS {public static final String FILENAME = "/trips.txt", ROUTE_ID = "route_id", ID = "trip_id";}
    static class STOPS {public static final String FILENAME = "/stops.txt", ID = "stop_id", NAME = "stop_name",
            LON = "stop_lon", LAT = "stop_lat";}
    static class ROUTES {public static final String FILENAME = "/routes.txt", ID = "route_id",TYPE = "route_type",
            NAME = "route_long_name";}
    static class TIMES {public static final String FILENAME = "/stop_times.txt", TRIP_ID = "trip_id",
            ARRIVAL_TIME = "arrival_time", STOP_ID = "stop_id", SEQUENCE = "stop_sequence";}
    static class PATHWAYS {public static final String FILENAME = "/pathways.txt", ID = "pathway_id", LENGTH = "length",
            FROM_ID = "from_stop_id", TO_ID = "to_stop_id", BIDIRECTIONAL = "is_bidirectional",TIME = "traversal_time";}
    static class TRANSFERS {public static final String FILENAME = "/transfers.txt", FROM_ID = "from_stop_id",
            TO_ID = "to_stop_id", TIME = "min_transfer_time";}
    static class SECTIONS {public static final String FILENAME = "/route_sections.txt", TIME = "traversal_time",
            ROUTE_ID = "route_id", ROUTE_TYPE = "route_type", ROUTE_NAME = "route_name", FROM_ID = "from_stop_id",
            TO_ID = "to_stop_id", FREQUENCY = "traversal_frequency", FIRST = "first_traversal", LAST = "last_traversal";}
}

