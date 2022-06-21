/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.utils;

import at.paasmoo.config.Config;
import at.paasmoo.graphicobjects.*;
import at.paasmoo.graphicobjects.Rectangle;
import at.paasmoo.gui.drawingspace.GraphicPanel;
import at.paasmoo.gui.toolbar.FileChooser;
import at.paasmoo.gui.toolbar.TextInputDialog;
import at.paasmoo.graphicobjects.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.prefs.Preferences;


public class MouseAdapt extends MouseAdapter {
    private final GraphicPanel gPanel;
    private MODE mode;
    private Color fillColor;
    private Color lineColor;
    private int strokeWidth;
    private Font font;
    private final Preferences pref = Config.getPrefs();
    private boolean mouseEditing;

    public MouseAdapt(GraphicPanel graphicsPanel) {
        gPanel = graphicsPanel;
        mode = MODE.SELECT;
        mouseEditing = false;
        setPreferences();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        mouseEditing = true;
        if (gPanel.isAnimating()) {
            return;
        }
        if (mode == MODE.SELECT) {

            LinkedList<GraphicObject> components = gPanel.getGraphicObjects();

            int i = components.size() - 1;
            while (i != -1) {
                if (components.get(i).within(e.getX(), e.getY())) {
                    gPanel.setSelectedItemIndex(i);
                    components.get(i).setGrabPoint(new Point(e.getX(), e.getY()));
                    //show the properties of the component in ObjectFrame
                    gPanel.getObjectFrame().setSelectedComponent(components.get(i));
                    i = 0;
                }
                i--;
            }
        } else {
            GraphicObject newC = createChosenComponent(e);
            if (newC != null) {
                newC.setNewObjectNumber();
                gPanel.addComponent(newC);
                gPanel.trimToPreferredSize(newC);
                gPanel.getObjectFrame().setSelectedComponent(gPanel.getGraphicObjects().getLast());
                gPanel.repaint();
                if (newC instanceof Wordfield) {
                    mouseEditing = false;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        mouseEditing = false;
        if (gPanel.isAnimating()) {
            return;
        }
        if (mode == MODE.SELECT) {
            if (gPanel.getSelectedItemIndex() != -1) {
                gPanel.trimToPreferredSize(gPanel.getGraphicObjects().get(gPanel.getSelectedItemIndex()));
                gPanel.storeEdit();
            }
            gPanel.setSelectedItemIndex(-1);
        } else if (gPanel.getGraphicObjects().size() != 0) {
            if (gPanel.getGraphicObjects().getLast() instanceof Picture) {
                FileChooser chooser = new FileChooser();
                chooser.setDialogTitle(Config.bundle.getString("OpenFile"));
                chooser.showOpenDialog(gPanel);
                BufferedImage image;
                try {
                    if (chooser.getSelectedFile() == null) {
                        throw new IOException();
                    }
                    image = ImageIO.read(chooser.getSelectedFile());
                    ((Picture) gPanel.getGraphicObjects().getLast()).setImage(image); //for repaint
                    ((Picture) gPanel.getGraphicObjects().getLast()).
                            setFilePath(chooser.getSelectedFile().getAbsolutePath());
                    gPanel.trimToPreferredSize(gPanel.getGraphicObjects().getLast());
                    gPanel.getObjectFrame().setSelectedComponent(gPanel.getGraphicObjects().getLast());
                } catch (IOException ioException) {
                    gPanel.removeComponent(gPanel.getGraphicObjects().size() - 1);
                }
                gPanel.repaint();
            } else {
                gPanel.trimToPreferredSize(gPanel.getGraphicObjects().getLast());
                gPanel.repaint();
                gPanel.getObjectFrame().setSelectedComponent(gPanel.getGraphicObjects().getLast());
            }
            gPanel.storeEdit();
        }
    }

    public GraphicObject createChosenComponent(MouseEvent e) {
        switch (mode) {
            case STRAIGHTLINE:
                return new StraightLine(e.getX(), e.getY(), e.getX() + 10, e.getY() + 10, lineColor, strokeWidth);

            case RECTANGLE:
                return new Rectangle(e.getX(), e.getY(), 10, 10, lineColor, fillColor, strokeWidth);

            case ELLIPSE:
                return new Ellipse(e.getX(), e.getY(), 10, 10, lineColor, fillColor, strokeWidth);

            case LINE:
                return new FreehandLine(e.getX(), e.getY(), lineColor, strokeWidth);

            case WORDFIELD:
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setSelectedFont(font);
                textInputDialog.setSelectedFontSize(12);
                textInputDialog.setSelectedFontFamily("Arial");
                textInputDialog.setSelectedFontStyle(0);
                int result = textInputDialog.showDialog(null);
                if (JFontChooser.OK_OPTION == result) {
                    Font myFont = textInputDialog.getSelectedFont();
                    String text = textInputDialog.getSampleTextField().getText();
                    if (text != null && !text.equals("") && myFont != null) {
                        font = myFont;
                        return new Wordfield(e.getX(), e.getY(), lineColor, fillColor, text, myFont, gPanel);
                    }
                }
                mouseEditing = false;
                break;

            case IMAGE:
                return new Picture(e.getX(), e.getY(), 10, 10, lineColor, fillColor, strokeWidth);

        }
        return null;
    }

    private void setPreferences() {
        fillColor = new Color(0, 0, 0, 1);
        lineColor = Color.black;
        strokeWidth = pref.getInt("St-Width", 1);
        font = new Font(pref.get("Font-Name", Font.DIALOG),
                pref.getInt("Font-Style", Font.PLAIN),
                pref.getInt("Font-Size", 1));

        String newRGB = pref.get("LineColorRGB", null);
        if (newRGB != null) {
            lineColor = new Color(Integer.parseInt(newRGB), true);
        } else {
            lineColor = new Color(0, 0, 0);
        }

        newRGB = pref.get("FillColorRGB", null);
        if (newRGB != null) {
            fillColor = new Color(Integer.parseInt(newRGB), true);
        } else {
            fillColor = new Color(0, 0, 0, 1);
        }
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color newColor) {
        fillColor = newColor;
    }

    public Color getColor() {
        return lineColor;
    }

    public void setColor(Color color) {
        lineColor = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public boolean isMouseEditing() {
        return mouseEditing;
    }
}