package dictionary.display;

import dictionary.Dictionary;
import javax.microedition.lcdui.*;

/**
 * Form about dictionary.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class FormAbout extends Form implements CommandListener {
	/**
	 * Dictionary midlet.
	 */
	private Dictionary dictionary = null;

	/**
	 * String item about.
	 */
  private StringItem strAbout = null;

	/**
	 * Command button back.
	 */
	private Command cmdBack = null;

	/**
	 * Initialization form about.
	 *
	 * @param dictionary
	 */
	public FormAbout(Dictionary dictionary) {
		super(dictionary.translate("O slovníku"));

		this.dictionary = dictionary;

		strAbout = new StringItem(dictionary.translate("Verze slovníku") + ": " + Dictionary.VERSION + "\n", dictionary.translate("O slovníku: text"));
		cmdBack = new Command(dictionary.translate("Zpět"), Command.SCREEN, 0);

		this.append(strAbout);
		this.addCommand(cmdBack);
	}

	/**
	 * Action listener.
	 * 
	 * @param c
	 * @param d
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == cmdBack) {
			dictionary.back();
		}
	}
}
