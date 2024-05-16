package engine3D.renderers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import engine3D.Rectangle3D;
import engine3D.Texture;
import engine3D.ViewWindow;
import engine3D.math3D.Transform;
import engine3D.math3D.Vector;

public class FastTexturedPolygonRenderer extends PolygonRenderer
{
    public FastTexturedPolygonRenderer(Transform camera, ViewWindow window) {
        super(camera, window);
        //TODO Auto-generated constructor stub
    }

    protected Vector a = new Vector();
    protected Vector b = new Vector();
    protected Vector c = new Vector();
    protected Vector viewPos = new Vector();
    protected Rectangle3D textureBounds = new Rectangle3D();
    protected BufferedImage doubleBuffer;
    protected short[] bufferData;

    /* This class is an abstract inner class that provides an interface
     * for rendering a horizontal scan line */
    public abstract class ScanRenderer 
    {
        protected Texture currentTexture;

        public void setTexture(Texture texture)
        {
            this.currentTexture = texture;
        }
        public abstract void render(int offset, int left, int right);
    }

    @Override
    void drawCurrentPolygon(Graphics pen) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawCurrentPolygon'");
    }
}