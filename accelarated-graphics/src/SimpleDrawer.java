import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class SimpleDrawer extends JPanel {
	private static final long serialVersionUID = 1650250697526556971L;
	
	private int at = 0;
	private Color[] palette; 
	private FPSCounter fps; 
	
	public SimpleDrawer() {
		at = 0;
		palette = new Color[256];
		for(int i = 0; i < 256; i++)
			palette[i] = new Color(i,i,i);
		
		fps = new FPSCounter();
	}
	
	@Override
	public void paint(Graphics g) {
		Rectangle clip = g.getClipBounds();
		
		g.setColor(palette[at]);
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		fps.frame();
		
		g.setColor(palette[palette.length-1 - at]);
		g.drawString(Long.toString(fps.fps()), clip.x+16, clip.y+16);
		
		at = (at+1) % palette.length;
		
		this.repaint();
	}
}
