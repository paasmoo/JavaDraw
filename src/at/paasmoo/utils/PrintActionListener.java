/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


public class PrintActionListener implements Runnable {
    private BufferedImage image;

    public PrintActionListener(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void run() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new ImagePrintable(printJob, image));
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException prt) {
                Logger.jlogger.warning(Logger.stackTraceToString(prt));
                Logger.jlogger.warning("Printing failed!");
            }
        }
    }

    public static class ImagePrintable implements Printable {
        private final double x;
        private final double y;
        private final double width;
        private final int orientation;
        private final BufferedImage image;

        public ImagePrintable(PrinterJob printJob, BufferedImage image) {
            PageFormat pageFormat = printJob.defaultPage();
            this.x = pageFormat.getImageableX();
            this.y = pageFormat.getImageableY();
            this.width = pageFormat.getImageableWidth();
            this.orientation = pageFormat.getOrientation();
            this.image = image;
        }

        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
            if (pageIndex == 0) {
                int pWidth;
                int pHeight;
                if (orientation == PageFormat.PORTRAIT) {
                    pWidth = (int) Math.min(width, image.getWidth());
                    pHeight = pWidth * image.getHeight() / image.getWidth();
                } else {
                    pHeight = (int) Math.min(width, image.getHeight());
                    pWidth = pHeight * image.getWidth() / image.getHeight();
                }
                g.drawImage(image, (int) x, (int) y, pWidth, pHeight, null);
                return PAGE_EXISTS;
            } else {
                return NO_SUCH_PAGE;
            }
        }
    }
}
