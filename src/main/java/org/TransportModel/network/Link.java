package org.TransportModel.network;
///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a connection between two nodes in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    //Essentials
    String id;
    ROUTE_TYPE type;
    String name;
    private Node fromNode, toNode;
    //Optional
    private double lengthInM, normalSpeedInMS, capacityPerHour;
    public enum ROUTE_TYPE {TRAM_OR_LIGHT_SUBWAY, TRAIN, BUS, SUBWAY, UNDEFINED, FOOT, CAR;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Link(String id, Node fromNode, Node toNode, double speed, double capacity, double length,ROUTE_TYPE type, String name)
    {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.normalSpeedInMS = speed;
        this.capacityPerHour = capacity;
        this.lengthInM = length;
        this.type = type;
        this.name = name;
    }
    public Link(String id, Node fromNode, Node toNode, double speed, double capacity, double length,ROUTE_TYPE type)
    {this(id,fromNode,toNode,speed,capacity,length,type,"Unnamed");}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double getNormalSpeedInMS(){return this.normalSpeedInMS;}
    public double getLengthInM(){return this.lengthInM;}
    public double getCapacityPerHour(){return this.capacityPerHour;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    public void setNormalSpeedInMS(double speed){this.normalSpeedInMS = speed;}
    public void setLengthInM(double length){this.lengthInM = length;}
    public void setCapacityPerHour(double capacityPerHour){this.capacityPerHour = capacityPerHour;}
    public void setFromNode(Node node){this.fromNode = node;}
    public void setToNode(Node node){this.toNode = node;}
    public String getId(){return this.id;}
    public ROUTE_TYPE getType(){return this.type;}
    public String getName(){return this.name;}
}

