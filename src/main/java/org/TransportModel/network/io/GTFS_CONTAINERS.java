package org.TransportModel.network.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GTFS_CONTAINERS
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Route
    {
        private final String id;
        private final int type;
        private final String name;
        private final HashMap<String,Trip> trips;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public Route(String id, int type, String name)
        {
            this.id = id;
            this.type = type;
            this.name = name;
            this.trips = new HashMap<>();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public int getType(){return this.type;}
        public String getName(){return this.name;}
        public List<Trip> getTrips(){return new ArrayList<>(this.trips.values());}
        public void addTrip(Trip trip){this.trips.put(trip.getId(),trip);}
        public String getId(){return this.id;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Trip
    {
        private final String id;
        private final HashMap<String, TripStop> stops;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public Trip(String id)
        {
            this.id = id; this.stops = new HashMap<>();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public void addStop(TripStop tripStop){this.stops.put(tripStop.getId(),tripStop);}
        public HashMap<String, TripStop> getStops() {return stops;}
        public String getId() {return id;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TripStop
    {
        private final String id;
        private final int sequence;
        private final int arrivalTime;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public TripStop(String id, int sequence, int arrivalTime)
        {
            this.id = id;
            this.sequence = sequence;
            this.arrivalTime = arrivalTime;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public String getId(){return this.id;}
        public int getSequence(){return this.sequence;}
        public int getArrivalTime(){return this.arrivalTime;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Section
    {
        private final String toId;
        private final String fromId;
        private int traversalCount;
        private int firstTraversal;
        private int lastTraversal;
        private int maxTraversalTime;
        private int averageTraversalTime;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public Section(String fromId, String toId)
        {
            this.fromId = fromId;
            this.toId = toId;
            this.traversalCount = 0;
            this.maxTraversalTime = 0;
            this.averageTraversalTime = 0;
            this.firstTraversal = Integer.MAX_VALUE;
            this.lastTraversal = Integer.MIN_VALUE;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public int getAverageFrequency(){return (this.lastTraversal-this.firstTraversal)/this.traversalCount;}
        public int getAverageTraversalTime() {return this.averageTraversalTime;}

        public int getFirstTraversal(){return this.firstTraversal;}
        public int getLastTraversal(){return this.lastTraversal;}
        public String getFromId(){return this.fromId;}
        public String getToId(){return this.toId;}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public void addTraversal(int fromArrivalTime, int toArrivalTime)
        {
            this.firstTraversal = Math.min(fromArrivalTime, this.firstTraversal);
            this.lastTraversal = Math.max(fromArrivalTime, this.lastTraversal);
            int traversalTime = Math.max(29,(toArrivalTime - fromArrivalTime));
            this.averageTraversalTime = (averageTraversalTime*traversalCount+traversalTime)/(traversalCount+1);
            this.maxTraversalTime = Math.max(traversalTime,this.maxTraversalTime);
            this.traversalCount++;
        }
    }
}
