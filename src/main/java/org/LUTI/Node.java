package org.LUTI;

import com.vividsolutions.jts.geom.Point;

///////////////////////////////////////////////////////////////////////////////////////////////////
/**                     Noeud de réseau = arret de ligne de TC ou croisement de routes           */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Node
{
    // Constantes pour les types de transport
    public static final int UNDEFINED = 0;
    public static final int SUBWAY = 1;
    public static final int BUS = 2;
    //  Attributs
    final String id;
    final int type;
    final private double x;
    final private double y;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                    Constructeur                                              */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Node(final double x, final double y)
    {
        this.type = UNDEFINED;
        this.id = null;
        this.x = x;
        this.y = y;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                     Accesseurs                                               */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double getX(){return this.x;}
    public double getY(){return this.y;}
}
