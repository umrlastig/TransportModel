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

package org.TransportModel.utils;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** A utility class for working with coordinates*/
///////////////////////////////////////////////////////////////////////////////////////////////////
public class CoordinateUtils
{
    public final static String LAMBERT93 = "EPSG:2154", WSG84 = "EPSG:4326";
    private CoordinateUtils(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Calculates the total distance  in meters between a series of coordinates
     * @param coordinates An array of coordinates
     * @return The total calculated distance between the given coordinates*/
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
    /** Converts an array of coordinates from one coordinate system to another
     * @param coordinates The array of input Coordinates to be converted
     * @param source The source Coordinate Reference System (CRS) representation
     * @param target The target Coordinate Reference System (CRS) representation
     * @return An array of new Coordinate objects representing the converted coordinates
     * @throws RuntimeException If any error occurs during the coordinate conversion */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate[] convertCoordinatesSystems(Coordinate[] coordinates,String source, String target)
    {
        Coordinate[] convertedCoordinates = new Coordinate[coordinates.length];
        for (int i = 0; i < coordinates.length; i++)
            convertedCoordinates[i] = convertCoordinateSystem(coordinates[i],source,target);
        return convertedCoordinates;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Converts a coordinate from one coordinate system to another
     * @param coordinate The input Coordinate to be converted
     * @param source The source Coordinate Reference System (CRS) representation
     * @param target The target Coordinate Reference System (CRS) representation
     * @return A new Coordinate object representing the converted coordinate
     * @throws RuntimeException If any error occurs during the coordinate conversion */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate convertCoordinateSystem(Coordinate coordinate,String source, String target)
    {
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode(source);
            CoordinateReferenceSystem targetCRS = CRS.decode(target);
            MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
            DirectPosition2D sourcePosition = new DirectPosition2D(sourceCRS, coordinate.getX(), coordinate.getY());
            DirectPosition2D targetPosition = new DirectPosition2D();
            transform.transform(sourcePosition, targetPosition);
            double latitude = targetPosition.getY();
            double longitude = targetPosition.getX();
            return new Coordinate(latitude, longitude);
        }
        catch(Exception e){throw new RuntimeException();}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate[] convertLambert93ToWGS84(Coordinate[] coordinates)
    {return convertCoordinatesSystems(coordinates,LAMBERT93,WSG84);}
}
