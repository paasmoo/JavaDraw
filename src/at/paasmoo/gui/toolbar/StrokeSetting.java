/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.gui.toolbar;

import at.paasmoo.config.Config;
import at.paasmoo.gui.MainFrame;
import at.paasmoo.utils.MouseAdapt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.Objects;


public class  StrokeSetting extends JFrame {
    public StrokeSetting(MainFrame mainFrame, MouseAdapt mouseAdapt, int oldStrokeWidth) {
        super(Config.bundle.getString("LineWidthSettings"));
        setMinimumSize(new Dimension(500, 150));
        setBounds(0, 0, 500, 150);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ImageIcon im = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")));
        setIconImage(im.getImage());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 1, 20, oldStrokeWidth);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        container.add(slider);

        JPanel reviewLinePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ((Graphics2D) g).setStroke(new BasicStroke(slider.getValue()));
                g.setColor(Color.BLACK);
                ((Graphics2D) g).draw(new Line2D.Double(0, 24.5, this.getWidth(), 24.5));
            }
        };
        slider.addChangeListener(e -> reviewLinePanel.repaint());
        container.add(reviewLinePanel);
        add(container, BorderLayout.CENTER);

        JButton jButton = new JButton(Config.bundle.getString("Ok"));
        jButton.addActionListener(e -> {
            dispose();
            mainFrame.setEnabled(true);
            mouseAdapt.setStrokeWidth(slider.getValue());
        });
        add(jButton, BorderLayout.SOUTH);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                mainFrame.setEnabled(true);
            }
        });
    }
}