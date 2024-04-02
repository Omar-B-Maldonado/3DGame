package engine3D;

import engine3D.math3D.*;

/* 
 * This is a simpler version of ScanConverter that
 * directly uses a linear interpolation function
 * for demonstration purposes */
public class ScanConverter2 extends ScanConverter
{
    ScanConverter2(ViewWindow window) 
    {
        super(window);
    }

    /* Scan-converts a projected polygon.
     * Returns true if the polygon is visible in the view window. */
    @Override
    public boolean convert(Polygon poly)
    {
        super.ensureCapacity();
        super.clearCurrentScan(); //will clear the entire screen's worth of scan lines after ensuring capacity

        //int minX = window.getX();
        //int maxX = window.getX() + window.getWidth() - 1;
        int minY = window.getY();
        int maxY = window.getY() + window.getHeight() - 1;

        int numVertices = poly.getNumVertices();
        
        for (int i = 0; i < numVertices; i++)
        {
            Vector v1 = poly.getVertex(i);
            Vector v2 = poly.getVertex((i + 1) % numVertices); //if i = numVertices - 1, v2 = 0
           
            /* ensure v1.y < v2.y via a swap
             * This means v1.y will be above (or equal to) v2.y on the screen */
            if (v1.y > v2.y)
            {
                Vector temp = v1;
                v1 = v2;
                v2 = temp;
            }
            float dy = v2.y - v1.y;
            
            if (dy == 0) continue; //ignore horizontal lines

            //calculate the start and end y of the two vertices
            int startY = Math.max(MoreMath.ceiling(v1.y), minY);
            int endY   = Math.min(MoreMath.floor  (v2.y), maxY);

            //set the top and bottom boundary of this polygon
            top    = Math.min(top,  startY);
            bottom = Math.max(bottom, endY);

            //from top to bottom, interpolate to set the x boundary (either left or right) for each y (scan line)
            for (int y = startY; y <= endY; y++) 
            {
                int interpolatedX = MoreMath.lerpX(v1.x, v1.y, v2.x, v2.y, y);
                scans[y].setBoundary(interpolatedX);
            } 
        }
        //check if visible (any valid scans)
        for (int i = top; i <= bottom; i++)
        {
            if (scans[i].isValid()) return true;
        }
        return false;
    }
}

