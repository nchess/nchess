package com.github.elementbound.nchess.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TintedImageSet {
	private Map<String, BufferedImage> originalImages = new HashMap<>();
	private Map<Entry<String, Color>, BufferedImage> tintedImages = new HashMap<>();
	
	public void addImage(String name, Image image) {
		this.originalImages.put(name, toBufferedImage(image));
	}
	
	public BufferedImage getImage(String name) {
		return this.originalImages.get(name);
	}
	
	public BufferedImage getImage(String name, Color tint) {
		if(!this.hasImage(name, tint))
			this.createTinted(name, tint);
		
		return this.tintedImages.get(new SimpleEntry<>(name, tint));
	}
	
	public void createTinted(String name, Color tint) {
		if(this.hasImage(name, tint))
			return; 
		
		BufferedImage source = this.getImage(name);
		if(source == null)
			return; 
		
		BufferedImage result = new BufferedImage(source.getWidth(null), source.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < result.getHeight(); y++) {
			for(int x = 0; x < result.getWidth(); x++) {
				int i = source.getRGB(x, y);
				Color srcColor = new Color(i, true);
				Color dstColor = new Color(
						(srcColor.getRed()*tint.getRed())/256,
						(srcColor.getGreen()*tint.getGreen())/256,
						(srcColor.getBlue()*tint.getBlue())/256,
						srcColor.getAlpha());

				result.setRGB(x, y, dstColor.getRGB());
			}
		}
		
		this.tintedImages.put(new SimpleEntry<>(name, tint), result);
	}
	
	public boolean hasImage(String name, Color tint) {
		return this.tintedImages.containsKey(new SimpleEntry<>(name, tint));
	}
	
	//http://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
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

	public void clear() {
		originalImages.clear();
		tintedImages.clear();
	}
}
