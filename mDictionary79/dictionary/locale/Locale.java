package dictionary.locale;

import javax.microedition.rms.*;

/**
 * Locale class.
 *
 * @author Jakub Trmota | Forrest79
 */
public final class Locale {
	/**
	 * CS lang.
	 */
	public static final String CS = "cs";

	/**
	 * EN lang.
	 */
	public static final String EN = "en";

	/**
	 * Record lang id.
	 */
	public static final int LANG_RECORD_ID = 1;

	/**
	 * Records.
	 */
	private RecordStore records = null;

	/**
	 * Class with translations.
	 */
	private BaseLocale locale = null;

	/**
	 * Actual locale id.
	 */
	private String localeId = "";

	/**
	 * Indicate first run of application.
	 */
	private boolean firstRun = false;

	/**
	 * Initialize locale class and load locale id.
	 */
	public Locale() throws RecordStoreException {
		openRecords();

		if (records.getNumRecords() == 0) {
			firstRun = true;

			if (System.getProperty("microedition.locale").startsWith("cs")) {
				records.addRecord(Locale.CS.getBytes(), 0, Locale.CS.getBytes().length);
			} else {
				records.addRecord(Locale.EN.getBytes(), 0, Locale.EN.getBytes().length);
			}
		}

		byte[] byteLang = new byte[records.getRecordSize(LANG_RECORD_ID)];
		records.getRecord(1, byteLang, 0);

		setLocale(new String(byteLang));
	}

	/**
	 * Translate word.
	 * 
	 * @param word
	 * @return
	 */
	public String translate(String word) {
		if (locale == null) {
			return "!" + word;
		}

		String translate = locale.translate(word);

		if (translate == null) {
			return "!" + word;
		}

		return translate;
	}

	/**
	 * Set locale.
	 * 
	 * @param locale
	 * @return true if locale is changed
	 */
	public boolean setLocale(String localeId) throws RecordStoreException {
		if (this.localeId.equals(localeId)) {
			return false;
		}

		if (localeId.equals(CS)) {
			this.locale = new LocaleCs();
			this.localeId = CS;
		} else {
			this.locale = new LocaleEn();
			this.localeId = EN;
		}

		records.setRecord(LANG_RECORD_ID, this.localeId.getBytes(), 0, this.localeId.getBytes().length);

		return true;
	}

	/**
	 * Open records.
	 *
	 * @throws RecordStoreException
	 */
	public void openRecords() throws RecordStoreException {
		records = RecordStore.openRecordStore("locale", true);
	}

	/**
	 * Close records.
	 *
	 * @throws RecordStoreNotOpenException
	 * @throws RecordStoreException
	 */
	public void closeRecords() throws RecordStoreNotOpenException, RecordStoreException {
		records.closeRecordStore();
	}

	/**
	 * Get locale.
	 *
	 * @return locale id
	 */
	public String getLocale() {
		return localeId;
	}

	/**
	 * Indicate first run of application.
	 *
	 * @return
	 */
	public boolean firstRun() {
		return firstRun;
	}
}
