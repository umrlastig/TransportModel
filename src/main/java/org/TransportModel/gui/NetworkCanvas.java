package org.TransportModel.gui;

import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.locationtech.jts.geom.Coordinate;

import javax.swing.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**                        Représentation graphique d'un réseau                                  */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class NetworkCanvas extends JComponent
{
    private Double[] bounds;
    private Network network;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Constructor                                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public NetworkCanvas(Network network)
    {
        this.network = network;
        this.setupBounds();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Modificateurs                                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setNetwork(Network network) {this.network = network;this.setupBounds();}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                  Calcul les limites de coordonnés pour la mise à l'échelle                   */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds()
    {
        this.bounds = new Double[]{null, null, null, null};
        for (Node node : this.network.getNodes().values()) {
            bounds[0] = (bounds[0] == null || bounds[0] > node.getCoordinate().x) ? node.getCoordinate().x : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > node.getCoordinate().y) ? node.getCoordinate().y : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < node.getCoordinate().x) ? node.getCoordinate().x : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < node.getCoordinate().y) ? node.getCoordinate().y : bounds[3];
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                   PaintComponent                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics g)
    {
        if (this.network != null && this.bounds[0]!=null) {
            this.drawNodes(g);
            this.drawEdges(g);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                     Dessine les noeuds                                      */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawNodes(Graphics g)
    {
        g.setColor(Color.BLUE);
        for(Node node: this.network.getNodes().values())
        {
            Coordinate coordinate = this.getScaledCoordinate(node.getCoordinate());
            g.fillOval((int)coordinate.x,(int)coordinate.y,2,2);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                     Dessine les arcs                                        */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    private void drawEdges(Graphics g)
    {
        g.setColor(Color.red);
        for (Link link : this.network.getLinks().values())
        {
            Node fromNode = link.getFromNode();
            Node toNode = link.getToNode();
            Coordinate from = this.getScaledCoordinate(fromNode.getCoordinate());
            Coordinate to = this.getScaledCoordinate(toNode.getCoordinate());
            g.drawLine((int)from.x, (int)from.y, (int)to.x, (int)to.y);
        }
    }
    public Coordinate getScaledCoordinate(Coordinate coordinate)
    {
        double screenWidth = this.getWidth(), screenHeight = this.getHeight();
        double range = Math.max(this.bounds[2] - this.bounds[0], this.bounds[3] - this.bounds[1]);
        int x = (int)((coordinate.x - bounds[0]) * screenWidth / range);
        int y = (int)((coordinate.y - bounds[1]) * screenHeight / range);
        return new Coordinate(x,y);
    }
}
