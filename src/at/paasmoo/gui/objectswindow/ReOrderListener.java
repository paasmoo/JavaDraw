/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ReOrderListener extends MouseAdapter {
    private JList list;
    private int pressIndex;
    private int releaseIndex;
    private ObjectFrame frame;

    public ReOrderListener(JList list, ObjectFrame frame){
        this.list = list;
        pressIndex = 0;
        releaseIndex = 0;
        this.frame = frame;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressIndex = list.locationToIndex(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releaseIndex = list.locationToIndex(e.getPoint());
        if (releaseIndex != pressIndex && releaseIndex != -1) {
            reorder();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseReleased(e);
        pressIndex = releaseIndex;
    }

    private void reorder() {
        DefaultListModel model = (DefaultListModel) list.getModel();
        Object dragee = model.elementAt(pressIndex);
        model.removeElementAt(pressIndex);
        model.insertElementAt(dragee, releaseIndex);

        List<Object> list = Arrays.asList(model.toArray());
        frame.setNewReorderedList(new LinkedList<>(list));
        frame.getPanel().repaint();
    }
}
