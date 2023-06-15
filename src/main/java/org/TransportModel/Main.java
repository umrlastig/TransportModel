package org.TransportModel;

import org.TransportModel.gui.AreaCanvas;
import org.TransportModel.gui.NetworkCanvas;
import org.TransportModel.gui.UserInterface;
import org.TransportModel.generation.Area;
import org.TransportModel.generation.io.ZoneReaderBDTOPO;
import org.TransportModel.network.Network;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;
import org.checkerframework.checker.units.qual.A;
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
        /*
        //TI
        Network network_TI = new Network();
        NetworkReaderBDTOPO networkReaderBDTOPO = new NetworkReaderBDTOPO();
        String shpFilePath = "src/main/resources/TI/BDTOPO_94/TRONCON_DE_ROUTE.shp";
        try{networkReaderBDTOPO.readBDTOPOFile(network_TI,shpFilePath);}
        catch(Exception e){e.printStackTrace();}
        NetworkCanvas tiCanvas = new NetworkCanvas(network_TC);

        //TC
        Network network_TC = new Network();
        NetworkReaderGTFS networkReaderGTFS = new NetworkReaderGTFS();
        String gtfsFolderPath = "src/main/resources/TC/GTFS_IDF";
        try{networkReaderGTFS.readGTFSFolder(network_TC,gtfsFolderPath);}
        catch(Exception e){e.printStackTrace();}
        NetworkCanvas tcCanvas = new NetworkCanvas(network_TC);
        */

        //Zone
        Area idf = new Area();
        ZoneReaderBDTOPO zoneReaderBDTOPO = new ZoneReaderBDTOPO();
        String communesShapeFile = "src/main/resources/Zone/BDTOPO_IDF/communes.shp";
        try{zoneReaderBDTOPO.readBDTOPOFile(idf, communesShapeFile);}
        catch(Exception e){e.printStackTrace();}
        AreaCanvas idfCanvas = new AreaCanvas(idf);

        //Display
        UserInterface gUI = new UserInterface();
        gUI.setComponent(idfCanvas);
    }
}