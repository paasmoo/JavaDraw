/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import at.paasmoo.config.Config;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


public class StraightLine extends GraphicObject implements Line {
    private int endX;
    private int endY;

    private final Line2D line;

    public StraightLine(int x, int y, int endX, int endY, Color lineColor, int strokeWidth) {
        super(x, y);
        name = Config.bundle.getString("StraightLine");
        this.endX = endX;
        this.endY = endY;
        this.lineColor = lineColor;
        this.strokeWidth = strokeWidth;

        line = new Line2D.Double(x, y, endX, endY);
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        line.setLine(x, y, endX, endY);

        graphics2D.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics2D.setColor(lineColor);
        graphics2D.draw(line);

    }

    @Override
    public Point getEndPoint() {
        Point endPoint = new Point(x, y);
        endPoint.x = Math.max(x, endX);
        endPoint.y = Math.max(y, endY);
        return endPoint;
    }

    @Override
    public void resize(int cX, int cY, boolean shiftPressed) {
        if (!shiftPressed) {
            this.endX = cX;
            this.endY = cY;
        } else {
            //width and height from an abstract rectangle around the line
            int maxSize = Math.max(cX - x, cY - y);
            endX = x + maxSize;
            endY = y + maxSize;
        }
    }

    @Override
    public boolean within(int cX, int cY) {
        Rectangle2D frame = new Rectangle2D.Double(cX - DELTA, cY - DELTA, DELTA_WIDTH, DELTA_WIDTH);
        return line.intersects(frame);
    }

    @Override
    public Point getCords() {
        return new Point(x, y);
    }

    @Override
    public void move(int cX, int cY) {
        int spaceBetweenX = cX - grabPoint.x;
        int spaceBetweenY = cY - grabPoint.y;

        this.endX += spaceBetweenX;
        this.endY += spaceBetweenY;
        this.x += spaceBetweenX;
        this.y += spaceBetweenY;

        grabPoint.x = cX;
        grabPoint.y = cY;
    }

    @Override
    public void setCords(int x, int y) {
        this.endX += x - this.x;
        this.endY += y - this.y;
        this.x = x;
        this.y = y;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    @Override
    public void refreshName() {
        name = Config.bundle.getString("StraightLine");
    }

    @Override
    public StraightLine clone() throws CloneNotSupportedException {
        StraightLine line = new StraightLine(x, y, endX, endY, lineColor, strokeWidth);
        line.setObjectNumber(this.objectNumber);
        return line;
    }
}
