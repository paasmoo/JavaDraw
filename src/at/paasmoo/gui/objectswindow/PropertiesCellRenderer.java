/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.config.Config;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


public class PropertiesCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Color color) {
            setBackground(color);
            setText("");
            return this;
        } else if (value instanceof Font font) {
            setText(font.getFamily());
            return this;
        } else if (table.getValueAt(row, 0).equals(Config.bundle.getString("Image"))) {
            String name = (String) value;
            setText(name);
            return this;
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
