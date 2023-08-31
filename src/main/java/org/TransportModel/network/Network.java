package org.TransportModel.network;

import org.TransportModel.generation.Zone;
import org.TransportModel.utils.CoordinateUtils;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.kdtree.KdNode;
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
    public Node getNode(String id){return this.nodes.get(id);}
    public List<Node> getNodes(){return new ArrayList<>(this.nodes.values());}
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
    /** Removes the link with the specified ID from the network
     * @param id The ID of the link to be removed */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeLink(String id)
    {
        Link link = this.links.remove(id);
        link.getFromNode().removeOutLink(link);
        link.getToNode().removeInLink(link);
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
    /** Creates a directed weighted graph representation of the network (weight = time)
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
    /** Creates a KdTree, a data structure used for efficient range searches
     @return A KdTree populated with nodes from the network */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public KdTree createKdTree()
    {
        KdTree kdTree = new KdTree();
        for(Node node:this.getNodes())
            kdTree.insert(node.getCoordinate(),node);
        return kdTree;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Removes all transit nodes and connects their neighboring nodes directly. A transit node is a
     * node that is connected to exactly two other nodes, either unidirectionally or bidirectionally */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeTransitNodes()
    {
        for (Node node : this.getNodes()) {
            //Remove unidirectional
            if (node.getInLinks().size() == 1 && node.getOutLinks().size() == 1) {
                Link inLink = node.getInLinks().get(0);
                Link outLink = node.getOutLinks().get(0);
                if (!inLink.getFromNode().equals(outLink.getToNode())) {
                    addLink(inLink.fusLink(outLink));
                    removeNode(node);}}
            //remove bidirectional
            if (node.getInLinks().size() == 2 && node.getOutLinks().size() == 2) {
                Link inLink1 = node.getInLinks().get(0);
                Link inLink2 = node.getInLinks().get(1);
                Link outLink1 = node.getOutLinks().get(0);
                Link outLink2 = node.getOutLinks().get(1);
                //inLink1 -> node -> outLink1 | outLink2 <- node <- inLink2
                boolean cond1 = inLink1.getFromNode().equals(outLink2.getToNode())
                             && inLink2.getFromNode().equals(outLink1.getToNode());
                //inLink1 -> node -> outLink2 | outLink1 <- node <- inLink2
                boolean cond2 = inLink1.getFromNode().equals(outLink1.getToNode())
                             && inLink2.getFromNode().equals(outLink2.getToNode());
                if ((cond1 || cond2) && !inLink1.getFromNode().equals(inLink2.getFromNode())) {
                    addLink(inLink1.fusLink(cond1 ? outLink1 : outLink2));
                    addLink(inLink2.fusLink(cond1 ? outLink2 : outLink1));
                    removeNode(node);}}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /*** Removes nodes that are not part of the largest strongly connected component in the graph */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeNotStronglyConnectedNodes()
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
    /** Links zones' centroids to the nearest nodes
     * @param zones A HashMap containing zones to be linked */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void linkZones(HashMap<String, Zone> zones)
    {
        KdTree kdTree = this.createKdTree();
        for(Zone zone:zones.values()) {
            Node zoneNode = new Node(zone.getId(),zone.getName(),zone.getCentroid());
            Node closestNode = this.getClosestNode(kdTree,zoneNode);
            this.addNode(zoneNode);
            this.addLink(new Link(zoneNode,closestNode,"centroidLink"));
            this.addLink(new Link(closestNode,zoneNode,"centroidLink"));}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Returns the closest node of the KdTree to the given node
     @param kdTree The KdTree to search in
     @param node The node to find the closest node to
     @return The closest node of the KdTree */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private Node getClosestNode(KdTree kdTree, Node node)
    {
        List<?> nearKdNodes = new ArrayList<>();
        List<Node> nearNodes = new ArrayList<>();
        double size = 0.01;
        while(nearKdNodes == null || nearKdNodes.isEmpty() || nearKdNodes.size() == 1) {
            double x = node.getX(), y =  node.getY();
            Envelope envelope = new Envelope(x-size,x+size,y-size,y+size);
            nearKdNodes = kdTree.query(envelope);
            size = size*2;}
        for(Object kdNode: nearKdNodes)
            nearNodes.add((Node)((KdNode)kdNode).getData());
        double minDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for(Node nearNode: nearNodes){
            if(!nearNode.equals(node)) {
                double distance = CoordinateUtils.calculateWSG84Distance(nearNode.getCoordinate(),node.getCoordinate());
                if(closestNode == null || distance < minDistance) {
                    closestNode = nearNode;
                    minDistance = distance;}}}
        return closestNode;
    }
}