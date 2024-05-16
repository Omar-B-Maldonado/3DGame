package engine3D.math3D;

public class Lookup 
{
    public static double[] cos, sin;

    private static double[] generateCos()
    {
        double[] cos = new double[360];
        for(int angle = 0; angle < 360; angle++)
        {
            cos[angle] = Math.cos(angle * Math.PI / 180);
        }
        return cos;
    }

    private static double[] generateSin()
    {
        double[] sin = new double[360];
        for(int angle = 0; angle < 360; angle++)
        {
            sin[angle] = Math.sin(angle * Math.PI / 180);
        }
        return sin;
    }
}