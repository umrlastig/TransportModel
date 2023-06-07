package org.TransportModel.network.io;

import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.locationtech.jts.geom.Coordinate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** GTFSReader is a class that reads GTFS (General Transit Feed Specification) and fill a network */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class GTFSReader extends NetworkReader
{
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a GTFS folder and fill the network with data
     * @param network    the network to fill
     * @param folderPath the path to the GTFS folder */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void readGTFSFolder(Network network, String folderPath)
    {
        if (!new File(folderPath + "/stops_links.txt").exists())
            this.createStopLinksFromStopTimes(folderPath + "/stop_times.txt", folderPath + "/stops_links.txt");
        this.readStopFile(network, folderPath + "/stops.txt");
        this.readPathwayFile(network, folderPath + "/pathways.txt");
        this.readTransfersFile(network, folderPath + "/transfers.txt");
        this.readStopLinksFile(network, folderPath + "/stops_links.txt");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a stop file and adds stop nodes to the network.
     * @param network  the network to add the stop nodes to
     * @param filePath the path to the stop file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void readStopFile(Network network, String filePath)
    {
        List<String[]> dataLines = this.extractData(filePath, ',');
        List<String> headers = Arrays.asList(dataLines.remove(0));
        HashMap<NODE_ATTRIBUTES, Integer> headersIndex = new HashMap<>();
        headersIndex.put(NODE_ATTRIBUTES.ID, headers.indexOf("stop_id"));
        headersIndex.put(NODE_ATTRIBUTES.X, headers.indexOf("stop_lon"));
        headersIndex.put(NODE_ATTRIBUTES.Y, headers.indexOf("stop_lat"));
        this.addNodes(network, dataLines, headersIndex);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a transfers file and add transfers links to the network
     * Transfer time on foot between nearby stops
     * @param network  the network to add the pathways links to
     * @param filePath the path to the pathway file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void readTransfersFile(Network network, String filePath)
    {
        List<String[]> dataLines = this.extractData(filePath, ',');
        List<String> headers = Arrays.asList(dataLines.remove(0));
        HashMap<LINK_ATTRIBUTES, Integer> headersIndex = new HashMap<>();
        headersIndex.put(LINK_ATTRIBUTES.FROM_ID, headers.indexOf("from_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.TO_ID, headers.indexOf("to_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.TIME, headers.indexOf("min_transfer_time"));
        this.addLinks(network, dataLines, headersIndex);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  Reads a pathways file and add pathways links to the network
     * Linking route on foot between two stops of a station
     * @param network  the network to add the pathways links to
     * @param filePath the path to the pathway file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void readPathwayFile(Network network, String filePath)
    {
        List<String[]> dataLines = this.extractData(filePath, ',');
        List<String> headers = Arrays.asList(dataLines.remove(0));
        HashMap<LINK_ATTRIBUTES, Integer> headersIndex = new HashMap<>();
        headersIndex.put(LINK_ATTRIBUTES.ID, headers.indexOf("pathway_id"));
        headersIndex.put(LINK_ATTRIBUTES.FROM_ID, headers.indexOf("from_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.TO_ID, headers.indexOf("to_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.BIDIRECTIONAL, headers.indexOf("is_bidirectional"));
        headersIndex.put(LINK_ATTRIBUTES.LENGTH, headers.indexOf("length"));
        headersIndex.put(LINK_ATTRIBUTES.TIME, headers.indexOf("traversal_time"));
        this.addLinks(network, dataLines, headersIndex);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Reads a transport line file and add pathways links to the network
     * Transport Link between two stops station
     * @param network  The network to which
     * @param filePath The path to the file containing the stop links data */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void readStopLinksFile(Network network, String filePath)
    {
        List<String[]> dataLines = this.extractData(filePath, ',');
        List<String> headers = Arrays.asList(dataLines.remove(0));
        HashMap<LINK_ATTRIBUTES, Integer> headersIndex = new HashMap<>();
        headersIndex.put(LINK_ATTRIBUTES.FROM_ID, headers.indexOf("from_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.TO_ID, headers.indexOf("to_stop_id"));
        headersIndex.put(LINK_ATTRIBUTES.TIME, headers.indexOf("traversal_time"));
        this.addLinks(network, dataLines, headersIndex);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Adds nodes to a network based on the provided data
     * @param network      The network to which the nodes are added
     * @param dataLines    A list of string arrays representing the node data (1 line = 1 node)
     * @param headersIndex A HashMap containing the indexes of column headers in the node data */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addNodes(Network network, List<String[]> dataLines, HashMap<NODE_ATTRIBUTES, Integer> headersIndex)
    {
        for (String[] dataLine : dataLines) {
            try {
                Node node;
                double x = Double.parseDouble(dataLine[headersIndex.get(NODE_ATTRIBUTES.X)]);
                double y = Double.parseDouble(dataLine[headersIndex.get(NODE_ATTRIBUTES.Y)]);
                if (headersIndex.containsKey(NODE_ATTRIBUTES.ID))
                    node = new Node(dataLine[headersIndex.get(NODE_ATTRIBUTES.ID)], new Coordinate(x, y));
                else
                    node = new Node(new Coordinate(x, y));
                network.addNode(node);
            } catch(Exception e){e.printStackTrace();}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Adds links to a network based on the provided data
     * @param network      The network to which the links are added
     * @param dataLines    A list of string arrays representing the link data (1 line = 1 link)
     * @param headersIndex A HashMap containing the indexes of column headers in the link data */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void addLinks(Network network, List<String[]> dataLines, HashMap<LINK_ATTRIBUTES, Integer> headersIndex)
    {
        for (String[] dataLine : dataLines) {
            try {
                Link link;
                Node from_node = network.getNode(dataLine[headersIndex.get(LINK_ATTRIBUTES.FROM_ID)]);
                Node to_node = network.getNode(dataLine[headersIndex.get(LINK_ATTRIBUTES.TO_ID)]);
                if (headersIndex.containsKey(LINK_ATTRIBUTES.ID))
                    link = new Link(dataLine[headersIndex.get(LINK_ATTRIBUTES.ID)], from_node, to_node);
                else
                    link = new Link(from_node, to_node);
                if (headersIndex.containsKey(LINK_ATTRIBUTES.BIDIRECTIONAL))
                    link.setBidirectional(dataLine[headersIndex.get(LINK_ATTRIBUTES.TO_ID)].equals("1"));
                if (headersIndex.containsKey(LINK_ATTRIBUTES.LENGTH))
                    link.setLengthInM((int)(Double.parseDouble(dataLine[headersIndex.get(LINK_ATTRIBUTES.LENGTH)])));
                if (headersIndex.containsKey(LINK_ATTRIBUTES.SPEED))
                    link.setNormalSpeedInKMH(Integer.valueOf(dataLine[headersIndex.get(LINK_ATTRIBUTES.SPEED)]));
                if (headersIndex.containsKey(LINK_ATTRIBUTES.TIME))
                    link.setNormalSpeedInKMH(Integer.valueOf(dataLine[headersIndex.get(LINK_ATTRIBUTES.TIME)]));
                if (headersIndex.containsKey(LINK_ATTRIBUTES.CAPACITY))
                    link.setCapacity(Integer.valueOf(dataLine[headersIndex.get(LINK_ATTRIBUTES.CAPACITY)]));
                network.addLink(link);
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Creates a stop links file from a stop times file
     * @param stopTimesFilePath The path to the stop times file
     * @param stopLinksFilePath The path to the output stop links file */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void createStopLinksFromStopTimes(String stopTimesFilePath, String stopLinksFilePath)
    {
        List<String[]> trips = this.extractStopTimesLinksData(stopTimesFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stopLinksFilePath))) {
            writer.write("from_stop_id,to_stop_id,traversal_time");
            for (String[] stopTimeData : trips) {
                writer.newLine();
                writer.write(stopTimeData[0] + "," + stopTimeData[1] + "," + stopTimeData[2]);
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Extracts stop times data from a given file and creates links between stops with associated delays
     * @param filePath The path to the stop times file.
     * @return A HashMap containing the stop links data, where the key is the link ID and the value is an array
     * containing the from_node_id, to_node_id, and delay */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private List<String[]> extractStopTimesLinksData(String filePath)
    {
        HashMap<String, String[]> linksData = new HashMap<>();
        List<String> dataLines = this.extractData(filePath);
        List<String> headers = Arrays.asList(dataLines.remove(0).split(","));
        for (int i = 0; i < dataLines.size() - 1; i++) {
            String[] firstDataLine = dataLines.get(i).split(",");
            String[] secondDataLine = dataLines.get(i + 1).split(",");
            //If two consecutive data lines belong to the same trip, create a link between stops
            if (firstDataLine[headers.indexOf("trip_id")].equals(secondDataLine[headers.indexOf("trip_id")])) {
                String from_node_id = firstDataLine[headers.indexOf("stop_id")];
                String to_node_id = secondDataLine[headers.indexOf("stop_id")];
                String link_id = from_node_id + ":" + to_node_id;
                String[] firstTimeStrings = firstDataLine[headers.indexOf("arrival_time")].split(":");
                String[] secondTimeStrings = secondDataLine[headers.indexOf("arrival_time")].split(":");
                int delay = this.getStopTimeDelay(firstTimeStrings,secondTimeStrings);
                //If the link already exists, keep the longest traversal time, otherwise add the link
                if (linksData.containsKey(link_id))
                    linksData.get(link_id)[2] = "" + Math.max(delay, Integer.parseInt(linksData.get(link_id)[2]));
                else
                    linksData.put(link_id, new String[]{from_node_id, to_node_id, "" + delay});
            }
        }
        return new ArrayList<>(linksData.values());
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Computes the delay between two given departure/arrival times.
     * @param firstTimeStrings  An array of strings representing the first time in the format HH:mm:ss
     * @param secondTimeStrings An array of strings representing the second time in the format HH:mm:ss
     * @return The delay in seconds between the two times */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private int getStopTimeDelay(String[] firstTimeStrings, String[] secondTimeStrings)
    {
        Duration firstTime = Duration.ofHours(Integer.parseInt(firstTimeStrings[0]))
                .plusMinutes(Integer.parseInt(firstTimeStrings[1]))
                .plusSeconds(Integer.parseInt(firstTimeStrings[2]));
        Duration secondTime = Duration.ofHours(Integer.parseInt(secondTimeStrings[0]))
                .plusMinutes(Integer.parseInt(secondTimeStrings[1]))
                .plusSeconds(Integer.parseInt(secondTimeStrings[2]));
        return (int) (secondTime.getSeconds() - firstTime.getSeconds());
    }
}