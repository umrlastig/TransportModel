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

package org.TransportModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.TransportModel.network.Link;

import java.io.File;
import java.io.IOException;


/**
 * Configuration class
 **/
 public final class Config
{
    private static final String CONFIG_PATH = "src/main/resources/config.json";
    @JsonProperty("transport_values") public TransportValues transportValues;
    @JsonProperty("network_files") public NetworkFiles networkFiles;
    @JsonProperty("generation_files") public GenerationFiles generationFiles;
    private static Config instance;
    private Config() {}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static double getTransportCapacity(Link.ROUTE_TYPE type){return getInstance().transportValues.getCapacity(type);}
    public static NetworkFiles getNetworkFiles(){return getInstance().networkFiles;}
    public static GenerationFiles getGenerationFiles(){return getInstance().generationFiles;}
    public static TransportValues getTransportValues(){return getInstance().transportValues;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Singleton */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Config getInstance()
    {
        if(instance == null){
            try {instance = new ObjectMapper().readValue(new File(CONFIG_PATH), Config.class);}
            catch (IOException e) {e.printStackTrace();throw new RuntimeException();}}
        return instance;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Class for transport values configuration */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportValues
    {
        @JsonProperty("capacities") public Capacities capacities;
        @JsonProperty("valid_hours") public ValidHours validHours;
        private TransportValues(){}
        public int getCapacity(Link.ROUTE_TYPE routeType){return this.capacities.getCapacity(routeType);}
        public boolean areHoursValid(double first, double last){return validHours.areValid(first,last);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Class for transport max capacities */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public static class Capacities
        {
            @JsonProperty("tram_or_light_subway") public int tramOrLightSubwayCapacity;
            @JsonProperty("train") public int trainCapacity;
            @JsonProperty("bus") public int busCapacity;
            @JsonProperty("subway") public int subwayCapacity;
            @JsonProperty("default") public int defaultCapacity;
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            /** Get the max capacity for a specific route type
             * @param routeType The route type
             * @return The capacity for the specified route type */
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            public int getCapacity(Link.ROUTE_TYPE routeType)
            {
                switch(routeType) {
                    case TRAM_OR_LIGHT_SUBWAY:return this.tramOrLightSubwayCapacity;
                    case BUS:return this.busCapacity;
                    case TRAIN:return this.trainCapacity;
                    case SUBWAY:return this.subwayCapacity;
                    default:return this.defaultCapacity;}
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Represents the allowable time range during which a transportation service is considered valid */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public static class ValidHours
        {
            @JsonProperty("min") public int min;
            @JsonProperty("max") public int max;
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            /** Check if hours are valid within the specified range
             * @param firstTraversal The first traversal time
             * @param lastTraversal  The last traversal time
             * @return True if the hours are valid, false otherwise */
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            public boolean areValid(double firstTraversal,double lastTraversal)
            {
                return firstTraversal/3600 <= max && lastTraversal/3600 >= min;
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Class for network files configuration */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class NetworkFiles
    {
        @JsonProperty("bdtopo") public String[] bdtopo;
        @JsonProperty("gtfs") public GTFS gtfs;
        private NetworkFiles() {}

        /**
         *  Class for GTFS configuration
         **/
        public static class GTFS
        {
            @JsonProperty("stops") public String stops;
            @JsonProperty("trips") public String trips;
            @JsonProperty("routes") public String routes;
            @JsonProperty("stop_times") public String stopTimes;
            @JsonProperty("route_sections") public String routeSections;
            @JsonProperty("transfers") public String transfers;
        }
    }

    /**
     * Class for Generation files configuration
     * */
    public static class GenerationFiles
    {
        @JsonProperty("zone_shapes") public String zoneShapes;
        @JsonProperty("population") public String population;
        @JsonProperty("students_in_population") public String studentsInPopulation;
        @JsonProperty("workers_in_population") public String workersInPopulation;
        @JsonProperty("jobs_at_workplace") public String jobsAtWorkplace;
        @JsonProperty("study_flows") public String studyFlows;
        @JsonProperty("work_flows") public String workFlows;
        private GenerationFiles() {}
    }
}