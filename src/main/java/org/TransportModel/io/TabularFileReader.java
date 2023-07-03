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
public abstract class TabularFileReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** This interface defines methods for processing lines of data */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public interface LineProcessor
    {
        String[] split(String line);
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /** Processes a line of data using the provided headers and values.
         * @param headers The list of headers indicating the position of values in the line
         * @param values  The array of values extracted from the line
         * @throws Exception if an error occurs during the processing of the line */
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        void processLine(List<String> headers, String[] values) throws Exception;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a file line by line and processes each line using the provided LineProcessor
     * @param filePath the path to the file to be read
     * @param lineProcessor the LineProcessor object that processes each line */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readFile(Path filePath, LineProcessor lineProcessor)
    {
        try (BufferedReader reader = Files.newBufferedReader(filePath))
        {
            String dataLine = reader.readLine();
            List<String> headers = Arrays.asList(lineProcessor.split(dataLine));
            while ((dataLine = reader.readLine()) != null)
            {
                String[] values = lineProcessor.split(dataLine);
                assert(values.length == headers.size());
                try{lineProcessor.processLine(headers,values);} catch(Exception e){e.printStackTrace();}
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
