package org.TransportModel.generation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Zone
{
    private final String id;
    private final String name;
    private final Coordinate centroid;
    private final Polygon shape;
    private int population, workers, students;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Zone(String id, Polygon polygon,String name)
    {
        this.id = id;
        this.shape = polygon;
        this.name = name;
        this.population = 0;
        this.workers = 0;
        this.students = 0;
        this.centroid = new Coordinate(polygon.getCentroid().getX(),polygon.getCentroid().getY());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Getters/Setters */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public String getId() {return this.id;}
    public Polygon getShape() {return this.shape;}
    @SuppressWarnings("unused") public Coordinate getCentroid() {return this.centroid;}
    @SuppressWarnings("unused") public String getName(){return this.name;}
    public void addPopulation(int population){this.population+=population;}
    public void addWorkers(int workers){this.workers+=workers;}
    @SuppressWarnings("unused") public void addStudents(int students){this.students+=students;}
    @SuppressWarnings("unused") public int getPopulation(){return this.population;}
    @SuppressWarnings("unused") public int getWorkers(){return this.workers;}
    @SuppressWarnings("unused") public int getStudents(){return this.students;}
 }
