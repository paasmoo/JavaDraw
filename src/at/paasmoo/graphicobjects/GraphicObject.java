/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import java.awt.*;
import java.io.Serializable;


public abstract class GraphicObject implements Cloneable, Serializable {
    protected String name;
    protected int x;
    protected int y;
    protected int strokeWidth;
    protected static final Object syncObject = new Object();
    protected static int lastObjectNumber = 0;
    protected int objectNumber;
    protected Point grabPoint;

    protected Color lineColor;

    protected final int DELTA_WIDTH = 5;
    protected final int DELTA = 2;

    public GraphicObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setGrabPoint(Point grabPoint) {
        this.grabPoint = grabPoint;
    }

    public abstract void draw(Graphics graphics);

    public abstract Point getEndPoint();

    public abstract void resize(int cX, int cY, boolean shiftPressed);

    public abstract void move(int cX, int cY);

    public abstract boolean within(int cX, int cY);

    public abstract void refreshName();

    public void setNewObjectNumber() {
        synchronized (syncObject) {
            lastObjectNumber++;
            objectNumber = lastObjectNumber;
        }
    }

    protected void setObjectNumber(int number) {
        objectNumber = number;
    }

    @Override
    public String toString() {
        return name + objectNumber;
    }

    public abstract void setCords(int x, int y);

    public abstract Point getCords();

    public static void resetObjectNumber() {
        lastObjectNumber = 0;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getName() {
        return name;
    }

    @Override
    public abstract GraphicObject clone() throws CloneNotSupportedException;
}