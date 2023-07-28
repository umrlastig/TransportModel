package org.TransportModel.network.io;

import org.TransportModel.Config;
import org.TransportModel.io.ShapeFileReader;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.TransportModel.utils.CoordinateUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.simple.SimpleFeature;

import java.nio.file.Paths;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Reads a BDTOPO Files and fill a network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public final class NetworkReaderBDTOPO
{
    private NetworkReaderBDTOPO(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Imports a shapefile of BDTOPO format and creates links from the features */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Network readFiles()
    {
        Network network = new Network();
        for(String bdtopoFile:Config.getInstance().networkFiles.bdtopo)
            ShapeFileReader.readFile(Paths.get(bdtopoFile), new RoadProcessor(network));
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 feature represents a road section with information like the shape, the direction etc */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class RoadProcessor implements ShapeFileReader.FeatureProcessor
    {
        public final static String CONDITION = "ETAT", USED = "En service", IMPORTANCE = "IMPORTANCE", NATURE = "NATURE";
        public final static String SPEED = "VIT_MOY_VL", ACCESS = "ACCES_VL",FREE = "Libre", LANES_NBR = "NB_VOIES";
        public final static String DIRECTION = "SENS", INVERSE = "Sens inverse", BIDIRECTIONAL = "Double sens";
        private final Network network;
        //////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public RoadProcessor(Network network){this.network = network;}
        //////////////////////////////////////////////////////////////////////////////////////////////////
        /** Create links and Nodes and them adds to the network */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processFeature(SimpleFeature feature) throws Exception
        {
            //File values
            MultiLineString multiLineString = (MultiLineString) feature.getDefaultGeometry();
            String imp = (String) feature.getAttribute(IMPORTANCE);
            String condition = (String) feature.getAttribute(CONDITION);
            String access = (String) feature.getAttribute(ACCESS);
            String direction = (String)feature.getAttribute(DIRECTION);
            String nature = (String)feature.getAttribute(NATURE);
            Integer lanesNbr = (Integer) feature.getAttribute(LANES_NBR);
            Integer speedInKMH = (Integer) feature.getAttribute(SPEED);
            //If valid road, create links and nodes and add them to network
            boolean impOk = (imp.equals("1")||imp.equals("2")||imp.equals("3")||imp.equals("4")||imp.equals("5"));
            if (impOk && condition.equals(USED) && access.equals(FREE) && lanesNbr != null)
                for (int i = 0; i < multiLineString.getNumGeometries(); i++)
                    createRoadLinks((LineString)multiLineString.getGeometryN(i),nature,direction,speedInKMH,lanesNbr);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Adds nodes and links to the network based on the provided LineString geometry and direction
         * @param lineString The LineString geometry representing the route
         * @param direction  The direction of the route
         * @param speedInKMH The speed value
         * @param lanesNbr   The lane nbr */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        private void createRoadLinks(LineString lineString,String nature,String direction,int speedInKMH,int lanesNbr)
        {
            Coordinate[] wsgCoordinates;
            try{ wsgCoordinates = CoordinateUtils.convertLambert93ToWGS84(lineString.getCoordinates());}
            catch(Exception e){e.printStackTrace();return;}
            Coordinate lastCoordinate = wsgCoordinates[wsgCoordinates.length-1];
            Coordinate firstCoordinate =  wsgCoordinates[0];
            double speedInMS = speedInKMH * (1000.0 / 3600.0);
            double maxCapacity = lanesNbr * 1800;
            double totalLength = CoordinateUtils.calculateWSG84Distance(wsgCoordinates);
            Link.ROUTE_TYPE type = Link.ROUTE_TYPE.ROAD;
            //Create nodes and links
            Node fromNode = new Node(direction.equals(INVERSE) ? lastCoordinate:firstCoordinate);
            Node toNode = new Node(direction.equals(INVERSE) ? firstCoordinate:lastCoordinate);
            network.addNode(fromNode);
            network.addNode(toNode);
            network.addLink(new Link(fromNode, toNode, speedInMS, maxCapacity, totalLength, type, nature));
            if(direction.equals(BIDIRECTIONAL))
                network.addLink( new Link(toNode, fromNode, speedInMS, maxCapacity, totalLength, type,nature));
        }
    }
}