package org.TransportModel.generation;


import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.utils.CoordinateUtils;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.FastMath;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.kdtree.KdNode;
import org.locationtech.jts.index.kdtree.KdTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class FlowDistributor
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix calculateMinTimes(HashMap<String,Zone> zones,Network... networks)
    {
        RealMatrix minTimes = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        List<Zone> zoneList = new ArrayList<>(zones.values());
        for (int i = 0; i < zoneList.size() - 1; i++)
            for (int j = i + 1; j < zoneList.size(); j++) {
                Zone from = zoneList.get(i);
                Zone to = zoneList.get(j);
                double distance = CoordinateUtils.calculateWSG84Distance(from.getCentroid(),to.getCentroid());
                minTimes.setEntry(from.getIndex(),to.getIndex(),distance);}
        return minTimes;
        /**
        DijkstraShortestPath<Node, Link> shortestPathAlgorithm = new DijkstraShortestPath<>(network.createGraph());
        for(Zone from:zones.values())
            for(Zone to:zones.values())
                if(!from.equals(to)){
                    GraphPath<Node,Link> path = shortestPathAlgorithm.getPath(network.getNode(from.getId()), network.getNode(to.getId()));
                    times.setEntry(from.getIndex(),to.getIndex(),path.getWeight());}
        return times;**/
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void linkZones(Network network, HashMap<String, Zone> zones)
    {
        KdTree kdTree = network.createKDTree();
        for(Zone zone:zones.values()) {
            Node zoneNode = new Node(zone.getId(),zone.getName(),zone.getCentroid());
            network.addNode(zoneNode);
            List<?> nearNodes = null;
            double size = 0.01;
            while(nearNodes == null || nearNodes.isEmpty() || nearNodes.size() == 1)
            {
                double x = zone.getCentroid().x;
                double y = zone.getCentroid().y;
                Envelope envelope = new Envelope(x-size,x+size,y-size,y+size);
                nearNodes = kdTree.query(envelope);
                size=size*2;
            }
            double minDistance = Double.MAX_VALUE;
            Node closestNode = null;
            for(Object kdNode: nearNodes){
                Node node = (Node)((KdNode)kdNode).getData();
                if(node != zoneNode) {
                    double distance = CoordinateUtils.calculateWSG84Distance(node.getCoordinate(),zoneNode.getCoordinate());
                    if(closestNode == null || distance < minDistance) {
                        closestNode = node;
                        minDistance = distance;}}}
            network.addLink(new Link(zoneNode,closestNode,"centroidLink"));
            network.addLink(new Link(closestNode,zoneNode,"centroidLink"));}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void calculateWeights(RealVector inPopulation, RealVector atActivityPlace, RealMatrix minTimes)
    {
        final double epsilon = 1e-5, t0 = 1;
        int zoneNbr = inPopulation.getDimension();
        RealVector wA, wNextA = new ArrayRealVector(zoneNbr,1);
        RealVector wB, wNextB = new ArrayRealVector(zoneNbr,1);
        RealMatrix expTimes = minTimes.scalarMultiply(-1/t0);
        expTimes.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor(){public double visit(int r, int c, double v) {return FastMath.exp(v);}});
        do {
            wA = wNextA.copy();
            wB = wNextB.copy();
            for(int i = 0; i < wNextA.getDimension(); i++)
                wNextA.setEntry(i,1/wB.ebeMultiply(atActivityPlace).ebeMultiply(expTimes.getRowVector(i)).getL1Norm());
            for(int j = 0; j < wNextB.getDimension(); j++)
                wNextB.setEntry(j,1/wA.ebeMultiply(inPopulation).ebeMultiply(expTimes.getRowVector(j)).getL1Norm());
        } while(wNextA.subtract(wA).getL1Norm()+wNextB.subtract(wB).getL1Norm() > epsilon);
    }
}
