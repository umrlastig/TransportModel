package org.TransportModel.network;

import org.geotools.referencing.GeodeticCalculator;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.Objects;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Network
{
    private final HashMap<String,Node> nodes;
    private final HashMap<String,Link> links;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Network()
    {
        this.nodes = new HashMap<>();
        this.links = new HashMap<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
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
    /** Adds a new node
     * If the node already exists, replace  link's node linked
     * @param node The Node to add */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addNode(Node node)
    {
        //If Node already exist, replace it in every link
        if(this.containsNode(node.getId()))
        {
            for(Link inLink:this.nodes.get(node.getId()).getInLinks().values())
            {
                node.addInLink(inLink);
                inLink.setToNode(node);
            }
            for(Link outLink:this.nodes.get(node.getId()).getOutLinks().values())
            {
                node.addOutLink(outLink);
                outLink.setFromNode(node);
            }
        }
        this.nodes.put(node.getId(),node);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Adds a new link
     * @param link The link to add */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addLink(Link link)
    {
        Node fromNode = link.getFromNode();
        Node toNode = link.getToNode();
        if(fromNode.getId().equals(toNode.getId()))
            return;
        toNode.addInLink(link);
        fromNode.addOutLink(link);
        if(fromNode != this.getNode(fromNode.getId()))
            this.addNode(fromNode);
        if(toNode != this.getNode(toNode.getId()))
            this.addNode(toNode);
        this.links.put(link.getId(),link);
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
    /** Creates a graph representation of the network using JGraphT library (weight = time)
     * @return The created graph */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public DirectedWeightedMultigraph<Node,Link> createGraph()
    {
        DirectedWeightedMultigraph<Node, Link> graph = new DirectedWeightedMultigraph<>(Link.class);
        for(Node node:this.nodes.values())
            graph.addVertex(node);
        for(Link link:this.links.values())
        {
            graph.addEdge(link.getFromNode(),link.getToNode(),link);
            double time = link.getLengthInM()/link.getNormalSpeedInMS();
            if(graph.containsEdge(link))
                graph.setEdgeWeight(link,time);
        }
        return graph;
    }
}
