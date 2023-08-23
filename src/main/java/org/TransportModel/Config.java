package org.TransportModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.TransportModel.network.Link;

import java.io.File;
import java.io.IOException;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
 public final class Config
{
    private static final String CONFIG_PATH = "src/main/resources/config.json";
    @JsonProperty("transport_values") public TransportValues transportValues;
    @JsonProperty("network_files") public NetworkFiles networkFiles;
    @JsonProperty("generation_files") public GenerationFiles generationFiles;
    private static Config instance;
    private Config() {}
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
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportValues
    {
        @JsonProperty("capacities") public Capacities capacities;
        @JsonProperty("valid_hours") public ValidHours validHours;
        private TransportValues(){}
        public int getCapacity(Link.ROUTE_TYPE routeType){return this.capacities.getCapacity(routeType);}
        public boolean areHoursValid(double first, double last){return validHours.areValid(first,last);}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public static class Capacities
        {
            @JsonProperty("tram_or_light_subway") public int tramOrLightSubwayCapacity;
            @JsonProperty("train") public int trainCapacity;
            @JsonProperty("bus") public int busCapacity;
            @JsonProperty("subway") public int subwayCapacity;
            @JsonProperty("default") public int defaultCapacity;
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            /** */
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
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public static class ValidHours
        {
            @JsonProperty("min") public int min;
            @JsonProperty("max") public int max;
            public boolean areValid(double firstTraversal,double lastTraversal)
            {return firstTraversal/3600 <= max && lastTraversal/3600 >= min;}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class NetworkFiles
    {
        @JsonProperty("bdtopo") public String[] bdtopo;
        @JsonProperty("gtfs") public GTFS gtfs;
        private NetworkFiles() {}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
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