package dictionary.locale;

/**
 * English locale.
 *
 * @author Jakub Trmota
 */
public final class LocaleEn extends BaseLocale {
	/**
	 * Initialization english locale.
	 */
	public LocaleEn() {
		super();

		locales.put("Slovíčko", "Word");
		locales.put("Hledat slovíčko", "Search word");
		locales.put("Chyba", "Error");
		locales.put("Konec", "Exit");
	}
}
