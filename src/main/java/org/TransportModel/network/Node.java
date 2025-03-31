/*
 * Copyright (C) 2023 Erwan Hamzaoui
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
}
