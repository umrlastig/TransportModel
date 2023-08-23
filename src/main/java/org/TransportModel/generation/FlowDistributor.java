package org.TransportModel.generation;


import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.utils.CoordinateUtils;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.FastMath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class FlowDistributor
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix calculateMinTimes(HashMap<String,Zone> zones,Network... networks) {
        RealMatrix minTimes = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        for (Network network : networks) {
            DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
            DijkstraShortestPath<Node,Link> shortestPath = new DijkstraShortestPath<>(graph);
            for (Zone from : zones.values())
                for (Zone to : zones.values())
                    if (!from.equals(to)) {
                        double start = System.currentTimeMillis();
                        Coordinate fromC = from.getCentroid(), toC = to.getCentroid();
                        Coordinate center = new Coordinate((fromC.x + toC.x) / 2, (fromC.y + toC.y) / 2);
                        double radius = CoordinateUtils.calculateWSG84Distance(fromC,center);
                        double maxDistance = 1.2*radius;
                        double time = shortestPath.getPath(network.getNode(from.getId()),network.getNode(to.getId())).getWeight();
                        System.out.println(System.currentTimeMillis()-start);
                    }
        }
        return minTimes;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector[] calculateWeights(RealVector inPopulation, RealVector atActivityPlace, RealMatrix minTimes)
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
        return new RealVector[]{wA,wB};
    }
}
