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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** Represents a reader for tabular files */
///////////////////////////////////////////////////////////////////////////////////////////////////
public abstract class TabularFileUtil
{
    @FunctionalInterface public interface LineSplitter { String[] split(String line);}
    @FunctionalInterface public interface LineProcessor
    {
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Processes a line of data using the provided headers and values.
         * @throws Exception if an error occurs during the processing of the line */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        void processLine(HashMap<String,String> valuesMap) throws Exception;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a file line by line and processes each line using the provided LineProcessor
     * @param filePath the path to the file to be read
     * @param lineProcessor the LineProcessor object that processes each line */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readFile(Path filePath, LineSplitter lineSplitter, LineProcessor lineProcessor)
    {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String dataLine = reader.readLine();
            List<String> headers = Arrays.asList(lineSplitter.split(dataLine));
            while ((dataLine = reader.readLine()) != null) {
                String[] values = lineSplitter.split(dataLine);
                assert(values.length == headers.size());
                HashMap<String,String> valuesMap = new HashMap<>();
                for(int i = 0; i<headers.size();i++)
                    valuesMap.put(headers.get(i),values[i]);
                try{lineProcessor.processLine(valuesMap);}
                catch(Exception e){e.printStackTrace();}
            }
        }
        catch (IOException e){throw new RuntimeException(e);}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Writes a file to the specified folder path
     * @param filePath The path to write the file
     * @param lines A list of hashMap<Header,Value> representing each line of the file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void writeFile(String delimiter, Path filePath, List<HashMap<String, String>> lines)
    {
        String[] headers = lines.get(0).keySet().toArray(new String[0]);
        String[] values = new String[headers.length];
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile())))
        {
            writer.write(String.join(delimiter, headers));
            for(HashMap<String,String> line: lines) {
                Arrays.setAll(values, i -> line.get(headers[i]));
                writer.newLine();
                writer.write(String.join(delimiter, values));
            }
        }
        catch (IOException e){throw new RuntimeException(e);}
    }
}
