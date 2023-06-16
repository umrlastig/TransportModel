package org.TransportModel.network;

import org.locationtech.jts.geom.Coordinate;

import java.util.HashMap;
///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a node in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Node
{
    final private String id;
    private Coordinate coordinate;
    final HashMap<String,Link> inLinks;
    final HashMap<String,Link> outLinks;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructor                                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Node(String id, Coordinate coordinate)
    {
        this.id = id;
        this.coordinate = coordinate;
        this.inLinks = new HashMap<>();
        this.outLinks = new HashMap<>();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Getters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Coordinate getCoordinate(){return this.coordinate;}
    public String getId(){return this.id;}
    public HashMap<String,Link> getInLinks(){return this.inLinks;}
    public HashMap<String,Link> getOutLinks(){return this.outLinks;}
    public void addInLink(Link link){this.inLinks.put(link.getId(),link);}
    public void addOutLink(Link link){this.outLinks.put(link.getId(),link);}
    public void setCoordinate(Coordinate coordinate){this.coordinate = coordinate;}
}
