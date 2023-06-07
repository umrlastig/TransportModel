package org.TransportModel.network;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**     Link class represents a connection between two nodes in the transportation network       */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Link
{
    //Essentials
    final String id;
    private Node fromNode, toNode;
    //Optional
    private boolean isBidirectional;
    private Integer normalSpeedInKMH, normalTimeInS, capacity, lengthInM;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                        Constructor                                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Link(Node fromNode, Node toNode){this(fromNode.getId() + ":" + toNode.getId(),fromNode,toNode);}
    public Link(String id, Node fromNode, Node toNode)
    {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.isBidirectional = false;
        this.normalSpeedInKMH = this.normalTimeInS = this.capacity = this.lengthInM = null;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Getters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isBidirectional(){return this.isBidirectional;}
    public Integer getNormalSpeedInKMH(){return this.normalSpeedInKMH;}
    public Integer getNormalTimeInS(){return this.normalTimeInS;}
    public Integer getLengthInM(){return this.lengthInM;}
    public Integer getCapacity(){return this.capacity;}
    public Node getFromNode(){return this.fromNode;}
    public Node getToNode(){return this.toNode;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                                          Setters                                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void inverseDirection(){Node tempo = this.fromNode; this.fromNode = this.toNode; this.toNode = tempo;}
    public void setBidirectional(boolean isBidirectional){this.isBidirectional = isBidirectional;}
    public void setNormalSpeedInKMH(Integer speed){this.normalSpeedInKMH = speed;}
    public void setNormalTimeInS(Integer time){this.normalTimeInS = time;}
    public void setLengthInM(Integer length){this.lengthInM = length;}
    public void setCapacity(Integer capacity){this.capacity = capacity;}
    public void setFromNode(Node node){this.fromNode = node;}
    public void setToNode(Node node){this.toNode = node;}
    public String getId(){return this.id;}
}
