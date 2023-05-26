package org.LUTI;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**                        Représentation graphique d'un réseau                                  */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class NetworkCanvas extends JComponent
{
    private Double[] bounds;
    private Network network;
    private int displayedNodeType;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Constructeur                                        */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public NetworkCanvas() {this.displayedNodeType = Node.UNDEFINED;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Modificateurs                                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setNetwork(Network network) {this.network = network;this.setupBounds();}
    public void setDisplayedNodeType(int nodeType){this.displayedNodeType = nodeType;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                  Calcul les limites de coordonnés pour la mise à l'échelle                   */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds()
    {
        this.bounds = new Double[]{null, null, null, null};
        for (Node node : this.network.getGraph().vertexSet()) {
            bounds[0] = (bounds[0] == null || bounds[0] > node.getX()) ? node.getX() : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > node.getY()) ? node.getY() : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < node.getX()) ? node.getX() : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < node.getY()) ? node.getY() : bounds[3];
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                   PaintComponent                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g)
    {
        if (this.network != null)
            this.drawEdges(g);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                     Dessine les arcs                                        */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawEdges(Graphics g)
    {
        g.setColor(Color.blue);
        double screenWidth = this.getWidth(), screenHeight = this.getHeight();
        double range = Math.max(this.bounds[2] - this.bounds[0], this.bounds[3] - this.bounds[1]);
        Graph<Node, Link> graph = this.network.getGraph();
        for (Link link : graph.edgeSet())
        {
            Node source = graph.getEdgeSource(link);
            Node target = graph.getEdgeTarget(link);
            if (source.getType() == displayedNodeType && target.getType() == displayedNodeType)
            {
                int x1 = (int)((source.getX() - bounds[0]) * screenWidth / range);
                int y1 = (int)((source.getY() - bounds[1]) * screenHeight / range);
                int x2 = (int)((target.getX() - bounds[0]) * screenWidth / range);
                int y2 = (int)((target.getY() - bounds[1]) * screenHeight / range);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
