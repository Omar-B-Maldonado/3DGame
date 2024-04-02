package engine3D.renderers;

import java.awt.Graphics;

import engine3D.*;
import engine3D.math3D.Transform;

public class SolidPolygonRenderer extends PolygonRenderer 
{
    public SolidPolygonRenderer(Transform camera, ViewWindow window)
    {
        super(camera, window);
    }

    /* Draws the current polygon. At this point, the current polygon is:
     * transformed, clipped, projected, scan-converted, and visible. */
    protected void drawCurrentPolygon(Graphics pen)
    {
        pen.setColor(sourcePolygon.getColor());
        for (int y = scanConverter.getTop(); y <= scanConverter.getBottom(); y++)
        {
            ScanConverter.Scan scan = scanConverter.getScan(y);
            if (scan.isValid()) pen.drawLine(scan.left, y, scan.right, y);
        }
    }
}
