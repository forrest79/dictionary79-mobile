package dictionary.display;

import dictionary.Dictionary;
import javax.microedition.lcdui.*;

/**
 *
 * @author Forrest79
 */
public final class FormSearch extends Form implements CommandListener {

	private Dictionary dictionary = null;

	private TextField txtWord = null;

	private ChoiceGroup chgDirection = null;

	private Command cmdResults = null;

	private Command cmdSearch = null;

	private Command cmdLang = null;
	
	private Command cmdAbout = null;

	private Command cmdExit = null;

	public FormSearch(Dictionary dictionary) {
		super(dictionary.translate("Hledat slovíčko"));

		this.dictionary = dictionary;

		txtWord = new TextField(dictionary.translate("Slovíčko") + ":", "", 50, TextField.ANY);
		chgDirection = new ChoiceGroup(dictionary.translate("Směr překladu") + ":", Choice.EXCLUSIVE);
		chgDirection.append(dictionary.translate("z angličtiny do češtiny"), null);
		chgDirection.append(dictionary.translate("z češtiny do angličtiny"), null);
		cmdResults = new Command(dictionary.translate("Výsledky"), Command.SCREEN, 0);
		cmdSearch = new Command(dictionary.translate("Hledej"), Command.SCREEN, 1);
		cmdLang = new Command(dictionary.translate("Jazyk"), Command.SCREEN, 2);
		cmdAbout = new Command(dictionary.translate("O slovníku"), Command.SCREEN, 3);
		cmdExit = new Command(dictionary.translate("Konec"), Command.SCREEN, 4);

		this.append(txtWord);
		this.append(chgDirection);
		this.addCommand(cmdSearch);
		this.addCommand(cmdLang);
		this.addCommand(cmdAbout);
		this.addCommand(cmdExit);
	}

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
