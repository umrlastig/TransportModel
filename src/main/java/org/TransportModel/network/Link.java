package org.TransportModel.network;


///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a connection between two nodes in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    public enum ROUTE_TYPE {TRAM_OR_LIGHT_SUBWAY, TRAIN, BUS, SUBWAY, UNDEFINED, FOOT, ROAD}
    private final String id;
    private final ROUTE_TYPE type;
    private final String name;
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
    {this(name+":"+fromNode.getId()+":"+toNode.getId(),fromNode,toNode,speed,capacity,length,type,name);}
    public Link(String id, Node fromNode, Node toNode, String name)
    {this(id,fromNode,toNode,Double.MAX_VALUE,Double.MAX_VALUE,0,ROUTE_TYPE.FOOT,name);}
    public Link(Node fromNode, Node toNode, String name)
    {this(name+":"+fromNode.getId()+":"+toNode.getId(),fromNode,toNode,name);}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double getNormalSpeedInMS(){return this.normalSpeedInMS;}
    public double getLengthInM(){return this.lengthInM;}
    public double getCapacityPerHour(){return this.capacityPerHour;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    public void setFromNode(Node node){this.fromNode = node;}
    public void setToNode(Node node){this.toNode = node;}
    public String getId(){return this.id;}
    public ROUTE_TYPE getType(){return this.type;}
    public String getName(){return this.name;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Combines two Link objects to create a new Link with fused properties
     * The resulting Link will have properties calculated based on the average of the input links' properties
     * @param linkToFusWith The Link to fuse with the current Link
     * @return A new fused Link object
     * @throws RuntimeException If the two input links are not connected (don't have en common node) */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Link fusLink(Link linkToFusWith)
    {
        String id = this.getId()+":"+linkToFusWith.getId();
        double speed = (this.getNormalSpeedInMS() + linkToFusWith.getNormalSpeedInMS())/2;
        double capacity = this.getCapacityPerHour()+linkToFusWith.capacityPerHour;
        double length = this.getLengthInM() + linkToFusWith.getLengthInM();
        ROUTE_TYPE type = this.getType();
        String name = this.getName() + "-"+linkToFusWith.getName();
        if(this.getNormalSpeedInMS() == Double.MAX_VALUE){speed = linkToFusWith.getNormalSpeedInMS();}
        else if(linkToFusWith.getNormalSpeedInMS() == Double.MAX_VALUE){speed = this.getNormalSpeedInMS();}
        if(this.getCapacityPerHour() == Double.MAX_VALUE){capacity = linkToFusWith.capacityPerHour;}
        else if(linkToFusWith.getCapacityPerHour() == Double.MAX_VALUE){capacity = this.getCapacityPerHour();}
        if(this.getFromNode().equals(linkToFusWith.getToNode()))
            return new Link(id,linkToFusWith.getFromNode(),this.getToNode(),speed,capacity,length,type,name);
        else if(linkToFusWith.getFromNode().equals(this.getToNode()))
            return new Link(id,this.getFromNode(),linkToFusWith.getToNode(),speed,capacity,length,type,name);
        else throw new RuntimeException();
    }
}

