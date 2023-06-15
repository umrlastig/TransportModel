package org.TransportModel.network;

import org.geotools.referencing.GeodeticCalculator;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**    Network class represents a transportation network containing a graph of nodes and links   */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Network
{
    private final HashMap<String,Node> nodes;
    private final HashMap<String,Link> links;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                       Constructor                                            */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Network(){
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Getters                                               */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean containsNode(String id){return this.nodes.containsKey(id);}
    public boolean containsLink(String id){return this.nodes.containsKey(id);}
    public Node getNode(String id) {return this.nodes.get(id);}
    public HashMap<String,Node> getNodes(){return this.nodes;}
    public Link getLink(String id){return this.links.get(id);}
    public HashMap<String,Link> getLinks(){return this.links;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                                                                              */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addNode(Node node) {
        //If node id already exists, do nothing
        if(!this.containsNode(node.getId()))
            this.nodes.put(node.getId(),node);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                                                                              */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addLink(Link link) {
        //Check if from node already exits
        if(!this.containsNode(link.getFromNode().getId()))
            this.addNode(link.getFromNode());
        else
            link.setFromNode(this.getNode(link.getFromNode().getId()));
        //Check if to node already exists
        if(!this.containsNode(link.getToNode().getId()))
            this.addNode(link.getToNode());
        else
            link.setToNode(this.getNode(link.getToNode().getId()));
        //If link id already exists, replace it
        this.links.put(link.getId(),link);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public GraphPath<Node, Link> getShortestPath(Coordinate from, Coordinate to)
    {
        Node fromNode = new Node("from",from);
        Node toNode = new Node("to",from);
        Graph<Node, Link> graph = this.createGraph();
        //link coordinates to the shortest coordinates
        Double fromShortestDistance = null;
        Node fromShortestNode = null;
        Double toShortestDistance = null;
        Node toShortestNode = null;
        for(Node node:this.nodes.values())
        {
            double fromDistance = this.calculateDistance(node.getCoordinate(),from);
            double toDistance = this.calculateDistance(node.getCoordinate(),to);
            if(fromShortestDistance == null || fromDistance<fromShortestDistance)
            {
                fromShortestDistance = fromDistance;
                fromShortestNode=node;
            }
            if(toShortestDistance == null || toDistance<toShortestDistance)
            {
                toShortestDistance = fromDistance;
                toShortestNode=node;
            }
        }
        graph.addVertex(fromNode);
        graph.addVertex(toNode);
        Link link1 = new Link("link1",fromNode,fromShortestNode,true,1000,100000,1);
        Link link2 = new Link("link2",toNode,toShortestNode,true,1000,100000,1);
        graph.addEdge(fromNode,fromShortestNode,link1);
        graph.addEdge(toNode,toNode,link2);

        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        return shortestPathAlgorithm.getPath(fromNode, toNode);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private Graph<Node, Link> createGraph()
    {
        Graph<Node, Link> graph = new DefaultDirectedWeightedGraph<>(Link.class);
        for(Node node:this.nodes.values())
            graph.addVertex(node);
        for(Link link:this.links.values())
        {
            graph.addEdge(link.getFromNode(),link.getToNode(),link);
            graph.setEdgeWeight(link,link.getLengthInM());
        }
        return graph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Calculates the distance in meters between two coordinates
     * @param coordinate1 The first coordinate
     * @param coordinate2 The second coordinate
     * @return The distance in meters between the two coordinates */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateDistance(Coordinate coordinate1, Coordinate coordinate2)
    {
        GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(coordinate1.getX(), coordinate1.getY());
        calculator.setDestinationGeographicPoint(coordinate2.getX(), coordinate2.getY());
        return calculator.getOrthodromicDistance();
    }
}
