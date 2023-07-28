package org.TransportModel.generation.io;

import org.TransportModel.Config;
import org.TransportModel.generation.Zone;
import org.TransportModel.io.ShapeFileReader;
import org.TransportModel.io.TabularFileReader;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Contains methods for creating Zone objects that represent communes from data files */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class CommunesReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each feature of the shp file, create a Zone object with a shape and add it to the network
     * Each Zone represents a commune identified by its InseeCode */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static HashMap<String,Zone> readShapeFile()
    {
        final String COMMUNE_NAME = "nomcom", CODE_INSEE = "insee";
        Path filePath = Paths.get(Config.getInstance().generationFiles.zoneShapes);
        HashMap<String, Zone> zones = new HashMap<>();
        ShapeFileReader.readFile(filePath, feature -> {
            long codeINSEE = (long) feature.getAttribute(CODE_INSEE);
            String commune = (String) feature.getAttribute(COMMUNE_NAME);
            Polygon polygon = (Polygon) ((MultiPolygon) feature.getDefaultGeometry()).getGeometryN(0);
            Zone zone = new Zone(Long.toString(codeINSEE), polygon, commune);
            zones.put(Long.toString(codeINSEE), zone);
        });
        return zones;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readPopulationFile(HashMap<String, Zone> zones)
    {
        RealVector population = new ArrayRealVector(zones.size());
        final String CODE_INSEE = "COM", POPULATION = "P19_POP";
        Path filePath = Paths.get(Config.getInstance().generationFiles.population);
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double totalIrisPopulation = Double.parseDouble(values[headers.indexOf(POPULATION)]);
            if (zones.containsKey(codeINSEE))
                population.addToEntry(zones.get(codeINSEE).getIndex(),totalIrisPopulation);
        });
        return population;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readWorkersInPopulationFile(HashMap<String, Zone> zones)
    {
        RealVector workersInPopulation = new ArrayRealVector(zones.size());
        final String CODE_INSEE = "CODGEO", NUMBER = "NB", TYPE = "TACTR_2";
        Path filePath = Paths.get(Config.getInstance().generationFiles.workersInPopulation);
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double workers = Double.parseDouble(values[headers.indexOf(NUMBER)]);
            int type = Integer.parseInt(values[headers.indexOf(TYPE)]);
            if (zones.containsKey(codeINSEE) && type == 11)
                workersInPopulation.addToEntry(zones.get(codeINSEE).getIndex(),workers);
        });
        return workersInPopulation;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readStudentsInPopulationFile(HashMap<String, Zone> zones)
    {
        RealVector studentsInPopulation = new ArrayRealVector(zones.size());
        final String CODE_INSEE = "CODGEO", NUMBER = "NB", EDUCATION = "ILETUR";
        Path filePath = Paths.get(Config.getInstance().generationFiles.studentsInPopulation);
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double students = Double.parseDouble(values[headers.indexOf(NUMBER)]);
            String education = values[headers.indexOf(EDUCATION)];
            if (zones.containsKey(codeINSEE) && !education.equals("Z"))
                studentsInPopulation.addToEntry(zones.get(codeINSEE).getIndex(),students);
        });
        return studentsInPopulation;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readJobsAtWorkplaceFile(HashMap<String, Zone> zones)
    {
        RealVector jobsAtWorkplace =  new ArrayRealVector(zones.size());
        final String CODE_INSEE = "CODGEO", NUMBER = "NB";
        Path filePath = Paths.get(Config.getInstance().generationFiles.jobsAtWorkplace);
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String codeINSEE = values[headers.indexOf(CODE_INSEE)];
            double jobs = Double.parseDouble(values[headers.indexOf(NUMBER)]);
            if (zones.containsKey(codeINSEE))
                jobsAtWorkplace.addToEntry(zones.get(codeINSEE).getIndex(),jobs);
        });
        return jobsAtWorkplace;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readEducationAtStudyPlaceFile(HashMap<String, Zone> zones)
    {
        RealVector jobsAtWorkplace =  new ArrayRealVector(zones.size());
        return jobsAtWorkplace;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix readStudyFlowsFile(HashMap<String,Zone> zones)
    {
        final String FROM = "COMMUNE", TO = "DCETUF", NBR = "IPONDI";
        Path filePath = Paths.get(Config.getInstance().generationFiles.studyFlows);
        RealMatrix observedFlows = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String fromId = values[headers.indexOf(FROM)];
            String toId = values[headers.indexOf(TO)];
            double studyFlow = Double.parseDouble(values[headers.indexOf(NBR)]);
            if (zones.containsKey(fromId) && zones.containsKey(toId)) {
                int fromIndex = zones.get(fromId).getIndex();
                int toIndex = zones.get(toId).getIndex();
                double totalFlow = observedFlows.getEntry(fromIndex, toIndex) + studyFlow;
                observedFlows.setEntry(fromIndex, toIndex, totalFlow);
            }
        });
        return observedFlows;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found,
     * add the flow to the flow matrix of the area */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix readWorkFlowsFile(HashMap<String,Zone> zones)
    {
        final String FROM = "COMMUNE", TO = "DCLT", MODE = "TRANS", NBR = "IPONDI";
        Path filePath = Paths.get(Config.getInstance().generationFiles.workFlows);
        RealMatrix observedFlows = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        TabularFileReader.readFile(filePath, (headers, values) -> {
            String fromId = values[headers.indexOf(FROM)];
            String toId = values[headers.indexOf(TO)];
            int mode = Integer.parseInt(values[headers.indexOf(MODE)]);
            double workFlow = Double.parseDouble(values[headers.indexOf(NBR)]);
            if (zones.containsKey(fromId) && zones.containsKey(toId) && (mode == 6 || mode == 5 || mode == 4)){
                int fromIndex = zones.get(fromId).getIndex();
                int toIndex = zones.get(toId).getIndex();
                double totalFlow = observedFlows.getEntry(fromIndex, toIndex) + workFlow;
                observedFlows.setEntry(fromIndex, toIndex, totalFlow);
            }
        });
        return observedFlows;
    }
}
