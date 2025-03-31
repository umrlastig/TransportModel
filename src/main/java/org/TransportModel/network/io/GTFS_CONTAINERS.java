/*
 * Copyright (C) 2023 Erwan Hamzaoui
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.TransportModel.network.io;

import java.util.HashMap;

public class GTFS_CONTAINERS
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Represents a specific route which is a specific path that vehicles follow */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Route
    {
        private final String name;
        private final int type;
        /** Constructor */
        public Route(int type, String name) {this.type = type;this.name = name;}
        /** Getters/Setters */
        public int getType(){return this.type;}
        public String getName(){return this.name;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Represents a specific trip which is a vehicle's journey along a route at a particular time */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Trip
    {
        private final HashMap<String, TripStop> stops;
        /** Constructor */
        public Trip() {this.stops = new HashMap<>();}
        /**  Getters/Setters */
        public void addStop(TripStop tripStop){this.stops.put(tripStop.getId(),tripStop);}
        public HashMap<String, TripStop> getStops() {return stops;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Represents a stop for a specific trip */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class TripStop
    {
        private final String id;
        private final int sequence, arrivalTime;
        /** Constructor */
        public TripStop(String id, int sequence, int arrivalTime)
        {this.id = id;this.sequence = sequence;this.arrivalTime = arrivalTime;}
        /** Getters/Setters  */
        public String getId(){return this.id;}
        public int getSequence(){return this.sequence;}
        public int getArrivalTime(){return this.arrivalTime;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Represents the link between two stops */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class Section
    {
        private final String fromId, toId;
        private int traversalCount, firstTraversal, lastTraversal, maxTraversalTime, averageTraversalTime;
        /** Constructor */
        public Section(String fromId, String toId) {
            this.fromId = fromId;
            this.toId = toId;
            this.firstTraversal = Integer.MAX_VALUE;
            this.lastTraversal = Integer.MIN_VALUE;
            this.traversalCount = this.maxTraversalTime = this.averageTraversalTime = 0;
        }
        /** Getters/Setters */
        public int getAverageFrequency(){return (this.lastTraversal-this.firstTraversal)/this.traversalCount;}
        public int getAverageTraversalTime() {return this.averageTraversalTime;}
        public int getFirstTraversal(){return this.firstTraversal;}
        public int getLastTraversal(){return this.lastTraversal;}
        public String getFromId(){return this.fromId;}
        public String getToId(){return this.toId;}
        /** Adds a traversal between two arrival times and updates traversal statistics
         * @param fromArrivalTime The arrival time of the starting tripStop
         * @param toArrivalTime The arrival time of the ending tripStop */
        public void addTraversal(int fromArrivalTime, int toArrivalTime)
        {
            int traversalTime = Math.max(29,(toArrivalTime - fromArrivalTime));
            this.firstTraversal = Math.min(fromArrivalTime, this.firstTraversal);
            this.lastTraversal = Math.max(fromArrivalTime, this.lastTraversal);
            this.averageTraversalTime = (averageTraversalTime*traversalCount+traversalTime)/(traversalCount+1);
            this.maxTraversalTime = Math.max(traversalTime,this.maxTraversalTime);
            this.traversalCount++;
        }
    }
}
