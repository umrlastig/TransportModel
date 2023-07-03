package org.TransportModel.generation.io;

import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
import org.TransportModel.io.ShapeFileReader;
import org.TransportModel.io.TabularFileReader;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;

import java.nio.file.Paths;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Contains methods for creating Zone objects that represent communes from data files */
///////////////////////////////////////////////////////////////////////////////////////////////////
public final class CommunesReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each feature of the shp file, create a Zone object with a shape and add it to the network
     * Each Zone represents a commune identified by its InseeCode */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readBDTOPOFile(Area area, String filePath)
    {
        ShapeFileReader.readFile(Paths.get(filePath),new BDTOPOShapeProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the InseeCode is found in the area, add the number of
     * inhabitants and workers to the zone */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readINSEEFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new INSEEPopulationProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found,
     * add the flow to the flow matrix of the area */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readMOBPROFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new MOBPROFlowProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 feature contains a commune shape with a centroid, a InseeCode, and the commune name */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class BDTOPOShapeProcessor implements ShapeFileReader.FeatureProcessor
    {
        public final static String COMMUNE_NAME = "nomcom";
        public final static String CODE_INSEE = "insee";
        private final Area area;
        //////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public BDTOPOShapeProcessor(Area area){this.area = area;}
        //////////////////////////////////////////////////////////////////////////////////////////////////
        /** Create a Zone object and add it to the area */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processFeature(SimpleFeature feature)
        {
            //Feature values
            long codeINSEE = (long)feature.getAttribute(CODE_INSEE);
            String commune = (String)feature.getAttribute(COMMUNE_NAME);
            MultiPolygon multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
            Polygon polygon = (Polygon)multiPolygon.getGeometryN(0);
            //Create and add Zone
            Zone zone = new Zone(""+codeINSEE,polygon,commune);
            area.addZone(zone);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 line contains information about the population of an iris */
     ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class INSEEPopulationProcessor implements TabularFileReader.LineProcessor
    {
        public static final String CODE_INSEE = "COM";
        public static final String POPULATION = "P19_POP";
        public static final String FARMERS = "C19_POP15P_CS1";
        public static final String SELF_EMPLOYED = "C19_POP15P_CS2";
        public static final String EXECUTIVES = "C19_POP15P_CS3";
        public static final String INTERMEDIATE_OCCUPATIONS = "C19_POP15P_CS4";
        public static final String EMPLOYEES = "C19_POP15P_CS5";
        public static final String LABORERS = "C19_POP15P_CS6";
        private final Area area;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public INSEEPopulationProcessor(Area area){this.area = area;}
        @Override public String[] split(String line){return line.split(";");}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** If the area contains the InseeCode, add the iris population and workers to the zone */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double totalIrisPopulation = Double.parseDouble(values[headers.indexOf(POPULATION)]);
            double farmers = Double.parseDouble(values[headers.indexOf(FARMERS)]);
            double selfEmployed = Double.parseDouble(values[headers.indexOf(SELF_EMPLOYED)]);
            double executives = Double.parseDouble(values[headers.indexOf(EXECUTIVES)]);
            double intermediate = Double.parseDouble(values[headers.indexOf(INTERMEDIATE_OCCUPATIONS)]);
            double employees = Double.parseDouble(values[headers.indexOf(EMPLOYEES)]);
            double laborers = Double.parseDouble(values[headers.indexOf(LABORERS)]);
            //If contains commune, add population and workers
            if(area.containsZone(codeINSEE)){
                int workers = (int)(farmers + selfEmployed + executives + intermediate + employees + executives + laborers);
                area.getZone(codeINSEE).addPopulation((int)totalIrisPopulation);
                area.getZone(codeINSEE).addWorkers(workers);
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 line contains information about the flow between two communes */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class MOBPROFlowProcessor implements TabularFileReader.LineProcessor
    {
        public final static String FROM = "COMMUNE", TO = "DCLT", MODE = "TRANS", WEIGHTING = "IPONDI";
        private final Area area;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public MOBPROFlowProcessor(Area area){this.area = area;}
        @Override public String[] split(String line){return line.split(";");}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** If the Insee code of the source and of the target is found, add the flow to corresponding
         * flow matrix of the area depending on the transport mode */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String fromCodeINSEE = values[headers.indexOf(FROM)];
            String toCodeINSEE = values[headers.indexOf(TO)];
            int mode = Integer.parseInt(values[headers.indexOf(MODE)]);
            double weighting = Double.parseDouble(values[headers.indexOf(WEIGHTING)]);
            //If area contains communes, add flows
            if(area.containsZone(fromCodeINSEE) && area.containsZone(toCodeINSEE))
                if(mode == 6)
                    area.addFlowTC(fromCodeINSEE,toCodeINSEE,weighting);
                else if(mode == 5 || mode == 4)
                    area.addFlowTI(fromCodeINSEE,toCodeINSEE,weighting);
        }
    }
}
