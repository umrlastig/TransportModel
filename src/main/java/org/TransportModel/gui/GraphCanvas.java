package org.TransportModel.gui;

import org.TransportModel.network.Link;
import org.TransportModel.network.Node;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.locationtech.jts.geom.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("unused") public class GraphCanvas extends JComponent
{
    private Double[] bounds;
    private final Graph<Node,Link> graph;
    private final List<GraphPath<Node, Link>> graphPaths;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public GraphCanvas(Graph<Node,Link> graph)
    {
        this.graphPaths = new ArrayList<>();
        this.graph = graph;
        this.setupBounds();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") public void addPath(GraphPath<Node, Link> graphPath) {this.graphPaths.add(graphPath);}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds()
    {
        this.bounds = new Double[]{null, null, null, null};
        for (Node node : this.graph.vertexSet()) {
            bounds[0] = (bounds[0] == null || bounds[0] > node.getCoordinate().x) ? node.getCoordinate().x : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > node.getCoordinate().y) ? node.getCoordinate().y : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < node.getCoordinate().x) ? node.getCoordinate().x : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < node.getCoordinate().y) ? node.getCoordinate().y : bounds[3];
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g)
    {
        //Graph
        g.setColor(Color.red);
        for (Link link : this.graph.edgeSet()) {
            Coordinate from = this.getScaledCoordinate(link.getFromNode().getCoordinate());
            Coordinate to = this.getScaledCoordinate(link.getToNode().getCoordinate());
            g.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        }
        //Paths
        g.setColor(Color.green);
        for(GraphPath<Node,Link> graphPath:this.graphPaths)
            for (Link link : graphPath.getEdgeList()) {
                Coordinate from = this.getScaledCoordinate(link.getFromNode().getCoordinate());
                Coordinate to = this.getScaledCoordinate(link.getToNode().getCoordinate());
                g.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
            }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public Coordinate getScaledCoordinate(Coordinate coordinate)
    {
        double screenWidth = this.getWidth(), screenHeight = this.getHeight();
        double range = Math.max(this.bounds[2] - this.bounds[0], this.bounds[3] - this.bounds[1]);
        int x = (int)((coordinate.x - bounds[0]) * screenWidth / range);
        int y = (int)((coordinate.y - bounds[1]) * screenHeight / range) ;
        int yMax = (int)((bounds[3]-bounds[1]) * (screenHeight / range));
        y = (int)(screenHeight - (y + (screenHeight - yMax)/2));
        return new Coordinate(x,y);
    }
}
