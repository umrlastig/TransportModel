package org.TransportModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
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
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
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
        isConnected(tiNetwork);
        //Node bobignyPabloPicasso = tcNetwork.getNode("IDFM:22015");
        //Node berault = tcNetwork.getNode("IDFM:27881");
        //Node placeDItalie = tcNetwork.getNode("IDFM:22365");
        //displayShortestPath(tcNetwork,bobignyPabloPicasso,berault);
        Zone bobigny = area.getZone("93008");
        Zone vincenne = area.getZone("75112");
        GraphPath<Node, Link> shortestPath = displayShortestPath(tiNetwork,bobigny,vincenne);
        //Display
        UserInterface userInterface = new UserInterface();
        GraphCanvas graphCanvas = new GraphCanvas(tiNetwork.createGraph());
        graphCanvas.addPath(shortestPath);
        userInterface.display(graphCanvas);
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
    /** @return y*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static GraphPath<Node, Link> displayShortestPath(Network network, Zone zone1, Zone zone2)
    {
        Node node1 = new Node(zone1.getId(),zone1.getName(),zone1.getCentroid());
        Node node2 = new Node(zone2.getId(),zone2.getName(),zone2.getCentroid());
        network.addAndLinkNode(node1);
        network.addAndLinkNode(node2);
        return displayShortestPath(network,node1,node2);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Displays the shortest path between two nodes in the network
     * @param network  the network in which to search for the shortest path
     * @param fromNode the starting node
     * @param toNode   the destination node
     * @return y*/
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static GraphPath<Node,Link> displayShortestPath(Network network, Node fromNode, Node toNode)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        GraphPath<Node, Link> shortestPath = shortestPathAlgorithm.getPath(fromNode, toNode);
        for(Link link:shortestPath.getEdgeList()) {
            System.out.print("\nEntre "+link.getFromNode().getName()+" et "+link.getToNode().getName()+ ": ");
            System.out.print((int)graph.getEdgeWeight(link)+ "s en "+link.getType().name() + "("+link.getName()+")");
            //System.out.print("  "+link.getFromNode().getId()+" et "+link.getToNode().getId());
        }
        int time = (int)shortestPath.getWeight(), linkNumber = shortestPath.getEdgeList().size();
        System.out.print("\nTemps total entre : "+fromNode.getName()+ " et "+ toNode.getName()+": ");
        System.out.print(String.format("%02d:%02d:%02d",time/3600,time%3600/60,time%60)+ " (" +linkNumber+" liens)");
        return shortestPath;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void isConnected(Network network)
    {
        DirectedWeightedMultigraph<Node,Link> graph = network.createGraph();
        KosarajuStrongConnectivityInspector<Node,Link> connectivityInspector = new KosarajuStrongConnectivityInspector<>(graph);
        System.out.println(connectivityInspector.isStronglyConnected()+ ": "+connectivityInspector.stronglyConnectedSets().size());
    }
}