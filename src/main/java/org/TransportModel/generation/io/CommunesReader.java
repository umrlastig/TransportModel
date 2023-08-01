package org.TransportModel.generation.io;

import org.TransportModel.Config;
import org.TransportModel.generation.Zone;
import org.TransportModel.io.ShapeFileUtil;
import org.TransportModel.io.TabularFileUtil;
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
        final Path filePath = Paths.get(Config.getGenerationFiles().zoneShapes);
        HashMap<String, Zone> zones = new HashMap<>();
        ShapeFileUtil.readFile(filePath, feature -> {
            //File values
            long codeINSEE = (long) feature.getAttribute(CODE_INSEE);
            String commune = (String) feature.getAttribute(COMMUNE_NAME);
            Polygon polygon = (Polygon) ((MultiPolygon) feature.getDefaultGeometry()).getGeometryN(0);
            //Create zone and add to map
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
        final String CODE_INSEE = "COM", POPULATION = "P19_POP";
        final Path filePath = Paths.get(Config.getGenerationFiles().population);
        RealVector population = new ArrayRealVector(zones.size());
        TabularFileUtil.readFile(filePath,(line)->line.split(";"),(val) -> {
            //File values
            String codeINSEE = val.get(CODE_INSEE);
            double totalIrisPopulation = Double.parseDouble(val.get(POPULATION));
            //Add to vector at zone index
            if (zones.containsKey(codeINSEE)){
                int zoneIndex = zones.get(codeINSEE).getIndex();
                population.addToEntry(zoneIndex,totalIrisPopulation);}
        });
        return population;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readWorkersInPopulationFile(HashMap<String, Zone> zones)
    {
        final String CODE_INSEE = "CODGEO", NUMBER = "NB", TYPE = "TACTR_2";
        final Path filePath = Paths.get(Config.getGenerationFiles().workersInPopulation);
        RealVector workersInPopulation = new ArrayRealVector(zones.size());
        TabularFileUtil.readFile(filePath, (line)->line.split(";"), (val) -> {
            //File values
            String codeINSEE = val.get(CODE_INSEE);
            double workers = Double.parseDouble(val.get(NUMBER));
            boolean haveAJob = Integer.parseInt(val.get(TYPE)) == 11;
            //Add to vector at zone index
            if (zones.containsKey(codeINSEE) && haveAJob){
                int zoneIndex = zones.get(codeINSEE).getIndex();
                workersInPopulation.addToEntry(zoneIndex,workers);}
        });
        return workersInPopulation;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readStudentsInPopulationFile(HashMap<String, Zone> zones)
    {
        final String CODE_INSEE = "CODGEO", NUMBER = "NB", EDUCATION = "ILETUR";
        final Path filePath = Paths.get(Config.getGenerationFiles().studentsInPopulation);
        RealVector studentsInPopulation = new ArrayRealVector(zones.size());
        TabularFileUtil.readFile(filePath, (line)->line.split(";"), (val) -> {
            //File values
            String codeINSEE = val.get(CODE_INSEE);
            double students = Double.parseDouble(val.get(NUMBER));
            boolean areEducated = !val.get(EDUCATION).equals("Z");
            //Add to vector at zone index
            if (zones.containsKey(codeINSEE) && areEducated){
                int zoneIndex = zones.get(codeINSEE).getIndex();
                studentsInPopulation.addToEntry(zoneIndex,students);}
        });
        return studentsInPopulation;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readJobsAtWorkplaceFile(HashMap<String, Zone> zones)
    {
        final String CODE_INSEE = "CODGEO", NUMBER = "NB";
        final Path filePath = Paths.get(Config.getGenerationFiles().jobsAtWorkplace);
        RealVector jobsAtWorkplace =  new ArrayRealVector(zones.size());
        TabularFileUtil.readFile(filePath, (line)->line.split(";"), (val) -> {
            //File values
            String codeINSEE = val.get(CODE_INSEE);
            double jobs = Double.parseDouble(val.get(NUMBER));
            //Add to vector at zone index
            if (zones.containsKey(codeINSEE)){
                int zoneIndex = zones.get(codeINSEE).getIndex();
                jobsAtWorkplace.addToEntry(zoneIndex, jobs);}
        });
        return jobsAtWorkplace;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealVector readEducationAtStudyPlaceFile(HashMap<String, Zone> zones)
    {
        return new ArrayRealVector(zones.size());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** For each line of the file, if the Insee code of the source and of the target is found */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static RealMatrix readStudyFlowsFile(HashMap<String,Zone> zones)
    {
        final String FROM = "COMMUNE", TO = "DCETUF", NBR = "IPONDI";
        final Path filePath = Paths.get(Config.getGenerationFiles().studyFlows);
        RealMatrix observedFlows = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        TabularFileUtil.readFile(filePath, (line)->line.split(";"),  (val) -> {
            //File values
            String fromId = val.get(FROM), toId = val.get(TO);
            double studyFlow = Double.parseDouble(val.get(NBR));
            //Add to Matrix at [fromIndex,toIndex]
            if (zones.containsKey(fromId) && zones.containsKey(toId)) {
                int fromIndex = zones.get(fromId).getIndex();
                int toIndex = zones.get(toId).getIndex();
                observedFlows.addToEntry(fromIndex, toIndex, studyFlow);}
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
        final Path filePath = Paths.get(Config.getGenerationFiles().workFlows);
        RealMatrix observedFlows = MatrixUtils.createRealMatrix(zones.size(), zones.size());
        TabularFileUtil.readFile(filePath, (line)->line.split(";"), (val) -> {
            //File values
            String fromId = val.get(FROM), toId = val.get(TO);
            double workFlow = Double.parseDouble(val.get(NBR));
            int mode = Integer.parseInt(val.get(MODE));
            boolean ti = mode == 4 || mode == 5, tc = mode == 6;
            //Add to Matrix at [fromIndex,toIndex]
            if (zones.containsKey(fromId) && zones.containsKey(toId) && (tc || ti)){
                int fromIndex = zones.get(fromId).getIndex();
                int toIndex = zones.get(toId).getIndex();
                observedFlows.addToEntry(fromIndex, toIndex, workFlow);
            }
        });
        return observedFlows;
    }
}
