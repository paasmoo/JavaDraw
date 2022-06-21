/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import at.paasmoo.config.Config;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;


public class FreehandLine extends GraphicObject implements Line {
    private final LinkedList<Line2D> lines;
    private Point firstPoint;

    public FreehandLine(int x, int y, Color lineColor, int strokeWidth) {
        super(x, y);
        name = Config.bundle.getString("FreeLine");
        lines = new LinkedList<>();
        firstPoint = new Point(x, y);

        this.lineColor = lineColor;
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics2D.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics2D.setColor(lineColor);
        for (Line2D line : lines) {
            graphics2D.draw(line);
        }
    }

    @Override
    public Point getEndPoint() {
        if (firstPoint != null) {
            return firstPoint;
        }
        Point endPoint = new Point((int) lines.getFirst().getX1(), (int) lines.getFirst().getY1());
        for (Line2D line : lines) {
            if (line.getX1() > endPoint.x)
                endPoint.x = (int) line.getX1();
            if (line.getX2() > endPoint.x)
                endPoint.x = (int) line.getX2();

            if (line.getY1() > endPoint.y)
                endPoint.y = (int) line.getY1();
            if (line.getY2() > endPoint.y)
                endPoint.y = (int) line.getY2();
        }
        return endPoint;
    }

    @Override
    public void resize(int cX, int cY, boolean shiftPressed) {
        if (firstPoint != null) {
            lines.add(new Line2D.Double(firstPoint.x, firstPoint.y, cX, cY));
            firstPoint = null;
        } else {
            if (!shiftPressed) {
                lines.add(new Line2D.Double(lines.getLast().getX2(), lines.getLast().getY2(), cX, cY));
            } else {
                Line2D lastLine = lines.getLast();
                lastLine.setLine(lastLine.getX1(), lastLine.getY1(), cX, cY);
            }
        }

    }

    @Override
    public boolean within(int cX, int cY) {
        Rectangle2D frame = new Rectangle2D.Double(cX - DELTA, cY - DELTA, DELTA_WIDTH, DELTA_WIDTH);
        for (Line2D line : lines) {
            if (line.intersects(frame))
                return true;
        }
        return false;
    }

    @Override
    public Point getCords() {
        return new Point(x, y);
    }

    @Override
    public void move(int cX, int cY) {
        int spaceBetweenX = cX - grabPoint.x;
        int spaceBetweenY = cY - grabPoint.y;

        for (Line2D line : lines) {
            line.setLine(line.getX1() + spaceBetweenX, line.getY1() + spaceBetweenY,
                    line.getX2() + spaceBetweenX, line.getY2() + spaceBetweenY);
        }

        x += spaceBetweenX;
        y += spaceBetweenY;

        grabPoint.x = cX;
        grabPoint.y = cY;
    }

    @Override
    public void setCords(int x, int y) {
        int spaceBetweenX = x - this.x;
        int spaceBetweenY = y - this.y;

        for (Line2D line : lines) {
            line.setLine(line.getX1() + spaceBetweenX, line.getY1() + spaceBetweenY,
                    line.getX2() + spaceBetweenX, line.getY2() + spaceBetweenY);
        }

        this.x = x;
        this.y = y;
    }

    public int getEndX() {
        if (lines.size() == 0) {
            return firstPoint.x;
        }
        return (int) lines.getLast().getX2();
    }

    public int getEndY() {
        if (lines.size() == 0) {
            return firstPoint.y;
        }
        return (int) lines.getLast().getY2();
    }

    @Override
    public void refreshName() {
        name = Config.bundle.getString("FreeLine");
    }

    public void setLines(LinkedList<Line2D> lines) {
        for (Line2D line : lines) {
            this.lines.add(new Line2D.Double(line.getX1(), line.getY1(), line.getX2(), line.getY2()));
        }
    }

    @Override
    public GraphicObject clone() throws CloneNotSupportedException {
        FreehandLine freeHandLine = new FreehandLine(x, y, lineColor, strokeWidth);
        freeHandLine.setLines(new LinkedList<>(lines));
        freeHandLine.setObjectNumber(this.objectNumber);
        return freeHandLine;
    }
}
