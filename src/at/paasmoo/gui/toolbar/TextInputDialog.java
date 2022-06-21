/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.toolbar;

import at.paasmoo.config.Config;
import at.paasmoo.utils.JFontChooser;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class TextInputDialog extends JFontChooser {
    @Override
    public int showDialog(Component parent) {
        dialogResultValue = ERROR_OPTION;
        JDialog dialog = createDialog(parent);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialogResultValue = CANCEL_OPTION;
            }
        });

        dialog.setTitle(Config.bundle.getString("TextInput"));
        getSampleTextField().requestFocus();
        dialog.setVisible(true);
        dialog.dispose();

        return dialogResultValue;
    }

    @Override
    protected JPanel getSamplePanel() {
        if (samplePanel == null) {
            Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), (Config.bundle.getString("TextInput")));
            Border empty = BorderFactory.createEmptyBorder(5, 10, 10, 10);
            Border border = BorderFactory.createCompoundBorder(titledBorder, empty);

            samplePanel = new JPanel();
            samplePanel.setLayout(new BorderLayout());
            samplePanel.setBorder(border);

            samplePanel.add(getSampleTextField(), BorderLayout.CENTER);
        }
        return samplePanel;
    }
}