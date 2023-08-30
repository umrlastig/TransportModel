package org.TransportModel.network;

import org.TransportModel.generation.Zone;
import org.TransportModel.utils.CoordinateUtils;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.geom.Coordinate;
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
    public boolean containsNode(String id){return this.nodes.containsKey(id);}
    public boolean containsNode(Node node){return this.containsNode(node.getId());}
    public boolean containsLink(String id){return this.links.containsKey(id);}
    public boolean containsLink(Link link){return this.containsLink(link.getId());}
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
    public void removeTransitNodes()
    {
        removeBidirectionalTransitNodes();
        removeUnidirectionalTransitNodes();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
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
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeBidirectionalTransitNodes()
    {
        for (Node node : this.getNodes())
            if(node.getInLinks().size() == 2 && node.getOutLinks().size() == 2) {
                Node fromNode0 = node.getInLinks().get(0).getFromNode(), toNode0 = node.getOutLinks().get(0).getToNode();
                Node fromNode1 = node.getInLinks().get(1).getFromNode(), toNode1 = node.getOutLinks().get(1).getToNode();
                if(fromNode0.equals(toNode0) && fromNode1.equals(toNode1) && !fromNode0.equals(fromNode1))
                {
                    Link directLink1 = node.getInLinks().get(0), directLink2 = node.getOutLinks().get(1);
                    Link inverseLink1 = node.getInLinks().get(1), inverseLink2 = node.getOutLinks().get(0);
                    Link newDirectLink = directLink1.fusLink(directLink2);
                    Link newInverseLink = inverseLink1.fusLink(inverseLink2);
                    addLink(newDirectLink);
                    addLink(newInverseLink);
                    removeNode(node);
                }
                else if(fromNode0.equals(toNode1) && fromNode1.equals(toNode0) && !fromNode0.equals(fromNode1))
                {
                    Link directLink1 = node.getInLinks().get(0), directLink2 = node.getOutLinks().get(0);
                    Link inverseLink1 = node.getInLinks().get(1), inverseLink2 = node.getOutLinks().get(1);
                    Link newDirectLink = directLink1.fusLink(directLink2);
                    Link newInverseLink = inverseLink1.fusLink(inverseLink2);
                    addLink(newDirectLink);
                    addLink(newInverseLink);
                    removeNode(node);
                }
            }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeUnidirectionalTransitNodes()
    {
        for (Node node : this.getNodes())
            if (node.getInLinks().size() == 1 && node.getOutLinks().size() == 1 ) {
                Node fromNode = node.getInLinks().get(0).getFromNode(), toNode = node.getOutLinks().get(0).getToNode();
                if(!fromNode.equals(toNode)) {
                    Link link1 = node.getInLinks().get(0);
                    Link link2 = node.getOutLinks().get(0);
                    Link newLink = link1.fusLink(link2);
                    addLink(newLink);
                    removeNode(node);}}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void linkZones(HashMap<String, Zone> zones)
    {
        KdTree kdTree = new KdTree();
        for(Node node:this.getNodes())
            kdTree.insert(node.getCoordinate(),node);
        for(Zone zone:zones.values()) {
            Node zoneNode = new Node(zone.getId(),zone.getName(),zone.getCentroid());
            this.addNode(zoneNode);
            List<Node> nearNodes = getNearNodes(kdTree,zone.getCentroid());
            Node closestNode = getNearestNode(nearNodes,zoneNode);
            this.addLink(new Link(zoneNode,closestNode,"centroidLink"));
            this.addLink(new Link(closestNode,zoneNode,"centroidLink"));}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private Node getNearestNode(List<Node> nearNodes, Node zoneNode)
    {
        double minDistance = Double.MAX_VALUE;
        Node closestNode = null;
        for(Node node: nearNodes){
            if(!node.equals(zoneNode)) {
                double distance = CoordinateUtils.calculateWSG84Distance(node.getCoordinate(),zoneNode.getCoordinate());
                if(closestNode == null || distance < minDistance) {
                    closestNode = node;
                    minDistance = distance;}}}
        return closestNode;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Node> getNearNodes(KdTree kdTree, Coordinate coordinate)
    {
        List<?> nearKdNodes = new ArrayList<>();
        List<Node> nearNodes = new ArrayList<>();
        double size = 0.01;
        while(nearKdNodes == null || nearKdNodes.isEmpty() || nearKdNodes.size() == 1) {
            double x = coordinate.x, y = coordinate.y;
            Envelope envelope = new Envelope(x-size,x+size,y-size,y+size);
            nearKdNodes = kdTree.query(envelope);
            size = size*2;}
        for(Object kdNode: nearKdNodes)
            nearNodes.add((Node)((KdNode)kdNode).getData());
        return nearNodes;
    }
}