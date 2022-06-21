/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.utils;

import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.gui.drawingspace.GraphicPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class MouseMotion implements MouseMotionListener {
    private final GraphicPanel gPanel;
    private MODE mode;

    public MouseMotion(GraphicPanel graphicsPanel) {
        gPanel = graphicsPanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(gPanel.isAnimating()) {
            return;
        }
        if (mode == MODE.SELECT) {
            if (gPanel.getSelectedItemIndex() != -1) {
                GraphicObject component = gPanel.getGraphicObjects().get(gPanel.getSelectedItemIndex());
                component.move(e.getX(), e.getY());
                gPanel.getObjectFrame().setSelectedComponent(component);
                gPanel.trimToPreferredSize(component);
            }
        } else {
            gPanel.getGraphicObjects().getLast().resize(e.getX(), e.getY(), gPanel.isShiftPressed());
            gPanel.getObjectFrame().setSelectedComponent(gPanel.getGraphicObjects().getLast());
            gPanel.trimToPreferredSize(gPanel.getGraphicObjects().getLast());

        }
        gPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }
}