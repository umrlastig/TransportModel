package org.TransportModel.utils;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class CoordinateUtils
{
    private CoordinateUtils(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static double calculateWSG84Distance(Coordinate[] coordinates)
    {
        double totalLength = 0;
        for (int i = 0; i < coordinates.length - 1; i++)
            totalLength += CoordinateUtils.calculateWSG84Distance(coordinates[i], coordinates[i + 1]);
        return totalLength;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Calculates the distance in meters between two coordinates
     * @param coordinate1 The first coordinate
     * @param coordinate2 The second coordinate
     * @return The distance in meters between the two coordinates */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static double calculateWSG84Distance(Coordinate coordinate1, Coordinate coordinate2)
    {
        GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(coordinate1.getX(), coordinate1.getY());
        calculator.setDestinationGeographicPoint(coordinate2.getX(), coordinate2.getY());
        return calculator.getOrthodromicDistance();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate[] convertLambert93ToWGS84(Coordinate[] lambertCoordinates)
    {
        Coordinate[] wsgCoordinates = new Coordinate[lambertCoordinates.length];
        for (int i = 0; i < lambertCoordinates.length; i++)
            wsgCoordinates[i] = convertLambert93ToWGS84(lambertCoordinates[i]);
        return wsgCoordinates;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate[] convertWGS84ToLambert93(Coordinate[] lambertCoordinates)
    {
        Coordinate[] wsgCoordinates = new Coordinate[lambertCoordinates.length];
        for (int i = 0; i < lambertCoordinates.length; i++)
            wsgCoordinates[i] = convertWGS84ToLambert93(lambertCoordinates[i]);
        return wsgCoordinates;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Converts Lambert-93 coordinates to degrees (latitude and longitude)
     * @param lambertCoordinate The Lambert-93 coordinate to be converted
     * @return A Coordinate object representing the converted latitude and longitude in degrees */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate convertLambert93ToWGS84(Coordinate lambertCoordinate)
    {
        return convertCoordinateSystem(lambertCoordinate,"EPSG:2154","EPSG:4326");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate convertWGS84ToLambert93(Coordinate lambertCoordinate)
    {
        return convertCoordinateSystem(lambertCoordinate,"EPSG:4326","EPSG:2154");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate convertCoordinateSystem(Coordinate lambertCoordinate,String source, String target)
    {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode(source);
            CoordinateReferenceSystem targetCRS = CRS.decode(target);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            DirectPosition2D sourcePosition = new DirectPosition2D(sourceCRS, lambertCoordinate.getX(), lambertCoordinate.getY());
            DirectPosition2D targetPosition = new DirectPosition2D();
            transform.transform(sourcePosition, targetPosition);
            double latitude = targetPosition.getY();
            double longitude = targetPosition.getX();
            return new Coordinate(latitude, longitude);
        }
        catch(Exception e){throw new RuntimeException();}
    }
}
