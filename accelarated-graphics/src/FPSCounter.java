
public class FPSCounter {
	private long lastSecond = 0;
	private long fps = 0;
	private long counter = 0;
	
	public void frame() {
		if(System.currentTimeMillis() - lastSecond > 1000) {
			fps = counter;
			counter = 0;
			lastSecond = System.currentTimeMillis();
		}
		
		counter++;
	}
	
	public long fps() {
		return this.fps;
	}
}
