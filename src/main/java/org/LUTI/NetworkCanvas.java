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
    private String linesType;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Constructeur                                        */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public NetworkCanvas() {
        this.setPreferredSize(new Dimension(900, 900));
        this.linesType = null;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Modificateurs                                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setNetwork(Network network)
    {
        this.network = network;
        this.setupBounds();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                  Calcul les limites de coordonnés pour la mise à l'échelle                   */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds() {
        this.bounds = new Double[]{null, null, null, null};
        for (TC_Line transportLine : this.network.getTC_Lines().values())
            if (linesType == null || transportLine.type.equals(linesType))
                for (Node node : transportLine.getGraph().vertexSet()) {
                    bounds[0] = (bounds[0] == null || bounds[0] > node.getX()) ? node.getX() : bounds[0];
                    bounds[1] = (bounds[1] == null || bounds[1] > node.getY()) ? node.getY() : bounds[1];
                    bounds[2] = (bounds[2] == null || bounds[2] < node.getX()) ? node.getX() : bounds[2];
                    bounds[3] = (bounds[3] == null || bounds[3] < node.getY()) ? node.getY() : bounds[3];
                }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                             Paint                                            */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 800, 800);
        if (this.network != null)
            this.drawEdges(g);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                     Dessine les arcs                                        */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawEdges(Graphics g) {
        g.setColor(Color.blue);
        double screenWidth = 780, screenHeight = 780;
        double range = Math.max(this.bounds[2] - this.bounds[0], this.bounds[3] - this.bounds[1]);
        for (TC_Line transportLine : this.network.getTC_Lines().values()) {
            Graph<Node, DefaultEdge> graph = transportLine.getGraph();
            if (linesType == null || transportLine.type.equals(linesType))
                for (DefaultEdge edge : graph.edgeSet()) {
                    int x1 = (int) ((graph.getEdgeSource(edge).getX() - bounds[0]) * screenWidth / range) + 10;
                    int y1 = (int) ((graph.getEdgeSource(edge).getY() - bounds[1]) * screenHeight / range) + 10;
                    int x2 = (int) ((graph.getEdgeTarget(edge).getX() - bounds[0]) * screenWidth / range) + 10;
                    int y2 = (int) ((graph.getEdgeTarget(edge).getY() - bounds[1]) * screenHeight / range) + 10;
                    g.drawLine(x1, y1, x2, y2);
                }
        }
    }
}
