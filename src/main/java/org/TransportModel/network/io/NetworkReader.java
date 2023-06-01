package org.TransportModel.network.io;

import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
///////////////////////////////////////////////////////////////////////////////////////////////////
/**          NetworkReader class in MATSim is used for reading and importing network data        */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class NetworkReader
{
    public final static String NODE_ID = "stop_id", NODE_X = "stop_lon",NODE_Y = "stop_la";
    public final static String LINK_ID = "pathway_id", LINK_FROM_ID = "from_stop_id",LINK_TO_ID = "to_stop_id";
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**     Reads a file containing nodes data and imports nodes into the network as a parameter     */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readNodeFile(Network network, String filePath, char delimiter)
    {
        List<String[]> dataLines = NetworkReader.extractData(filePath, delimiter);
        List<String> headers = Arrays.asList(dataLines.get(0));
        if(!headers.contains(NODE_ID)||!headers.contains(NODE_X)||!headers.contains(NODE_Y))
            throw new RuntimeException("Node's header not found");
        for(int i = 1; i < dataLines.size(); i++){
            network.addNode(NetworkReader.createNodeFromDataLine(headers, dataLines.get(i)));}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                               Create a Node from a data line                                 */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Node createNodeFromDataLine(List<String> headers, String[] dataLine)
    {
        String id = dataLine[headers.indexOf(NODE_ID)];
        double x = Double.parseDouble(dataLine[headers.indexOf(NODE_X)]);
        double y = Double.parseDouble(dataLine[headers.indexOf(NODE_Y)]);
        return new Node(id,x,y);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**     Reads a file containing links data and imports links into the network as a parameter     */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void readLinkFile(Network network, String filePath, char delimiter)
    {
        List<String[]> dataLines = NetworkReader.extractData(filePath, delimiter);
        List<String> headers = Arrays.asList(dataLines.get(0));
        if(!headers.contains(LINK_ID)||!headers.contains(LINK_FROM_ID)||!headers.contains(LINK_TO_ID))
            throw new RuntimeException("Link's header not found");
        for(int i = 1; i < dataLines.size(); i++){
            network.addLink(NetworkReader.createLinkFromDataLine(network, headers, dataLines.get(i)));}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                               Create a Node from a data line                                 */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static Link createLinkFromDataLine(Network network, List<String> headers, String[] dataLine)
    {
        String id = dataLine[headers.indexOf(LINK_ID)];
        Node fromNode = network.getNode(dataLine[headers.indexOf(LINK_FROM_ID)]);
        Node toNode = network.getNode(dataLine[headers.indexOf(LINK_TO_ID)]);
        return new Link(id,fromNode,toNode);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**                          Returns a list of String arrays of a TXT file                       */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static List<String[]> extractData(String filePath, char delimiter)
    {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while((line = reader.readLine()) != null)
                lines.add(line.split(String.valueOf(delimiter)));
        }catch (IOException e) {e.printStackTrace();}
        return lines;
    }
}