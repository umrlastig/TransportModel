package org.TransportModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.TransportModel.network.Link;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Config
{
    private static Config instance;
    @JsonProperty("transportTimes") private TransportTimes transportTimes;
    @JsonProperty("transportCapacities") private TransportCapacities transportCapacities;
    @JsonProperty("filePaths") private FilePaths filePaths;

    private Config() {}
    public static void setInstance(Config config) {instance = config;}
    public static TransportTimes getTransportTimes(){return instance.transportTimes;}
    public static FilePaths getFilePaths(){return instance.filePaths;}
    public static int getMaxTime(){return instance.transportTimes.getMaxTime();}
    public static int getMinTime(){return instance.transportTimes.getMinTime();}
    public static int getTransportCapacity(Link.ROUTE_TYPE routeType)
    {
        switch(routeType)
        {
            case TRAM_OR_LIGHT_SUBWAY:return instance.transportCapacities.tramOrLightSubwayCapacity;
            case BUS:return instance.transportCapacities.busCapacity;
            case TRAIN:return instance.transportCapacities.trainCapacity;
            case SUBWAY:return instance.transportCapacities.subwayCapacity;
            default:return instance.transportCapacities.defaultCapacity;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportTimes
    {
        @JsonProperty("minTime") private int minTime;
        @JsonProperty("maxTime") private int maxTime;

        public int getMinTime() {return minTime;}
        public int getMaxTime() {return maxTime;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportCapacities
    {
        @JsonProperty("tram_or_light_subway") private int tramOrLightSubwayCapacity;
        @JsonProperty("train") private int trainCapacity;
        @JsonProperty("bus") private int busCapacity;
        @JsonProperty("subway") private int subwayCapacity;
        @JsonProperty("default") private int defaultCapacity;

        public int getTramOrLightSubwayCapacity() {return tramOrLightSubwayCapacity;}
        public int getTrainCapacity() {return trainCapacity;}
        public int getBusCapacity() {return busCapacity;}
        public int getSubwayCapacity() {return subwayCapacity;}
        public int getDefaultCapacity() {return defaultCapacity;}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FilePaths
    {
        @JsonProperty("networkFolderGTFS") private String networkFolderGTFS;
        @JsonProperty("networkFilesBDTOPO") private String[] networkFilesBDTOPO;
        @JsonProperty("communesShapeFileBDTOPO") private String communesShapeFileBDTOPO;
        @JsonProperty("communesPopulationFileINSEE") private String communesPopulationFileINSEE;

        public String getNetworkFolderGTFS(){return this.networkFolderGTFS;}
        public String[] getNetworkFilesBDTOPO(){return this.networkFilesBDTOPO;}
        public String getCommunesShapeFileBDTOPO(){return this.communesShapeFileBDTOPO;}
        public String getCommunesPopulationFileINSEE(){return this.communesPopulationFileINSEE;}
    }
}