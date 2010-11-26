package prepare;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * Prepare dictionary.
	 *
	 * @param args dictionary input file
	 */
	public static void main (String[] args) throws IOException {
		DIRECTORY_SEPARATOR = System.getProperty("file.separator");

		// Dictinonary input filename
		String dictionaryInput = "slovnik_data_uft8.txt";
		if((args.length >= 1) && !args[0].isEmpty()) {
			dictionaryInput = args[0];
		}

		// Case insensitives word comparator
		Comparator<String> wordComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.toLowerCase().compareTo(s2.toLowerCase());
			}
		};

		// Hash maps for translations
		SortedMap<String, Translate> mapEng = new TreeMap<String, Translate>(wordComparator);
		SortedMap<String, Translate> mapCze = new TreeMap<String, Translate>(wordComparator);

		// Read translations to map for sorting
		Scanner dictionaryScanner = null;
		try {
			dictionaryScanner = new Scanner(new FileInputStream(dictionaryInput), INPUT_ENCODING);

			String line = "";
			while (dictionaryScanner.hasNextLine()){
				line = dictionaryScanner.nextLine().trim();

				if (line.startsWith("#")) {
					continue;
				}

				String parts[] = line.split("\t");

				if (parts.length >= 2) {
					String eng = parts[0].trim();
					String cze = parts[1].trim();

					if (testWord(eng) && testWord(cze)) {
						Translate tEng = mapEng.get(eng);
						if (tEng == null) {
							tEng = new Translate(cze);
							mapEng.put(eng, tEng);
						} else {
							tEng.add(cze);
						}

						Translate tCze = mapCze.get(cze);
						if (tCze == null) {
							tCze = new Translate(eng);
							mapCze.put(cze, tCze);
						} else {
							tCze.add(eng);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			dictionaryScanner.close();
		}

		// Write eng text file
		OutputStreamWriter writerEng = null;
		try {
			writerEng = new OutputStreamWriter(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "eng.txt"), OUTPUT_ENCODING);

			Iterator iteratorEng = mapEng.entrySet().iterator();
			while(iteratorEng.hasNext()) {
				Map.Entry m =(Map.Entry) iteratorEng.next();

				String word = (String) m.getKey();
				Translate translate = (Translate) m.getValue();
				writerEng.write(word + ":" + translate.getTranslates() + "\n");
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			writerEng.close();
		}

		// Write cze text file
		OutputStreamWriter writerCze = null;
		try {
			writerCze = new OutputStreamWriter(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "cze.txt"), OUTPUT_ENCODING);

			Iterator iteratorCze = mapCze.entrySet().iterator();
			while(iteratorCze.hasNext()) {
				Map.Entry m =(Map.Entry) iteratorCze.next();

				String word = (String) m.getKey();
				Translate translate = (Translate) m.getValue();
				writerCze.write(word + ":" + translate.getTranslates() + "\n");
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			writerCze.close();
		}

		// Write data eng files
		DataOutputStream indexEng = null;
		try {
			indexEng = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "eng.index"));

			int fileIndex = 1;
			int wordsCount = 0;

			DataOutputStream dataEng = null;
			try {
				Iterator iteratorEng = mapEng.entrySet().iterator();
				while(iteratorEng.hasNext()) {
					Map.Entry m = (Map.Entry) iteratorEng.next();
					String word = (String) m.getKey();
					Translate translate = (Translate) m.getValue();

					if ((wordsCount % 1000) == 0) {
						if (wordsCount > 0) {
							dataEng.close();
						}
						indexEng.writeUTF(word);
						indexEng.writeShort(fileIndex);
						dataEng = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "eng" + fileIndex++ + ".dat"));
					}

					dataEng.writeUTF(word + ":" + translate.getTranslates());
					wordsCount++;
				}
			} catch (Exception e) {
				System.err.print(e);
			} finally {
				dataEng.close();
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			indexEng.close();
		}

		// Write data cze files
		DataOutputStream indexCze = null;
		try {
			indexCze = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "cze.index"));

			int fileIndex = 1;
			int wordsCount = 0;

			DataOutputStream dataCze = null;
			try {
				Iterator iteratorCze = mapCze.entrySet().iterator();
				while(iteratorCze.hasNext()) {
					Map.Entry m = (Map.Entry) iteratorCze.next();
					String word = (String) m.getKey();
					Translate translate = (Translate) m.getValue();

					if ((wordsCount % 1000) == 0) {
						if (wordsCount > 0) {
							dataCze.close();
						}
						indexCze.writeUTF(word);
						indexCze.writeShort(fileIndex);
						dataCze = new DataOutputStream(new FileOutputStream("data" + DIRECTORY_SEPARATOR + "cze" + fileIndex++ + ".dat"));
					}

					dataCze.writeUTF(word + ":" + translate.getTranslates());
					wordsCount++;
				}
			} catch (Exception e) {
				System.err.print(e);
			} finally {
				dataCze.close();
			}
		} catch (Exception e) {
			System.err.print(e);
		} finally {
			indexCze.close();
		}
	}

	/**
	 * Return if word will be saved in distionary.
	 *
	 * @param word
	 * @return permit word
	 */
	public static boolean testWord(String word) {
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
	 * Return translates as string separated with ":"
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
			translate.append(":").append(sTranslates[i]);
		}

		return translate.toString();
	}
}
