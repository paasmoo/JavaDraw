/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.toolbar;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.gui.MainFrame;
import at.paasmoo.utils.MODE;
import at.paasmoo.utils.MouseAdapt;
import at.paasmoo.utils.MouseMotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Objects;

public class ToolBarPanel extends JPanel implements ActionListener {
    private final MainFrame frame;
    private final MouseAdapt mouseAdapt;
    private final MouseMotion mouseMotion;

    private final String[] fileButtonsSrc = {"open", "new", "saveas", "save", "graphic_template", "export"};
    private final String[] fileButtonsTooltips = {"OpenFile", "CreateNewFile", "SaveFileAs", "SaveFile",
            "SetBackgroundImage", "ExportFile", "ExportFile"};

    private final String[] moveButtonsSrc = {"undo", "redo", "delete"};
    private final String[] moveButtonsTooltips = {"UndoChange", "RedoChange", "DeleteElement"};

    private final String[] drawButtonsSrc = {"arrow", "free_line", "straight_line",
            "rectangle", "ellipse", "font", "picture",};
    private final String[] drawButtonsTooltips = {"SelectAndMove", "FreeLine", "StraightLine",
            "Rectangle", "Ellipse", "Wordfield", "Image",};

    private final String[] editButtonsSrc = {"pen_color", "fill_color", "line_width"};
    private final String[] editButtonsTooltips = {"ChangeLineColor", "ChangeFillColor", "ChangeLineWidth"};

    private final String[] animationButtonsSrc = {"play", "stop"};
    private final String[] animationButtonsTooltips = {"StartAnimation", "StopAnimation"};

    private JToolBar fileBar;
    private JToolBar moveBar;
    private JToolBar drawBar;
    private JToolBar editBar;
    private JToolBar animationBar;
    private JButton save;

    private int buttonPressedIndex;

    private static final Object syncOb = new Object();
    private Thread animationThread;
    private boolean[] states;
    private LinkedList<GraphicObject> saveList;
    public int animationTime;

    public ToolBarPanel(MouseAdapt mouseAdapt, MouseMotion mouseMotion, MainFrame frame, int anTime) {
        this.frame = frame;
        this.mouseAdapt = mouseAdapt;
        this.mouseMotion = mouseMotion;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        buttonPressedIndex = 0;
        setButtons();
        buttonChangeState(4, 1, false);

        mouseAdapt.setMode(MODE.SELECT);
        mouseMotion.setMode(MODE.SELECT);

        animationTime = anTime;
    }

    private void setButtons() {
        ToolBarButton jButton;
        Icon icon;

        // new file buttons, etc.
        fileBar = new JToolBar();
        for (int i = 0; i != fileButtonsSrc.length; i++) {
            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(fileButtonsSrc[i] + ".png")));

            jButton = new ToolBarButton(icon, fileButtonsSrc[i]);
            jButton.addActionListener(this);
            jButton.setToolTipText(Config.bundle.getString(fileButtonsTooltips[i]));
            jButton.setFocusPainted(false);
            fileBar.add(jButton);
            if (fileButtonsSrc[i].equals("save")) {
                jButton.setEnabled(false);
                save = jButton;
            }
        }
        add(fileBar);

        // undo redo and delete button
        moveBar = new JToolBar();
        for (int i = 0; i != moveButtonsSrc.length; i++) {
            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(moveButtonsSrc[i] + ".png")));

            jButton = new ToolBarButton(icon, moveButtonsSrc[i]);
            jButton.addActionListener(this);
            jButton.setToolTipText(Config.bundle.getString(moveButtonsTooltips[i]));
            jButton.setEnabled(i == 2);
            jButton.setFocusPainted(false);
            moveBar.add(jButton);
        }
        add(moveBar);

        // drawing lines, rectangle, etc.
        drawBar = new JToolBar();
        for (int i = 0; i != drawButtonsSrc.length; i++) {
            if (i == 0) {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("pressed_" + drawButtonsSrc[i] + ".png")));
            } else {
                icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(drawButtonsSrc[i] + ".png")));
            }
            jButton = new ToolBarButton(icon, drawButtonsSrc[i]);
            jButton.addActionListener(this);
            jButton.setToolTipText(Config.bundle.getString(drawButtonsTooltips[i]));
            jButton.setFocusPainted(false);
            drawBar.add(jButton);
        }
        add(drawBar);

        // editing objects
        editBar = new JToolBar();
        for (int i = 0; i != editButtonsSrc.length; i++) {
            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().
                    getResource(editButtonsSrc[i] + ".png")));

            jButton = new ToolBarButton(icon, editButtonsSrc[i]);
            jButton.addActionListener(this);
            jButton.setToolTipText(Config.bundle.getString(editButtonsTooltips[i]));
            jButton.setFocusPainted(false);
            editBar.add(jButton);
        }
        add(editBar);

        // animation buttons
        animationBar = new JToolBar();
        for (int i = 0; i != animationButtonsSrc.length; i++) {
            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(animationButtonsSrc[i] + ".png")));

            jButton = new ToolBarButton(icon, animationButtonsSrc[i]);
            jButton.addActionListener(this);
            jButton.setToolTipText(Config.bundle.getString(animationButtonsTooltips[i]));
            jButton.setFocusPainted(false);
            animationBar.add(jButton);
        }
        add(animationBar);
    }

    public void setSaveButtonEnable(Boolean state) {
        save.setEnabled(state);
    }

    private void createDialogNewFileWarning() {
        int userChoice = JOptionPane.showConfirmDialog(frame,
                new String[]{Config.bundle.getString("SaveCheck"),
                        Config.bundle.getString("SaveWarning")},
                Config.bundle.getString("NewProjectCheck"), JOptionPane.YES_NO_OPTION);

        if (userChoice == 0) {
            interruptAnimation();
            frame.getGraphicPanel().reset();
            frame.setTitle("JavaDraw - Pamer -" + Config.bundle.getString("Unnamed"));
            buttonChangeState(0, 3, false);
        }
    }

    private void createDialogBackgroundImgWarning() {
        int userChoice = JOptionPane.showConfirmDialog(frame,
                new String[]{Config.bundle.getString("SaveCheck"),
                        Config.bundle.getString("SaveWarning")},
                Config.bundle.getString("NewProjectCheck"), JOptionPane.YES_NO_OPTION);

        if (userChoice == 0) {
            interruptAnimation();
            frame.getGraphicPanel().reset();
            frame.setTitle("JavaDraw - Pamer - " + Config.bundle.getString("Unnamed"));
            buttonChangeState(0, 3, false);
            frame.getGraphicPanel().setBackgroundImage();
        }
    }

    public void playAnimation() {
        prepareForAnimation();
        animationThread = new Thread(() -> {
            synchronized (syncOb) {
                saveList = new LinkedList<>(frame.getGraphicPanel().getGraphicObjects());
                frame.getGraphicPanel().clearLists();
                frame.getGraphicPanel().prepareAnimation();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                int i = 0;
                while (saveList.size() != i && frame.getGraphicPanel().isAnimating()) {
                    frame.getGraphicPanel().addComponent(saveList.get(i));
                    try {
                        frame.getGraphicPanel().repaint();
                    } catch (Exception ignored) {
                    }

                    if (saveList.size() - 1 != i) {
                        try {
                            Thread.sleep(animationTime);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    i++;
                }
                frame.getGraphicPanel().finishAnimation();
                ToolBarPanel.this.finishAnimation();
            }
        });
        animationThread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ("open"):
                interruptAnimation();
                frame.load();
                break;
            case ("new"):
                createDialogNewFileWarning();
                break;
            case ("saveas"):
                interruptAnimation();
                frame.getGraphicPanel().setFile(null);
                frame.saveAs();
                break;
            case ("save"):
                interruptAnimation();
                frame.save();
                break;
            case ("graphic_template"):
                createDialogBackgroundImgWarning();
                break;
            case ("export"):
                interruptAnimation();
                frame.export();
                break;
            case ("undo"):
                if (!mouseAdapt.isMouseEditing())
                    frame.getGraphicPanel().undo();
                break;
            case ("redo"):
                if (!mouseAdapt.isMouseEditing())
                    frame.getGraphicPanel().redo();
                break;
            case ("delete"):
                if (frame.getGraphicPanel().getObjectFrame().removeSelected() && !mouseAdapt.isMouseEditing())
                    frame.getGraphicPanel().storeEdit();
                break;
            case ("arrow"):
                setModeOfMouse(MODE.SELECT);
                break;
            case ("free_line"):
                setModeOfMouse(MODE.LINE);
                break;
            case ("straight_line"):
                setModeOfMouse(MODE.STRAIGHTLINE);
                break;
            case ("rectangle"):
                setModeOfMouse(MODE.RECTANGLE);
                break;
            case ("ellipse"):
                setModeOfMouse(MODE.ELLIPSE);
                break;
            case ("font"):
                setModeOfMouse(MODE.WORDFIELD);
                break;
            case ("picture"):
                setModeOfMouse(MODE.IMAGE);
                break;
            case ("pen_color"):
                createPenColorDialog();
                break;
            case ("fill_color"):
                createFillColorDialog();
                break;
            case ("line_width"):
                createLineWidthDialog();
                break;
            case ("play"):
                playAnimation();
                break;
            case ("stop"):
                interruptAnimation();
                break;
        }
    }

    public void createPenColorDialog() {
        Color color = mouseAdapt.getColor();
        Color newColor = JColorChooser.showDialog(this, Config.bundle.getString("LineColor"), color);
        if (newColor != null) {
            mouseAdapt.setColor(newColor);
        }
    }

    public void createFillColorDialog() {
        Color fillColor = mouseAdapt.getFillColor();
        Color newFillColor = JColorChooser.showDialog(this, Config.bundle.getString("FillColor"), fillColor);
        if (newFillColor != null) {
            mouseAdapt.setFillColor(newFillColor);
        }
    }

    public void createLineWidthDialog() {
        frame.setEnabled(false);
        int strokeWidth = mouseAdapt.getStrokeWidth();
        new StrokeSetting(frame, mouseAdapt, strokeWidth);
    }

    public void setModeOfMouse(MODE mode) {
        int index = frame.getIndexMODE(mode);
        Icon icon;

        JButton jButton = (JButton) drawBar.getComponentAtIndex(index);
        if (buttonPressedIndex != index) {
            JButton pressedJButton = (JButton) drawBar.getComponentAtIndex(buttonPressedIndex);
            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(drawButtonsSrc[buttonPressedIndex] + ".png")));
            pressedJButton.setIcon(icon);

            icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("pressed_" + drawButtonsSrc[index] + ".png")));
            jButton.setIcon(icon);
            buttonPressedIndex = index;
        }
        frame.setToolButtonsIndex(mode);

        mouseAdapt.setMode(mode);
        mouseMotion.setMode(mode);
    }

    public void buttonChangeState(int bar, int button, boolean state) {
        switch (bar) {
            case (0):
                fileBar.getComponentAtIndex(button).setEnabled(state);
                break;
            case (1):
                moveBar.getComponentAtIndex(button).setEnabled(state);
                break;
            case (2):
                drawBar.getComponentAtIndex(button).setEnabled(state);
                break;
            case (3):
                editBar.getComponentAtIndex(button).setEnabled(state);
            case (4):
                animationBar.getComponentAtIndex(button).setEnabled(state);
                break;
        }
    }

    public MODE getMode() {
        return switch (buttonPressedIndex) {
            case 0 -> MODE.SELECT;
            case 1 -> MODE.LINE;
            case 2 -> MODE.STRAIGHTLINE;
            case 3 -> MODE.RECTANGLE;
            case 4 -> MODE.ELLIPSE;
            case 5 -> MODE.WORDFIELD;
            case 6 -> MODE.IMAGE;
            default -> null;
        };
    }

    private void prepareForAnimation() {
        for (int i = 0; i != drawButtonsSrc.length; i++) {
            drawBar.getComponentAtIndex(i).setEnabled(false);
        }

        states = new boolean[]{moveBar.getComponentAtIndex(0).isEnabled(),
                moveBar.getComponentAtIndex(1).isEnabled(),
                moveBar.getComponentAtIndex(2).isEnabled()};

        for (int i = 0; i != states.length; i++) {
            moveBar.getComponentAtIndex(i).setEnabled(false);
        }

        frame.setResizable(false);
        frame.getGraphicPanel().getObjectFrame().setResizable(false);
        buttonChangeState(4, 0, false);
        buttonChangeState(4, 1, true);
    }

    private void finishAnimation() {
        for (int i = 0; i != drawButtonsSrc.length; i++) {
            drawBar.getComponentAtIndex(i).setEnabled(true);
        }
        for (int i = 0; i != states.length; i++) {
            moveBar.getComponentAtIndex(i).setEnabled(states[i]);
        }

        frame.setResizable(true);
        frame.getGraphicPanel().getObjectFrame().setResizable(true);
        buttonChangeState(4, 0, true);
        buttonChangeState(4, 1, false);
    }

    public void interruptAnimation() {
        if (frame.getGraphicPanel().isAnimating()) {
            frame.getGraphicPanel().setIsAnimating(false);
            if (!animationThread.isInterrupted()) {
                animationThread.interrupt();
            }
            frame.getGraphicPanel().setList(saveList);
        }
    }
}