/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.graphicobjects;

import at.paasmoo.config.Config;
import at.paasmoo.utils.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class Picture extends Rectangle {
    private transient Image image;
    private Image originalImage;
    private String imagePath;

    public Picture(int x, int y, int width, int height, Color lineColor, Color fillColor, int strokeWidth) {
        super(x, y, width, height, lineColor, fillColor, strokeWidth);
        imagePath = Config.bundle.getString("Unknown");
        name = Config.bundle.getString("Image");
    }

    public void setImage(Image image) {
        this.originalImage = image;
        this.rescale();
    }

    public Image getImage() {
        return image;
    }

    public void loadImage(String filePath) throws IOException {
        imagePath = filePath;
        BufferedImage bufferedImage = ImageIO.read(new File(filePath));
        setImage(bufferedImage);
    }

    public void rescale() {
        image = originalImage.getScaledInstance(getWidth(), getHeight(),
                Image.SCALE_SMOOTH);
    }

    @Override
    public void draw(Graphics g) {
        super.setRect();

        Graphics2D graphics2D = (Graphics2D) g;

        if (fillColor != null) {
            graphics2D.setColor(fillColor);
            graphics2D.fill(getRect());
        }

        if (image != null) {
            graphics2D.drawImage(image, drawX, drawY, getWidth(), getHeight(), null);
        } else {
            super.draw(g);
        }

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.setColor(lineColor);
        graphics2D.draw(getRect());
    }

    public String getFilePath() {
        return imagePath;
    }

    public void setFilePath(String filePath) {
        imagePath = filePath;
    }

    @Serial
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        Picture readPicture = (Picture) aInputStream.readObject();
        this.x = readPicture.x;
        this.y = readPicture.y;
        this.lineColor = readPicture.lineColor;
        this.fillColor = readPicture.fillColor;
        this.strokeWidth = readPicture.strokeWidth;
        this.objectNumber = readPicture.objectNumber;
        ImageIcon serializedImage = (ImageIcon) aInputStream.readObject();
        this.image = serializedImage.getImage();
        this.originalImage = image;
        this.imagePath = serializedImage.getDescription();
        try {
            ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            imagePath = Config.bundle.getString("Unknown");
            Logger.jlogger.info(Logger.stackTraceToString(e));
            Logger.jlogger.info("File not found.");
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        ImageIcon serializedImage = new ImageIcon(image, imagePath);
        aOutputStream.writeObject(this);
        aOutputStream.writeObject(serializedImage);
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @Override
    public Color getFillColor() {
        return super.getFillColor();
    }

    @Override
    public void refreshName() {
        name = Config.bundle.getString("Image");
    }

    @Override
    public Picture clone() throws CloneNotSupportedException {
        Picture p = new Picture(drawX, drawY, getWidth(), getHeight(), lineColor, fillColor, strokeWidth);
        try {
            p.loadImage(imagePath);
        } catch (IOException e) {
            p.setImage(image);
        }
        p.setObjectNumber(this.objectNumber);
        return p;
    }
}
