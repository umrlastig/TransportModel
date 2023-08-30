package org.TransportModel.io;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.nio.file.Path;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a reader for shape files */
///////////////////////////////////////////////////////////////////////////////////////////////////
public abstract class ShapeFileUtil
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** This interface defines methods for processing features */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public interface FeatureProcessor
    {
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Processes a feature
         * @param feature the feature to processes
         * @throws Exception if an error occurs during the processing of the feature */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        void processFeature(SimpleFeature feature) throws Exception;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a shp file and processes each feature using the provided featureProcessor
     * @param filePath the path to the file to be read
     * @param featureProcessor the featureProcessor object that processes each feature */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readFile(Path filePath, FeatureProcessor featureProcessor)
    {
        try {
            ShapefileDataStore dataStore = new ShapefileDataStore(filePath.toUri().toURL());
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            dataStore.dispose();
            try (SimpleFeatureIterator featureIterator = featureSource.getFeatures().features()) {
                while (featureIterator.hasNext()){
                    try{featureProcessor.processFeature(featureIterator.next());}
                    catch(Exception e){e.printStackTrace();}}
            }
            catch (Exception e) {e.printStackTrace();}
        }
        catch(IOException e){throw new RuntimeException();}
    }
}
