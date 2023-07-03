package org.TransportModel;

import org.TransportModel.generation.Area;
import org.TransportModel.generation.io.CommunesReader;
import org.TransportModel.network.Network;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**                                       Main Class                                             */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Main
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Main Function                                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args)
    {
        //Setup
        Area area = setupArea();//1500ms
        Network tcNetwork = setupTCNetwork();//2500ms
        Network tiNetwork = setupTINetwork();//46 000ms
        //Tests
        Tests.testConnectivity(tcNetwork);
        Tests.testConnectivity(tiNetwork);
        Tests.testPopulation(area);
        Tests.testPathTC(tcNetwork);
        Tests.testPathZones(tiNetwork,area);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Area setupArea()
    {
        Area area = new Area();
        try {
            CommunesReader.readBDTOPOFile(area, Config.getInstance().getFilePaths().getCommunesShapeFileBDTOPO());
            CommunesReader.readINSEEFile(area,Config.getInstance().getFilePaths().getCommunesPopulationFileINSEE());
            CommunesReader.readMOBPROFile(area,Config.getInstance().getFilePaths().getCommunesFlowFileMOBPRO());
        }
        catch(Exception e){e.printStackTrace();}
        return area;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network setupTCNetwork()
    {
        Network tcNetwork = new Network();
        try{NetworkReaderGTFS.readGTFSFolder(tcNetwork,Config.getInstance().getFilePaths().getNetworkFolderGTFS());}
        catch(Exception e){e.printStackTrace();}
        return tcNetwork;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network setupTINetwork()
    {
        Network tiNetwork = new Network();
        try {for(String shpFilePath:Config.getInstance().getFilePaths().getNetworkFilesBDTOPO())
                NetworkReaderBDTOPO.readBDTOPORouteFile(tiNetwork,shpFilePath);}
        catch(Exception e){e.printStackTrace();}
        return tiNetwork;
    }
}