package com.github.elementbound.nchess.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Class to render images with a tint color.
 */
class TintedImageRenderer {

    public static final int COMPONENT_MAX = 256;

    public Image render(Image image, Color tint) {
        BufferedImage source = toBufferedImage(image);

        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                int i = source.getRGB(x, y);
                Color srcColor = new Color(i, true);
                Color dstColor = new Color(
                        (srcColor.getRed() * tint.getRed()) / COMPONENT_MAX,
                        (srcColor.getGreen() * tint.getGreen()) / COMPONENT_MAX,
                        (srcColor.getBlue() * tint.getBlue()) / COMPONENT_MAX,
                        srcColor.getAlpha());

                result.setRGB(x, y, dstColor.getRGB());
            }
        }

        return result;
    }

    //http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
