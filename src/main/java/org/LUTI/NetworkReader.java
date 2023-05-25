package org.LUTI;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vividsolutions.jts.geom.*;
import org.checkerframework.checker.units.qual.C;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.FileReader;
import java.io.IOException;
/** Classe chargée d'ajouter les noeuds et liens du réseau de transport à partir de fichiers **/
public class NetworkReader
{
    final private Network aNetwork;
    /** Constructeur **/
    public NetworkReader(final Network network)
    {
        this.aNetwork = network;
    }
    /////////////////////////////////////////////////
    /**     Traite un fichier de donnée csv       **/
    /////////////////////////////////////////////////
    public void readFile(String csvFilePath)
    {
        try (CSVReader reader =  new CSVReaderBuilder(new FileReader(csvFilePath))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build())
        {
            // Lecture des lignes du fichier CSV
            // Une case du tableau = une colonne de la ligne
            //Première ligne = entête
            String[] headers = reader.readNext();
            //Pour chaque ligne
            String[] line;
            while ((line = reader.readNext()) != null)
                parseLine(headers, line);
        }
        catch(IOException | CsvException e){e.printStackTrace();}
    }
    ///////////////////////////////////////////////////////////////
    /** Traite une ligne d'un fichier de donnees csv
     * (Repartition des colonnes vers les methodes appropriees) **/
    //////////////////////////////////////////////////////////////
    private void parseLine( String[] headerColumns, String[] lineColumns)
    {
        for(int i = 0; i < headerColumns.length; i++)
        {
            System.out.println(headerColumns[i]);
            switch (headerColumns[i]) {
                case "Shape": parseShape(lineColumns[i]);break;
            }
        }
    }
    ////////////////////////////////////////////////////////////////////
    /** Traite la colonne Shape au format JSon pour une ligne donnée **/
    ////////////////////////////////////////////////////////////////////
    private void parseShape(String shape) {
        GeometryJSON geometryJSON = new GeometryJSON();
        try {
            Geometry geometry = geometryJSON.read(shape);

            if (geometry instanceof MultiLineString) {
                MultiLineString multiLineString = (MultiLineString) geometry;
                int numGeometries = multiLineString.getNumGeometries();
                Coordinate[][][] coordinates = new Coordinate[numGeometries][][];

                for (int i = 0; i < numGeometries; i++)
                {
                    LineString lineString = (LineString) multiLineString.getGeometryN(i);
                    Coordinate[] lineCoordinates = lineString.getCoordinates();
                    for (int j = 0; j < lineCoordinates.length - 1; j++)
                    {
                        Node fromNode = new Node(new GeometryFactory().createPoint(lineCoordinates[j]));
                        Node toNode = new Node(new GeometryFactory().createPoint(lineCoordinates[j + 1]));
                        Link link = new Link(fromNode, toNode);
                        this.aNetwork.addLink(link);
                        this.aNetwork.addNode(toNode);
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }
}