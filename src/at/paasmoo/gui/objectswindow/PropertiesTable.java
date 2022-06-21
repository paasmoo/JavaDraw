/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.config.Config;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class PropertiesTable extends JTable {
    public PropertiesTable(PropertiesTableModel model) {
        super(model);
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        Object object = this.getValueAt(row, column);
        if (object instanceof Color || object == null) {
            return new ColorEditor();
        } else if (object instanceof Font) {
            return new FontEditor();
        } else if (getValueAt(row, 0).equals(Config.bundle.getString("Image"))) {
            return new ImageEditor();
        }
        return super.getCellEditor(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == 1) {
            return new PropertiesCellRenderer();
        }
        return super.getCellRenderer(row, column);
    }
}