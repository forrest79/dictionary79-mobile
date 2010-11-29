package dictionary;

import dictionary.display.*;
import dictionary.locale.Locale;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

public class Dictionary extends MIDlet {
	public static final String VERSION = "2.0 Beta";

	private Locale locale = null;
	private Search search = null;

	private Display display = null;

	private CanvasLoading canvasLoading = null;
	private CanvasResults canvasResults = null;
	private FormSearch formSearch = null;
	private FormLang formLang = null;
	private FormAbout formAbout = null;

	private Displayable back = null;

	private Alert alert = null;

	private boolean run = false;

	public void startApp() {
		try {
			if(!run) {
				display = Display.getDisplay(this);

				locale = new Locale();

				canvasLoading = new CanvasLoading(this);
				canvasLoading.start();

				show(canvasLoading);

				search = new Search(this, canvasResults);

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

	public void pauseApp() {
		try {
			locale.closeRecords();
		} catch(Exception e) {
			System.err.print(e);
		}
	}

	public void destroyApp(boolean unconditional) {
		try {
			locale.closeRecords();
		} catch(Exception e) {
			System.err.print(e);
		}
		
		notifyDestroyed();
	}

	private void initializeForms() {
		formSearch = new FormSearch(this);
		formLang = new FormLang(this);
		formLang.setLangChoice(locale.getLocale());
		formAbout = new FormAbout(this);

		if (back instanceof FormSearch) {
			back = formSearch;
		}
	}

	private void show(Displayable displayable) {
		if (displayable == formSearch || displayable == canvasResults) {
			back = displayable;
		}

		display.setCurrent(displayable);
		displayable.setCommandListener((CommandListener) displayable);
	}

	public void back() {
		if (back != null) {
			show(back);
		} else {
			show(formSearch);
		}
	}

	public void alert(String title, String text, AlertType type) {
		alert = new Alert(title, text, null, type);
		alert.setTimeout(Alert.FOREVER);
		display.setCurrent(alert, back == null ? formSearch : back);
	}

	public void showSearch() {
		show(formSearch);
	}

	public void showResults() {
		show(canvasResults);
	}

	public void showLang() {
		show(formLang);
	}

	public void showAbout() {
		show(formAbout);
	}

	public void exit() {
		destroyApp(true);
	}

	public void setLocale(String locale) {
		try {
			if (this.locale.setLocale(locale)) {
				this.locale.closeRecords();
				initializeForms();
			}
		} catch (RecordStoreException e) {
			System.err.print(e);
			alert("Chyba", e.getMessage(), AlertType.ERROR);
		}
	}

	public Search getSearch() {
		return search;
	}

	public String translate(String word) {
		return locale.translate(word);
	}
}