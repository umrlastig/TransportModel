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
    public static final String SHAPE = "Shape", TYPE = "Route Type";
    public static final String SUBWAY = "Subway", BUS = "Bus", RAIL = "Rail";
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**          Lit un fichier CSV et renvoit une liste contenant chaque ligne du fichier           */
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
    /**                     Transforme chaque ligne du fichier en un Graph                           */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,Link> createGlobalGraph(List<String[]> dataLines)
    {
        Graph<Node,Link> globalGraph = new SimpleGraph<>(Link.class);
        List<String> header = Arrays.asList(dataLines.get(0));
        if(header.contains(SHAPE))
            for(int i = 1; i < dataLines.size(); i++)
                Graphs.addGraph(globalGraph , createGraph(header,dataLines.get(i)));
        return globalGraph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                   Transforme une ligne d'un fichier en Graph                         */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,Link> createGraph(List<String> header, String[] dataLine)
    {
        //Créer le graph à partir de la shape
        Graph<Node,Link> graph = geoJSONToGraph(dataLine[header.indexOf(SHAPE)]);
        //Définit le type des noeuds (metro, bus etc)
        if(header.contains(TYPE))
            for(Node node:graph.vertexSet())
                node.setType(stringToNodeType(dataLine[header.indexOf(TYPE)]));
        return graph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                          Convertit une String en type de noeud                               */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static int stringToNodeType(String type)
    {
        switch(type) {
            case SUBWAY:return Node.SUBWAY;
            case BUS:return Node.BUS;
            case RAIL:return Node.RAIL;
            default:return Node.UNDEFINED;
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                   Convertit une String au format GeoJSON en graph                            */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,Link> geoJSONToGraph(String shape)
    {
        Graph<Node,Link> graph = new SimpleGraph<>(Link.class);
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
    public static Graph<Node,Link> multiLineStringToGraph(MultiLineString multiLineString)
    {
        Graph<Node,Link> graph = new SimpleGraph<>(Link.class);
        int numGeometries = multiLineString.getNumGeometries();
        for (int i = 0; i < numGeometries; i++) {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            Graph<Node,Link> lineGraph = lineStringToGraph(lineString);
            Graphs.addGraph(graph,lineGraph);
        }
        return graph;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                              Convertit un LineString en Graph                                */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Graph<Node,Link>  lineStringToGraph(LineString lineString)
    {
        Graph<Node,Link> graph = new SimpleGraph<>(Link.class);
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