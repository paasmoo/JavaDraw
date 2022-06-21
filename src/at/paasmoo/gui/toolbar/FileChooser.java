/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.toolbar;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FileChooser extends JFileChooser {
    public FileChooser() {
        super();
        FileFilter filter = new FileNameExtensionFilter("Images (gif, png, jpg)",
                "gif", "png", "jpg");
        this.setFileFilter(filter);
    }
}
