package org.TransportModel.network;

import org.TransportModel.utils.CoordinateUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.index.kdtree.KdTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    public Node getNode(String id){return this.nodes.get(id);}
    public List<Node> getNodes(){return new ArrayList<>(this.nodes.values());}
    public List<Link> getLinks(){return new ArrayList<>(this.links.values());}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Adds a new node. If the node already exists, replace  link's node linked
     * @param node The Node to add */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addNode(Node node)
    {
        Node existingNode = this.nodes.get(node.getId());
        if (existingNode != null && !existingNode.equals(node)) {
            existingNode.getInLinks().forEach(inLink -> {node.addInLink(inLink); inLink.setToNode(node);});
            existingNode.getOutLinks().forEach(outLink -> {node.addOutLink(outLink); outLink.setFromNode(node);});}
        this.nodes.put(node.getId(),node);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Removes the node and all associated links from the network
     * @param id The ID of the node to be removed */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeNode(String id)
    {
        Node node = this.nodes.remove(id);
        for(Link inLink:node.getInLinks())
            this.removeLink(inLink.getId());
        for(Link outLink:node.getOutLinks())
            this.removeLink(outLink.getId());
    }
    public void removeNode(Node node){this.removeNode(node.getId());}
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
        for(Link link:this.links.values()) {
            graph.addEdge(link.getFromNode(),link.getToNode(),link);
            double time = link.getLengthInM()/link.getNormalSpeedInMS();
            if(graph.containsEdge(link))
                graph.setEdgeWeight(link,time);}
        return graph;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeNotStronglyConnected()
    {
        DirectedWeightedMultigraph<Node, Link> graph = this.createGraph();
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        int maxSize = 0;
        Set<Node> largestComponent = null;
        for (Set<Node> nodes : strongInspector.stronglyConnectedSets())
            if (nodes.size() > maxSize) {
                maxSize = nodes.size();
                largestComponent = nodes;}
        if (largestComponent != null)
            for (Node node:this.getNodes())
                if (!largestComponent.contains(node))
                   this.removeNode(node);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public KdTree createKDTree()
    {
        KdTree kdTree = new KdTree();
        for(Node node:this.getNodes())
            kdTree.insert(node.getCoordinate(),node);
        return kdTree;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") public GraphPath<Node,Link> getShortestPath(DirectedWeightedMultigraph<Node, Link> graph, String fromNodeId, String toNodeId)
    {
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        return shortestPathAlgorithm.getPath(nodes.get(fromNodeId), nodes.get(toNodeId));
    }
}