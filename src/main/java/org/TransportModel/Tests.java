package org.TransportModel;
import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Tests
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void testConnectivity(Network network)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        if(!connectivityInspector.isConnected())
            System.out.println("Not connected graph ("+connectivityInspector.connectedSets().size()+" components)");
        else if(!strongInspector.isStronglyConnected())
            System.out.println("Not strongly connected graph ("+strongInspector.stronglyConnectedSets().size()+" components)");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Displays a path with strings */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") private static void displayPath(GraphPath<Node,Link> graphPath)
    {
        for(Link link:graphPath.getEdgeList())
        {
            String fromName = link.getFromNode().getName(), toName = link.getToNode().getName();
            String linkType = link.getType().name(), linkName = link.getName();
            int linkTime = (int)(link.getLengthInM()/link.getNormalSpeedInMS());
            System.out.print("\nEntre "+fromName+" et "+toName+ ": "+linkTime+ "s en "+linkType + " ("+linkName+")");
        }
        int linkNumber = graphPath.getEdgeList().size(), timeInS = (int)graphPath.getWeight();
        String time = String.format("%02d:%02d:%02d",timeInS/3600,timeInS%3600/60,timeInS%60);
        System.out.print("\nTemps total: "+time+" (" +linkNumber+" liens)");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void testPathTC(Network tcNetwork)
    {
        GraphPath<Node,Link> picasso_placeDItalie = tcNetwork.getShortestPath("IDFM:22015","IDFM:71033");
        displayPath(picasso_placeDItalie);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void testPopulation(Area area)
    {

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void testPathZones(Network tiNetwork, Area area)
    {
        Zone bobigny = area.getZone("93008");
        Zone vincenne = area.getZone("75112");

    }
}
