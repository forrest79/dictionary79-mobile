package dictionary;

import dictionary.display.*;
import dictionary.locale.Locale;
import java.io.IOException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

/**
 * Dictionary midlet.
 * 
 * @author Jakub Trmota | Forrest79
 */
public final class Dictionary extends MIDlet {
	/**
	 * Version.
	 */
	public static final String VERSION = "2.0.0";

	/**
	 * Locale class.
	 */
	private Locale locale = null;

	/**
	 * Search class.
	 */
	private Search search = null;

	/**
	 * Display.
	 */
	private Display display = null;

	/**
	 * Loading canvas.
	 */
	private CanvasLoading canvasLoading = null;

	/**
	 * Results canvas.
	 */
	private CanvasResults canvasResults = null;

	/**
	 * Search form.
	 */
	private FormSearch formSearch = null;

	/**
	 * Lang form.
	 */
	private FormLang formLang = null;

	/**
	 * About form.
	 */
	private FormAbout formAbout = null;

	/**
	 * Back form or canvas.
	 */
	private Displayable back = null;

	/**
	 * Alert on display.
	 */
	private Alert alert = null;

	/**
	 * Is midlet running.
	 */
	private boolean run = false;

	/**
	 * Start midlet and initilize.
	 */
	public void startApp() {
		try {
			if (!run) {
				display = Display.getDisplay(this);

				locale = new Locale();

				canvasLoading = new CanvasLoading(this);
				canvasLoading.start();

				show(canvasLoading);

				search = new Search(this);

				initializeForms();

				canvasResults = new CanvasResults(this);

				run = true;

				if (locale.firstRun()) {
					show(formLang);
				} else {
					show(formSearch);
				}

				canvasLoading.stop();
				canvasLoading = null;
			} else {
				locale.openRecords();
			}
		} catch (Exception e) {
			System.err.print(e);
			alert(translate("Chyba"), e.getMessage(), AlertType.ERROR);
		}
	}

	/**
	 * Pause midlet.
	 */
	public void pauseApp() {
		try {
			locale.closeRecords();
		} catch(Exception e) {
			System.err.print(e);
		}
	}

	/**
	 * Exit midlet.
	 * 
	 * @param unconditional
	 */
	public void destroyApp(boolean unconditional) {
		try {
			locale.closeRecords();
		} catch(Exception e) {
			System.err.print(e);
		}
		
		notifyDestroyed();
	}

	/**
	 * Initialize forms.
	 * 
	 * @throws IOException
	 */
	private void initializeForms() throws IOException {
		canvasResults = new CanvasResults(this);
		formSearch = new FormSearch(this);
		formLang = new FormLang(this);
		formLang.setLangChoice(locale.getLocale());
		formAbout = new FormAbout(this);

		canvasResults.repaint();

		if (back instanceof FormSearch) {
			back = formSearch;
		} else if (back instanceof CanvasResults) {
			back = canvasResults;
		}
	}

	/**
	 * Show form or canvas.
	 * 
	 * @param displayable
	 */
	private void show(Displayable displayable) {
		if (displayable == formSearch || displayable == canvasResults) {
			back = displayable;
		}

		display.setCurrent(displayable);
		displayable.setCommandListener((CommandListener) displayable);
	}

	/**
	 * Return back.
	 */
	public void back() {
		if (back != null) {
			show(back);
		} else {
			show(formSearch);
		}
	}

	/**
	 * Alert on display.
	 *
	 * @param title
	 * @param text
	 * @param type
	 */
	public void alert(String title, String text, AlertType type) {
		alert = new Alert(title, text, null, type);
		alert.setTimeout(Alert.FOREVER);
		display.setCurrent(alert, back == null ? formSearch : back);
	}

	/**
	 * Show search form.
	 */
	public void showSearch() {
		show(formSearch);
	}

	/**
	 * Show result canvas.
	 */
	public void showResults() {
		show(canvasResults);
	}

	/**
	 * Show lang form.
	 */
	public void showLang() {
		show(formLang);
	}

	/**
	 * Show about form.
	 */
	public void showAbout() {
		show(formAbout);
	}

	/**
	 * Exit midlet.
	 */
	public void exit() {
		destroyApp(true);
	}

	/**
	 * Set new locale.
	 * 
	 * @param locale
	 */
	public void setLocale(String locale) {
		try {
			if (this.locale.setLocale(locale)) {
				this.locale.closeRecords();
				initializeForms();
			}
		} catch (RecordStoreException e) {
			System.err.print(e);
			alert(translate("Chyba"), e.getMessage(), AlertType.ERROR);
		} catch (IOException e) {
			System.err.print(e);
			alert(translate("Chyba"), e.getMessage(), AlertType.ERROR);
		}
	}

	/**
	 * Get search class.
	 * 
	 * @return
	 */
	public Search getSearch() {
		return search;
	}

	/**
	 * Repaint result canvas.
	 */
	public void repaintResults() {
		canvasResults.repaint();
	}

	/**
	 * Start searching.
	 */
	public void startSearch() {
		canvasResults.startSearch();
	}

	/**
	 * Stop searching.
	 */
	public void stopSearch() {
		canvasResults.stopSearch();
	}

	/**
	 * Translate word in locale.
	 * 
	 * @param word
	 * @return
	 */
	public String translate(String word) {
		return locale.translate(word);
	}
}