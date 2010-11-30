package dictionary.display;

import dictionary.Dictionary;
import dictionary.Search;
import javax.microedition.lcdui.*;
import java.util.Timer;
import java.util.TimerTask;

public final class CanvasResults extends Canvas implements CommandListener {

	private Dictionary dictionary = null;

	private Image iLogo = null;
	private Image iFlag[] = new Image[2];
	private Image iSearching[] = new Image[2];

	private Timer timer_key = null;
	private TimerTask task_key = null;

	private static final int IFLAG_WIDTH = 18;
	private static final int IFLAG_HEIGHT = 12;
	private static final int HEADER_HEIGHT = 20;
	private static final int FOOTER_HEIGHT = 18;
	private static final int SCROLLBAR_WIDTH = 9;

	private int FORM_HEIGHT = 0;

	private Command cmdDictionarySearch = null;
	private Command cmdNewSearch = null;
	private Command cmdInfo = null;
	private Command cmdEnd = null;

	public CanvasResults(Dictionary midlet) {
		this.dictionary = midlet;
		
		cmdNewSearch = new Command("Nové", Command.SCREEN, 0);
		cmdDictionarySearch = new Command("Hledat ve slovníku", Command.SCREEN, 1);
		cmdInfo = new Command("Informace", Command.SCREEN, 2);
		cmdEnd = new Command("Konec", Command.SCREEN, 3);

		this.addCommand(cmdNewSearch);
		this.addCommand(cmdDictionarySearch);
		this.addCommand(cmdInfo);
		this.addCommand(cmdEnd);

		try {
			iLogo = Image.createImage("/logo.png");
			//iFlag[Search.DICTIONARY_CZE] = Image.createImage("/cze.png");
			//iFlag[Search.DICTIONARY_ENG] = Image.createImage("/eng.png");
			iSearching[0] = Image.createImage("/searching1.png");
			iSearching[1] = Image.createImage("/searching2.png");
		} catch(Exception e) {}

		FORM_HEIGHT = getHeight() - HEADER_HEIGHT - FOOTER_HEIGHT;
	}

	protected void paint(Graphics g) {
		g.setColor(255, 255, 255);
		g.fillRect(0, HEADER_HEIGHT, getWidth(), (getHeight() - FOOTER_HEIGHT));

		readWords();

		drawWords(g);

		header(g);

		footer(g, String.valueOf(dictionary.getSearch().translate.size()));

		scrollbar(g);

		if(dictionary.getSearch().searching_run) search(g);

		if(dictionary.getSearch().searchNotice != Search.NOTICE_OK) notice(g, dictionary.getSearch().searchNotice);
	}

	private void header(Graphics g) {
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), HEADER_HEIGHT);
		//g.drawImage(iLogo, 6, 5, Graphics.TOP | Graphics.LEFT);
	}

	private void footer(Graphics g, String count) {
		g.setColor(0, 0, 0);
		g.fillRect(0, (getHeight() - FOOTER_HEIGHT), getWidth(), getHeight());

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));
		g.setColor(255, 255, 255);
		g.drawString("Nalezeno překladů: " + count, (getWidth() - 5), (getHeight() - 2), Graphics.RIGHT | Graphics.BOTTOM);
	}

	private void readWords() {
		int x, height;

		for(x = dictionary.getSearch().heights.size(); x < dictionary.getSearch().translate.size(); x++) {
			height = heightWord(String.valueOf(dictionary.getSearch().original.elementAt(x)), String.valueOf(dictionary.getSearch().translate.elementAt(x)));

			dictionary.getSearch().heights.addElement(new Integer(height));
			dictionary.getSearch().words_height_all = dictionary.getSearch().words_height_all + height;
		}
	}

	private void drawWords(Graphics g) {
		int all_height = 0;
		int actual_height = 0;
		boolean draw_end_line = true;

		int x;
		for(x = 0; x < dictionary.getSearch().heights.size(); x++) {
			actual_height = ((Integer) (dictionary.getSearch().heights.elementAt(x))).intValue();

			if((((all_height + actual_height) >= dictionary.getSearch().words_position) && ((all_height + actual_height) <= (dictionary.getSearch().words_position + FORM_HEIGHT))) || ((all_height >= dictionary.getSearch().words_position) && ((all_height + actual_height) <= (dictionary.getSearch().words_position + FORM_HEIGHT))) || ((all_height <= (dictionary.getSearch().words_position + FORM_HEIGHT)) && (all_height > dictionary.getSearch().words_position))) {
				if(((x + 1) == dictionary.getSearch().heights.size()) && (dictionary.getSearch().words_height_all >= FORM_HEIGHT)) draw_end_line = false;
				drawWord(g, (all_height - dictionary.getSearch().words_position), draw_end_line, String.valueOf(dictionary.getSearch().original.elementAt(x)), String.valueOf(dictionary.getSearch().translate.elementAt(x)));
			}

			all_height = all_height + actual_height;
		}
	}

	private void scrollbar(Graphics g) {
		int MAX_HEIGHT = (FORM_HEIGHT - 2);
		float fScrollbarHeight = (((new Integer(FORM_HEIGHT)).floatValue() / (new Integer(dictionary.getSearch().words_height_all)).floatValue()) * (new Integer(MAX_HEIGHT)).floatValue());
		float fScrollbarPosition = (((new Integer(FORM_HEIGHT)).floatValue() / (new Integer(dictionary.getSearch().words_height_all)).floatValue()) * (new Integer(dictionary.getSearch().words_position)).floatValue());
		int scrollbar_height = (new Float(fScrollbarHeight)).intValue();
		int scrollbar_position = (new Float(fScrollbarPosition)).intValue();

		if(scrollbar_height > MAX_HEIGHT) scrollbar_height = MAX_HEIGHT;
		if((scrollbar_position + scrollbar_height) > MAX_HEIGHT) scrollbar_position = (MAX_HEIGHT - scrollbar_height);

		g.setColor(0, 0, 0);

		g.drawLine((getWidth() - SCROLLBAR_WIDTH), HEADER_HEIGHT, (getWidth() - SCROLLBAR_WIDTH), (getHeight() - FOOTER_HEIGHT));

		g.fillRect((getWidth() - SCROLLBAR_WIDTH + 2), (HEADER_HEIGHT + 1 + scrollbar_position), (SCROLLBAR_WIDTH - 3), scrollbar_height);
	}

	private void drawWord(Graphics g, int position, boolean draw_end_line, String original, String translate) {
		int height;
		int height_line;
		int width_line;

		int lineChar = 0;
		int x;

		height_line = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight();
		width_line = getWidth() - IFLAG_WIDTH - SCROLLBAR_WIDTH - 8;

		g.setColor(0, 0, 0);

		height = HEADER_HEIGHT + position + 2;

		//g.drawImage(iFlag[dictionary.getSearch().results_original], 2, (height + 2), Graphics.LEFT | Graphics.TOP);

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));

		if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM).stringWidth(original) <= width_line) {
			g.drawString(original, (IFLAG_WIDTH + 4), height, Graphics.LEFT | Graphics.TOP);

			height = height + height_line;
		} else {
			lineChar = 0;

			for(x = 0; x < original.length(); x++) {
				if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM).substringWidth(original, lineChar, (x - lineChar)) <= width_line) continue;
				else {
					g.drawSubstring(original, lineChar, (x - lineChar), (IFLAG_WIDTH + 4), height, Graphics.LEFT | Graphics.TOP);

					height = height + height_line;
					lineChar = x;
				}
			}

			g.drawSubstring(original, lineChar, (x - lineChar), (IFLAG_WIDTH + 4), height, Graphics.LEFT | Graphics.TOP);

			height = height + height_line;
		}


		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

		if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).stringWidth(translate) <= width_line) {
			g.drawString(translate, (getWidth() - IFLAG_WIDTH - SCROLLBAR_WIDTH - 4), height, Graphics.RIGHT | Graphics.TOP);

			height = height + height_line;
		} else {
			lineChar = 0;

			for(x = 0; x < translate.length(); x++) {
				if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).substringWidth(translate, lineChar, (x - lineChar)) <= width_line) continue;
				else {
					g.drawSubstring(translate, lineChar, (x - lineChar), (getWidth() - IFLAG_WIDTH - SCROLLBAR_WIDTH - 4), height, Graphics.RIGHT | Graphics.TOP);

					height = height + height_line;
					lineChar = x;
				}
			}

			g.drawSubstring(translate, lineChar, (x - lineChar), (getWidth() - IFLAG_WIDTH - SCROLLBAR_WIDTH - 4), height, Graphics.RIGHT | Graphics.TOP);

			height = height + height_line;
		}

		//g.drawImage(iFlag[dictionary.getSearch().results_translate], (getWidth() - SCROLLBAR_WIDTH - 2), (height - 3), Graphics.RIGHT | Graphics.BOTTOM);

		height = height + 1;

		if(draw_end_line) g.drawLine(0, height, (getWidth() - SCROLLBAR_WIDTH), height);
	}

	public void print() {
		System.out.println("xxx");
	}

	private int heightWord(String original, String translate) {
		int height = 0;
		int height_line = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).getHeight();
		int width_line = getWidth() - IFLAG_WIDTH - SCROLLBAR_WIDTH - 8;

		int lineChar = 0;
		int x;

		height = 2;

		if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM).stringWidth(original) <= width_line) {
			height = height + height_line;
		} else {
			lineChar = 0;

			for(x = 0; x < original.length(); x++) {
				if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM).substringWidth(original, lineChar, (x - lineChar)) <= width_line) continue;
				else {
					height = height + height_line;
					lineChar = x;
				}
			}

			height = height + height_line;
		}

		height = height + 2;

		if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).stringWidth(translate) <= width_line) {
			height = height + height_line;
		} else {
			lineChar = 0;

			for(x = 0; x < translate.length(); x++) {
				if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM).substringWidth(translate, lineChar, (x - lineChar)) <= width_line) continue;
				else {
					height = height + height_line;
					lineChar = x;
				}
			}

			height = height + height_line;
		}

		height = height + 1;

		return height;
	}

	private void search(Graphics g) {
		//g.drawImage(iSearching[dictionary.getSearch().searching_image], (getWidth() - 5), 4, Graphics.RIGHT | Graphics.TOP);
	}

	private void notice(Graphics g, int notice) {
		String text = "";
		int width_line = getWidth() - SCROLLBAR_WIDTH - 10;

		if(notice == Search.NOTICE_NO_RESULTS) {
			g.setColor(0, 0, 0);
			text = "Nebyl nalezen žádný překlad.";
		} else if(notice == Search.NOTICE_ERROR) {
			g.setColor(255, 0, 0);
			text = "Nastala chyba při hledání!";
		}

		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL));

		if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL).stringWidth(text) <= width_line) {
			g.drawString(text, ((width_line / 2) + 5), (getHeight() / 2), Graphics.HCENTER | Graphics.TOP);
		} else {
			int height = 0;
			int height_line = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL).getHeight();
			int lineChar = 0;

			int x;

			for(x = 0; x < text.length(); x++) {
				if(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL).substringWidth(text, lineChar, (x - lineChar)) <= width_line) continue;
				else {
					g.drawSubstring(text, lineChar, (x - lineChar), ((width_line / 2) + 5), ((getHeight() / 2) + height), Graphics.HCENTER | Graphics.TOP);

					height = height + height_line;
					lineChar = x;
				}
			}

			g.drawSubstring(text, lineChar, (x - lineChar), (width_line / 2), ((getHeight() / 2) + height), Graphics.HCENTER | Graphics.TOP);
		}
	}

	protected void keyPressed(int key) {
		int keyCode = getGameAction(key);

		if((keyCode == Canvas.DOWN) || (key == Canvas.KEY_NUM8)) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			if((dictionary.getSearch().words_position + FORM_HEIGHT + 5) > dictionary.getSearch().words_height_all) dictionary.getSearch().words_position = (dictionary.getSearch().words_height_all - FORM_HEIGHT);
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position + 5;
			repaint();

			task_key = new DownKey();
			timer_key = new Timer();
			timer_key.schedule(task_key, 0, 50);
		} else if((keyCode == Canvas.UP) || (key == Canvas.KEY_NUM2)) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			if((dictionary.getSearch().words_position - 5) < 0) dictionary.getSearch().words_position = 0;
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position - 5;
			repaint();

			task_key = new UpKey();
			timer_key = new Timer();
			timer_key.schedule(task_key, 0, 50);
		} else if(key == Canvas.KEY_NUM7) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			if((dictionary.getSearch().words_position + FORM_HEIGHT + FORM_HEIGHT) > dictionary.getSearch().words_height_all) dictionary.getSearch().words_position = (dictionary.getSearch().words_height_all - FORM_HEIGHT);
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position + FORM_HEIGHT;
			repaint();
		} else if(key == Canvas.KEY_NUM1) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			if((dictionary.getSearch().words_position - FORM_HEIGHT) < 0) dictionary.getSearch().words_position = 0;
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position - FORM_HEIGHT;
			repaint();
		} else if(key == Canvas.KEY_NUM9) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			dictionary.getSearch().words_position = (dictionary.getSearch().words_height_all - FORM_HEIGHT);
			repaint();
		} else if(key == Canvas.KEY_NUM3) {
			if(dictionary.getSearch().words_height_all <= FORM_HEIGHT) return;

			dictionary.getSearch().words_position = 0;
			repaint();
		} if((keyCode == Canvas.FIRE) || (key == Canvas.KEY_NUM5)) {
			dictionary.showSearch();
		}
	}

	protected void keyReleased(int key) {
		int keyCode = getGameAction(key);

		if((keyCode == Canvas.DOWN) || (keyCode == Canvas.KEY_NUM2)) {
			if(timer_key != null) {
				timer_key.cancel();
				task_key = null;
				timer_key = null;
			}
		} else if((keyCode == Canvas.UP) || (keyCode == Canvas.KEY_NUM8)) {
			if(timer_key != null) {
				timer_key.cancel();
				task_key = null;
				timer_key = null;
			}
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (c == cmdNewSearch) {
			dictionary.showSearch();
		}
	}

	private class DownKey extends TimerTask {
		public void run() {
			if((dictionary.getSearch().words_position + FORM_HEIGHT + 5) > dictionary.getSearch().words_height_all) dictionary.getSearch().words_position = (dictionary.getSearch().words_height_all - FORM_HEIGHT);
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position + 5;
			repaint();
		}
	}

	private class UpKey extends TimerTask {
		public void run() {
			if((dictionary.getSearch().words_position - 5) < 0) dictionary.getSearch().words_position = 0;
			else dictionary.getSearch().words_position = dictionary.getSearch().words_position - 5;
			repaint();
		}
	}

	protected void hideNotify() {
		dictionary.getSearch().cancelSearching();
	}
}