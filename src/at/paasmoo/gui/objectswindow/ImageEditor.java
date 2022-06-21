/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.objectswindow;

import at.paasmoo.gui.toolbar.FileChooser;
import at.paasmoo.utils.Logger;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton delegate = new JButton();
    private String oldFile;
    private final FileChooser chooser;

    public ImageEditor() {
        chooser = new FileChooser();

        ActionListener actionListener = actionEvent -> {
            try {
                chooser.showOpenDialog(null);
                ImageEditor.this.changeImage(chooser.getSelectedFile().getAbsolutePath());
            } catch (Exception e) {
                Logger.jlogger.warning(Logger.stackTraceToString(e));
                Logger.jlogger.warning("Changing image failed!");
            }
            fireEditingStopped();
        };
        delegate.addActionListener(actionListener);
    }

    private void changeImage(String file){
        if (file != null && !file.equals("")){
            oldFile = file;
            delegate.setText(oldFile);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return oldFile;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        chooser.setCurrentDirectory(new File((String)table.getValueAt(row, column)));
        changeImage((String) value);
        return delegate;
    }
}