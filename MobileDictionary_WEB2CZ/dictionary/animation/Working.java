package dictionary.animation;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.*;

/**
 * Working animation.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class Working {
	/**
	 * Animation frames count.
	 */
	public static final short FRAMES = 8;

	/**
	 * Animation image width.
	 */
	public static final short WIDTH = 16;

	/**
	 * Animation image height.
	 */
	public static final short HEIGHT = 11;

	/**
	 * Timer.
	 */
	private Timer timer = null;

	/**
	 * Canvas to repaint.
	 */
	private Canvas canvas = null;

	/**
	 * Animation images.
	 */
	private Image[] working = null;

	/**
	 * Where paint animation - right.
	 */
	private int x = 0;

	/**
	 * Where paint animation - top.
	 */
	private int y = 0;

	/**
	 * Actual animation frame.
	 */
	private int frame = 0;

	private Animation animation = null;

	/**
	 * Is animation running.
	 */
	boolean running = false;

	/**
	 * Initialize animation.
	 *
	 * @param canvas canvas to repaint
	 * @param x right in px
	 * @param y top in px
	 * @throws IOException
	 */
	public Working(Canvas canvas, int x, int y) throws IOException {
		this.canvas = canvas;
		this.x = x;
		this.y = y;

		working = new Image[FRAMES];

		for (int i = 0; i < FRAMES; i++) {
			working[i] = Image.createImage("/resources/working" + (i + 1) + ".png");
		}
	}

	/**
	 * Start animation.
	 */
	public void start() {
		if (!running) {
			running = true;
			timer = new Timer();
			animation = new Animation();
			timer.schedule(animation, 0, 100);
		}
	}

	/**
	 * Restart animation from first frame.
	 */
	public void restart() {
		frame = 0;
		start();
	}

	/**
	 * Stop animation.
	 */
	public void stop() {
		running = false;
		animation.cancel();
		timer.cancel();
	}

	/**
	 * Return if animation is running.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Draw animation to canvas.
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {
		g.drawImage(working[frame], x, y, Graphics.LEFT | Graphics.TOP);
	}

	private final class Animation extends TimerTask {
		public void run() {
			canvas.repaint();
			frame++;
			if (frame == FRAMES) {
				frame = 0;
			}
		}
	}
}
