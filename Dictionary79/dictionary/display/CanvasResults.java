package dictionary.display;

import dictionary.Dictionary;
import dictionary.Search;
import dictionary.animation.Working;
import java.io.IOException;
import javax.microedition.lcdui.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Canvas with results.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class CanvasResults extends Canvas implements CommandListener {
	/**
	 * Flag image width.
	 */
	private static final int FLAG_WIDTH = 16;

	/**
	 * Flag image height.
	 */
	private static final int FLAG_HEIGHT = 11;

	/**
	 * Scrollbar width.
	 */
	private static final int SCROLLBAR_WIDTH = 5;

	/**
	 * Small scroll step.
	 */
	private static final int SCROLL_STEP = 10;

	/**
	 * Vertical space between words.
	 */
	private static final int VERTICAL_SPACE = 2;

	/**
	 * Horizontal space between word and flag.
	 */
	private static final int HORIZONTAL_SPACE = 2;

	/**
	 * Margin after flag.
	 */
	private static final int FLAG_MARGIN = 5;

	/**
	 * Right margin.
	 */
	private static final int WIDTH_MARGIN = 7;

	/**
	 * Words seperator height.
	 */
	private static final int LINE_HEIGHT = 5;

	/**
	 * No reload.
	 */
	private static final int RELOAD_NONE = 0;

	/**
	 * Make reload.
	 */
	private static final int RELOAD = 1;

	/**
	 * Is already reloaded.
	 */
	private static final int RELOADED = 2;

	/**
	 * Dictionary midlet.
	 */
	private Dictionary dictionary = null;

	/**
	 * Czech and english flag.
	 */
	private Image flags[] = null;

	/**
	 * Header height.
	 */
	private int headerHeight = 25;

	/**
	 * Footer height.
	 */
	private int footerHeight = 25;

	/**
	 * Scrollbar width.
	 */
	private int scrollbarWidth = 0;

	/**
	 * Timer for keys down and up.
	 */
	private Timer timerKey = null;

	/**
	 * Down key timer.
	 */
	private TimerTask downKey = null;

	/**
	 * Up key timer.
	 */
	private TimerTask upKey = null;

	/**
	 * Is key down pressed.
	 */
	private boolean downKeyRunning = false;

	/**
	 * Is key up pressed.
	 */
	private boolean upKeyRunning = false;

	/**
	 * Form height (without header and footer).
	 */
	private int formHeight = 0;

	/**
	 * Words height.
	 */
	private Vector heights = null;

	/**
	 * Reload heights.
	 */
	int reloadHeights = RELOAD_NONE;

	/**
	 * All words height.
	 */
	private int wordsAllHeight = 0;

	/**
	 * Words position (with scrollbar).
	 */
	private int wordsPosition = 0;

	/**
	 * Results words.
	 */
	private String words[] = null;

	/**
	 * Which word is original.
	 */
	private boolean originals[] = null;

	/**
	 * Working animation.
	 */
	private Working working = null;

	/**
	 * New search command.
	 */
	private Command cmdNewSearch = null;

	/**
	 * Lang command.
	 */
	private Command cmdLang = null;

	/**
	 * About command.
	 */
	private Command cmdAbout = null;

	/**
	 * Exit command.
	 */
	private Command cmdExit = null;

	/**
	 * Result canvas initialization.
	 * 
	 * @param midlet
	 * @throws IOException
	 */
	public CanvasResults(Dictionary midlet) throws IOException {
		this.dictionary = midlet;

		initialize();

		flags = new Image[2];
		flags[Search.ENG_CZE] = Image.createImage("/resources/eng.png");
		flags[Search.CZE_ENG] = Image.createImage("/resources/cze.png");

		headerHeight = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight() + 6;
		if (headerHeight < Working.HEIGHT) {
			headerHeight = Working.HEIGHT + 2;
		}
		footerHeight = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL).getHeight() + 4;
		formHeight = getHeight() - headerHeight - footerHeight;

		heights = new Vector();

		this.working = new Working(this, getWidth() - Working.WIDTH - 5, (headerHeight - Working.HEIGHT) / 2);

		words = new String[0];
		originals = new boolean[0];
	}

	/**
	 * Initialize components.
	 */
	public void initialize() {
		cmdNewSearch = new Command(dictionary.translate("Nové hledání"), Command.SCREEN, 0);
		cmdLang = new Command(dictionary.translate("Jazyk"), Command.SCREEN, 1);
		cmdAbout = new Command(dictionary.translate("O slovníku"), Command.SCREEN, 2);
		cmdExit = new Command(dictionary.translate("Konec"), Command.SCREEN, 3);

		addCommand(cmdNewSearch);
		addCommand(cmdLang);
		addCommand(cmdAbout);
		addCommand(cmdExit);
	}

	/**
	 * Reinitialize components.
	 */
	public void reinitialize() {
		removeCommand(cmdNewSearch);
		removeCommand(cmdLang);
		removeCommand(cmdAbout);
		removeCommand(cmdExit);

		initialize();
	}

	/**
	 * Read results and paint all objects.
	 *
	 * @param g
	 */
	protected void paint(Graphics g) {
		readWords();
		
		paintWords(g);

		paintHeader(g);

		paintFooter(g);

		paintScrollbar(g);
	}

	/**
	 * Paint header with working animation.
	 * 
	 * @param g
	 */
	private void paintHeader(Graphics g) {
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), headerHeight);

		g.setColor(255, 255, 255);
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
		g.drawString(dictionary.translate("Výsledky hledání"), 5, (headerHeight - Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight()) / 2, Graphics.LEFT | Graphics.TOP);

		if (working.isRunning()) {
			working.draw(g);
		}
	}

	/**
	 * Paint footer with count of results.
	 * 
	 * @param g
	 */
	private void paintFooter(Graphics g) {
		g.setColor(0, 0, 0);
		g.fillRect(0, (getHeight() - footerHeight), getWidth(), getHeight());

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
		g.setColor(255, 255, 255);
		g.drawString(dictionary.translate("Nalezeno překladů") + ": " + dictionary.getSearch().getResultsCount(), getWidth() / 2, (getHeight() - footerHeight + ((footerHeight - Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL).getHeight()) / 2)), Graphics.HCENTER | Graphics.TOP);
	}

	/**
	 * Read results and compute their heights (only new).
	 */
	private void readWords() {
		int height = 0;

		if (reloadHeights == RELOAD) {
			heights.removeAllElements();
			wordsAllHeight = 0;
			reloadHeights = RELOADED;
		}

		words = dictionary.getSearch().getResults();
		originals = getOriginals(words);

		for (int i = heights.size(); i < words.length; i++) {
			height = getWordHeight(words[i], originals[i]);

			heights.addElement(new Integer(height));
			wordsAllHeight += height;
		}
	}

	/**
	 * Paint all words.
	 * 
	 * @param g
	 */
	private void paintWords(Graphics g) {
		int allHeight = 0;
		int actualHeight = 0;

		g.setColor(255, 255, 255);
		g.fillRect(0, headerHeight, getWidth(), (getHeight() - footerHeight));

		for (int i = 0; i < words.length - 1; i++) {
			actualHeight = ((Integer) (heights.elementAt(i))).intValue();

			if ((((allHeight + actualHeight) >= wordsPosition) && ((allHeight + actualHeight) <= (wordsPosition + formHeight)))
					|| ((allHeight >= wordsPosition) && ((allHeight + actualHeight) <= (wordsPosition + formHeight)))
					|| ((allHeight <= (wordsPosition + formHeight)) && (allHeight > wordsPosition))) {

				paintWord(g, (allHeight - wordsPosition), words[i], originals[i]);
			}

			allHeight += actualHeight;
		}
	}

	/**
	 * Print one word.
	 * 
	 * @param g
	 * @param position position on canvas
	 * @param word word
	 * @param original is original word
	 */
	private void paintWord(Graphics g, int position, String word, boolean original) {
		position += headerHeight + VERTICAL_SPACE;

		g.setColor(0, 0, 0);

		if (word.equals("-")) {
			g.drawLine(0, position, (getWidth() - scrollbarWidth), position);
			return;
		}

		Image flag = null;
		int heightLine;
		int widthLine;

		if (original) {
			flag = flags[dictionary.getSearch().getDirection()];
		} else {
			flag = flags[(dictionary.getSearch().getDirection() == Search.ENG_CZE) ? Search.CZE_ENG : Search.ENG_CZE];
		}

		heightLine = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight();
		widthLine = getWidth() - FLAG_WIDTH - scrollbarWidth - HORIZONTAL_SPACE - FLAG_MARGIN - WIDTH_MARGIN;

		g.drawImage(flag, HORIZONTAL_SPACE, position + ((heightLine - FLAG_HEIGHT) / 2) + 1, Graphics.LEFT | Graphics.TOP);

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, original ? Font.STYLE_BOLD : Font.STYLE_PLAIN, Font.SIZE_MEDIUM));

		if (Font.getFont(Font.FACE_PROPORTIONAL, original ? Font.STYLE_BOLD : Font.STYLE_PLAIN, Font.SIZE_MEDIUM).stringWidth(word) <= widthLine) {
			g.drawString(word, (FLAG_WIDTH + FLAG_MARGIN), position, Graphics.LEFT | Graphics.TOP);
		} else {
			int lineChar = 0;

			int i = 0;
			for (i = 0; i < word.length(); i++) {
				if (Font.getFont(Font.FACE_PROPORTIONAL, original ? Font.STYLE_BOLD : Font.STYLE_PLAIN, Font.SIZE_MEDIUM).substringWidth(word, lineChar, (i - lineChar)) <= widthLine) {
					continue;
				} else {
					g.drawSubstring(word, lineChar, (i - lineChar), (FLAG_WIDTH + FLAG_MARGIN), position, Graphics.LEFT | Graphics.TOP);

					position += heightLine;
					lineChar = i;
				}
			}

			g.drawSubstring(word, lineChar, (i - lineChar), (FLAG_WIDTH + FLAG_MARGIN), position, Graphics.LEFT | Graphics.TOP);
		}
	}

	/**
	 * Paint scrollbar.
	 * 
	 * @param g
	 */
	private void paintScrollbar(Graphics g) {
		int scrollbarHeight = (int) (((float) formHeight / (float) wordsAllHeight) * formHeight);
		int scrollbarPosition = (int) (((float) formHeight / (float) wordsAllHeight) * wordsPosition);

		if (scrollbarHeight > formHeight) {
			return;
		} else if (reloadHeights == RELOAD_NONE) {
			scrollbarWidth = SCROLLBAR_WIDTH;
			reloadHeights = RELOAD;
		}
		
		if ((scrollbarPosition + scrollbarHeight) > formHeight) {
			scrollbarPosition = (formHeight - scrollbarHeight);
		}

		g.setColor(224, 224, 224);
		g.fillRect(getWidth() - SCROLLBAR_WIDTH, headerHeight, getWidth(), getHeight() - headerHeight - footerHeight);

		g.setColor(128, 128, 128);
		g.fillRect(getWidth() - SCROLLBAR_WIDTH, headerHeight + scrollbarPosition, SCROLLBAR_WIDTH, scrollbarHeight + 1);
	}

	/**
	 * Get word height.
	 * 
	 * @param word
	 * @param original is original word
	 * @return height
	 */
	private int getWordHeight(String word, boolean original) {
		if (word.equals("-")) {
			return LINE_HEIGHT;
		}

		int height = VERTICAL_SPACE;
		
		int heightLine = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight();
		int widthLine = getWidth() - FLAG_WIDTH - scrollbarWidth - HORIZONTAL_SPACE - FLAG_MARGIN - WIDTH_MARGIN;

		if (Font.getFont(Font.FACE_PROPORTIONAL, original ? Font.STYLE_BOLD : Font.STYLE_PLAIN, Font.SIZE_MEDIUM).stringWidth(word) <= widthLine) {
			height += heightLine;
		} else {
			int lineChar = 0;

			for (int i = 0; i < word.length(); i++) {
				if (Font.getFont(Font.FACE_PROPORTIONAL, original ? Font.STYLE_BOLD : Font.STYLE_PLAIN, Font.SIZE_MEDIUM).substringWidth(word, lineChar, (i - lineChar)) <= widthLine) {
					continue;
				} else {
					height += heightLine;
					lineChar = i;
				}
			}

			height = height + heightLine;
		}

		return height + VERTICAL_SPACE;
	}

	/**
	 * Set which word is original.
	 * 
	 * @param words array with words
	 * @return array where original word is true
	 */
	private boolean[] getOriginals(String[] words) {
		boolean allOriginals[] = new boolean[words.length];
		boolean original = true;

		for (int i = 0; i < words.length; i++) {
			allOriginals[i] = original;

			if (original) {
				original = false;
			}
			if (words[i].equals("-")) {
				original = true;
			}
		}

		return allOriginals;
	}

	/**
	 * Before search.
	 */
	public void startSearch() {
		working.restart();
		heights.removeAllElements();
		words = new String[0];
		originals = new boolean[0];
		wordsAllHeight = 0;
		wordsPosition = 0;
		scrollbarWidth = 0;
		reloadHeights = RELOAD_NONE;
	}

	/**
	 * After search.
	 */
	public void stopSearch() {
		working.stop();
	}

	/**
	 * Key pressed.
	 * 
	 * @param key
	 */
	protected void keyPressed(int key) {
		int keyCode = getGameAction(key);

		if ((keyCode == Canvas.DOWN) || (key == Canvas.KEY_NUM8)) {
			if (wordsAllHeight <= formHeight) {
				return;
			}

			if ((wordsPosition + formHeight + SCROLL_STEP) > wordsAllHeight) {
				wordsPosition = wordsAllHeight - formHeight;
			} else {
				wordsPosition += SCROLL_STEP;

				if (!downKeyRunning) {
					timerKey = new Timer();
					downKey = new DownKey();
					timerKey.schedule(downKey, 0, 50);
					downKeyRunning = true;
				}
			}		
		} else if ((keyCode == Canvas.UP) || (key == Canvas.KEY_NUM2)) {
			if (wordsAllHeight <= formHeight) {
				return;
			}

			if ((wordsPosition - SCROLL_STEP) < 0) {
				wordsPosition = 0;
			} else {
				wordsPosition -= SCROLL_STEP;

				if (!upKeyRunning) {
					timerKey = new Timer();
					upKey = new UpKey();
					timerKey.schedule(upKey, 0, 50);
					upKeyRunning = true;
				}
			}		
		} else if (key == Canvas.KEY_NUM7) {
			if (wordsAllHeight <= formHeight) {
				return;
			}

			if ((wordsPosition + (2 * formHeight)) > wordsAllHeight) {
				wordsPosition = wordsAllHeight - formHeight;
			} else {
				wordsPosition += formHeight;
			}
		} else if (key == Canvas.KEY_NUM1) {
			if (wordsAllHeight <= formHeight) {
				return;
			}

			if ((wordsPosition - formHeight) < 0) {
				wordsPosition = 0;
			} else {
				wordsPosition -= formHeight;
			}
		} else if (key == Canvas.KEY_NUM9) {
			if (wordsAllHeight <= formHeight) {
				return;
			}

			wordsPosition = wordsAllHeight - formHeight;
		} else if (key == Canvas.KEY_NUM3) {
			if (wordsAllHeight <= formHeight) return;

			wordsPosition = 0;
		}
		
		repaint();
	}

	/**
	 * Key release (only stop key timer).
	 * 
	 * @param key
	 */
	protected void keyReleased(int key) {
		int keyCode = getGameAction(key);

		if ((keyCode == Canvas.DOWN) || (keyCode == Canvas.KEY_NUM2) || (keyCode == Canvas.UP) || (keyCode == Canvas.KEY_NUM8)) {
			if (downKeyRunning) {
				downKey.cancel();
				downKeyRunning = false;
			}

			if (upKeyRunning) {
				upKey.cancel();
				upKeyRunning = false;
			}

			timerKey.cancel();
		}
	}

	/**
	 * Action listener.
	 * 
	 * @param c
	 * @param d
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == cmdNewSearch) {
			dictionary.showSearch();
		} else if (c == cmdLang) {
			dictionary.showLang();
		} else if (c == cmdAbout) {
			dictionary.showAbout();
		} else if (c == cmdExit) {
			dictionary.exit();
		}
	}

	/**
	 * Down key timer class.
	 */
	private final class DownKey extends TimerTask {
		/**
		 * Move words down.
		 */
		public void run() {
			if ((wordsPosition + formHeight + SCROLL_STEP) > wordsAllHeight) {
				wordsPosition = wordsAllHeight - formHeight;
				timerKey.cancel();
			} else {
				wordsPosition = wordsPosition + SCROLL_STEP;
			}
			
			repaint();
		}
	}

	/**
	 * Up key timer class.
	 */
	private final class UpKey extends TimerTask {
		/**
		 * Move word up.
		 */
		public void run() {
			if ((wordsPosition - SCROLL_STEP) < 0) {
				wordsPosition = 0;
				timerKey.cancel();
			} else {
				wordsPosition = wordsPosition - SCROLL_STEP;
			}
			
			repaint();
		}
	}
}