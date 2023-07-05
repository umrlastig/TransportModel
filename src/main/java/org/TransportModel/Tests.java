package org.TransportModel;

import com.vividsolutions.jts.util.Assert;
import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
import org.TransportModel.generation.io.CommunesReader;
import org.TransportModel.gui.GraphCanvas;
import org.TransportModel.gui.UserInterface;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.network.io.NetworkReaderBDTOPO;
import org.TransportModel.network.io.NetworkReaderGTFS;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import javax.swing.*;
import java.util.Arrays;
import java.util.Set;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Tests
{
    private final Network tiNetwork, tcNetwork;
    private final Area area;
    private final UserInterface userInterface;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Tests()
    {
        //Setup area
        this.area = new Area();
        CommunesReader.readShapeFile(area, Config.getInstance().getFilePaths().getCommunesShapeFileBDTOPO());
        CommunesReader.readPopulationAndWorkersFile(area,Config.getInstance().getFilePaths().getCommunesPopulationAndWorkersFileINSEE());
        CommunesReader.readStudentsFile(area,Config.getInstance().getFilePaths().getCommunesStudentsFileINSEE());
        CommunesReader.readWorkFlowsFile(area,Config.getInstance().getFilePaths().getCommunesWorkFlowsFileINSEE());
        CommunesReader.readStudentFlowsFile(area,Config.getInstance().getFilePaths().getCommunesStudentFlowsFileINSEE());
        //Setup tc
        tcNetwork = new Network();
        NetworkReaderGTFS.readGTFSFolder(tcNetwork, Config.getInstance().getFilePaths().getNetworkFolderGTFS());
        //Setup ti
        tiNetwork = new Network();
        for(String shpFilePath:Arrays.asList(Config.getInstance().getFilePaths().getNetworkFilesBDTOPO()))
            NetworkReaderBDTOPO.readBDTOPORouteFile(tiNetwork,shpFilePath);
        //UserInterface
        this.userInterface = new UserInterface();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void testArea()
    {
        //Take a commune for tests
        Zone bobigny = area.getZone("93008");
        Assert.isTrue(bobigny != null,"Commune missing");
        //Test pop
        double populationDifference = Math.abs(54363 - bobigny.getPopulation());
        boolean validPopulation = (populationDifference / bobigny.getPopulation()) * 100 <= 5;
        Assert.isTrue(validPopulation, "population not good "+populationDifference);
        //Test workers
        double workersDifference = Math.abs(22855 - bobigny.getWorkers());
        boolean validWorkers = (workersDifference / bobigny.getWorkers()) * 100 <= 5;
        Assert.isTrue(validWorkers, "workers not good: " + bobigny.getWorkers()+"/22855");
        //Test students
        double studentsDifference = Math.abs(16887 - bobigny.getStudents());
        boolean validStudents = (studentsDifference / bobigny.getStudents()) * 100 <= 5;
        Assert.isTrue(validStudents, "students not good "+bobigny.getStudents()+"/16887");
        //ok
        System.out.println("Area ok");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void testTC()
    {
        DirectedWeightedMultigraph<Node, Link> graph = tcNetwork.createGraph();
        //Connected
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        int elements = connectivityInspector.connectedSets().size();
        Assert.isTrue(connectivityInspector.isConnected(),"TC not connected ("+elements+")");
        //Strongly Connected
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        elements = strongInspector.stronglyConnectedSets().size();
        Assert.isTrue(strongInspector.isStronglyConnected(),"TC not strongly connected ("+elements+")");
        //ok
        System.out.println("TC ok");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void testTI()
    {
        DirectedWeightedMultigraph<Node, Link> graph = tiNetwork.createGraph();
        //Connected
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        int elements = connectivityInspector.connectedSets().size();
        Assert.isTrue(connectivityInspector.isConnected(),"TI not connected ("+elements+")");
        //Strongly Connected
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        elements = strongInspector.stronglyConnectedSets().size();
        Assert.isTrue(strongInspector.isStronglyConnected(),"TI not strongly connected ("+elements+")");
        //ok
        System.out.println("TI ok");
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
    public void displayTI()
    {
        DirectedWeightedMultigraph<Node, Link> graph = tiNetwork.createGraph();
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        GraphCanvas graphCanvas = new GraphCanvas();
        System.out.println(connectivityInspector.connectedSets().size());
        for(Set<Node> nodes : connectivityInspector.connectedSets())
        {
            DirectedWeightedMultigraph<Node, Link> connectedGraph = new DirectedWeightedMultigraph<>(Link.class);
            for(Node node:nodes)
                for(Link link:node.getOutLinks())
                {
                    connectedGraph.addVertex(link.getFromNode());
                    connectedGraph.addVertex(link.getToNode());
                    connectedGraph.addEdge(link.getFromNode(),link.getToNode(),link);
                }
            graphCanvas.addGraph(connectedGraph);
        }
        userInterface.display(graphCanvas);
        JButton button = new JButton("nextConnectedGraph");
        button.addActionListener(e -> {graphCanvas.nextIndex();userInterface.repaint();});
        userInterface.addButton(button);

    }
}
