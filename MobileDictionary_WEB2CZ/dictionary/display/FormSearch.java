package dictionary.display;

import dictionary.Dictionary;
import javax.microedition.lcdui.*;

/**
 * Form search.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class FormSearch extends Form implements CommandListener {
	/**
	 * Dictionary midlet.
	 */
	private Dictionary dictionary = null;

	/**
	 * Text fielt word to translate.
	 */
	private TextField txtWord = null;

	/**
	 * Choise group direction.
	 */
	private ChoiceGroup chgDirection = null;

	/**
	 * Command search.
	 */
	private Command cmdSearch = null;

	/**
	 * Command show results.
	 */
	private Command cmdResults = null;

	/**
	 * Command lang.
	 */
	private Command cmdLang = null;

	/**
	 * Command about.
	 */
	private Command cmdAbout = null;

	/**
	 * Command exit.
	 */
	private Command cmdExit = null;

	/**
	 * Form search initialization.
	 * 
	 * @param dictionary
	 */
	public FormSearch(Dictionary dictionary) {
		super(dictionary.translate("Hledat slovíčko"));

		this.dictionary = dictionary;

		initialize();
	}

	/**
	 * Initialize components.
	 */
	public void initialize() {
		txtWord = new TextField(dictionary.translate("Slovíčko") + ":", "", 50, TextField.ANY);
		chgDirection = new ChoiceGroup(dictionary.translate("Směr překladu") + ":", Choice.EXCLUSIVE);
		chgDirection.append(dictionary.translate("z angličtiny do češtiny"), null);
		chgDirection.append(dictionary.translate("z češtiny do angličtiny"), null);
		cmdSearch = new Command(dictionary.translate("Hledej"), Command.SCREEN, 0);
		cmdResults = new Command(dictionary.translate("Výsledky"), Command.SCREEN, 1);
		cmdLang = new Command(dictionary.translate("Jazyk"), Command.SCREEN, 2);
		cmdAbout = new Command(dictionary.translate("O slovníku"), Command.SCREEN, 3);
		cmdExit = new Command(dictionary.translate("Konec"), Command.SCREEN, 4);

		append(txtWord);
		append(chgDirection);
		addCommand(cmdSearch);
		addCommand(cmdLang);
		addCommand(cmdAbout);
		addCommand(cmdExit);
	}

	/**
	 * Reinitialize components.
	 */
	public void reinitialize() {
		deleteAll();
		removeCommand(cmdSearch);
		removeCommand(cmdLang);
		removeCommand(cmdAbout);
		removeCommand(cmdExit);

		initialize();
	}

	/**
	 * Action listener.
	 * 
	 * @param c
	 * @param d
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == cmdExit) {
			dictionary.exit();
		} else if (c == cmdLang) {
			dictionary.showLang();
		} else if (c == cmdAbout) {
			dictionary.showAbout();
		} else if (c == cmdSearch) {
			if (txtWord.getString().length() > 1) {
				addCommand(cmdResults);
				dictionary.showResults();
				dictionary.getSearch().search(txtWord.getString(), chgDirection.getSelectedIndex());
			} else {
				dictionary.alert(dictionary.translate("Hledání ve slovníku"), dictionary.translate("Hledaný výraz musí obsahovat alespoň dva znaky."), AlertType.WARNING);
			}
		} else if (c == cmdResults) {
			dictionary.showResults();
		}
	}
}
