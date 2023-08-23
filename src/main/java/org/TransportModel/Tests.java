package org.TransportModel;

import org.TransportModel.generation.Zone;
import org.TransportModel.gui.UserInterface;
import org.TransportModel.network.Link;
import org.TransportModel.network.Network;
import org.TransportModel.network.Node;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
public class Tests
{
    private Tests(){}
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") public void testNetwork(Network network)
    {
        DirectedWeightedMultigraph<Node, Link> graph = network.createGraph();
        //Connected
        ConnectivityInspector<Node,Link> connectivityInspector = new ConnectivityInspector<>(graph);
        int elements = connectivityInspector.connectedSets().size();
        Assert.isTrue(connectivityInspector.isConnected(),"Network not connected ("+elements+")");
        //Strongly Connected
        KosarajuStrongConnectivityInspector<Node,Link> strongInspector = new KosarajuStrongConnectivityInspector<>(graph);
        elements = strongInspector.stronglyConnectedSets().size();
        Assert.isTrue(strongInspector.isStronglyConnected(),"Network not strongly connected ("+elements+")");
        //ok
        System.out.println("Network ok");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Displays a path with strings */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused") private static void displayPath(GraphPath<Node,Link> graphPath)
    {
        for(Link link:graphPath.getEdgeList()) {
            String fromName = link.getFromNode().getName(), toName = link.getToNode().getName();
            String linkType = link.getType().name(), linkName = link.getName();
            int linkTime = (int)(link.getLengthInM()/link.getNormalSpeedInMS());
            System.out.print("\nEntre "+fromName+" et "+toName+ ": "+linkTime+ "s en "+linkType + " ("+linkName+")");
        }
        int linkNumber = graphPath.getEdgeList().size(), timeInS = (int)graphPath.getWeight();
        String time = String.format("%02d:%02d:%02d",timeInS/3600,timeInS%3600/60,timeInS%60);
        System.out.print("\nTemps total: "+time+" (" +linkNumber+" liens)");
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void printMatrixPart(RealMatrix matrix, int startRow, int endRow, int startColumn, int endColumn)
    {
        int numRows = matrix.getRowDimension();
        int numColumns = matrix.getColumnDimension();
        if (startRow < 0 || startRow >= numRows || endRow < 0 || endRow >= numRows || startColumn < 0 ||
                startColumn >= numColumns || endColumn < 0 || endColumn >= numColumns)
            throw new IllegalArgumentException();
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                System.out.print(matrix.getEntry(i, j) + "\t");
            }
            System.out.println();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void displayNetwork(Network network)
    {
        Double[] bounds = new Double[]{null,null,null,null};
        for(Node node:network.getNodes()) {
            Coordinate coordinate = node.getCoordinate();
            bounds[0] = (bounds[0] == null || bounds[0] > coordinate.x) ? coordinate.x : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > coordinate.y) ? coordinate.y : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < coordinate.x) ? coordinate.x : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < coordinate.y) ? coordinate.y : bounds[3];}
        UserInterface.getInstance().display(new JComponent() {
            @Override public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                double width = this.getWidth(), height = this.getHeight();
                for(Link link:network.getLinks()) {
                    Coordinate from = getScaledCoordinate(link.getFromNode().getCoordinate(), bounds, width, height);
                    Coordinate to = getScaledCoordinate(link.getToNode().getCoordinate(), bounds, width, height);
                    g.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
                    g2d.setStroke(new BasicStroke(0.2f));}
                for(Node node:network.getNodes()) {
                    Coordinate coordinate = getScaledCoordinate(node.getCoordinate(), bounds, width, height);
                    g.setColor(new Color(90,70,210));
                    g2d.fillOval((int) coordinate.x, (int) coordinate.y, 3,3);
                    g.setColor(Color.black);
                    g2d.drawOval((int) coordinate.x, (int) coordinate.y, 3,3);}}
        });
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static void displayZones(HashMap<String,Zone> zones, RealVector jobs, RealVector workers)
    {
        Double[] bounds = new Double[]{null,null,null,null};
        for(Zone zone:zones.values()) {
            for(Coordinate coordinate:zone.getShape().getCoordinates()) {
                bounds[0] = (bounds[0] == null || bounds[0] > coordinate.x) ? coordinate.x : bounds[0];
                bounds[1] = (bounds[1] == null || bounds[1] > coordinate.y) ? coordinate.y : bounds[1];
                bounds[2] = (bounds[2] == null || bounds[2] < coordinate.x) ? coordinate.x : bounds[2];
                bounds[3] = (bounds[3] == null || bounds[3] < coordinate.y) ? coordinate.y : bounds[3];}}
        UserInterface.getInstance().display(new JComponent() {
            @Override public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(new BasicStroke(0.4f));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                double width = this.getWidth(), height = this.getHeight();
                width = width*4;height=height*4;
                for(Zone zone:zones.values()) {
                    Coordinate[] coordinates =  zone.getShape().getCoordinates();
                    g2d.setColor(Color.black);
                    for (int i = 0; i < coordinates.length; i++) {
                        Coordinate from = getScaledCoordinate(coordinates[i], bounds, width, height);
                        Coordinate to = getScaledCoordinate(coordinates[(i + 1) % coordinates.length],bounds,width,height);
                        g2d.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);}
                    Coordinate centroid = zone.getCentroid();
                    double zoneJobs = jobs.getEntry(zone.getIndex());
                    double zonePop = workers.getEntry(zone.getIndex());
                    double total = zoneJobs+zonePop;
                    int centerX = (int) getScaledCoordinate(centroid, bounds, width, height).x;
                    int centerY = (int) getScaledCoordinate(centroid, bounds, width, height).y;
                    double jobsAngle = (zoneJobs / total) * 360;
                    double workersAngle = (zonePop / total) * 360;
                    double maxValue = 303817,  maxSize = 40;
                    int size = (int)(total*maxSize/maxValue)+10;
                    g2d.setColor(Color.BLUE);
                    g2d.fillArc(centerX - size/2, centerY - size/2, size, size, 0, (int) jobsAngle);
                    g2d.setColor(Color.RED);
                    g2d.fillArc(centerX - size/2, centerY - size/2, size, size, (int) (0+ jobsAngle), (int) workersAngle);
                }}
        });

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static Coordinate getScaledCoordinate(Coordinate coordinate, Double[] bounds, double width, double height)
    {
        double range = Math.max(bounds[2] - bounds[0], bounds[3] - bounds[1]);
        int x = (int)((coordinate.x - bounds[0]) * width / range)-1100;
        int y = (int)((coordinate.y - bounds[1]) * height / range*1.5);
        int yMax = (int)((bounds[3]-bounds[1]) * (height / range)*1.5);
        y = (int)(height - (y + (height - yMax)/2))-900;
        return new Coordinate(x,y);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public static boolean isStronglyConnected(Network networkTC)
     {return new KosarajuStrongConnectivityInspector<>(networkTC.createGraph()).isStronglyConnected();}
}
