/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import at.paasmoo.config.Config;
import at.paasmoo.gui.drawingspace.GraphicPanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;


public class Wordfield extends GraphicObject implements Fillable {
    private final GraphicPanel gPanel;

    private int width;
    private int height;
    private String text;

    private Color fillColor;
    private Font font;

    private int offset;
    private boolean storeTheEdit = true;

    public Wordfield(int x, int y, Color outlineColor, Color fillColor, String text, Font font, GraphicPanel gPanel) {
        super(x, y);
        name = Config.bundle.getString("Wordfield");
        this.lineColor = outlineColor;
        this.fillColor = fillColor;
        this.font = font;
        this.gPanel = gPanel;
        this.width = -1;
        this.height = -1;
        this.text = text;
    }

    public Wordfield(int x, int y, Color outlineColor, Color fillColor, String text, Font font, GraphicPanel gPanel,
                     int width, int height) {
        super(x, y);
        name = Config.bundle.getString("Wordfield");
        this.lineColor = outlineColor;
        this.fillColor = fillColor;
        this.font = font;
        this.gPanel = gPanel;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    @Override
    public void draw(Graphics graphics) {
        if (text != null) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            if (width == -1 && height == -1) {
                FontMetrics fm = graphics.getFontMetrics(font);
                Rectangle2D textBound = fm.getStringBounds(text, graphics);
                offset = fm.getAscent();
                width = (int) textBound.getWidth();
                height = (int) textBound.getHeight();
                gPanel.trimToPreferredSize(this);
                if (storeTheEdit) {
                    gPanel.storeEdit();
                }
                storeTheEdit = false;
            }

            if (fillColor != null && width != -1 && height != -1) {
                graphics2D.setColor(fillColor);
                graphics2D.fill(new Rectangle2D.Double(x, y - offset, width, height));
            }

            graphics2D.setColor(lineColor);
            graphics2D.setFont(font);
            graphics2D.drawString(text, x, y);
        }
    }

    @Override
    public Point getEndPoint() {
        if (width == -1 && height == -1) {
            return new Point(width, height);
        }
        return new Point(x + width, y + height);
    }

    @Override
    public void resize(int cX, int cY, boolean shiftPressed) {

    }

    @Override
    public boolean within(int cX, int cY) {
        Rectangle2D frame = new Rectangle2D.Double(x, y - offset, width, height);
        return frame.contains(cX, cY);
    }

    @Override
    public void setCords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point getCords() {
        return new Point(x, y);
    }

    @Override
    public void move(int cX, int cY) {
        int spaceBetweenX = cX - grabPoint.x;
        int spaceBetweenY = cY - grabPoint.y;

        this.x += spaceBetweenX;
        this.y += spaceBetweenY;

        grabPoint.x = cX;
        grabPoint.y = cY;
    }

    public void setText(String text) {
        height = -1;
        width = -1;
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public void setFillColor(Color newColor) {
        fillColor = newColor;
    }

    @Override
    public Color getFillColor() {
        return fillColor;
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

    @Override
    public int getWidth() {
        if (width < 0) {
            return width * (-1);
        }
        return width;
    }

    public void setFont(Font font) {
        height = -1;
        width = -1;
        this.font = font;

    }

    public String getText() {
        return text;
    }

    @Override
    public void refreshName() {
        name = Config.bundle.getString("Wordfield");
    }

    @Override
    public Wordfield clone() throws CloneNotSupportedException {
        Wordfield w = new Wordfield(x, y, lineColor, fillColor, text, font, gPanel, width, height);
        w.setObjectNumber(this.objectNumber);
        return w;
    }
}
