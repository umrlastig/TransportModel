/*
 * Copyright (C) 2023 Erwan Hamzaoui
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.TransportModel.generation;


import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.FastMath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.HashMap;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** A utility class for calculating flow distributions within zones */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class FlowDistributor
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  Calculate minimum travel times */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix calculateMinTimes(HashMap<String,Zone> zones, Network networkTC, Network networkTI)
    {
        RealMatrix minTimes = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        DijkstraShortestPath<Node,Link> shortestPathTC = new DijkstraShortestPath<>(networkTC.createGraph());
        DijkstraShortestPath<Node,Link> shortestPathTI = new DijkstraShortestPath<>(networkTI.createGraph());
        for (Zone from : zones.values())
            for (Zone to : zones.values())
                if (!from.equals(to)) {
                    double timeTC = shortestPathTC.getPathWeight(networkTC.getNode(from.getId()),networkTC.getNode(to.getId()));
                    double timeTI = shortestPathTI.getPathWeight(networkTI.getNode(from.getId()),networkTI.getNode(to.getId()));
                    minTimes.setEntry(from.getIndex(),to.getIndex(),Math.min(timeTI,timeTC));}
        return minTimes;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Fratar algorithm */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector[] calculateWeightsFratar(RealVector inPopulation, RealVector atActivityPlace, RealMatrix minTimes)
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
