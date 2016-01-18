import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.VolatileImage;

import javax.swing.JPanel;

public class VolatileDrawer extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 2890289869018559819L;
	
	private int at = 0;
	private Color[] palette; 
	private VolatileImage image; 
	private FPSCounter fps; 
	
	private GraphicsEnvironment ge;
	private GraphicsConfiguration gc;
	
	public VolatileDrawer() {
		at = 0;
		palette = new Color[256];
		for(int i = 0; i < 256; i++)
			palette[i] = new Color(i,i,i);
		
		fps = new FPSCounter();
		
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		
		image = gc.createCompatibleVolatileImage(32, 32);
		this.addComponentListener(this);
	}

	@Override
	public void paint(Graphics pg) {
		Graphics2D g = null;
		
		do {
			try {
				int valid = image.validate(gc);
				if(valid == VolatileImage.IMAGE_INCOMPATIBLE) {
					gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
					image = gc.createCompatibleVolatileImage(this.getWidth(), this.getHeight());
				}
				
				g = image.createGraphics();
				
				g.setColor(palette[at]);
				g.fillRect(0,0, image.getWidth(), image.getHeight());
				
				g.setColor(palette[palette.length-1 - at]);
				g.drawString(Long.toString(fps.fps()), 16,16);
				
				pg.drawImage(image, 0,0, this);
			}
			finally {
				g.dispose();
			}
		}
		while(image.contentsLost());
		
		at = (at+1) % palette.length;
		fps.frame();
		
		this.repaint();
	}
	
	//

	@Override
	public void componentHidden(ComponentEvent e) {
		// Do nothing
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// Do nothing
	}

	@Override
	public void componentResized(ComponentEvent e) {
		image = gc.createCompatibleVolatileImage(this.getWidth(), this.getHeight());
		
		if(!image.getCapabilities().isAccelerated())
			System.out.println("Waring: VolatileImage not actually accelarated");
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// Do nothing
		
	}
}
