package engine3D;

import engine3D.math3D.MoreMath;
import engine3D.math3D.Vector;

/* A Scan Converter converts a projected polygon into a series of horizontal scans
 * so the polygon can be drawn as a series of horizontal lines 
 * The Scan Converter also ensures that all scans are in the view window */
public class ScanConverter 
{
    ViewWindow window;
    Scan[]     scans;
    int        top;
    int        bottom;

    /* Integers are 32 bits long, so using a scale of 2**16 means that 
     * the  most significant 16 bits are the integer part and
     * the least significant 16 bits are the fractional part of the number.
     */
    //FIXED POINT MATH!!! Note: 1 << 16 is equivalent to 2**16
    private static final int SCALE_BITS = 16;
    private static final int SCALE = 1 << SCALE_BITS;
    private static final int SCALE_MASK = SCALE - 1;

    //The ViewWindow's properties can change in between scan conversions
    public ScanConverter(ViewWindow window)
    {
        this.window = window;
    }
    //gets the top boundary of the last scan-converted polygon
    public int getTop()         { return top; }
    
    //gets the bottom boundary of the last scan-converted polygon
    public int getBottom()      { return bottom; }
    
    //gets the scan line for the specified y
    public Scan getScan(int y)  { return scans[y]; }

    //clears the current scan
    public void clearCurrentScan()
    {
        for (int i = top; i <= bottom; i++)
        {
            scans[i].clear();
        }
        top    = Integer.MAX_VALUE;
        bottom = Integer.MIN_VALUE;
    }

    /* Ensures this ScanConverter has the capacity
     * to scan-convert a polygon to the viewWindow */
    public void ensureCapacity() //we need a horizontal scan line for each screen-y along the screen's height
    {
        int height = window.getY() + window.getHeight();
        if (scans == null || scans.length != height)
        {
            scans = new Scan[height];
        }
        for (int i = 0; i < height; i++) 
        {
            scans[i] = new Scan();
        }
        //set top and bottom so clearCurrentScan clears all scans
        top    = 0;
        bottom = height - 1;
    }

    /* Scan-converts a projected polygon.
     * Returns true if the polygon is visible in the view window. */
    public boolean convert(Polygon poly)
    {
        ensureCapacity();
        clearCurrentScan(); //will clear the entire screen's worth of scan lines after ensuring capacity

        int minX = window.getX();
        int maxX = window.getX() + window.getWidth() - 1;
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

            top    = Math.min(top,  startY);
            bottom = Math.max(bottom, endY);

            float dx = v2.x - v1.x;

            //special case: vertical line
            if (dx == 0)
            {
                int x = MoreMath.ceiling(v1.x);
                
                //ensure x is within view bounds
                x = Math.min(maxX + 1, Math.max(x, minX));
                for (int y = startY; y <= endY; y++)
                {
                    scans[y].setBoundary(x);
                }
            }
            else
            {   //scan-convert this edge from top to bottom with the line equation
                float gradient = dx / dy;
                
                //trim the start of the line
                float startX = v1.x + (startY - v1.y) * gradient;
                if (startX < minX)
                {
                    int yInt = (int)(v1.y + (minX - v1.x) / gradient);
                        yInt = Math.min(yInt, endY);
                    while (startY <= yInt) scans[startY++].setBoundary(minX);
                }
                else if (startX > maxX)
                {
                    int yInt = (int)(v1.y + (maxX - v1.x) / gradient);
                        yInt = Math.min(yInt, endY);
                    while (startY <= yInt) scans[startY++].setBoundary(maxX + 1);
                }
                
                if (startY > endY) continue;

                //trim back of line
                float endX = v1.x + (endY - v1.y) * gradient;
                if (endX < minX)
                {
                    int yInt = MoreMath.ceiling(v1.y + (minX - v1.x) / gradient);
                        yInt = Math.max(yInt, startY);
                    while (endY >= yInt) scans[endY--].setBoundary(minX);
                }
                else if (endX > maxX)
                {
                    int yInt = MoreMath.ceiling(v1.y + (maxX - v1.x) / gradient);
                        yInt = Math.max(yInt, startY);
                    while (endY >= yInt) scans[endY--].setBoundary(maxX + 1);
                }

                if (startY > endY) continue;

                //line equation using integers
                int  xScaled = (int)(SCALE * v1.x + SCALE * (startY - v1.y) * dx / dy) + SCALE_MASK;
                int dxScaled = (int)(dx * SCALE / dy);

                for (int y = startY; y <= endY; y++)
                {
                    scans[y].setBoundary(xScaled >> SCALE_BITS);
                    xScaled += dxScaled;
                }
            }
        }
        //check if visible (any valid scans)
        for (int i = top; i <= bottom; i++)
        {
            if (scans[i].isValid()) return true;
        }
        return false;
    }

    /* This inner class represents a horizontal scan line */
    public class Scan
    {
        public int left, right;

        /* Sets the left and right boundaries for this scan
         * if the x value is outside the current boundary */
        public void setBoundary(int x)
        {
            if (x     < left ) left  = x;
            if (x - 1 > right) right = x - 1;
        }

        /* Clears this scan line. */
        public void clear()
        {
            left  = Integer.MAX_VALUE;
            right = Integer.MIN_VALUE;
        }

        /* Determines if this scan is valid. */
        public boolean isValid()
        {
            return (left <= right);
        }

        /* Sets this scan. */
        public void setTo(int left, int right)
        {
            this.left  = left;
            this.right = right;
        }

        /* Checks if this scan is equal to the specified values. */
        public boolean equals(int left, int right)
        {
            return (this.left == left && this.right == right);
        }
    }
}
