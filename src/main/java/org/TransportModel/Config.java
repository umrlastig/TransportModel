package org.TransportModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.TransportModel.network.Link;

import java.io.File;
import java.io.IOException;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
 public final class  Config
{
    private static final String CONFIG_PATH = "src/main/resources/config.json";
    private static Config instance;
    @SuppressWarnings("unused") @JsonProperty("filePaths") private FilePaths filePaths;
    @SuppressWarnings("unused") @JsonProperty("transportTimes") private TransportTimes transportTimes;
    @SuppressWarnings("unused") @JsonProperty("transportCapacities") private TransportCapacities transportCapacities;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Singleton */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Config getInstance()
    {
        if(instance == null){
            try {instance = new ObjectMapper().readValue(new File(CONFIG_PATH), Config.class);}
            catch (IOException e) {throw new RuntimeException();}}
        return instance;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private Config() {}
    public FilePaths getFilePaths() {return this.filePaths;}
    public int getMaxTime(){return this.transportTimes.maxTime;}
    public int getMinTime(){return this.transportTimes.minTime;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public int getTransportCapacity(Link.ROUTE_TYPE routeType)
    {
        switch(routeType) {
            case TRAM_OR_LIGHT_SUBWAY:return this.transportCapacities.tramOrLightSubwayCapacity;
            case BUS:return this.transportCapacities.busCapacity;
            case TRAIN:return this.transportCapacities.trainCapacity;
            case SUBWAY:return this.transportCapacities.subwayCapacity;
            default:return this.transportCapacities.defaultCapacity;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportTimes
    {
        @SuppressWarnings("unused") @JsonProperty("minTime") private int minTime;
        @SuppressWarnings("unused") @JsonProperty("maxTime") private int maxTime;
        private TransportTimes(){}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class TransportCapacities
    {
        @SuppressWarnings("unused") @JsonProperty("tram_or_light_subway") private int tramOrLightSubwayCapacity;
        @SuppressWarnings("unused") @JsonProperty("train") private int trainCapacity;
        @SuppressWarnings("unused") @JsonProperty("bus") private int busCapacity;
        @SuppressWarnings("unused") @JsonProperty("subway") private int subwayCapacity;
        @SuppressWarnings("unused") @JsonProperty("default") private int defaultCapacity;
        private TransportCapacities(){}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FilePaths
    {
        @SuppressWarnings("unused") @JsonProperty("networkFolderGTFS") private String networkFolderGTFS;
        @SuppressWarnings("unused") @JsonProperty("networkFilesBDTOPO") private String[] networkFilesBDTOPO;
        @SuppressWarnings("unused") @JsonProperty("communesShapeFileBDTOPO") private String communesShapeFileBDTOPO;
        @SuppressWarnings("unused") @JsonProperty("communesPopulationFileINSEE") private String communesPopulationFileINSEE;
        @SuppressWarnings("unused") @JsonProperty("communesFlowFileMOBPRO") private String communesFlowFileMOBPRO;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        private FilePaths() {}
        public String getNetworkFolderGTFS(){return this.networkFolderGTFS;}
        public String[] getNetworkFilesBDTOPO(){return this.networkFilesBDTOPO;}
        public String getCommunesShapeFileBDTOPO(){return this.communesShapeFileBDTOPO;}
        public String getCommunesPopulationFileINSEE(){return this.communesPopulationFileINSEE;}
        public String getCommunesFlowFileMOBPRO(){return this.communesFlowFileMOBPRO;}
    }
}