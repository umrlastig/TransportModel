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
