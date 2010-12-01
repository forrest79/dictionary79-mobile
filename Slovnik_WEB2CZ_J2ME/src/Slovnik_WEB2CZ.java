import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class Slovnik_WEB2CZ extends MIDlet implements CommandListener {
	// Constants
	private static final String VERSION = "1.0.0";
	//private static final String URL = "http://forrest79/forrest79_net/_sub/web2cz";
	private static final String URL = "http://195.70.141.168/forrest79_net/_sub/web2cz";

	public static final int DICTIONARY_CZE = 0;
	public static final int DICTIONARY_ENG = 1;

	private static final int DICTIONARY_SEARCH = 1;
	private static final int RESULTS = 2;
	private static final int NET_SEARCH = 3;

	private int COUNT_WORDS = 0;

	// Classes
	private Settings settings = null;
	public Searching searching = null;

	// Display
	private Display disp = null;

	// Forms and canvases
	private Canvas canLoading = null;
	private Form frmDictionarySearch = null;
	public Canvas canResults = null;
	private Form frmNetSearch = null;
	private Form frmInfo = null;
	private Form frmVersion = null;

	// All
	private boolean run = false;
	private boolean results = false;
	private int back = 0;
	private int newSearch = 0;
	private Command cmdDictionarySearch = null;
	private Command cmdNetSearch = null;
	private Command cmdResults = null;
	private Command cmdInfo = null;
	private Command cmdEnd = null;
	private Alert alert = null;

	// Canvas loading
	public int loadingPercent = 0;

	// Form dictionary search
	private TextField frmDictionarySearch_txtWord = null;
	public ChoiceGroup frmDictionarySearch_chgDirection = null;
	public ChoiceGroup frmDictionarySearch_chgSettings = null;
	private ChoiceGroup frmDictionarySearch_chgSaveSettings = null;
	private Command frmDictionarySearch_cmdSearch = null;

	// Canvas results
	private Command canResults_cmdNewSearch = null;

	// Form net search
	private TextField frmNetSearch_txtWord = null;
	public ChoiceGroup frmNetSearch_chgDirection = null;
	public ChoiceGroup frmNetSearch_chgSettings = null;
	private ChoiceGroup frmNetSearch_chgSaveSettings = null;
	private Command frmNetSearch_cmdSearch = null;

	// Form info
    private StringItem frmInfo_strText;
	private Command frmInfo_cmdBack = null;
	private Command frmInfo_cmdVersion = null;
	private String textInfo = "";

	// Form version
    private StringItem frmVersion_strText;
	private Command frmVersion_cmdBack = null;

	public Slovnik_WEB2CZ() {
		// NOTHING TO DO HERE
	}

	public void startApp() {
		if(!run) {
			// Display
			disp = Display.getDisplay(this);

			// Canvas loading
			canLoading = new Loading(this);
			disp.setCurrent(canLoading);
			loadingDone(0);

			// All
			cmdDictionarySearch = new Command("Hledat ve slovnÌku", Command.SCREEN, 5);
			cmdNetSearch = new Command("Hledat na internetu", Command.SCREEN, 6);
			cmdResults = new Command("V˝sledky hled·nÌ", Command.SCREEN, 10);
			cmdInfo = new Command("Informace", Command.SCREEN, 20);
			cmdEnd = new Command("Konec", Command.SCREEN, 21);
			loadingDone(10);

			// Canvas loading 2
			canLoading.addCommand(cmdEnd);
			loadingDone(20);

			// Count words
			try {
				InputStream fis_words = this.getClass().getResourceAsStream("/eng");
				DataInputStream readerWords = new DataInputStream(fis_words);
				COUNT_WORDS = readerWords.readInt();
				readerWords.close();
			} catch(Exception e) {}
			loadingDone(30);

			// Form dictionary search
			frmDictionarySearch = new Form("Hledat slovÌËko");
				frmDictionarySearch_txtWord = new TextField("SlovÌËko:", "", 20, TextField.ANY);
				frmDictionarySearch_chgDirection = new ChoiceGroup("SmÏr p¯ekladu:", Choice.EXCLUSIVE);
				frmDictionarySearch_chgDirection.append("z angliËtiny do Ëeötiny", null);
				frmDictionarySearch_chgDirection.append("z Ëeötiny do angliËtiny", null);
				frmDictionarySearch_chgSettings = new ChoiceGroup("NastavenÌ:", Choice.MULTIPLE);
				frmDictionarySearch_chgSettings.append("hledat i podobn· slovÌËka", null);
				frmDictionarySearch_chgSaveSettings = new ChoiceGroup("", Choice.MULTIPLE);
				frmDictionarySearch_chgSaveSettings.append("uloûit nastavenÌ", null);
				frmDictionarySearch_cmdSearch = new Command("Hledej", Command.SCREEN, 0);
				frmDictionarySearch.append(frmDictionarySearch_txtWord);
				frmDictionarySearch.append(frmDictionarySearch_chgDirection);
				frmDictionarySearch.append(frmDictionarySearch_chgSettings);
				frmDictionarySearch.append(frmDictionarySearch_chgSaveSettings);
				frmDictionarySearch.addCommand(frmDictionarySearch_cmdSearch);
				frmDictionarySearch.addCommand(cmdNetSearch);
				frmDictionarySearch.addCommand(cmdInfo);
				frmDictionarySearch.addCommand(cmdEnd);
			loadingDone(40);

			// Canvas results
			canResults = new Results(this);
				canResults_cmdNewSearch = new Command("NovÈ", Command.SCREEN, 0);
				canResults.addCommand(canResults_cmdNewSearch);
				canResults.addCommand(cmdDictionarySearch);
				canResults.addCommand(cmdNetSearch);
				canResults.addCommand(cmdInfo);
				canResults.addCommand(cmdEnd);
			loadingDone(50);

			// Form net search
			frmNetSearch = new Form("Hledat na internetu");
				frmNetSearch_txtWord = new TextField("SlovÌËko:", "", 50, TextField.ANY);
				frmNetSearch_chgDirection = new ChoiceGroup("SmÏr p¯ekladu:", Choice.EXCLUSIVE);
				frmNetSearch_chgDirection.append("automaticky", null);
				frmNetSearch_chgDirection.append("z angliËtiny do Ëeötiny", null);
				frmNetSearch_chgDirection.append("z Ëeötiny do angliËtiny", null);
				frmNetSearch_chgSettings = new ChoiceGroup("NastavenÌ:", Choice.MULTIPLE);
				frmNetSearch_chgSettings.append("hledat i podobn· slovÌËka", null);
				frmNetSearch_chgSettings.append("hledat s vyuûitÌm logick˝ch oper·tor˘", null);
				frmNetSearch_chgSaveSettings = new ChoiceGroup("", Choice.MULTIPLE);
				frmNetSearch_chgSaveSettings.append("uloûit nastavenÌ", null);
				frmNetSearch_cmdSearch = new Command("Hledej", Command.SCREEN, 0);
				frmNetSearch.append(frmNetSearch_txtWord);
				frmNetSearch.append(frmNetSearch_chgDirection);
				frmNetSearch.append(frmNetSearch_chgSettings);
				frmNetSearch.append(frmNetSearch_chgSaveSettings);
				frmNetSearch.addCommand(frmNetSearch_cmdSearch);
				frmNetSearch.addCommand(cmdDictionarySearch);
				frmNetSearch.addCommand(cmdInfo);
				frmNetSearch.addCommand(cmdEnd);
			loadingDone(60);

			// Form info
			frmInfo = new Form("Informace");
				textInfo = "\nHLEDAT VE SLOVNÕKU\n- umoûÚuje hledat ve slovnÌku, kter˝ je souË·stÌ tohoto programu\n[SlovÌËko] zadejte hledan˝ v˝raz\n[SmÏr p¯ekladu] vyberte smÏr p¯ekladu\n[NastavenÌ: hledat i podobn· slovÌËka] hled· i slovÌËka, kter· obsahujÌ zadan˝ v˝raz\n[NastavenÌ: uloûit nastavenÌ] uloûÌ pouûitÈ nastavenÌ jako standartnÌ\n\n\nHLEDAT NA INTERNET\n- umoûÚuje vyhled·v·nÌ ve slovnÌku na internetu\n[SlovÌËko] zadejte hledan˝ v˝raz Ëi logick˝ v˝raz\n[SmÏr p¯ekladu] vyberte smÏr p¯ekladu. Pokud vyberete automaticky, program se pokusÌ vybrat vhodn˝ smÏr s·m.\n[NastavenÌ: hledat i podobn· slovÌËka] hled· i slovÌËka, kter· obsahujÌ zadan˝ v˝raz\n[NastavenÌ: hledat s vyuûitÌm logick˝ch oper·tor˘] pro vyhled·v·nÌ m˘ûete pouûÌt logick˝ v˝raz obsahujÌcÌ oper·tory AND, OR, NOT a z·vorky\n[NastavenÌ: uloûit nastavenÌ] uloûÌ pouûitÈ nastavenÌ jako standartnÌ\n\nV›SLEDKY HLED¡NÕ\n- rolov·nÌ v˝sledky nahoru a dolu pomocÌ öipek Ëi kl·ves 2 a 8\n- Page up / Page down pomocÌ kl·ves 1 a 7\n- ˙plnÏ nahoru a ˙plnÏ dolu pomocÌ kl·ves 3 a 9\n- kl·vesou pÏt nebo potvrzovacÌ kl·vesou zaËnÏte novÈ hled·nÌ\n\nDalöÌ informace naleznete na internetovÈ adrese " + URL + "/mobil";
				frmInfo_strText = new StringItem("Verze slovnÌku " + VERSION + "\n" + "PoËet slovÌËek: " + COUNT_WORDS + "\n", textInfo);
				frmInfo_cmdBack = new Command("ZpÏt", Command.SCREEN, 0);
				frmInfo_cmdVersion = new Command("Verze", Command.SCREEN, 1);
				frmInfo.append(frmInfo_strText);
				frmInfo.addCommand(frmInfo_cmdBack);
				frmInfo.addCommand(frmInfo_cmdVersion);
				frmInfo.addCommand(cmdEnd);
			loadingDone(70);

			// Form version
			frmVersion = new Form("Verze slovnÌku");
				frmVersion_strText = new StringItem("", "");
				frmVersion_cmdBack = new Command("ZpÏt", Command.SCREEN, 0);
				frmVersion.append(frmVersion_strText);
				frmVersion.addCommand(frmVersion_cmdBack);
				frmVersion.addCommand(cmdEnd);
			loadingDone(80);

			// Set classes
			settings = new Settings(this);
			searching = new Searching(this, canResults, COUNT_WORDS, URL);
			loadingDone(90);

			// Load settings
			settings.openDBSettings();
			settings.loadSettings();
			loadingDone(100);

			// Activate frmDictionarySearch
			frmDictionarySearch.setCommandListener(this);
			back = DICTIONARY_SEARCH;
			newSearch = DICTIONARY_SEARCH;
			disp.setCurrent(frmDictionarySearch);
			canLoading = null;

			run = true;
		} else {
			settings.openDBSettings();
		}
	}

	public void pauseApp() {
		try {
			settings.closeDBSettings();
		} catch(Exception e) {}
	}

	public void destroyApp(boolean unconditional) {
		try {
			settings.closeDBSettings();
		} catch(Exception e) {}
		notifyDestroyed();
	}

	public void commandAction(Command c, Displayable d) {
		// All
		if(c == cmdEnd) {
			destroyApp(true);
		} else if(c == cmdInfo) {
			frmInfo.setCommandListener(this);
			disp.setCurrent(frmInfo);
		} else if(c == cmdDictionarySearch) {
			back = DICTIONARY_SEARCH;
			newSearch = DICTIONARY_SEARCH;
			frmDictionarySearch.setCommandListener(this);
			disp.setCurrent(frmDictionarySearch);
		} else if(c == cmdNetSearch) {
			back = NET_SEARCH;
			newSearch = NET_SEARCH;
			frmNetSearch.setCommandListener(this);
			disp.setCurrent(frmNetSearch);
		} else if(c == cmdResults) {
			back = RESULTS;
			canResults.setCommandListener(this);
			disp.setCurrent(canResults);
		}

		// Form dictionary search
		if(c == frmDictionarySearch_cmdSearch) {
			if(frmDictionarySearch_txtWord.getString().length() > 1) {
				back = RESULTS;
				if(frmDictionarySearch_chgSaveSettings.isSelected(0)) settings.saveSettings();
				frmDictionarySearch_chgSaveSettings.setSelectedIndex(0, false);
				if(!results) {
					results = true;
					frmDictionarySearch.addCommand(cmdResults);
					frmNetSearch.addCommand(cmdResults);
				}
				canResults.setCommandListener(this);
				disp.setCurrent(canResults);
				searching.dictionarySearch(frmDictionarySearch_txtWord.getString(), frmDictionarySearch_chgDirection.getSelectedIndex(), frmDictionarySearch_chgSettings.isSelected(0));
			} else {
				alert = new Alert("Hled·nÌ ve slovnÌku", "Hledan˝ v˝raz musÌ mÌt vÌce jak dva znaky!", null, AlertType.INFO);
				alert.setTimeout(Alert.FOREVER);
				disp.setCurrent(alert, frmDictionarySearch);
			}
		}

		// Canvas results
		if(c == canResults_cmdNewSearch) {
			newSearch();
		}

		// Form net search
		if(c == frmNetSearch_cmdSearch) {
			if(frmNetSearch_txtWord.getString().length() > 1) {
				back = RESULTS;
				if(frmNetSearch_chgSaveSettings.isSelected(0)) settings.saveSettings();
				frmNetSearch_chgSaveSettings.setSelectedIndex(0, false);
				if(!results) {
					results = true;
					frmDictionarySearch.addCommand(cmdResults);
					frmNetSearch.addCommand(cmdResults);
				}
				canResults.setCommandListener(this);
				disp.setCurrent(canResults);
				searching.netSearch(frmNetSearch_txtWord.getString(), frmNetSearch_chgDirection.getSelectedIndex(), frmNetSearch_chgSettings.isSelected(0), frmNetSearch_chgSettings.isSelected(1));
			} else {
				alert = new Alert("Hled·nÌ na internetu", "Hledan˝ v˝raz musÌ mÌt vÌce jak dva znaky!", null, AlertType.INFO);
				alert.setTimeout(Alert.FOREVER);
				disp.setCurrent(alert, frmNetSearch);
			}
		}

		// Form info
		if(c == frmInfo_cmdBack) {
			if(back == DICTIONARY_SEARCH) {
				frmDictionarySearch.setCommandListener(this);
				disp.setCurrent(frmDictionarySearch);
			} else if(back == RESULTS) {
				canResults.setCommandListener(this);
				disp.setCurrent(canResults);
			} else if(back == NET_SEARCH) {
				frmNetSearch.setCommandListener(this);
				disp.setCurrent(frmNetSearch);
			}
		} else if(c == frmInfo_cmdVersion) {
			frmVersion.setCommandListener(this);
			frmVersion_strText.setText("Zjiöùuji aktu·lnÌ verzi...\nVydrûte prosÌm chviliËku.");
			disp.setCurrent(frmVersion);

			try {
				CheckVersion checkVersion = new CheckVersion(frmVersion_strText, VERSION, URL);
				Thread threadCheckVersion = new Thread(checkVersion);
				threadCheckVersion.start();
			} catch(Exception e) {}
		}

		// Form version
		if(c == frmVersion_cmdBack) {
			frmInfo.setCommandListener(this);
			disp.setCurrent(frmInfo);
		}
	}

	// New search
	public void newSearch() {
		if(newSearch == DICTIONARY_SEARCH) {
			frmDictionarySearch.setCommandListener(this);
			disp.setCurrent(frmDictionarySearch);
		} else if(newSearch == NET_SEARCH) {
			frmNetSearch.setCommandListener(this);
			disp.setCurrent(frmNetSearch);
		}
	}

	// Loading
	public void loadingDone(int percent) {
		loadingPercent = percent;
		canLoading.repaint();
	}
}