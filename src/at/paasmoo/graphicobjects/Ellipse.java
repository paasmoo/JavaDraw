/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import at.paasmoo.config.Config;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;


public class Ellipse extends GraphicObject implements Fillable {
    private int drawX;
    private int drawY;

    private int width;
    private int height;
    private Color fillColor;

    private final Ellipse2D ellipse;

    public Ellipse(int x, int y, int width, int height, Color outlineColor, Color fillColor, int strokeWidth) {
        super(x, y);
        name = Config.bundle.getString("Ellipse");
        this.width = width;
        this.height = height;
        this.lineColor = outlineColor;
        this.fillColor = fillColor;
        this.strokeWidth = strokeWidth;
        drawX = x;
        drawY = y;

        ellipse = new Ellipse2D.Double(x, y, width, height);
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        ellipse.setFrame(drawX, drawY, width, height);

        if (fillColor != null) {
            graphics2D.setColor(fillColor);
            graphics2D.fill(ellipse);
        }

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.setColor(lineColor);
        graphics2D.draw(ellipse);

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.setColor(lineColor);
        graphics2D.draw(ellipse);

    }

    @Override
    public Point getEndPoint() {
        int realWidth = width;
        int realHeight = height;
        if (width < 0) {
            realWidth *= -1;
        }
        if (height < 0) {
            realHeight *= -1;
        }
        return new Point(drawX + realWidth, drawY + realHeight);
    }

    @Override
    public void resize(int cX, int cY, boolean shiftPressed) {
        drawX = this.x;
        drawY = this.y;

        width = cX - x;
        height = cY - y;

        if (!shiftPressed) {
            if (width < 0) {
                width *= -1;
                drawX = cX;
            }
            if (height < 0) {
                height *= -1;
                drawY = cY;
            }
        } else {
            if (width < 0 && height < 0) {
                height *= -1;
                width *= -1;
                setCircleSize();
                drawX = x - width;
                drawY = y - height;
            } else if (width < 0) {
                width *= -1;
                setCircleSize();
                drawX = x - width;
            } else if (height < 0) {
                height *= -1;
                setCircleSize();
                drawY = y - height;
            } else {
                setCircleSize();
            }
        }
    }

    private void setCircleSize() {
        if (width >= height) {
            height = width;
        } else {
            width = height;
        }
    }

    @Override
    public boolean within(int cX, int cY) {
        Rectangle2D frame = new Rectangle2D.Double(cX - DELTA, cY - DELTA, DELTA_WIDTH, DELTA_WIDTH);
        return ellipse.intersects(frame);
    }

    @Override
    public void move(int cX, int cY) {
        int spaceBetweenX = cX - grabPoint.x;
        int spaceBetweenY = cY - grabPoint.y;

        drawX += spaceBetweenX;
        drawY += spaceBetweenY;
        this.x += spaceBetweenX;
        this.y += spaceBetweenY;

        grabPoint.x = cX;
        grabPoint.y = cY;
    }

    @Override
    public void setCords(int x, int y) {
        int spaceBetweenX = x - drawX;
        int spaceBetweenY = y - drawY;

        drawX = x;
        drawY = y;

        this.x += spaceBetweenX;
        this.y += spaceBetweenY;
    }

    @Override
    public Point getCords() {
        return new Point(drawX, drawY);
    }

    @Override
    public void setHeight(int newHeight) {
        if (height < 0) {
            height = newHeight * (-1);
        } else {
            height = newHeight;
        }
    }

    @Override
    public int getHeight() {
        if (height < 0) {
            return height * (-1);
        }
        return height;
    }

    @Override
    public void setWidth(int newWidth) {
        if (width < 0) {
            width = newWidth * (-1);
        } else {
            width = newWidth;
        }
    }

    public int getWidth() {
        if (width < 0) {
            return width * (-1);
        }
        return width;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color newColor) {
        fillColor = newColor;
    }

    @Override
    public int getX() {
        return drawX;
    }

    @Override
    public int getY() {
        return drawY;
    }

    @Override
    public void refreshName() {
        name = Config.bundle.getString("Ellipse");
    }

    @Override
    public Ellipse clone() throws CloneNotSupportedException {
        Ellipse e = new Ellipse(drawX, drawY, width, height, lineColor, fillColor, strokeWidth);
        e.setObjectNumber(this.objectNumber);
        return e;
    }
}
