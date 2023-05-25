package org.LUTI;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;

/** Réseau de transport **/
public class Network
{
    final private ArrayList<Node> aNodes;
    final private ArrayList<Link> aLinks;
    /** Constructeur **/
    public Network()
    {
        this.aNodes = new ArrayList<>();
        this.aLinks = new ArrayList<>();
    }
    /** Modificateurs **/
    public void addNode(final Node node){this.aNodes.add(node);}
    public void addLink(final Link link){this.aLinks.add(link);}
    /** Accesseurs **/
    public ArrayList<Node> getNodes(){return this.aNodes;}
    public ArrayList<Link> getLinks(){return this.aLinks;}
}
