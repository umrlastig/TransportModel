package org.LUTI;

import com.vividsolutions.jts.geom.Point;

/** Noeud du réseau (arrêt de métro, de bus, croisement de route etc) **/
public class Node
{
    final private Point aCoordinate;
    /** Constructeur **/
    public Node(final Point coordinate) {this.aCoordinate = coordinate;}
    /** Accesseurs **/
    public Point getCoordinate(){return this.aCoordinate;}

}
