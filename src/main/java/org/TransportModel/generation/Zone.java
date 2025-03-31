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

package org.TransportModel.generation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a geographical zone with unique index */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Zone
{
    public static int zoneNbr = 0;
    private final int zoneIndex;
    private final String id;
    private final String name;
    private final Coordinate centroid;
    private final Polygon shape;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Zone(String id, Polygon polygon,String name)
    {
        this.zoneIndex = zoneNbr;
        this.id = id;
        this.shape = polygon;
        this.name = name;
        this.centroid = new Coordinate(polygon.getCentroid().getX(),polygon.getCentroid().getY());
        zoneNbr++;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public int getIndex(){return this.zoneIndex;}
    public String getId() {return this.id;}
    public Polygon getShape() {return this.shape;}
    public Coordinate getCentroid() {return this.centroid;}
    public String getName(){return this.name;}
 }
