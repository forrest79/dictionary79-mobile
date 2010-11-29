package dictionary.display;

import dictionary.Dictionary;
import dictionary.animation.Working;
import java.io.IOException;
import javax.microedition.lcdui.*;

/**
 * Loading canvas.
 *
 * @author Jakub Trmota
 */
public final class CanvasLoading extends Canvas implements CommandListener {
	/**
	 * Welcome image width.
	 */
	public static final int WELCOME_WIDTH = 119;
	
	/**
	 * Welcome image height
	 */
	public static final int WELCOME_HEIGHT = 76;

	/**
	 * Dictionary midlet.
	 */
	private Dictionary dictionary = null;

	/**
	 * Image welcome.
	 */
	private Image welcome = null;

	/**
	 * Working animation.
	 */
	private Working working = null;

	/**
	 * End command.
	 */
	private Command cmdEnd = null;

	/**
	 * Initialize canvas loading.
	 *
	 * @param dictionary
	 * @throws IOException
	 */
	public CanvasLoading(Dictionary dictionary) throws IOException {
		this.dictionary = dictionary;

		this.working = new Working(this, (getWidth() - Working.WIDTH) / 2, (getHeight() + Working.HEIGHT + WELCOME_HEIGHT) / 2);

		cmdEnd = new Command(dictionary.translate("Konec"), Command.SCREEN, 0);
		this.addCommand(cmdEnd);

		welcome = Image.createImage("/resources/welcome.png");
	}

	/**
	 * Paing with working animation.
	 *
	 * @param g
	 */
	public void paint(Graphics g) {
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(welcome, ((getWidth() - WELCOME_WIDTH) / 2), ((getHeight() - WELCOME_HEIGHT) / 2), Graphics.TOP | Graphics.LEFT);

		working.draw(g);
	}

	/**
	 * Action listener.
	 *
	 * @param c
	 * @param d
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == cmdEnd) {
			dictionary.destroyApp(true);
		}
	}

	/**
	 * Start working animation.
	 */
	public void start() {
		working.start();
	}

	/**
	 * Stop working animation.
	 */
	public void stop() {
		working.stop();
	}
}