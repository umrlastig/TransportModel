package org.TransportModel.network.io;

import org.TransportModel.Config;
import org.TransportModel.io.ShapeFileUtil;
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
        final String CONDITION="ETAT",IMP="IMPORTANCE",ACCESS="ACCES_VL",FREE="Libre",USED="En service",TOLL="A pÃ©age";
        Network network = new Network();
        for(String bdtopoFilePath:Config.getNetworkFiles().bdtopo)
            ShapeFileUtil.readFile(Paths.get(bdtopoFilePath), feature -> {
                //File values
                boolean notPrivate = feature.getAttribute(ACCESS).equals(FREE)||feature.getAttribute(ACCESS).equals(TOLL);
                boolean importanceOk = !feature.getAttribute(IMP).equals("6") && !feature.getAttribute(IMP).equals("5");
                boolean used = feature.getAttribute(CONDITION).equals(USED);
                //If valid road, create and add links to the network
                if(importanceOk && notPrivate && used)
                    createRoadLinks(network,feature);
            });
        return network;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Adds nodes and links to the network based on the provided feature */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static void createRoadLinks(Network network, SimpleFeature feature)
    {
        final String SPEED="VIT_MOY_VL",SENS="SENS",BI="Double sens",INV="Sens inverse",LANES="NB_VOIES",NATURE="NATURE";
        //File values
        MultiLineString multiLineString = (MultiLineString) feature.getDefaultGeometry();
        String nature = (String)feature.getAttribute(NATURE), direction = (String)feature.getAttribute(SENS);
        boolean bidirectional = direction.equals(BI), inverse = direction.equals(INV);
        int lanesNbr = feature.getAttribute(LANES) == null ? 1:(int)feature.getAttribute(LANES);
        int speedInKMH = (int) feature.getAttribute(SPEED);
        //Create and add a link to the network for each lineString
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString lineString = (LineString) multiLineString.getGeometryN(i);
            Coordinate[] coordinates = CoordinateUtils.convertLambert93ToWGS84(lineString.getCoordinates());
            //Create and add nodes
            Node fromNode = new Node(inverse ? coordinates[coordinates.length-1]:coordinates[0]);
            Node toNode = new Node(inverse ? coordinates[0]:coordinates[coordinates.length-1]);
            network.addNode(fromNode);
            network.addNode(toNode);
            //Create and add links
            double speedInMS = speedInKMH * (1000.0/3600.0);
            double maxCapacity = lanesNbr * 1800;
            double totalLength = CoordinateUtils.calculateWSG84Distance(coordinates);
            Link.ROUTE_TYPE type = Link.ROUTE_TYPE.ROAD;
            network.addLink(new Link(fromNode, toNode, speedInMS, maxCapacity, totalLength, type, nature));
            if(bidirectional)
                network.addLink(new Link(toNode, fromNode, speedInMS, maxCapacity, totalLength, type, nature));
        }
    }
}