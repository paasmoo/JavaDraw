/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.toolbar;

import javax.swing.*;


public class ToolBarButton extends JButton {
    private final String title;

    public ToolBarButton(Icon icon, String title) {
        super(icon);
        this.title = title;
    }

    @Override
    public String getActionCommand() {
        return title;
    }
}