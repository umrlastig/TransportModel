package org.TransportModel.gui;

import org.TransportModel.generation.Area;
import org.TransportModel.generation.Zone;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import javax.swing.*;
import java.awt.*;

///////////////////////////////////////////////////////////////////////////////////////////////////
/** */
///////////////////////////////////////////////////////////////////////////////////////////////////
@SuppressWarnings("unused") public class AreaCanvas extends JComponent
{
    private Double[] bounds;
    private final Area area;
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** Constructor */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public AreaCanvas(Area area)
    {
        this.area = area;
        this.setupBounds();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBounds()
    {
        this.bounds = new Double[]{null, null, null, null};
        for (Zone zone : this.area.getZones()) {
            Envelope envelope = zone.getShape().getEnvelopeInternal();
            bounds[0] = (bounds[0] == null || bounds[0] > envelope.getMinX()) ? envelope.getMinX() : bounds[0];
            bounds[1] = (bounds[1] == null || bounds[1] > envelope.getMinY()) ? envelope.getMinY() : bounds[1];
            bounds[2] = (bounds[2] == null || bounds[2] < envelope.getMaxX()) ?  envelope.getMaxX() : bounds[2];
            bounds[3] = (bounds[3] == null || bounds[3] < envelope.getMaxY()) ? envelope.getMaxY() : bounds[3];
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /** */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override public void paintComponent(Graphics g)
    {
        g.setColor(Color.green);
        if (this.area != null && this.bounds[0]!=null)
            for (Zone zone : this.area.getZones()) {
                Coordinate[] coordinates =  zone.getShape().getCoordinates();
                for (int j = 0; j < coordinates.length; j++) {
                    Coordinate c1 = this.getScaledCoordinate(coordinates[j]);
                    Coordinate c2 = this.getScaledCoordinate(coordinates[(j + 1) % coordinates.length]);
                    g.drawLine((int) c1.x, (int) c1.y, (int) c2.x, (int) c2.y);
                }
            }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**  */
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Coordinate getScaledCoordinate(Coordinate coordinate)
    {
        double screenWidth = this.getWidth(), screenHeight = this.getHeight();
        double range = Math.max(this.bounds[2] - this.bounds[0], this.bounds[3] - this.bounds[1]);
        int x = (int)((coordinate.x - bounds[0]) * screenWidth / range);
        int y = (int)((coordinate.y - bounds[1]) * screenHeight / range);
        return new Coordinate(x,y);
    }
}
