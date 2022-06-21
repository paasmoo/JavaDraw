/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.utils;

import at.paasmoo.graphicobjects.GraphicObject;

import javax.swing.*;
import java.awt.*;


public class GraphicObjectListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList jList, Object o,
                                                  int i, boolean b,
                                                  boolean b1) {
        GraphicObject object = (GraphicObject) o;
        JLabel text = (JLabel) super.getListCellRendererComponent(jList, o, i, b, b1);
        text.setForeground(object.getLineColor());
        return text;
    }
}
