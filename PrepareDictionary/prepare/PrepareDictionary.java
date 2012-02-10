package prepare;

import java.io.*;
import java.util.*;

/**
 * Prepare dicrionary and store output in data directory.
 *
 * @author Jakub Trmota (jakub@trmota.cz)
 */
class PrepareDictionary {
	/**
	 * Input encoding.
	 */
	public static final String INPUT_ENCODING = "UTF8";

	/**
	 * Output encoding.
	 */
	public static final String OUTPUT_ENCODING = "UTF8";

	/**
	 * Directory separator.
	 */
	public static String DIRECTORY_SEPARATOR = "/";

	/**
	 * Word separator.
	 */
	public static final String WORD_SEPARATOR = ":";

	/**
	 * Dictionary file.
	 */
	private String dictionaryFile = "";

	/**
	 * Lang 1.
	 */
	private String lang1 = "";

	/**
	 * Lang 2.
	 */
	private String lang2 = "";

	/**
	 * Prepare dictionary.
	 *
	 * @param args dictionary input file
	 */
	public static void main (String[] args) throws IOException {
		DIRECTORY_SEPARATOR = System.getProperty("file.separator");

		Locale.setDefault(new Locale("cs_CZ"));

		if (args.length < 3) {
			System.out.println("Use: PrepareDictionary \"input_file\" \"lang1\" \"lang2\"");
			return;
		}

		PrepareDictionary prepare = new PrepareDictionary(args[0], args[1], args[2]);
		prepare.prepare();
	}

	/**
	 * Initialize class.
	 *
	 * @param dictionaryFile
	 * @param lang1
	 * @param lang2
	 */
	private PrepareDictionary(String dictionaryFile, String lang1, String lang2) {
		this.dictionaryFile = dictionaryFile;
		this.lang1 = lang1;
		this.lang2 = lang2;
	}

	/**
	 * Make text and data files.
	 */
	private void prepare() {
		// Case insensitives word comparator
		Comparator<String> wordComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		};

		// Hash maps for translations
		SortedMap<String, Translate> map1 = new TreeMap<String, Translate>(wordComparator);
		SortedMap<String, Translate> map2 = new TreeMap<String, Translate>(wordComparator);

		// Read dictionary
		readDictionary(dictionaryFile, map1, map2);

		try {
			// Write text files
			writeText(map1, lang1);
			writeText(map2, lang2);

			// Write data files
			writeData(map1, lang1);
			writeData(map2, lang2);
		} catch (IOException e) {
			System.err.print(e);
		}
	}

	/**
	 * Read from dictionary file and save to maps.
	 *
	 * @param dictionaryFile
	 * @param map1
	 * @param map2
	 */
	private void readDictionary(String dictionaryFile, SortedMap<String, Translate> map1, SortedMap<String, Translate> map2) {
		Scanner dictionaryScanner = null; // Read translations to map for sorting

		try {
			dictionaryScanner = new Scanner(new FileInputStream(dictionaryFile), INPUT_ENCODING);

			while (dictionaryScanner.hasNextLine()){
				String line = dictionaryScanner.nextLine().trim();

				if (line.startsWith("#")) {
					continue;
				}

				String parts[] = line.split("\t");

				if (parts.length >= 2) {
					String wLang1 = parts[0].trim();
					String wLang2 = parts[1].trim();

					if (testWord(wLang1) && testWord(wLang2)) {
						Translate tLang1 = map1.get(wLang1);
						if (tLang1 == null) {
							tLang1 = new Translate(wLang2);
							map1.put(wLang1, tLang1);
						} else {
							tLang1.add(wLang2);
						}

						Translate tLang2 = map2.get(wLang2);
						if (tLang2 == null) {
							tLang2 = new Translate(wLang1);
							map2.put(wLang2, tLang2);
						} else {
							tLang2.add(wLang1);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			dictionaryScanner.close();
		}
	}

	/**
	 * Write text file.
	 *
	 * @param map
	 * @param lang
	 * @throws IOException
	 */
	public void writeText(SortedMap<String, Translate> map, String lang) throws IOException {
		OutputStreamWriter writer = null;

		try {
			writer = new OutputStreamWriter(new FileOutputStream("data" + DIRECTORY_SEPARATOR + lang + ".txt"), OUTPUT_ENCODING);

			Iterator iterator = map.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry m =(Map.Entry) iterator.next();

				String word = (String) m.getKey();
				Translate translate = (Translate) m.getValue();
				writer.write(word + ":" + translate.getTranslates() + "\n");
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			writer.close();
		}
	}

	/**
	 * Write data file.
	 *
	 * @param map
	 * @param lang
	 * @throws IOException
	 */
	public void writeData(SortedMap<String, Translate> map, String lang) throws IOException {
		DataOutputStream index = null;

		try {
			index = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + lang + ".index"));

			int fileIndex = 1;
			int wordsCount = 0;

			DataOutputStream data = null;
			try {
				Iterator iteratorEng = map.entrySet().iterator();
				while(iteratorEng.hasNext()) {
					Map.Entry m = (Map.Entry) iteratorEng.next();
					String word = (String) m.getKey();
					Translate translate = (Translate) m.getValue();

					if ((wordsCount % 1000) == 0) {
						if (wordsCount > 0) {
							data.close();
						}
						index.writeUTF(word.toLowerCase());
						index.writeShort(fileIndex);
						data = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + lang + fileIndex++ + ".dat"));
					}

					data.writeUTF(word + ":" + translate.getTranslates());
					wordsCount++;
				}
			} catch (Exception e) {
				System.err.print(e);
			} finally {
				data.close();
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			index.close();
		}
	}

	/**
	 * Return if word will be saved in distionary.
	 *
	 * @param word
	 * @return permit word
	 */
	public boolean testWord(String word) {
		if (word.isEmpty() || (word.length() < 2) || (word.length() > 40)) {
			return false;
		}

		return !word.toLowerCase().matches(".*[0-9:\"'\\!\\?\\.\\#\\*-=\\(\\)].*");
	}
}

/**
 * Class to save sorted translates.
 *
 * @author Jakub Trmota (jakub@trmota.cz)
 * @version 1.0.0 Beta (2010-11-26)
 */
class Translate {
	/**
	 * Sorted set with translates.
	 */
	SortedSet<String> translates = null;

	/**
	 * Basic constructor.
	 */
	public Translate() {
		translates = new TreeSet<String>();
	}

	/**
	 * Basic constructor and save translate word
	 *
	 * @param translate
	 */
	public Translate(String translate) {
		translates = new TreeSet<String>();
		translates.add(translate);
	}

	/**
	 * Add translate word.
	 *
	 * @param translate
	 */
	public void add(String translate) {
		translates.add(translate);
	}

	/**
	 * Return translates as string separated with WORD_SEPARATOR.
	 *
	 * @return translates
	 */
	public String getTranslates() {
		String[] sTranslates = (String[]) this.translates.toArray(new String[translates.size()]);
		if (sTranslates.length == 0) {
			return null;
		}

		StringBuilder translate = new StringBuilder(sTranslates[0]);
		for (int i = 1; i < sTranslates.length; i++) {
			translate.append(PrepareDictionary.WORD_SEPARATOR).append(sTranslates[i]);
		}

		return translate.toString();
	}
}
