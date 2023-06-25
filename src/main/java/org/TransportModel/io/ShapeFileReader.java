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
public abstract class ShapeFileReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected interface FeatureProcessor {void processFeature(SimpleFeature feature)throws Exception;}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Represents a reader for shape files */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    protected static void readFile(Path filePath, FeatureProcessor featureProcessor)
    {
        try
        {
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
