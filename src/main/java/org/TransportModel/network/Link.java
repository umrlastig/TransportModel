package org.TransportModel.network;
///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a connection between two nodes in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    public enum ROUTE_TYPE {TRAM_OR_LIGHT_SUBWAY, TRAIN, BUS, SUBWAY, UNDEFINED, FOOT, ROAD}
    String id;
    ROUTE_TYPE type;
    String name;
    private Node fromNode, toNode;
    private final double lengthInM, normalSpeedInMS, capacityPerHour;
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
    public Link(Node fromNode, Node toNode, double speed, double capacity, double length,ROUTE_TYPE type, String name)
    {this(fromNode.getId()+":"+toNode.getId(),fromNode,toNode,speed,capacity,length,type,name);}
    public Link(String id, Node fromNode, Node toNode, String name)
    {this(id,fromNode,toNode,9999,9999999,1,ROUTE_TYPE.FOOT,name);}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double getNormalSpeedInMS(){return this.normalSpeedInMS;}
    public double getLengthInM(){return this.lengthInM;}
    @SuppressWarnings("unused") public double getCapacityPerHour(){return this.capacityPerHour;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    public void setFromNode(Node node){this.fromNode = node;}
    public void setToNode(Node node){this.toNode = node;}
    public String getId(){return this.id;}
    public ROUTE_TYPE getType(){return this.type;}
    public String getName(){return this.name;}
}

