/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.config.Config;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;

public class ColorEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton delegate = new JButton();
    private Color savedColor;

    public ColorEditor() {
        ActionListener actionListener = actionEvent -> {
            Color color = JColorChooser.showDialog(delegate, Config.bundle.getString("ColorChooser"), savedColor);
            ColorEditor.this.changeColor(color);
            fireEditingStopped();
        };
        delegate.addActionListener(actionListener);
    }

    public Object getCellEditorValue() {
        return savedColor;
    }

    private void changeColor(Color color) {
        if (color != null) {
            savedColor = color;
            delegate.setBackground(color);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        changeColor((Color) value);
        return delegate;
    }
}
