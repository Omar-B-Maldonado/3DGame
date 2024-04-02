package engine3D.renderers;

import java.awt.Color;
import java.awt.Graphics;

import engine3D.Polygon;
import engine3D.math3D.Transform;
import engine3D.*;

/* Final Rendering Pipeline
 * 1. Check if facing camera
 * 2. Apply transform
 * 3. Clip
 * 4. Project onto view window
 * 5. Scan-convert
 * 6. Draw
 */
/* This is a class that transforms and draws polygons onto the screen */
public abstract class PolygonRenderer 
{
    protected ScanConverter scanConverter;
    protected Transform     camera;
    protected ViewWindow    window;
    protected boolean       clearViewEveryFrame;
    protected Polygon       sourcePolygon;
    protected Polygon       scratchPolygon;

    /* Creates a new PolygonRenderer with the specified camera and viewWindow.
     * The view is cleared when startFrame() is called. */
    public PolygonRenderer(Transform camera, ViewWindow window)
    {
        this(camera, window, true);
    }
    public PolygonRenderer(Transform camera, ViewWindow window, boolean clearViewEveryFrame)
    {
        this.camera = camera;
        this.window = window;
        this.clearViewEveryFrame = clearViewEveryFrame;
        init();
    }

    /* Creates the scan converter and destination polygon */
    protected void init()
    {
        scratchPolygon = new Polygon();
        scanConverter  = new ScanConverter(window);
    }

    /* Gets the camera used for this PolygonRenderer */
    public Transform getCamera()
    {
        return camera;
    }

    /* Indicates the start of rendering of a frame.
     * This method should be called every frame before any Polygons are drawn. */
    public void startFrame(Graphics pen)
    {
        if (clearViewEveryFrame)
        {
            pen.setColor(Color.gray);
            pen.fillRect(window.getX(), window.getY(),
                         window.getWidth(), window.getHeight());
        }
    }

    /* Indicates the end of rendering of a frame.
     * This method should be called every frame after all Polygons are drawn. */
    public void endFrame(Graphics pen)
    {
        //do nothing for now.
    }

    /* Transforms and draws a polygon. */
    public boolean draw(Graphics pen, Polygon poly)
    {
        if (poly.isFacing(camera.getLocation()))
        {
            sourcePolygon = poly;
            scratchPolygon.setTo(poly);
            scratchPolygon.subtract(camera);
            boolean visible = scratchPolygon.clip(-1); //clip with z == -1, which is where the view plane is
            if (visible)
            {
                scratchPolygon.project(window);
                visible = scanConverter.convert(scratchPolygon);
                if (visible)
                {
                    drawCurrentPolygon(pen);
                    return true;
                }
            }
        }
        return false; //not facing camera and/or not visible
    }

    //to be implemented by classes that extend PolygonRenderer
    abstract void drawCurrentPolygon(Graphics pen);
}
