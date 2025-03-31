/*
 * Copyright (C) 2023 Erwan Hamzaoui
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
