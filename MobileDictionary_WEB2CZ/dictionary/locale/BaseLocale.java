package dictionary.locale;

import java.util.Hashtable;

/**
 * Base locale class.
 *
 * @author Jakub Trmota
 */
abstract class BaseLocale {
	/**
	 * Hashtable with locales.
	 */
	protected Hashtable locales = null;

	/**
	 * Locale initialization.
	 */
	public BaseLocale() {
		locales = new Hashtable();
	}

	/**
	 * Translate word.
	 * 
	 * @param word
	 * @return
	 */
	public String translate(String word) {
		return (String) locales.get(word);
	}
}
