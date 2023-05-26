package org.LUTI;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vividsolutions.jts.geom.*;
import org.geotools.geojson.geom.GeometryJSON;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**              Contient tout les méthodes pour construire un Graph à partir de fichiers        */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class NetworkReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Lit un fichier CSV et renvoit une liste de tableau representant les colonnes de chaque ligne */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<String[]> extractDataCSV(String csvFilePath, char delimiter)
    {
        List<String[]> lines = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withSeparator(delimiter).build();
        try (CSVReader reader =  new CSVReaderBuilder(new FileReader(csvFilePath)).withCSVParser(parser).build()){
            lines = reader.readAll();}
        catch(IOException | CsvException e){e.printStackTrace();}
        return lines;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                Transforme chaque ligne du fichier en objet TC_Line                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<TC_Line> createTCLines(List<String[]> dataLines)
    {
        List<TC_Line> transportLines = new ArrayList<>();
        List<String> header = Arrays.asList(dataLines.get(0));
        if(header.contains("Shape"))
            for(int i = 2; i < dataLines.size(); i++)
                transportLines.add(createTCLine(header, dataLines.get(i)));
        return transportLines;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                   Transforme une ligne d'un fichier en objet TC_Line                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static TC_Line createTCLine(List<String> header, String[] dataLine)
    {
        String id = dataLine[0];
        String type = dataLine[header.indexOf("Route Type")];
        Graph<Node,DefaultEdge> graph = geoJSONToGraph(dataLine[header.indexOf("Shape")]);
        return new TC_Line(id,type, graph);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                   Convertit une String au format GeoJSON en graph                            */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,DefaultEdge> geoJSONToGraph(String shape)
    {
        Graph<Node,DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        GeometryJSON geometryJSON = new GeometryJSON();
        try
        {
            Geometry geometry = geometryJSON.read(shape);
            if (geometry instanceof  MultiLineString)
                graph = multiLineStringToGraph((MultiLineString) geometry);
            if (geometry instanceof  LineString)
                graph = lineStringToGraph((LineString) geometry);
        }
        catch (Exception e) {e.printStackTrace();}
        return graph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                            Convertit un MultiLineString en Graph                             */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,DefaultEdge> multiLineStringToGraph(MultiLineString multiLineString)
    {
        Graph<Node,DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        int numGeometries = multiLineString.getNumGeometries();
        for (int i = 0; i < numGeometries; i++)
        {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            Graph<Node,DefaultEdge> lineGraph = lineStringToGraph(lineString);
            Graphs.addAllVertices(graph, lineGraph.vertexSet());
            Graphs.addAllEdges(graph, lineGraph, lineGraph.edgeSet());
        }
        return graph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                              Convertit un LineString en Graph                                */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,DefaultEdge>  lineStringToGraph(LineString lineString)
    {
        Graph<Node,DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Coordinate[] lineCoordinates = lineString.getCoordinates();
        Point point = new GeometryFactory().createPoint(lineCoordinates[0]);
        Node fromNode = new Node(point.getX(),point.getY());
        graph.addVertex(fromNode);
        GeometryFactory factory = new GeometryFactory();
        for (int j = 1; j < lineCoordinates.length; j++) {
            point = factory.createPoint(lineCoordinates[j]);
            Node toNode = new Node(point.getX(),point.getY());
            graph.addVertex(toNode);
            graph.addEdge(fromNode,toNode);
            fromNode = toNode;
        }
        return graph;
    }
}