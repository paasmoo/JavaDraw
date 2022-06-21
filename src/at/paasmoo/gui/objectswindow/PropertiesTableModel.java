/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.*;
import at.paasmoo.graphicobjects.Rectangle;
import at.paasmoo.utils.Logger;
import at.paasmoo.graphicobjects.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;


public class PropertiesTableModel extends AbstractTableModel {
    private final ObjectFrame objectFrame;
    private String[] names;
    private Object[][] data;

    public PropertiesTableModel(ObjectFrame objectFrame){
        this.objectFrame = objectFrame;

        names = new String[]{Config.bundle.getString("Attributes"), Config.bundle.getString("Values")};
        data = null;
    }

    public void setNewDataStructure(Object o){
        if (o instanceof Ellipse){
            data = new Object[][]{{"X", ((Ellipse) o).getX()},
                                  {"Y", ((Ellipse) o).getY()},
                                  {Config.bundle.getString("Width"), ((Ellipse) o).getWidth()},
                                  {Config.bundle.getString("Height"), ((Ellipse) o).getHeight()},
                                  {Config.bundle.getString("StrokeWidth"), ((Ellipse) o).getStrokeWidth()},
                                  {Config.bundle.getString("LineColor"), ((Ellipse) o).getLineColor()},
                                  {Config.bundle.getString("FillColor"), ((Ellipse) o).getFillColor()}
            };
        }
        else if (o instanceof Picture){
            data = new Object[][]{{"X", ((Picture) o).getX()},
                                  {"Y", ((Picture) o).getY()},
                                  {Config.bundle.getString("Width"), ((Picture) o).getWidth()},
                                  {Config.bundle.getString("Height"), ((Picture) o).getHeight()},
                                  {Config.bundle.getString("LineColor"), ((Picture) o).getLineColor()},
                                  {Config.bundle.getString("FillColor"), ((Picture) o).getFillColor()},
                                  {Config.bundle.getString("StrokeWidth"), ((Picture) o).getStrokeWidth()},
                                  {Config.bundle.getString("Image"), ((Picture) o).getFilePath()}
            };
        }
        else if (o instanceof at.paasmoo.graphicobjects.Rectangle){
            data = new Object[][]{{"X", ((at.paasmoo.graphicobjects.Rectangle) o).getX()},
                                  {"Y", ((at.paasmoo.graphicobjects.Rectangle) o).getY()},
                                  {Config.bundle.getString("Width"), ((at.paasmoo.graphicobjects.Rectangle) o).getWidth()},
                                  {Config.bundle.getString("Height"), ((at.paasmoo.graphicobjects.Rectangle) o).getHeight()},
                                  {Config.bundle.getString("StrokeWidth"), ((at.paasmoo.graphicobjects.Rectangle) o).getStrokeWidth()},
                                  {Config.bundle.getString("LineColor"), ((at.paasmoo.graphicobjects.Rectangle) o).getLineColor()},
                                  {Config.bundle.getString("FillColor"), ((Rectangle) o).getFillColor()}
            };
        }
        else if (o instanceof StraightLine){
            data = new Object[][]{{"X", ((StraightLine) o).getX()},
                                  {"Y", ((StraightLine) o).getY()},
                                  {Config.bundle.getString("StrokeWidth"), ((StraightLine) o).getStrokeWidth()},
                                  {Config.bundle.getString("LineColor"), ((StraightLine) o).getLineColor()},
            };
        }
        else if (o instanceof FreehandLine){
            data = new Object[][]{{"X", ((FreehandLine) o).getX()},
                                  {"Y", ((FreehandLine) o).getY()},
                                  {Config.bundle.getString("StrokeWidth"), ((FreehandLine) o).getStrokeWidth()},
                                  {Config.bundle.getString("LineColor"), ((FreehandLine) o).getLineColor()},
            };
        }
        else if (o instanceof Wordfield){
            data = new Object[][]{{"X", ((Wordfield) o).getX()},
                                  {"Y", ((Wordfield) o).getY()},
                                  {Config.bundle.getString("Text"), ((Wordfield) o).getText()},
                                  {Config.bundle.getString("Font"), ((Wordfield) o).getFont()},
                                  {Config.bundle.getString("TextColor"), ((Wordfield) o).getLineColor()},
                                  {Config.bundle.getString("BackgroundColor"), ((Wordfield) o).getFillColor()}
            };
        }

        else{
            data = null;
        }
        fireTableStructureChanged();
        fireTableDataChanged();
    }


    @Override
    public int getRowCount() {
        if (data == null){
            return 0;
        }
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data == null){
            return null;
        }
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return names[column];
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return Object.class;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        GraphicObject o = objectFrame.getSelectedComponent();
        boolean editedValue = false;
        String valueAt = (String) getValueAt(rowIndex, 0);

        if (valueAt.equals(Config.bundle.getString("LineColor"))
         || valueAt.equals(Config.bundle.getString("TextColor"))){
            Color newColor = (Color) aValue;
            if (newColor != data[rowIndex][columnIndex]){
                editedValue = true;
            }
            data[rowIndex][columnIndex] = newColor;
            o.setLineColor(newColor);
        } else if (valueAt.equals(Config.bundle.getString("FillColor"))
                || valueAt.equals(Config.bundle.getString("BackgroundColor"))){
            Color newColor = (Color) aValue;
            if (newColor != data[rowIndex][columnIndex]){
                editedValue = true;
            }
            data[rowIndex][columnIndex] = newColor;
            ((Fillable) o).setFillColor(newColor);
        } else if (valueAt.equals(Config.bundle.getString("Font"))){
            Font newFont = (Font) aValue;
            if (newFont != data[rowIndex][columnIndex]){
                editedValue = false;
            }
            data[rowIndex][columnIndex] = newFont;
            ((Wordfield) o).setFont(newFont);
        } else if (valueAt.equals(Config.bundle.getString("Text"))){
            String newText = (String) aValue;
            if (!newText.equals(data[rowIndex][columnIndex])){
                editedValue = true;
            }
            data[rowIndex][columnIndex] = newText;
            ((Wordfield) o).setText(newText);
        } else if (valueAt.equals(Config.bundle.getString("Image"))){
            String newFilePath = (String) aValue;
            if (!newFilePath.equals(data[rowIndex][columnIndex])){
                editedValue = true;
            }
            data[rowIndex][columnIndex] = newFilePath;
            try {
                ((Picture) o).loadImage(newFilePath);
            } catch (IOException e) {
                Logger.jlogger.info(Logger.stackTraceToString(e));
                Logger.jlogger.info("File not found.");
            }
        } else {
            int newInt;
            try {
                newInt = Integer.parseInt((String) aValue);
                if ((int)data[rowIndex][columnIndex] != newInt){
                    editedValue = true;
                }
            } catch (Exception e){
                JOptionPane.showMessageDialog(objectFrame.getPanel().getParent(),
                        Config.bundle.getString("InvalidValue"),
                        Config.bundle.getString("Warning"),
                        JOptionPane.WARNING_MESSAGE);
                Logger.jlogger.info(Logger.stackTraceToString(e));
                Logger.jlogger.info("Entered value is not an integer.");
                return;
            }

            if ("X".equals(valueAt)) {
                o.setCords(newInt, o.getY());
                if (o instanceof Line) {
                    data[rowIndex + 2][columnIndex] = ((Line) o).getEndX();
                    fireTableCellUpdated(rowIndex + 2, columnIndex);
                }
            } else if ("Y".equals(valueAt)) {
                o.setCords(o.getX(), newInt);
                if (o instanceof Line) {
                    //rowIndex+2 to update the End Y
                    data[rowIndex + 2][columnIndex] = ((Line) o).getEndY();
                    fireTableCellUpdated(rowIndex + 2, columnIndex);
                }
            } else if (Config.bundle.getString("Width").equals(valueAt)) {
                if(newInt <= 0) {
                    newInt = 1;
                    JOptionPane.showMessageDialog(objectFrame.getPanel().getParent(),
                            Config.bundle.getString("ObjWidthTooLow"),
                            Config.bundle.getString("Warning"),
                            JOptionPane.WARNING_MESSAGE);
                }
                ((Fillable) o).setWidth(newInt);
            } else if (Config.bundle.getString("Height").equals(valueAt)) {
                if(newInt <= 0) {
                    newInt = 1;
                    JOptionPane.showMessageDialog(objectFrame.getPanel().getParent(),
                            Config.bundle.getString("ObjHeightTooLow"),
                            Config.bundle.getString("Warning"),
                            JOptionPane.WARNING_MESSAGE);
                }
                ((Fillable) o).setHeight(newInt);
            } else if (Config.bundle.getString("StrokeWidth").equals(valueAt)) {
                if (newInt > 20) {
                    newInt = 20;
                    JOptionPane.showMessageDialog(objectFrame.getPanel().getParent(),
                            Config.bundle.getString("WidthTooBig"),
                            Config.bundle.getString("Warning"),
                            JOptionPane.WARNING_MESSAGE);
                } else if(newInt <= 0) {
                    newInt = 1;
                    JOptionPane.showMessageDialog(objectFrame.getPanel().getParent(),
                            Config.bundle.getString("WidthTooLow"),
                            Config.bundle.getString("Warning"),
                            JOptionPane.WARNING_MESSAGE);
                }
                o.setStrokeWidth(newInt);
            }
            data[rowIndex][columnIndex] = newInt;
        }
        objectFrame.getPanel().repaint();
        objectFrame.getPanel().trimToPreferredSize(o);
        if (editedValue) {
            objectFrame.getPanel().storeEdit();
        }
        fireTableCellUpdated(rowIndex, columnIndex);
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    public void reload(){
        names = new String[]{Config.bundle.getString("Attributes"), Config.bundle.getString("Values")};
        fireTableStructureChanged();
    }
}
