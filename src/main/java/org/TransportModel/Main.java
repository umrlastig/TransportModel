package org.TransportModel;

import org.TransportModel.generation.Area;
import org.TransportModel.generation.io.ZoneReaderBDTOPO;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
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
        //Zone
        Area idf = new Area();
        ZoneReaderBDTOPO zoneReaderBDTOPO = new ZoneReaderBDTOPO();
        String communesShapeFile = "src/main/resources/Zone/BDTOPO_IDF/communes.shp";
        try{zoneReaderBDTOPO.readBDTOPOFile(idf, communesShapeFile);}
        catch(Exception e){e.printStackTrace();}

        //Display
        UserInterface gUI = new UserInterface();
        GraphCanvas tcCanvas = new GraphCanvas();
        tcCanvas.addPath(shortestPath);
        gUI.setComponent(tcCanvas);
        */


        //TC
        Network network_TC = new Network();
        NetworkReaderGTFS networkReaderGTFS = new NetworkReaderGTFS();
        String gtfsFolderPath = "src/main/resources/TC/GTFS_IDF";
        try{networkReaderGTFS.readGTFSFolder(network_TC,gtfsFolderPath);}
        catch(Exception e){e.printStackTrace();}

        DirectedWeightedMultigraph<Node, Link> tcGraph = network_TC.createGraph();
        Node fromNode = network_TC.getNode("IDFM:22015");
        Node toNode = network_TC.getNode("IDFM:463025");

        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(tcGraph);
        GraphPath<Node, Link> shortestPath = shortestPathAlgorithm.getPath(fromNode, toNode);
        for(Link link:shortestPath.getEdgeList()){
            System.out.println("Entre "+link.getFromNode().getName()+" et "+link.getToNode().getName()+"" +
                    ": "+(int)tcGraph.getEdgeWeight(link)+ "s en "+link.getType().name() + "("+link.getName()+")");
            //System.out.println(link.getFromNode().getId()+" et "+link.getToNode().getId());
        }

        int hours = (int)shortestPath.getWeight() / 3600;
        int minutes = (int)(shortestPath.getWeight() % 3600) / 60;
        int seconds = (int)shortestPath.getWeight() % 60;
        System.out.println("\nTemps total en secondes entre : "+fromNode.getName()+
                " et "+ toNode.getName()+": "+String.format("%02d:%02d:%02d", hours, minutes, seconds)+ "s");


        /*
        //TI
        Network network_TI = new Network();
        NetworkReaderBDTOPO networkReaderBDTOPO = new NetworkReaderBDTOPO();
        String shpFilePath = "src/main/resources/TI/BDTOPO_94/TRONCON_DE_ROUTE.shp";
        try{networkReaderBDTOPO.readBDTOPOFile(network_TI,shpFilePath);}
        catch(Exception e){e.printStackTrace();}

        DirectedWeightedMultigraph<Node, Link> tcGraph = network_TI.createGraph();
        */
    }
}