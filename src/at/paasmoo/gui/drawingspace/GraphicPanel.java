/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.drawingspace;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.graphicobjects.Picture;
import at.paasmoo.gui.MainFrame;
import at.paasmoo.gui.toolbar.FileChooser;
import at.paasmoo.utils.Logger;
import at.paasmoo.gui.objectswindow.ObjectFrame;
import at.paasmoo.gui.toolbar.ToolBarPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class GraphicPanel extends JPanel {
    private LinkedList<GraphicObject> graphicObjects;
    private ObservableList<GraphicObject> observedGraphicObjects;
    private final ObjectFrame objectFrame;
    private final MainFrame owner;
    private String file;
    private int selectedItemIndex;
    private boolean isShiftPressed;
    private ActionHandler actionHandler;
    private boolean isAnimating;

    public GraphicPanel(MainFrame frame) {
        this.objectFrame = new ObjectFrame(this, frame);
        objectFrame.setVisible(true);
        selectedItemIndex = -1;
        owner = frame;
        isAnimating = false;
        graphicObjects = new LinkedList<>();
        observedGraphicObjects = FXCollections.observableArrayList(graphicObjects);
        observedGraphicObjects.addListener((ListChangeListener<GraphicObject>) change -> objectFrame.updateList(graphicObjects));

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK,
                false), "shift");
        getActionMap().put("shift", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isShiftPressed = true;
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0,
                true), "shiftReleased");
        getActionMap().put("shiftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isShiftPressed = false;
            }
        });
        setBackground(Color.WHITE);
        setFocusable(true);
    }

    // reset GraphicPanel & clear all GraphicObject lists
    public void reset() {
        graphicObjects.clear();
        observedGraphicObjects.clear();
        objectFrame.updateList(graphicObjects);
        GraphicObject.resetObjectNumber();
        actionHandler.clearStacks();
        file = null;
        repaint();
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GraphicObject component : graphicObjects) {
            component.draw(g);
        }
    }

    public LinkedList<GraphicObject> getGraphicObjects() {
        return graphicObjects;
    }

    public void trimToPreferredSize(GraphicObject graphicObject) {
        Point endPoint = graphicObject.getEndPoint();

        if (endPoint.x > getPreferredSize().width || endPoint.y > getPreferredSize().height) {

            Dimension preferredSize = new Dimension(Integer.max(endPoint.x + 20, getPreferredSize().width),
                    Integer.max(endPoint.y + 20, getPreferredSize().height));
            setPreferredSize(preferredSize);
        }
        revalidate();
    }

    public void removeComponent(int i) {
        graphicObjects.remove(i);
        observedGraphicObjects.remove(i);
        actionHandler.removeUndo();
    }

    public void addComponent(GraphicObject GraphicObject) {
        graphicObjects.add(GraphicObject);
        observedGraphicObjects.add(GraphicObject);
    }

    public void prepareAnimation() {
        owner.animationStop.setEnabled(true);
        owner.animationStart.setEnabled(false);
        isAnimating = true;
        repaint();
    }

    public void finishAnimation() {
        isAnimating = false;
        owner.animationStop.setEnabled(false);
        owner.animationStart.setEnabled(true);
        repaint();
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setIsAnimating(boolean isAnimating) {
        this.isAnimating = isAnimating;
    }

    public ObjectFrame getObjectFrame() {
        return objectFrame;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    public void saveToFile() throws IOException {
        actionHandler.clearStacks();
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(new LinkedList<>(observedGraphicObjects));
        objectOut.close();
    }

    public void readFromFile() throws IOException, ClassNotFoundException {
        actionHandler.clearStacks();
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        graphicObjects = (LinkedList<GraphicObject>) objectIn.readObject();
        objectIn.close();

        observedGraphicObjects = FXCollections.observableArrayList(graphicObjects);
        observedGraphicObjects.addListener((ListChangeListener<GraphicObject>) change -> objectFrame.updateList(graphicObjects));
        if (objectFrame != null) {
            objectFrame.updateList(graphicObjects);
        }
        for (GraphicObject graphicObject : graphicObjects) {
            trimToPreferredSize(graphicObject);
        }
        actionHandler.setDefault(this.graphicObjects);
        repaint();
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void pasteObject(GraphicObject copyObject) {
        boolean check = false;
        int i = 0;
        GraphicObject newObject = null;
        while (!check && i != graphicObjects.size()) {
            if (graphicObjects.get(i).getX() == copyObject.getX() &&
                    graphicObjects.get(i).getY() == copyObject.getY() &&
                    graphicObjects.get(i).getName().equals(copyObject.getName())) {
                check = true;
            }
            i++;
        }

        if (check) {
            try {
                newObject = getPasteObject(copyObject.clone());
            } catch (Exception e) {
                Logger.jlogger.warning(Logger.stackTraceToString(e));
                Logger.jlogger.warning("Cloning object failed!");
            }
        } else {
            newObject = copyObject;
        }

        assert newObject != null;
        newObject.setNewObjectNumber();
        addComponent(newObject);
        trimToPreferredSize(newObject);
        repaint();
        objectFrame.setSelectedComponent(graphicObjects.getLast());
    }

    private GraphicObject getPasteObject(GraphicObject pasteObject) {
        for (GraphicObject graphicObject : graphicObjects) {
            if (graphicObject.getX() == pasteObject.getX() && graphicObject.getY() == pasteObject.getY() &&
                    graphicObject.getName().equals(pasteObject.getName())) {
                pasteObject.setCords(pasteObject.getX() + 10, pasteObject.getY() + 10);
                return getPasteObject(pasteObject);
            }
        }
        return pasteObject;
    }

    public void storeEdit() {
        actionHandler.addStackState(graphicObjects);
    }

    public void reload(ToolBarPanel toolbarPanel) {
        actionHandler.setToolbarPanel(toolbarPanel);
        boolean[] states = actionHandler.getStateOfButtons();
        toolbarPanel.buttonChangeState(1, 0, states[0]);
        toolbarPanel.buttonChangeState(1, 1, states[1]);
        objectFrame.reload();
    }

    public void redo() {
        graphicObjects = actionHandler.redoChange();
        objectFrame.updateList(graphicObjects);
        observedGraphicObjects.addAll(graphicObjects);
        repaint();
    }

    public void undo() {
        graphicObjects = actionHandler.undoChange();
        objectFrame.updateList(graphicObjects);
        observedGraphicObjects.addAll(graphicObjects);
        repaint();
    }

    public void setToolbarPanel(ToolBarPanel toolbarPanel) {
        actionHandler = new ActionHandler(toolbarPanel);
    }

    public void clearLists() {
        graphicObjects.clear();
        observedGraphicObjects.clear();
    }

    public void setList(LinkedList<GraphicObject> saveList) {
        clearLists();
        graphicObjects.addAll(saveList);
        observedGraphicObjects.addAll(saveList);
    }

    public boolean isShiftPressed() {
        return isShiftPressed;
    }

    public void setBackgroundImage() {
        addComponent(new Picture(0, 0, this.getWidth(), this.getHeight(),
                new Color(Color.TRANSLUCENT), new Color(Color.TRANSLUCENT), 0));
        FileChooser chooser = new FileChooser();
        chooser.setDialogTitle(Config.bundle.getString("OpenFile"));
        chooser.showOpenDialog(this);
        BufferedImage image;
        try {
            if (chooser.getSelectedFile() == null) {
                throw new IOException();
            }
            image = ImageIO.read(chooser.getSelectedFile());
            ((Picture) this.getGraphicObjects().getLast()).setImage(image); //for repaint
            ((Picture) this.getGraphicObjects().getLast()).
                    setFilePath(chooser.getSelectedFile().getAbsolutePath());
            this.trimToPreferredSize(this.getGraphicObjects().getLast());
            this.getObjectFrame().setSelectedComponent(this.getGraphicObjects().getLast());
        } catch (IOException ioException) {
            this.removeComponent(this.getGraphicObjects().size() - 1);
        }
        this.repaint();
    }
}
