package org.TransportModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.TransportModel.generation.Area;
import org.TransportModel.generation.io.CommunesPopulationReaderINSEE;
import org.TransportModel.generation.io.CommunesShapeReaderBDTOPO;
import org.TransportModel.gui.GraphCanvas;
import org.TransportModel.gui.UserInterface;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.io.File;
import java.io.IOException;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**                                       Main Class                                             */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Main
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Main Function                                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws IOException
    {
        //Setup
        setupConfig();
        Area area = setupArea();
        Network tcNetwork = setupTCNetwork();
        Network tiNetwork = setupTINetwork();
        //Use
        //displayShortestPath(tcNetwork,tcNetwork.getNode("IDFM:22015"),tcNetwork.getNode("IDFM:463025"));
        /*
        tcNetwork.addAndLinkNode(commune1Node);
        tcNetwork.addAndLinkNode(commune2Node);
        displayShortestPath(tcNetwork,commune1Node,commune2Node);
        Zone commune1 = idf.getZone(""+7);
        Zone commune2 = idf.getZone(""+219);
        Node commune1Node = new Node(commune1.getId(),commune1.getName(),commune1.getCentroid());
        Node commune2Node = new Node(commune2.getId(),commune2.getName(),commune2.getCentroid());
        tiNetwork.addAndLinkNode(commune1Node);
        tiNetwork.addAndLinkNode(commune2Node);
        DirectedWeightedMultigraph<Node, Link> graph = tiNetwork.createGraph();
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        GraphPath<Node, Link> shortestPath = shortestPathAlgorithm.getPath(commune1Node, commune2Node);
        */
        //Display
        //UserInterface userInterface = new UserInterface();
        //GraphCanvas graphCanvas = new GraphCanvas(tcNetwork.createGraph());
        //graphCanvas.addPath(shortestPath);
        //userInterface.display(graphCanvas);
        //displayShortestPath(tiNetwork,commune1Node,commune2Node);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void setupConfig() throws IOException
    {
        String configFilePath = "src/main/resources/config.json";
        ObjectMapper objectMapper = new ObjectMapper();
        Config config = objectMapper.readValue(new File(configFilePath), Config.class);
        Config.setInstance(config);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Area setupArea()
    {
        Area area = new Area();
        try {
            CommunesShapeReaderBDTOPO.readBDTOPOFile(area, Config.getFilePaths().getCommunesShapeFileBDTOPO());
            CommunesPopulationReaderINSEE.readINSEEFILE(area,Config.getFilePaths().getCommunesPopulationFileINSEE());
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
        try{NetworkReaderGTFS.readGTFSFolder(tcNetwork,Config.getFilePaths().getNetworkFolderGTFS());}
        catch(Exception e){e.printStackTrace();}
        return tcNetwork;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Network setupTINetwork()
    {
        Network tiNetwork = new Network();
        try{for(String shpFilePath:Config.getFilePaths().getNetworkFilesBDTOPO())
                NetworkReaderBDTOPO.readBDTOPORouteFile(tiNetwork,shpFilePath);}
        catch(Exception e){e.printStackTrace();}
        return tiNetwork;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Displays the shortest path between two nodes in the network
     * @param network the network in which to search for the shortest path
     * @param fromNode the starting node
     * @param toNode the destination node */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void displayShortestPath(Network network,Node fromNode, Node toNode)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        GraphPath<Node, Link> shortestPath = shortestPathAlgorithm.getPath(fromNode, toNode);
        for(Link link:shortestPath.getEdgeList()) {
            System.out.print("\nEntre "+link.getFromNode().getName()+" et "+link.getToNode().getName()+ ": ");
            System.out.print((int)graph.getEdgeWeight(link)+ "s en "+link.getType().name() + "("+link.getName()+")");
            System.out.print("  "+link.getFromNode().getId()+" et "+link.getToNode().getId());
        }
        int time = (int)shortestPath.getWeight(), linkNumber = shortestPath.getEdgeList().size();
        System.out.print("\nTemps total entre : "+fromNode.getName()+ " et "+ toNode.getName()+": ");
        System.out.print(String.format("%02d:%02d:%02d",time/3600,time%3600/60,time%60)+ " (" +linkNumber+" liens)");
    }
}