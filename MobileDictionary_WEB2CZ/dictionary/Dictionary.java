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

	public CanvasResults getResults() {
		return canvasResults;
	}

	public String translate(String word) {
		return locale.translate(word);
	}

	public static String lowerCase(String str) {
		if(str == null) {
			return "";
		} else {
			str = str.toLowerCase();
		}

		StringBuffer lower = new StringBuffer(str.length());
		char c, n;
		for(int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			switch (c) {
				case 193: n = 225; break; // Á
				case 201: n = 233; break; // É
				case 205: n = 237; break; // Í
				case 211: n = 243; break; // Ó
				case 218: n = 250; break; // Ú
				case 221: n = 253; break; // Ý
				case 268: n = 269; break; // Č
				case 270: n = 271; break; // Ď
				case 282: n = 283; break; // Ě
				case 327: n = 328; break; // Ň
				case 344: n = 345; break; // Ř
				case 352: n = 353; break; // Š
				case 356: n = 357; break; // Ť
				case 366: n = 367; break; // Ů
				case 381: n = 382; break; // Ž
				default: n = c;
			}
			lower.append(n);
		}

		return lower.toString();
	}
}