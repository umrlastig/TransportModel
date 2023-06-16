package org.TransportModel.network;

import org.geotools.referencing.GeodeticCalculator;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
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
    public Network()
    {
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Getters                                               */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean containsNode(String id){return this.nodes.containsKey(id);}
    public boolean containsLink(String id){return this.nodes.containsKey(id);}
    public Node getNode(String id){return this.nodes.get(id);}
    public Link getLink(String id){return this.links.get(id);}
    public HashMap<String,Node> getNodes(){return this.nodes;}
    public HashMap<String,Link> getLinks(){return this.links;}
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates and adds a new node with the specified attributes
     * If the node already exists, updates its coordinates
     * @param id         The ID of the node
     * @param coordinate The coordinates of the node */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void createAndAddNode(String id, Coordinate coordinate)
    {
        if(this.containsNode(id))
            this.nodes.get(id).setCoordinate(coordinate);
        else
            this.nodes.put(id,new Node(id,coordinate));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates and adds a new link with the specified attributes
     * @param id         The ID of the link
     * @param fromNodeId The ID of the source node
     * @param toNodeId   The ID of the target node
     * @param speed      The speed (m/s)
     * @param capacity   The capacity (persons/hour)
     * @param length     The length (m) */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void createAndAddLink(String id,String fromNodeId,String toNodeId, double speed, double capacity, double length)
    {
        Node fromNode = this.getNode(fromNodeId);
        Node toNode = this.getNode(toNodeId);
        Link link = new Link(id,fromNode,toNode,speed,capacity,length);
        fromNode.addOutLink(link);
        toNode.addInLink(link);
        this.links.put(id,link);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Removes the node and all associated links from the network
     * @param id The ID of the node to be removed */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeNode(String id)
    {
        Node node = this.nodes.remove(id);
        for(String linkId:node.getInLinks().keySet())
            this.removeLink(linkId);
        for(String linkId:node.getOutLinks().keySet())
            this.removeLink(linkId);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Removes the link with the specified ID from the network
     * @param id The ID of the link to be removed */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeLink(String id)
    {
        this.links.remove(id);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Retrieves the shortest path between the specified source node and target node in the network.
     * @param fromNode The source node
     * @param toNode   The target node
     * @return The shortest path as a GraphPath object */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public GraphPath<Node, Link> getShortestPath(Node fromNode, Node toNode)
    {
        Graph<Node, Link> graph = this.createGraph();
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        return shortestPathAlgorithm.getPath(fromNode, toNode);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates a graph representation of the network using JGraphT library (weight = time)
     * @return The created graph */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public DirectedWeightedMultigraph<Node, Link> createGraph()
    {
        DirectedWeightedMultigraph<Node, Link> graph = new DirectedWeightedMultigraph<>(Link.class);
        for(Node node:this.nodes.values())
            graph.addVertex(node);
        for(Link link:this.links.values())
        {
            graph.addEdge(link.getFromNode(),link.getToNode(),link);
            double time = link.getNormalSpeedInMS()/link.getLengthInM();
            if(graph.containsEdge(link))
                graph.setEdgeWeight(link,time);
        }
        return graph;
    }
}
