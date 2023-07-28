package org.TransportModel.gui;

import org.TransportModel.network.Link;
import org.TransportModel.network.Node;
import org.checkerframework.checker.units.qual.C;
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
    private int graphIndex;
    private final List<Graph<Node,Link>> graphs;
    private final List<GraphPath<Node, Link>> graphPaths;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public GraphCanvas()
    {
        this.graphIndex = 0;
        this.graphPaths = new ArrayList<>();
        this.graphs = new ArrayList<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") public void addPath(GraphPath<Node, Link> graphPath) {this.graphPaths.add(graphPath);}
    @SuppressWarnings("unused") public void addGraph(Graph<Node, Link> graph) {this.graphs.add(graph);this.setupBounds();}
    public void nextIndex(){this.graphIndex++; if(this.graphIndex>=this.graphs.size())this.graphIndex=0;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds()
    {
        this.bounds = new Double[]{null, null, null, null};
        for(Graph<Node,Link> graph:graphs)
            for (Node node : graph.vertexSet()) {
            bounds[0] = (bounds[0] == null || bounds[0] > node.getCoordinate().x) ? node.getCoordinate().x : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > node.getCoordinate().y) ? node.getCoordinate().y : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < node.getCoordinate().x) ? node.getCoordinate().x : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < node.getCoordinate().y) ? node.getCoordinate().y : bounds[3];}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g)
    {
        //Graph
        g.setColor(Color.red);
        for (Link link : this.graphs.get(this.graphIndex).edgeSet()) {
            Coordinate from = this.getScaledCoordinate(link.getFromNode().getCoordinate());
            Coordinate to = this.getScaledCoordinate(link.getToNode().getCoordinate());
            if(link.getName().equals("centroidLink"))
            {g.setColor(Color.blue);
                g.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
                g.setColor(Color.red);}


            g.drawOval((int)from.x, (int)from.y,2,2);
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
