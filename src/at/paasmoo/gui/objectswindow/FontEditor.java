/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.gui.toolbar.TextInputDialog;
import at.paasmoo.utils.JFontChooser;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;

public class FontEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton delegate = new JButton();
    private Font font;

    public FontEditor() {
        ActionListener actionListener = actionEvent -> {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setSelectedFont(font);
            int result = textInputDialog.showDialog(null);
            if (JFontChooser.OK_OPTION == result) {
                Font myFont = textInputDialog.getSelectedFont();
                if (myFont != null) {
                    font = myFont;
                }
            }
            fireEditingStopped();
        };
        delegate.addActionListener(actionListener);
    }

     @Override
    public Object getCellEditorValue() {
        return font;
    }

    private void changeFont(Font font) {
        if (font != null) {
            this.font = font;
            delegate.setText(font.getFamily());
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        changeFont((Font) value);
        return delegate;
    }
}