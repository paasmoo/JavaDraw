/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import java.awt.*;


public interface Fillable {
    void setFillColor(Color newColor);

    Color getFillColor();

    void setWidth(int newWidth);

    int getWidth();

    void setHeight(int newHeight);

    int getHeight();
}
