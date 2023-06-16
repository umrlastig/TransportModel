package org.TransportModel.network;
///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a connection between two nodes in the transportation network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    //Essentials
    final String id;
    private Node fromNode, toNode;
    //Optional
    private double lengthInM, normalSpeedInMS, capacityPerHour;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructor                                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Link(String id, Node fromNode, Node toNode, double speed, double capacity, double length)
    {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.normalSpeedInMS = speed;
        this.capacityPerHour = capacity;
        this.lengthInM = length;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Getters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public double getNormalSpeedInMS(){return this.normalSpeedInMS;}
    public double getLengthInM(){return this.lengthInM;}
    public double getCapacityPerHour(){return this.capacityPerHour;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Setters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setNormalSpeedInMS(double speed){this.normalSpeedInMS = speed;}
    public void setLengthInM(double length){this.lengthInM = length;}
    public void setCapacityPerHour(double capacityPerHour){this.capacityPerHour = capacityPerHour;}
    public void setFromNode(Node node){this.fromNode = node;}
    public void setToNode(Node node){this.toNode = node;}
    public String getId(){return this.id;}
}
