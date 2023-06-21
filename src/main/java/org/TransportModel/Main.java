package org.TransportModel;

import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
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
        Area idf = setupIDFArea();
        Network tcNetwork = setupTCNetwork();
        Network tiNetwork = setupTCNetwork();
        //displayShortestPath(tcNetwork,tcNetwork.getNode("IDFM:22015"),tcNetwork.getNode("IDFM:463025"));
        Zone commune1 = idf.getZone(""+7);
        Node commune1Node = new Node(commune1.getId(),commune1.getName(),commune1.getCentroid());
        Zone commune2 = idf.getZone(""+219);
        Node commune2Node = new Node(commune2.getId(),commune2.getName(),commune2.getCentroid());
        tcNetwork.addAndLinkNode(commune1Node);
        tcNetwork.addAndLinkNode(commune2Node);
        displayShortestPath(tcNetwork,commune1Node,commune2Node);
        //entre les deux chateaux et lagny: 16.0833333333 (map = 34)
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Area setupIDFArea()
    {
        Area idf = new Area();
        ZoneReaderBDTOPO zoneReaderBDTOPO = new ZoneReaderBDTOPO();
        String communesShapeFile = "src/main/resources/Zone/BDTOPO_IDF/communes.shp";
        try{zoneReaderBDTOPO.readBDTOPOFile(idf, communesShapeFile);}
        catch(Exception e){e.printStackTrace();}
        return idf;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network setupTCNetwork()
    {
        Network tcNetwork = new Network();
        NetworkReaderGTFS networkReaderGTFS = new NetworkReaderGTFS();
        String gtfsFolderPath = "src/main/resources/TC/GTFS_IDF";
        try{networkReaderGTFS.readGTFSFolder(tcNetwork,gtfsFolderPath);}
        catch(Exception e){e.printStackTrace();}
        return tcNetwork;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network setupTINetwork()
    {
        Network tiNetwork = new Network();
        NetworkReaderBDTOPO networkReaderBDTOPO = new NetworkReaderBDTOPO();
        String shpFilePath = "src/main/resources/TI/BDTOPO_94/TRONCON_DE_ROUTE.shp";
        try{networkReaderBDTOPO.readBDTOPOFile(tiNetwork,shpFilePath);}
        catch(Exception e){e.printStackTrace();}
        return tiNetwork;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void displayShortestPath(Network network,Node fromNode, Node toNode)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        GraphPath<Node, Link> shortestPath = shortestPathAlgorithm.getPath(fromNode, toNode);
        for(Link link:shortestPath.getEdgeList()){
            System.out.println("Entre "+link.getFromNode().getName()+" et "+link.getToNode().getName()+"" +
                    ": "+(int)graph.getEdgeWeight(link)+ "s en "+link.getType().name() + "("+link.getName()+")");
           // System.out.println(link.getFromNode().getId()+" et "+link.getToNode().getId());
        }
        int hours = (int)shortestPath.getWeight() / 3600;
        int minutes = (int)(shortestPath.getWeight() % 3600) / 60;
        int seconds = (int)shortestPath.getWeight() % 60;
        System.out.println("\nTemps total en secondes entre : "+fromNode.getName()+
                " et "+ toNode.getName()+": "+String.format("%02d:%02d:%02d", hours, minutes, seconds)+ "s");
    }
}