package org.LUTI;

/** Arc entre deux noeuds **/
public class Link
{
    final private Node aFromNode;
    final private Node aToNode;
    /** Constructeur **/
    public Link(final Node fromNode, final Node toNode)
    {
        this.aFromNode = fromNode;
        this.aToNode = toNode;
    }
    /** Accesseurs **/
    public Node getStartingNode(){return this.aFromNode;}
    public Node getArrivalNode(){return this.aToNode;}
}
