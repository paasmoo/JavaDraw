/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.drawingspace;

import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.gui.toolbar.ToolBarPanel;
import at.paasmoo.utils.Logger;

import java.util.LinkedList;
import java.util.Stack;

public class ActionHandler {
    private Stack<LinkedList<GraphicObject>> undo;
    private Stack<LinkedList<GraphicObject>> redo;
    private ToolBarPanel toolbarPanel;

    public ActionHandler(ToolBarPanel tp) {
        undo = new Stack<>();
        redo = new Stack<>();
        undo.push(new LinkedList<>());
        this.toolbarPanel = tp;
    }

    public LinkedList<GraphicObject> undoChange() {
        if (undo.size() != 1) {
            redo.push(cloneList(undo.pop()));
            if (redo.size() == 1) {
                toolbarPanel.buttonChangeState(1, 1, true);
            }
            if (undo.size() == 1) {
                toolbarPanel.buttonChangeState(1, 0, false);
            }
        }
        return cloneList(undo.peek());
    }

    public LinkedList<GraphicObject> redoChange() {
        if (redo.size() != 0) {
            undo.push(cloneList(redo.pop()));
            if (undo.size() == 2) {
                toolbarPanel.buttonChangeState(1, 0, true);
            }
            if (redo.size() == 0) {
                toolbarPanel.buttonChangeState(1, 1, false);
            }
        }
        return cloneList(undo.peek());
    }

    public void addStackState(LinkedList<GraphicObject> objects) {
        if (redo.size() != 0) {
            redo.clear();
            toolbarPanel.buttonChangeState(1, 1, false);
        }
        if (undo.size() == 1) {
            toolbarPanel.buttonChangeState(1, 0, true);
        }
        undo.push(cloneList(objects));
    }

    public void clearStacks() {
        undo = new Stack<>();
        undo.push(new LinkedList<>());
        redo = new Stack<>();
        toolbarPanel.buttonChangeState(1, 0, false);
        toolbarPanel.buttonChangeState(1, 1, false);
    }

    public void setToolbarPanel(ToolBarPanel toolbarPanel) {
        this.toolbarPanel = toolbarPanel;
    }

    private LinkedList<GraphicObject> cloneList(LinkedList<GraphicObject> list) {
        LinkedList<GraphicObject> cloneList = new LinkedList<>();
        try {
            for (GraphicObject component : list) {
                cloneList.add(component.clone());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.jlogger.warning(Logger.stackTraceToString(e));
            Logger.jlogger.warning("Cloning objects failed!");
        }
        return cloneList;
    }

    public boolean[] getStateOfButtons() {
        return new boolean[]{undo.size() >= 2, redo.size() >= 1};
    }

    public void removeUndo() {
        undo.pop();
    }

    public void setDefault(LinkedList<GraphicObject> graphicObjects) {
        undo.clear();
        undo.add(cloneList(graphicObjects));
    }
}
