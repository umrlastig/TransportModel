package org.TransportModel.network;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**     Link class represents a connection between two nodes in the transportation network       */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    //Essentials
    final private String id;
    final private Node fromNode, toNode;
    //Optional
    private boolean isBidirectional;
    private Double capacity, lengthInM, averageSpeedInMS;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructor                                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Link(String id, Node fromNode, Node toNode)
    {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.isBidirectional = false;
        this.capacity = this.lengthInM = this.averageSpeedInMS = null;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Getters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Double getAverageSpeedInMS(){return this.averageSpeedInMS;}
    public boolean isBidirectional(){return this.isBidirectional;}
    public Double getLengthInM(){return this.lengthInM;}
    public Double getCapacity(){return this.capacity;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    public String getId(){return this.id;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Setters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void setAverageSpeedInMS(double averageSpeedInMS){this.averageSpeedInMS = averageSpeedInMS;}
    public void setBidirectional(boolean isBidirectional){this.isBidirectional = isBidirectional;}
    public void setLengthInM(double lengthInM){this.lengthInM = lengthInM;}
    public void setCapacity(double capacity){this.capacity = capacity;}
}
