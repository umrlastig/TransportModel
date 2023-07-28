package org.TransportModel;

import com.vividsolutions.jts.util.Assert;
import org.TransportModel.generation.Zone;
import org.TransportModel.gui.GraphCanvas;
import org.TransportModel.gui.UserInterface;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.apache.commons.math3.linear.RealMatrix;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.swing.*;
import java.util.Set;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Tests
{
    private Tests(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") public void testNetwork(Network network)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        //Connected
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        int elements = connectivityInspector.connectedSets().size();
        Assert.isTrue(connectivityInspector.isConnected(),"Network not connected ("+elements+")");
        //Strongly Connected
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        elements = strongInspector.stronglyConnectedSets().size();
        Assert.isTrue(strongInspector.isStronglyConnected(),"Network not strongly connected ("+elements+")");
        //ok
        System.out.println("Network ok");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Displays a path with strings */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") private static void displayPath(GraphPath<Node,Link> graphPath)
    {
        for(Link link:graphPath.getEdgeList()) {
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
    @SuppressWarnings("unused") private void displayConnectedGraphs(Network network)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        GraphCanvas graphCanvas = new GraphCanvas();
        System.out.println(connectivityInspector.connectedSets().size()+" connected graphs");
        for(Set<Node> nodes : connectivityInspector.connectedSets()) {
            DirectedWeightedMultigraph<Node, Link> connectedGraph = new DirectedWeightedMultigraph<>(Link.class);
            for(Node node:nodes)
                for(Link link:node.getOutLinks()) {
                    connectedGraph.addVertex(link.getFromNode());
                    connectedGraph.addVertex(link.getToNode());
                    connectedGraph.addEdge(link.getFromNode(),link.getToNode(),link);}
            graphCanvas.addGraph(connectedGraph);
        }
        UserInterface userInterface = new UserInterface();
        userInterface.display(graphCanvas);
        JButton button = new JButton("nextConnectedGraph");
        button.addActionListener(e -> {graphCanvas.nextIndex();userInterface.repaint();});
        userInterface.addButton(button);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void printMatrixPart(RealMatrix matrix, int startRow, int endRow, int startColumn, int endColumn)
    {
        int numRows = matrix.getRowDimension();
        int numColumns = matrix.getColumnDimension();
        if (startRow < 0 || startRow >= numRows || endRow < 0 || endRow >= numRows || startColumn < 0 ||
                startColumn >= numColumns || endColumn < 0 || endColumn >= numColumns)
            throw new IllegalArgumentException();
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                System.out.print(matrix.getEntry(i, j) + "\t");
            }
            System.out.println();
        }
    }
}
