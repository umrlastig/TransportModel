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
    public static void readShapeFile(Area area, String filePath)
    {
        ShapeFileReader.readFile(Paths.get(filePath),new BDTOPOShapeProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the InseeCode is found in the area, add the number of
     * inhabitants and workers to the zone */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readPopulationAndWorkersFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new INSEEPopulationAndWorkersProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** e */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readStudentsFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new INSEEStudentsProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found,
     * add the flow to the flow matrix of the area */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readWorkFlowsFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new WorkFlowsProcessor(area));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found,
     * add the flow to the flow matrix of the area */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readStudentFlowsFile(Area area, String filePath)
    {
        TabularFileReader.readFile(Paths.get(filePath),new StudentFlowsProcessor(area));
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
    static class INSEEPopulationAndWorkersProcessor implements TabularFileReader.LineProcessor
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
        public INSEEPopulationAndWorkersProcessor(Area area){this.area = area;}
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
                area.getZone(codeINSEE).addWorkers(workers);}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class INSEEStudentsProcessor implements TabularFileReader.LineProcessor
    {
        public final static String CODE_INSEE = "COM", STUDENTS2_5 = "P19_SCOL0205", STUDENTS6_10 = "P19_SCOL0610";
        public final static String STUDENTS11_14 = "P19_SCOL1114", STUDENTS15_17 = "P19_SCOL1517";
        public final static String STUDENTS18_24 = "P19_SCOL1824", STUDENTS25_29 = "P19_SCOL2529";
        public final static String STUDENTS30P = "P19_SCOL30P";
        private final Area area;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public INSEEStudentsProcessor(Area area){this.area = area;}
        @Override public String[] split(String line){return line.split(";");}
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        @Override public void processLine(List<String> headers, String[] values)
        {
            //File values
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double s2_5 = Double.parseDouble(values[headers.indexOf(STUDENTS2_5)]);
            double s6_10 = Double.parseDouble(values[headers.indexOf(STUDENTS6_10)]);
            double s11_14 = Double.parseDouble(values[headers.indexOf(STUDENTS11_14)]);
            double s15_17 = Double.parseDouble(values[headers.indexOf(STUDENTS15_17)]);
            double s18_24 = Double.parseDouble(values[headers.indexOf(STUDENTS18_24)]);
            double s25_29 = Double.parseDouble(values[headers.indexOf(STUDENTS25_29)]);
            double s30P = Double.parseDouble(values[headers.indexOf(STUDENTS30P)]);
            //If area contains commune, add students
            if(area.containsZone(codeINSEE)) {
                int students = (int)(s2_5+s6_10+s11_14+s15_17+s18_24+s25_29+s30P);
                area.getZone(codeINSEE).addStudents(students);}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 line contains information about the work flow between two communes */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class WorkFlowsProcessor implements TabularFileReader.LineProcessor
    {
        public final static String FROM = "COMMUNE", TO = "DCLT", MODE = "TRANS", WEIGHTING = "IPONDI";
        private final Area area;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public WorkFlowsProcessor(Area area){this.area = area;}
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
                    area.addWorkFlowTC(fromCodeINSEE,toCodeINSEE,weighting);
                else if(mode == 5 || mode == 4)
                    area.addWorkFlowTI(fromCodeINSEE,toCodeINSEE,weighting);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** 1 line contains information about the student flow between two communes */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    static class StudentFlowsProcessor implements TabularFileReader.LineProcessor
    {
        public final static String FROM = "CODGEO", TO = "DCETU", WEIGHTING = "NBFLUX_C19_SCOL02P";
        private final Area area;
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Constructor */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        public StudentFlowsProcessor(Area area){this.area = area;}
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
            double weighting = Double.parseDouble(values[headers.indexOf(WEIGHTING)]);
            //If area contains communes, add flows
            if(area.containsZone(fromCodeINSEE) && area.containsZone(toCodeINSEE))
                area.addStudentFlow(fromCodeINSEE,toCodeINSEE,weighting);
        }
    }
}
