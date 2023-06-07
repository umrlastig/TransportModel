package org.TransportModel.network.io;

import org.TransportModel.network.Link;
import org.TransportModel.network.Node;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**             NetworkReader class is used for reading and importing network data               */
///////////////////////////////////////////////////////////////////////////////////////////////////
public abstract class NetworkReader
{
    enum LINK_ATTRIBUTES {ID, FROM_ID, TO_ID, LENGTH, CAPACITY, SPEED, BIDIRECTIONAL,TIME}
    enum NODE_ATTRIBUTES {ID, X, Y}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Extracts data from a file using the specified delimiter.
     @param filePath the path to the file to be read
     @param delimiter the character used to separate values in each line
     @return a list of string arrays containing the extracted data (1 line = 1 array) */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected List<String[]> extractData(String filePath, char delimiter)
    {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = reader.readLine()) != null)
                lines.add(line.split(String.valueOf(delimiter)));
        }catch (IOException e) {e.printStackTrace();}
        return lines;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Extracts data from a file using the specified delimiter.
     @param filePath the path to the file to be read
     @return a list of string (1 line = 1 String) */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected List<String> extractData(String filePath)
    {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = reader.readLine()) != null)
                lines.add(line);
        }catch (IOException e) {e.printStackTrace();}
        return lines;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates a list of links from a MultiLineString by transforming each linestring into a link
    * @param multiLineString the MultiLineString from which to create the links
    * @return a list of links created from the MultiLineString */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected List<Link> createLinksFromMultiLineString( MultiLineString multiLineString)
    {
        List<Link> links = new ArrayList<>();
        int numGeometries = multiLineString.getNumGeometries();
        for (int i = 0; i < numGeometries; i++)
            links.add(createLinkFromLineString((LineString) multiLineString.getGeometryN(i)));
        return links;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates a Link between the first and the last point of the LineString
     * The length of the Link is calculated based on the entire LineString segment
     * @param lineString the LineString from which to create the link
     * @return a Link object created from the LineString */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected Link createLinkFromLineString(LineString lineString)
    {
        Coordinate[] lineCoordinates = lineString.getCoordinates();
        Node fromNode = new Node(lineCoordinates[0]);
        Node toNode = new Node(lineCoordinates[lineCoordinates.length-1]);
        Link link = new Link(fromNode,toNode);
        link.setLengthInM((int)lineString.getLength());
        return link;
    }
}