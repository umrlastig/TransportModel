package org.TransportModel.network;

import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a node in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Node
{
    final private String id;
    private final String name;
    private final Coordinate coordinate;
    final HashMap<String,Link> inLinks;
    final HashMap<String,Link> outLinks;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Node(String id, String name, Coordinate coordinate)
    {
        this.id = id;
        this.name = name;
        this.coordinate = coordinate;
        this.inLinks = new HashMap<>();
        this.outLinks = new HashMap<>();
    }
    public Node(String id, Coordinate coordinate) {this(id,"Unnamed",coordinate);}
    public Node(Coordinate coordinate) {this(coordinate.x+":"+coordinate.y,coordinate);}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Coordinate getCoordinate(){return this.coordinate;}
    public String getName(){return this.name;}
    public String getId(){return this.id;}
    public List<Link> getInLinks(){return new ArrayList<>(this.inLinks.values());}
    public List<Link> getOutLinks(){return new ArrayList<>(this.outLinks.values());}
    public void addInLink(Link link){this.inLinks.put(link.getId(),link);}
    public void addOutLink(Link link){this.outLinks.put(link.getId(),link);}
    public void removeInLink(Link link){this.removeInLink(link.getId());}
    public void removeOutLink(Link link){this.removeOutLink(link.getId());}
    public void removeInLink(String id){this.inLinks.remove(id);}
    public void removeOutLink(String id){this.outLinks.remove(id);}
    public double getX(){return this.getCoordinate().getX();}
    public double getY(){return this.getCoordinate().getY();}

    public Link getOutLink(String id) {return this.outLinks.get(id);}
    public Link getInLink(String id) {return this.inLinks.get(id);}
}
