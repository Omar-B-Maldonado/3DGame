package engine3D.math3D;

public class MoreMath 
{
    /* These functions perform ceiling and floor operations
     * while converting from a float to an int. This way,
     * we don't have to convert anything manually */
    public static int ceiling(float m)
    {
        if (m > 0) return (int)m + 1;
        else       return (int)m;
    }

    public static int floor(float m)
    {
        return ceiling(m) - 1;
    }
    
    //performs singular-value linear interpolation for x between two points (x1, y1) and (x2, y2), given y
    //returns the interpolated x-coordinate.
    public static int lerpX(float x1, float y1, float x2, float y2, int y) 
    {
        if (x1 == x2) return Math.round(x1); //handle vertical lines
        
        //calculate the fractional position of y between y1 and y2.
        float t = (y - y1) / (y2 - y1);
        
        //use linear interpolation formula to find the corresponding x-coordinate.
        return Math.round(x1 + t * (x2 - x1));
    }
}
