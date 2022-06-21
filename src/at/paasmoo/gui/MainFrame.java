/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.gui.drawingspace.GraphicPanel;
import at.paasmoo.gui.toolbar.ToolBarPanel;
import at.paasmoo.utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame implements ActionListener {
    private final GraphicPanel graphicPanel;
    private ToolBarPanel toolbarPanel;
    private final MouseMotion mouseMotion;
    private final MouseAdapt mouseAdapt;
    private final Preferences prefs = Config.getPrefs();

    private GraphicObject copyComponent;

    private ArrayList<JRadioButtonMenuItem> toolButtons;
    private ArrayList<JRadioButtonMenuItem> langButtons;

    private JMenuItem saveAs;
    private JMenuItem save;
    public JMenuItem animationStart;
    public JMenuItem animationStop;

    private JCheckBoxMenuItem showObjectFrame;
    private JCheckBoxMenuItem dockObjectFrame;

    private boolean outdated;
    public boolean dock;

    public MainFrame() {
        super();

        new Logger("log.txt");

        outdated = false;

        ImageIcon im = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")));
        setIconImage(im.getImage());

        setMainFrameSettings();
        setLanguage();

        setTitle("JavaDraw - " + Config.bundle.getString("Unnamed"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        graphicPanel = new GraphicPanel(this);
        JScrollPane scrollpane = new JScrollPane(graphicPanel);
        contentPane.add(scrollpane);

        mouseAdapt = new MouseAdapt(graphicPanel);
        mouseMotion = new MouseMotion(graphicPanel);
        graphicPanel.addMouseListener(mouseAdapt);
        graphicPanel.addMouseMotionListener(mouseMotion);

        toolbarPanel = new ToolBarPanel(mouseAdapt, mouseMotion, this, prefs.getInt("AnimationTime", 750));
        scrollpane = new JScrollPane(toolbarPanel);
        contentPane.add(scrollpane, BorderLayout.NORTH);

        graphicPanel.setToolbarPanel(toolbarPanel);

        toolButtons = new ArrayList<>(7);
        langButtons = new ArrayList<>(3);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createToolsMenu());
        menuBar.add(createAnimateMenu());
        menuBar.add(createWindowsMenu());
        menuBar.add(createHelpMenu());
        setJMenuBar(menuBar);

        localizeExternalUI();

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                if (dock) {
                    Component c = (Component) evt.getSource();
                    graphicPanel.getObjectFrame().trimToSize(c.getHeight());
                }
            }

            public void componentMoved(ComponentEvent evt) {
                super.componentMoved(evt);
                if (dock) {
                    Component c = (Component) evt.getSource();
                    graphicPanel.getObjectFrame().moveWindow(c.getX(), c.getY());
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                storePreferences();
            } catch (BackingStoreException ex) {
                Logger.jlogger.warning(Logger.stackTraceToString(ex));
                Logger.jlogger.warning("Preferences are invalid!");
            }
        }));
    }

    private void checkVersion() {
        try {
            URL url = new URL("https://gist.githubusercontent.com/paasmoo/59ee1eaae02baf516fed7dc437e71fcf/raw/f936510cf924d1d315219d6c9448f03244da271c/javadrawversion");
            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();

            while (sc.hasNext()) {
                sb.append(sc.next());
            }
            String result = sb.toString();
            if (!result.equals(Config.version)) {
                Logger.jlogger.info("You are running an older version of JavaDraw. [" + Config.version + "->" + result + "]");
                createOutdatedDialog(result);
                outdated = true;
            }
        } catch (IOException ignored) {
        }
    }

    private void createOutdatedDialog(String webVersion) {
        String msg = Config.bundle.getString("InvalidVersion")  + " (" + Config.version + " -> " + webVersion + ")";
        JOptionPane.showMessageDialog(this,
                msg,
                Config.bundle.getString("Warning"),
                JOptionPane.WARNING_MESSAGE);
    }

    private void setMainFrameSettings() {
        setMinimumSize(new Dimension(1050, 500));
        setBounds(prefs.getInt("X_Pos", 0)  , prefs.getInt("Y_Pos", 0),
                prefs.getInt("Width", 800), prefs.getInt("Height", 600));
        if (prefs.getInt("X_Pos", -1) == -1) {
            setLocationRelativeTo(null);
        }

        dock = prefs.getBoolean("Dock", true);
    }

    private void setLanguage() {
        Config.setNewLanguage(prefs.get("Language", "en"));
    }

    // File-Menu
    private JMenu createFileMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("File"));
        menu.setMnemonic(Config.bundle.getString("File").charAt(0));

        JMenuItem item;
        // New
        item = new JMenuItem(Config.bundle.getString("New"), Config.bundle.getString("New").charAt(0));
        setCtrlAccelerator(item, 'N');
        item.addActionListener(this);
        item.setActionCommand("New");
        menu.add(item);
        // Open
        item = new JMenuItem(Config.bundle.getString("Open"), Config.bundle.getString("Open").charAt(0));
        setCtrlAccelerator(item, 'O');
        item.addActionListener(this);
        item.setActionCommand("Open");
        menu.add(item);
        // Save as
        saveAs = new JMenuItem(Config.bundle.getString("SaveAs"), Config.bundle.getString("SaveAs").charAt(0));
        setCtrlAccelerator(saveAs, 'S');
        saveAs.addActionListener(this);
        saveAs.setActionCommand("SaveAs");
        menu.add(saveAs);
        // Save
        save = new JMenuItem(Config.bundle.getString("Save"), Config.bundle.getString("Save").charAt(0));
        save.addActionListener(this);
        save.setEnabled(false);
        save.setActionCommand("Save");
        menu.add(save);
        // Export
        item = new JMenuItem(Config.bundle.getString("Export"), Config.bundle.getString("Export").charAt(0));
        setCtrlAccelerator(item, 'E');
        item.addActionListener(this);
        item.setActionCommand("Export");
        menu.add(item);
        // Print
        item = new JMenuItem(Config.bundle.getString("Print"), Config.bundle.getString("Print").charAt(0));
        setCtrlAccelerator(item, 'P');
        item.addActionListener(this);
        item.setActionCommand("Print");
        menu.add(item);
        // Separator
        menu.addSeparator();
        // Exit
        item = new JMenuItem(Config.bundle.getString("Close"), Config.bundle.getString("Close").charAt(0));
        item.addActionListener(this);
        setCtrlAccelerator(item, 'Q');
        item.setActionCommand("Close");
        menu.add(item);

        return menu;
    }

    // Edit-Menu
    private JMenu createEditMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("Edit"));
        menu.setMnemonic(Config.bundle.getString("Edit").charAt(0));

        JMenuItem item;
        // Copy
        item = new JMenuItem(Config.bundle.getString("Copy"), Config.bundle.getString("Copy").charAt(0));
        setCtrlAccelerator(item, 'C');
        item.addActionListener(this);
        item.setActionCommand("Copy");
        menu.add(item);
        // Cut
        item = new JMenuItem(Config.bundle.getString("Cut"), Config.bundle.getString("Cut").charAt(0));
        setCtrlAccelerator(item, 'X');
        item.addActionListener(this);
        item.setActionCommand("Cut");
        menu.add(item);
        // Paste
        item = new JMenuItem(Config.bundle.getString("Paste"), Config.bundle.getString("Paste").charAt(0));
        setCtrlAccelerator(item, 'V');
        item.addActionListener(this);
        item.setActionCommand("Paste");
        menu.add(item);
        // Undo
        item = new JMenuItem(Config.bundle.getString("Undo"), Config.bundle.getString("Undo").charAt(0));
        setCtrlAccelerator(item, 'Z');
        item.addActionListener(this);
        item.setActionCommand("Undo");
        menu.add(item);
        // Redo
        item = new JMenuItem(Config.bundle.getString("Redo"), Config.bundle.getString("Redo").charAt(0));
        setCtrlAccelerator(item, 'Y');
        item.addActionListener(this);
        item.setActionCommand("Redo");
        menu.add(item);
        // Delete
        item = new JMenuItem(Config.bundle.getString("Delete"), Config.bundle.getString("Delete").charAt(0));
        setCtrlAccelerator(item, 'D');
        item.addActionListener(this);
        item.setActionCommand("Delete");
        menu.add(item);

        return menu;
    }

    // Tools-Menu
    private JMenu createToolsMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("Tools"));
        menu.setMnemonic(Config.bundle.getString("Tools").charAt(0));

        JMenuItem item;

        // Tool buttons
        String[] tools = {Config.bundle.getString("Select"), Config.bundle.getString("FreeLine"),
                Config.bundle.getString("StraightLine"), Config.bundle.getString("Rectangle"),
                Config.bundle.getString("Ellipse"), Config.bundle.getString("Wordfield"),
                Config.bundle.getString("Image")};
        ButtonGroup buttonGroup = new ButtonGroup();
        for (String tool : tools) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(tool);
            if (tool.equals(Config.bundle.getString("Select"))) {
                button.setSelected(true);
            }
            button.addActionListener(this);
            button.setActionCommand(searchKeys(Config.bundle, tool));
            toolButtons.add(button);
            buttonGroup.add(button);
            menu.add(button);
        }

        // Separator
        menu.addSeparator();

        JMenu colormenu = new JMenu(Config.bundle.getString("ColorChange"));
        colormenu.setMnemonic(Config.bundle.getString("ColorChange").charAt(0));

        item = new JMenuItem(Config.bundle.getString("LineColor"), Config.bundle.getString("LineColor").charAt(0));
        item.addActionListener(this);
        item.setActionCommand("ChangeLC");
        colormenu.add(item);

        item = new JMenuItem(Config.bundle.getString("FillColor"), Config.bundle.getString("FillColor").charAt(0));
        item.addActionListener(this);
        item.setActionCommand("ChangeFC");
        colormenu.add(item);

        menu.add(colormenu);

        item = new JMenuItem(Config.bundle.getString("ChangeLineWidth"), Config.bundle.getString("ChangeLineWidth").charAt(0));
        item.addActionListener(this);
        item.setActionCommand("ChangeLW");
        menu.add(item);

        return menu;
    }

    private JMenu createAnimateMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("Animation"));
        menu.setMnemonic(Config.bundle.getString("Animation").charAt(0));
        JMenuItem item;

        // Start animation
        animationStart = new JMenuItem(Config.bundle.getString("StartAnimation"), Config.bundle.getString("StartAnimation").charAt(0));
        animationStart.addActionListener(this);
        animationStart.setActionCommand("StartAnimation");
        menu.add(animationStart);
        animationStart.setEnabled(true);

        // Stop animation
        animationStop = new JMenuItem(Config.bundle.getString("StopAnimation"), Config.bundle.getString("StopAnimation").charAt(0));
        animationStop.addActionListener(this);
        animationStop.setActionCommand("StopAnimation");
        menu.add(animationStop);
        animationStop.setEnabled(false);

        // Separator
        menu.addSeparator();

        // Change animation time
        item = new JMenuItem(Config.bundle.getString("AnimationTimeChange"), Config.bundle.getString("AnimationTimeChange").charAt(0));
        item.addActionListener(this);
        item.setActionCommand("AnimationChange");
        menu.add(item);

        return menu;
    }

    // Windows-Menu
    private JMenu createWindowsMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("Windows"));
        menu.setMnemonic(Config.bundle.getString("Windows").charAt(0));

        JMenu objectsSettings = new JMenu(Config.bundle.getString("Objects"));
        objectsSettings.setMnemonic(Config.bundle.getString("Objects").charAt(0));

        // Show ObjectFrame
        showObjectFrame = new JCheckBoxMenuItem(Config.bundle.getString("Visible"));
        showObjectFrame.setMnemonic(Config.bundle.getString("Visible").charAt(0));
        showObjectFrame.addActionListener(this);
        showObjectFrame.setActionCommand("ShowObjectFrame");
        objectsSettings.add(showObjectFrame);
        showObjectFrame.setState(graphicPanel.getObjectFrame().isVisibility());
        // Dock ObjectFrame
        dockObjectFrame = new JCheckBoxMenuItem(Config.bundle.getString("Dock"));
        dockObjectFrame.setMnemonic(Config.bundle.getString("Dock").charAt(0));
        dockObjectFrame.addActionListener(this);
        dockObjectFrame.setActionCommand("DockObjectFrame");
        objectsSettings.add(dockObjectFrame);
        dockObjectFrame.setState(dock);

        menu.add(objectsSettings);
        return menu;
    }

    // Help-Menu
    private JMenu createHelpMenu() {
        JMenu menu = new JMenu(Config.bundle.getString("Help"));
        menu.setMnemonic(Config.bundle.getString("Help").charAt(0));

        JMenuItem item;
        // About
        item = new JMenuItem(Config.bundle.getString("About"), Config.bundle.getString("About").charAt(0));
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        item.setActionCommand("About");
        menu.add(item);

        // Language buttons
        String[] languages = {"English", "Deutsch", "Östareichisch", "Français", "Italiano", "Hangug-in"};
        ButtonGroup buttonGroup = new ButtonGroup();
        JMenu langMenu = new JMenu(Config.bundle.getString("LanguageSelect"));
        langMenu.setMnemonic(Config.bundle.getString("LanguageSelect").charAt(0));
        for (String lang : languages) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(lang);
            button.setMnemonic(lang.charAt(0));
            if (lang.equals(Config.getLanguage())) {
                button.setSelected(true);
            }
            button.addActionListener(this);
            // Set the action command to French because there was a problem with some characters
            if(lang.equals("Français")) {
                button.setActionCommand("French");
            } else {
                button.setActionCommand(searchKeys(Config.bundle, lang));
            }
            langButtons.add(button);
            buttonGroup.add(button);
            langMenu.add(button);
        }
        menu.add(langMenu);

        return menu;
    }

    // Search Resource Bundle for specific Key
    private static String searchKeys(ResourceBundle bundle, String value) {
        Enumeration<String> keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(bundle.getString(key).equals(value)) {
                return key;
            }
        }
        return null;
    }

    public void setShowObjectFrameButtonState(Boolean state) {
        showObjectFrame.setState(state);
    }

    public void setSaveButtonState(Boolean state) {
        save.setEnabled(state);
        toolbarPanel.setSaveButtonEnable(state);
    }

    public int getIndexMODE(MODE mode) {
        int index = switch (mode) {
            case SELECT -> 0;
            case LINE -> 1;
            case STRAIGHTLINE -> 2;
            case RECTANGLE -> 3;
            case ELLIPSE -> 4;
            case WORDFIELD -> 5;
            case IMAGE -> 6;
        };
        return index;
    }

    public void setToolButtonsIndex(MODE mode) {
        getIndexMODE(mode);
        toolButtons.get(getIndexMODE(mode)).setSelected(true);
    }

    public void setLanguageButtonsIndex(String lang) {
        int index = switch (lang) {
            case "en" -> 0;
            case "de" -> 1;
            case "at" -> 2;
            case "fr" -> 3;
            case "it" -> 4;
            case "kr" -> 5;
            default -> -1;
        };
        langButtons.get(index).setSelected(true);
    }

    private void setCtrlAccelerator(JMenuItem item, char acc) {
        KeyStroke ks = KeyStroke.getKeyStroke(acc, InputEvent.CTRL_MASK);
        item.setAccelerator(ks);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch(action) {
            case "New":
                createNewDialog();
                break;
            case "Open":
                load();
                break;
            case "SaveAs":
                saveAs();
                break;
            case "Save":
                save();
                break;
            case "Export":
                export();
                break;
            case "Print":
                createPrintDialog();
                break;
            case "Close":
                System.exit(0);
                break;
            case "Copy":
                if (graphicPanel.getObjectFrame().getSelectedComponent() != null) {
                    try {
                        copyComponent = graphicPanel.getObjectFrame().getSelectedComponent().clone();
                    } catch (CloneNotSupportedException exception) {
                        Logger.jlogger.warning(Logger.stackTraceToString(exception));
                        Logger.jlogger.warning("Cloning object failed!");
                    }
                }
                break;
            case "Cut":
                if (graphicPanel.getObjectFrame().getSelectedComponent() != null && !mouseAdapt.isMouseEditing() && !graphicPanel.isAnimating()) {
                    try {
                        copyComponent = graphicPanel.getObjectFrame().getSelectedComponent().clone();
                    } catch (CloneNotSupportedException eC) {
                        Logger.jlogger.warning(Logger.stackTraceToString(eC));
                        Logger.jlogger.warning("Cloning object failed!");
                    }
                    graphicPanel.getObjectFrame().removeSelected();
                    graphicPanel.storeEdit();
                }
                break;
            case "Paste":
                if (copyComponent != null && !mouseAdapt.isMouseEditing() && !graphicPanel.isAnimating()) {
                    graphicPanel.pasteObject(copyComponent);
                    graphicPanel.storeEdit();
                }
                break;
            case "Undo":
                if (!mouseAdapt.isMouseEditing() && !graphicPanel.isAnimating()) {
                    getGraphicPanel().undo();
                }
                break;
            case "Redo":
                if (!mouseAdapt.isMouseEditing() && !graphicPanel.isAnimating()) {
                    getGraphicPanel().redo();
                }
                break;
            case "Delete":
                if (getGraphicPanel().getObjectFrame().removeSelected() && !mouseAdapt.isMouseEditing() && !graphicPanel.isAnimating()) {
                    getGraphicPanel().storeEdit();
                }
                break;
            case "Select":
                toolbarPanel.setModeOfMouse(MODE.SELECT);
                break;
            case "FreeLine":
                toolbarPanel.setModeOfMouse(MODE.LINE);
                break;
            case "StraightLine":
                toolbarPanel.setModeOfMouse(MODE.STRAIGHTLINE);
                break;
            case "Rectangle":
                toolbarPanel.setModeOfMouse(MODE.RECTANGLE);
                break;
            case "Ellipse":
                toolbarPanel.setModeOfMouse(MODE.ELLIPSE);
                break;
            case "Wordfield":
                toolbarPanel.setModeOfMouse(MODE.WORDFIELD);
                break;
            case "Image":
                toolbarPanel.setModeOfMouse(MODE.IMAGE);
                break;
            case "ShowObjectFrame":
                graphicPanel.getObjectFrame().setVisible(!graphicPanel.getObjectFrame().isVisibility());
                break;
            case "DockObjectFrame":
                if (dock) {
                    dock = false;
                    dockObjectFrame.setState(false);
                } else {
                    dock = true;
                    graphicPanel.getObjectFrame().moveWindow(this.getX(), this.getY());
                    graphicPanel.getObjectFrame().trimToSize(this.getHeight());
                    dockObjectFrame.setState(true);
                }
                break;
            case "About":
                createAboutDialog();
                break;
            case "Deutsch":
                switchLanguage("de");
                break;
            case "Östareichisch":
                switchLanguage("at");
                break;
            case "English":
                switchLanguage("en");
                break;
            case "French":
                switchLanguage("fr");
                break;
            case "Italiano":
                switchLanguage("it");
                break;
            case "Hangug-in":
                switchLanguage("kr");
                break;
            case "AnimationChange":
                createAnimationTimeDialog();
                break;
            case "ChangeLC":
                toolbarPanel.createPenColorDialog();
                break;
            case "ChangeLW":
                toolbarPanel.createLineWidthDialog();
                break;
            case "ChangeFC":
                toolbarPanel.createFillColorDialog();
                break;
            case "StartAnimation":
                toolbarPanel.playAnimation();
                break;
            case "StopAnimation":
                toolbarPanel.interruptAnimation();
                break;
            default:
                Logger.jlogger.warning("Unknown action! [" + action + "]");
        }
    }

    private void createNewDialog() {
        int userChoice = JOptionPane.showConfirmDialog(this,
                new String[]{Config.bundle.getString("SaveCheck"),
                        Config.bundle.getString("SaveWarning")},
                Config.bundle.getString("NewProjectCheck"), JOptionPane.YES_NO_OPTION);
        if (userChoice == 0) {
            toolbarPanel.interruptAnimation();
            save.setEnabled(false);
            saveAs.setEnabled(true);
            graphicPanel.reset();
            this.setTitle("JavaDraw - " + Config.bundle.getString("Unnamed"));
            toolbarPanel.buttonChangeState(0, 3, false);
        }
    }

    private void switchLanguage(String lang) {
        toolbarPanel.interruptAnimation();
        Config.setNewLanguage(lang);
        reinitializeFrame();
        setLanguageButtonsIndex(lang);
    }

    private void createPrintDialog() {
        toolbarPanel.interruptAnimation();
        int w = graphicPanel.getWidth();
        int h = graphicPanel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        graphicPanel.paint(g);
        g.dispose();
        new Thread(new PrintActionListener(bi)).start();
    }

    private void createAboutDialog() {
        String title = Config.bundle.getString("About");
        String text;

        if(outdated) {
            text = Config.getCopyright() + "\n\n" + Config.bundle.getString("NewVersionAvailable");
        } else {
            text = Config.getCopyright();
        }

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")));
        JOptionPane.showMessageDialog(
                        this, text,
                        title, JOptionPane.INFORMATION_MESSAGE, icon);
    }

    public void createAnimationTimeDialog() {
        try {
            int oldAnimtime = toolbarPanel.animationTime;
            String userInput = (String) JOptionPane.showInputDialog(this,
                    Config.bundle.getString("AnimationDialogText"),
                    Config.bundle.getString("AnimationDialogTitle"),
                    JOptionPane.PLAIN_MESSAGE,
                    null, null,
                    toolbarPanel.animationTime);
            if (userInput != null && userInput.length() > 0) {
                toolbarPanel.animationTime = Integer.parseInt(userInput);
            }
            if (toolbarPanel.animationTime < 0) {
                toolbarPanel.animationTime = oldAnimtime;
                throw new IllegalArgumentException("Time is negative");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    Config.bundle.getString("InvalidValue"),
                    Config.bundle.getString("Warning"),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public void reinitializeFrame() {
        if (graphicPanel.getFile() == null) {
            setTitle("JavaDraw - " + Config.bundle.getString("Unnamed"));
        }
        setContentPane(new Container());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        MODE oldMode = toolbarPanel.getMode();

        for (GraphicObject drawComponent : graphicPanel.getGraphicObjects()) {
            drawComponent.refreshName();
        }

        toolbarPanel = new ToolBarPanel(mouseAdapt, mouseMotion, this, toolbarPanel.animationTime);

        graphicPanel.reload(toolbarPanel);
        JScrollPane scrollpane = new JScrollPane(graphicPanel);
        contentPane.add(scrollpane);

        scrollpane = new JScrollPane(toolbarPanel);
        contentPane.add(scrollpane, BorderLayout.NORTH);

        toolbarPanel.setModeOfMouse(oldMode);

        toolButtons = new ArrayList<>(7);
        langButtons = new ArrayList<>(7);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createToolsMenu());
        menuBar.add(createAnimateMenu());
        menuBar.add(createWindowsMenu());
        menuBar.add(createHelpMenu());
        setJMenuBar(menuBar);

        if (graphicPanel.getFile() != null) {
            setSaveButtonState(true);
        }
        localizeExternalUI();
        revalidate();
    }

    public void save() {
        toolbarPanel.interruptAnimation();
        try {
            graphicPanel.saveToFile();
        } catch (IOException ioException) {
            Logger.jlogger.warning(Logger.stackTraceToString(ioException));
            Logger.jlogger.warning("Saving file failed!");
        }
        this.revalidate();
    }

    public void saveAs() {
        toolbarPanel.interruptAnimation();
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("JavaDraw", "javadraw");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle(Config.bundle.getString("SaveFile"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getPath().endsWith(".javadraw")) {
                this.setTitle("JavaDraw - " + file.getName().replaceAll(".javadraw$", ""));
                graphicPanel.setFile(file.getPath());
            } else {
                this.setTitle("JavaDraw - " + file.getName());
                graphicPanel.setFile(file.getPath() + ".javadraw");
            }
            save.setEnabled(true);
            toolbarPanel.buttonChangeState(0, 3, true);
        }
        try {
            if (graphicPanel.getFile() != null) {
                graphicPanel.saveToFile();
            }
        } catch (IOException ioException) {
            Logger.jlogger.warning(Logger.stackTraceToString(ioException));
            Logger.jlogger.warning("Saving file failed!");
        }
        this.revalidate();
    }

    public void load() {
        toolbarPanel.interruptAnimation();
        JFileChooser openFileChooser = new JFileChooser();
        openFileChooser.setDialogTitle(Config.bundle.getString("OpenFile"));
        FileFilter filter = new FileNameExtensionFilter("JavaDraw", "javadraw");
        openFileChooser.setFileFilter(filter);
        if (openFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = openFileChooser.getSelectedFile();
            this.setTitle("JavaDraw - " + file.getName().substring(0, file.getName().length() - 9));
            graphicPanel.setFile(file.getPath());
            save.setEnabled(true);
            toolbarPanel.buttonChangeState(0, 3, true);
            try {
                graphicPanel.readFromFile();
            } catch (IOException | ClassNotFoundException ioException) {
                Logger.jlogger.warning(Logger.stackTraceToString(ioException));
                Logger.jlogger.warning("Reading file failed!");
            }
        }
        this.revalidate();
    }

    public void export() {
        toolbarPanel.interruptAnimation();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Config.bundle.getString("ExportFile"));
        FileFilter filter = new FileNameExtensionFilter("PNG", "png");
        fileChooser.setFileFilter(filter);
        FileFilter filter2 = new FileNameExtensionFilter("JPG", "jpg");
        fileChooser.setFileFilter(filter2);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".png") && !file.getName().endsWith(".jpg")) {
                if (fileChooser.getFileFilter() == filter2) {
                    file = new File(file.getPath() + ".jpg");
                } else {
                    file = new File(file.getPath() + ".png");
                }
            }
            int w = graphicPanel.getWidth();
            int h = graphicPanel.getHeight();
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            graphicPanel.paint(g);
            g.dispose();
            try {
                if (file.getName().endsWith(".png")) {
                    ImageIO.write(bi, "png", file);
                } else if (file.getName().endsWith(".jpg")) {
                    ImageIO.write(bi, "jpg", file);
                }
            } catch (IOException ioException) {
                Logger.jlogger.info(Logger.stackTraceToString(ioException));
                Logger.jlogger.info("File not found.");
            }
        }
    }

    public GraphicPanel getGraphicPanel() {
        return graphicPanel;
    }

    private void storePreferences() throws BackingStoreException {
        prefs.putInt("Width", this.getWidth());
        prefs.putInt("Height", this.getHeight());
        prefs.putInt("X_Pos", this.getX());
        prefs.putInt("Y_Pos", this.getY());
        prefs.putInt("AnimationTime", toolbarPanel.animationTime);
        switch (Config.getLanguage()) {
            case "Deutsch" -> prefs.put("Language", "de");
            case "Östareichisch" -> prefs.put("Language", "at");
            case "Français" -> prefs.put("Language", "fr");
            case "Italiano" -> prefs.put("Language", "it");
            case "Hangug-in" -> prefs.put("Language", "kr");
            default -> prefs.put("Language", "en");
        }
        prefs.putBoolean("Dock", dock);

        prefs.flush();
    }

    public void trimToSize(int height) {
        if (dock) {
            this.setSize(this.getWidth(), height);
            revalidate();
        }
    }

    public void localizeExternalUI() {
        // OptionPane Text
        UIManager.put("OptionPane.okButtonText", Config.bundle.getString("Ok"));
        UIManager.put("OptionPane.cancelButtonText", Config.bundle.getString("Cancel"));
        UIManager.put("OptionPane.yesButtonText", Config.bundle.getString("Yes"));
        UIManager.put("OptionPane.noButtonText", Config.bundle.getString("No"));

        // ColorChooser Text
        UIManager.put("ColorChooser.cancelText", Config.bundle.getString("Cancel"));
        UIManager.put("ColorChooser.okText", Config.bundle.getString("Ok"));
        UIManager.put("ColorChooser.resetText", Config.bundle.getString("Reset"));
        UIManager.put("ColorChooser.previewText", Config.bundle.getString("Preview"));
        UIManager.put("ColorChooser.sampleText", Config.bundle.getString("PreviewText"));
        UIManager.put("ColorChooser.swatchesNameText", Config.bundle.getString("Swatches"));
        UIManager.put("ColorChooser.swatchesRecentText", Config.bundle.getString("Recent"));
        UIManager.put("ColorChooser.hsvHueText", Config.bundle.getString("Hue"));
        UIManager.put("ColorChooser.hsvSaturationText", Config.bundle.getString("Saturation"));
        UIManager.put("ColorChooser.hsvValueText", Config.bundle.getString("Value"));
        UIManager.put("ColorChooser.hsvTransparencyText", Config.bundle.getString("Transparency"));
        UIManager.put("ColorChooser.hslHueText", Config.bundle.getString("Hue"));
        UIManager.put("ColorChooser.hslSaturationText", Config.bundle.getString("Saturation"));
        UIManager.put("ColorChooser.hslTransparencyText", Config.bundle.getString("Transparency"));
        UIManager.put("ColorChooser.hslLightnessText", Config.bundle.getString("Lightness"));
        UIManager.put("ColorChooser.rgbRedText", Config.bundle.getString("Red"));
        UIManager.put("ColorChooser.rgbGreenText", Config.bundle.getString("Green"));
        UIManager.put("ColorChooser.rgbBlueText", Config.bundle.getString("Blue"));
        UIManager.put("ColorChooser.rgbAlphaText", Config.bundle.getString("Alpha"));
        UIManager.put("ColorChooser.cmykCyanText", Config.bundle.getString("Cyan"));
        UIManager.put("ColorChooser.cmykMagentaText", Config.bundle.getString("Magenta"));
        UIManager.put("ColorChooser.cmykYellowText", Config.bundle.getString("Yellow"));
        UIManager.put("ColorChooser.cmykBlackText", Config.bundle.getString("Black"));
        UIManager.put("ColorChooser.cmykAlphaText", Config.bundle.getString("Alpha"));
        UIManager.put("ColorChooser.ColorCodeText", Config.bundle.getString("Alpha"));
    }

    public static void main(String[] args) {
        Runnable gui = () -> {
            MainFrame window = new MainFrame();
            window.setVisible(true);
            window.checkVersion();
        };
        SwingUtilities.invokeLater(gui);
    }
}