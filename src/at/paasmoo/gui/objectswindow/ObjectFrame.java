/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.GraphicObject;
import at.paasmoo.gui.MainFrame;
import at.paasmoo.gui.drawingspace.GraphicPanel;
import at.paasmoo.utils.GraphicObjectListRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ObjectFrame extends JFrame {
    private final JList<GraphicObject> list;
    private final DefaultListModel<GraphicObject> listModel;
    private boolean visibility;

    private final PropertiesTableModel model;
    private PropertiesTable propertiesTable;

    private final GraphicPanel panel;

    private final Preferences pref = Config.getPrefs();


    public ObjectFrame(GraphicPanel owner, MainFrame frame) {
        super(Config.bundle.getString("Objects"));
        panel = owner;

        ImageIcon im = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")));
        setIconImage(im.getImage());

        model = new PropertiesTableModel(this);
        propertiesTable = new PropertiesTable(model);

        setPreferences(frame.getX(), frame.getY());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ReOrderListener reOrderListener = new ReOrderListener(list, this);
        list.addMouseListener(reOrderListener);
        list.addMouseMotionListener(reOrderListener);

        list.addListSelectionListener(listSelectionEvent -> {
            if (list.getSelectedIndex() >= 0) {
                model.setNewDataStructure(list.getSelectedValue());
            }
        });
        list.setCellRenderer(new GraphicObjectListRenderer());
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        Container container = new Container();
        container.add(new JScrollPane(propertiesTable));
        container.setLayout(new BorderLayout());
        container.add(propertiesTable.getTableHeader(), BorderLayout.PAGE_START);
        container.add(propertiesTable, BorderLayout.CENTER);
        add(container, BorderLayout.SOUTH);
        setResizable(false);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.setShowObjectFrameButtonState(false);
                setVisible(false);
            }
        });
    }

    public void updateList(LinkedList<GraphicObject> drawComponents) {
        listModel.clear();
        LinkedList<GraphicObject> components = new LinkedList<>(drawComponents);
        model.setNewDataStructure(null);
        listModel.addAll(components);
        list.repaint();
    }

    public void setSelectedComponent(GraphicObject component) {
        int i = listModel.indexOf(component);
        list.setSelectedIndex(i);
        model.setNewDataStructure(component);
    }

    public void trimToSize(int height) {
        this.setSize(this.getWidth(), height);
        revalidate();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        visibility = b;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public boolean removeSelected() {
        if (list.getSelectedIndex() >= 0) {
            panel.removeComponent(list.getSelectedIndex());
            panel.repaint();
            return true;
        } else if (listModel.size() != 0) {
            panel.removeComponent(listModel.getSize() - 1);
            panel.repaint();
            return true;
        }
        return false;
    }

    public void moveWindow(int alignmentX, int alignmentY) {
        this.setLocation(alignmentX - (this.getWidth() - 15), alignmentY);
    }

    public GraphicObject getSelectedComponent() {
        if (list.getSelectedIndex() >= 0) {
            return list.getSelectedValue();
        }
        return null;
    }

    public void reload() {
        setTitle(Config.bundle.getString("Objects"));

        model.reload();
        propertiesTable = new PropertiesTable(model);

        LinkedList<GraphicObject> oldList = getPanel().getGraphicObjects();
        GraphicObject oldSelected = getSelectedComponent();

        listModel.clear();

        listModel.addAll(oldList);

        if (oldSelected != null) {
            setSelectedComponent(oldSelected);
        }

        list.repaint();
        setResizable(false);
        revalidate();
    }

    public GraphicPanel getPanel() {
        return panel;
    }

    private void setPreferences(int xFrame, int yFrame) {
        // setMinimumSize(new Dimension(250, 500));
        setBounds(pref.getInt("X_PosOF", xFrame - 150), pref.getInt("Y_PosOF", yFrame),
                265, pref.getInt("HeightOF", 600));
    }

    public void setNewReorderedList(LinkedList<Object> list) {
        LinkedList<GraphicObject> components = new LinkedList<>();
        for (Object o : list) {
            components.add((GraphicObject) o);
        }
        panel.setList(components);
    }
}