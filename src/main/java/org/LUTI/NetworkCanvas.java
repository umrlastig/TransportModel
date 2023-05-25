package org.LUTI;

import com.vividsolutions.jts.geom.Point;

import javax.swing.*;
import java.awt.*;

public class NetworkCanvas extends JComponent
{
    private final Network aNetwork;
    public NetworkCanvas(Network network)
    {
        this.setPreferredSize(new Dimension(1000,1000));
        this.aNetwork = network;
        this.setBackground(Color.blue);
    }
    @Override public void paint(Graphics g)
    {
        Double firstX = null, LastX = null, firstY = null, LastY = null;
        Point coordinate;
        //Trouve les limites pour redimensionner ensuite
        for(Node node: this.aNetwork.getNodes())
        {
            coordinate = node.getCoordinate();
            if(firstX == null || firstX > coordinate.getX())
                firstX = coordinate.getX();
            if(LastX == null || LastX < coordinate.getX())
                LastX = coordinate.getX();
            if(firstY == null || firstY > coordinate.getY())
                firstY = coordinate.getY();
            if(LastY == null || LastY < coordinate.getY())
                LastY = coordinate.getY();
        }
        if(firstX==null)
            return;
        //Redimension
        double screenWidth = 500;
        double screenHeight = 500;
        double xRange = LastX - firstX;
        double yRange = LastY - firstY;
        double range = Math.max(xRange, yRange);
        //Dessin des arcs
        g.setColor(Color.blue) ;
        for(Link link: this.aNetwork.getLinks())
        {
            Point coordinate1 = link.getStartingNode().getCoordinate();
            int x1 = (int)((coordinate1.getX()-firstX)* 1000/range);
            int y1 = (int)((coordinate1.getY()-firstY)* 1000/range);

            Point coordinate2 = link.getArrivalNode().getCoordinate();
            int x2 = (int)((coordinate2.getX()-firstX)* 1000/range);
            int y2 = (int)((coordinate2.getY()-firstY)* 1000/range);

            g.drawLine(x1,y1,x2,y2);
        }
        //Dessin des noeuds
        g.setColor(Color.red) ;
        for(Node node: this.aNetwork.getNodes())
        {
            coordinate = node.getCoordinate();
            int x = (int)((coordinate.getX()-firstX)* 1000/range);
            int y = (int)((coordinate.getY()-firstY)* 1000/range);
            g.fillOval(x,y,2,2);
        }

    }
}
