package dictionary.display;

import dictionary.Dictionary;
import dictionary.locale.Locale;
import javax.microedition.lcdui.*;

/**
 * Form lang.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class FormLang extends Form implements CommandListener {
	/**
	 * Dictionary midlet.
	 */
	private Dictionary dictionary = null;

	/**
	 * Lang choice group.
	 */
	private ChoiceGroup chgLang = null;

	/**
	 * Command save.
	 */
	private Command cmdSave = null;

	/**
	 * Command back.
	 */
	private Command cmdBack = null;

	/**
	 * Initialize form lang.
	 * 
	 * @param dictionary
	 */
	public FormLang(Dictionary dictionary) {
		super(dictionary.translate("Jazyk"));
		
		this.dictionary = dictionary;

		initialize();
	}

	/**
	 * Initialize components.
	 */
	public void initialize() {
		chgLang = new ChoiceGroup(dictionary.translate("Jazyk") + ":", Choice.EXCLUSIVE);
		chgLang.append(dictionary.translate("česky"), null);
		chgLang.append(dictionary.translate("anglicky"), null);
		cmdSave = new Command(dictionary.translate("Uložit"), Command.SCREEN, 0);
		cmdBack = new Command(dictionary.translate("Zpět"), Command.SCREEN, 1);

		append(chgLang);
		addCommand(cmdSave);
		addCommand(cmdBack);
	}

	/**
	 * Reinitialize components.
	 */
	public void reinitialize() {
		deleteAll();
		removeCommand(cmdSave);
		removeCommand(cmdBack);

		initialize();
	}

	/**
	 * Action listener.
	 * 
	 * @param c
	 * @param d
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == cmdSave) {
			dictionary.back();
			dictionary.setLocale(getLangChoice());
		} else if (c == cmdBack) {
			dictionary.back();
		}
	}

	/**
	 * Set lang choice.
	 * 
	 * @param lang
	 */
	public void setLangChoice(String lang) {
		if (lang.equals(Locale.CS)) {
			chgLang.setSelectedIndex(0, true);
		} else {
			chgLang.setSelectedIndex(1, true);
		}
	}

	/**
	 * Get lang id.
	 * 
	 * @return lang id
	 */
	private String getLangChoice() {
		if (chgLang.getSelectedIndex() == 0) {
			return Locale.CS;
		} else {
			return Locale.EN;
		}
	}
}
