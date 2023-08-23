package org.TransportModel.network.io;

import java.util.HashMap;

public class GTFS_CONTAINERS
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Route
    {
        private final String name;
        private final int type;
        public Route(int type, String name) {this.type = type;this.name = name;}
        public int getType(){return this.type;}
        public String getName(){return this.name;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Trip
    {
        private final HashMap<String, TripStop> stops;
        public Trip() {this.stops = new HashMap<>();}
        public void addStop(TripStop tripStop){this.stops.put(tripStop.getId(),tripStop);}
        public HashMap<String, TripStop> getStops() {return stops;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TripStop
    {
        private final String id;
        private final int sequence, arrivalTime;
        public TripStop(String id, int sequence, int arrivalTime)
        {this.id = id;this.sequence = sequence;this.arrivalTime = arrivalTime;}
        public String getId(){return this.id;}
        public int getSequence(){return this.sequence;}
        public int getArrivalTime(){return this.arrivalTime;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Section
    {
        private final String toId, fromId;
        private int traversalCount, firstTraversal, lastTraversal, maxTraversalTime, averageTraversalTime;
        public Section(String fromId, String toId) {
            this.fromId = fromId;
            this.toId = toId;
            this.firstTraversal = Integer.MAX_VALUE;
            this.lastTraversal = Integer.MIN_VALUE;
            this.traversalCount = this.maxTraversalTime = this.averageTraversalTime = 0;
        }
        public int getAverageFrequency(){return (this.lastTraversal-this.firstTraversal)/this.traversalCount;}
        public int getAverageTraversalTime() {return this.averageTraversalTime;}
        public int getFirstTraversal(){return this.firstTraversal;}
        public int getLastTraversal(){return this.lastTraversal;}
        public String getFromId(){return this.fromId;}
        public String getToId(){return this.toId;}
        public void addTraversal(int fromArrivalTime, int toArrivalTime) {
            int traversalTime = Math.max(29,(toArrivalTime - fromArrivalTime));
            this.firstTraversal = Math.min(fromArrivalTime, this.firstTraversal);
            this.lastTraversal = Math.max(fromArrivalTime, this.lastTraversal);
            this.averageTraversalTime = (averageTraversalTime*traversalCount+traversalTime)/(traversalCount+1);
            this.maxTraversalTime = Math.max(traversalTime,this.maxTraversalTime);
            this.traversalCount++;
        }
    }
}
