package dictionary;

import java.io.*;
import java.util.Vector;

public class Search {
	public static final int ENG_CZE = 0;
	public static final int CZE_ENG = 1;

	public static final String ENG = "eng";
	public static final String CZE = "cze";

	public static final int NOTICE_OK = 0;
	public static final int NOTICE_NO_RESULTS = 1;
	public static final int NOTICE_ERROR = 2;

	private Dictionary dictionary = null;

	private static final int MAX_RESULTS = 50;

	public Vector original = null;
	public Vector translate = null;
	public Vector heights = null;

	public boolean searching_run = false;
	public int searching_image = 0;

	public int searchNotice = NOTICE_OK;

	public int words_height_all = 0;
	public int words_position = 0;

	private DictionarySearch dictionarySearch = null;

	private boolean stopSearching = false;

	private Vector indexesCze = null;
	private Vector indexesEng = null;

	public Search(Dictionary dictionary) throws IOException {
		this.dictionary = dictionary;

		original = new Vector();
		translate = new Vector();
		heights = new Vector();

		indexesCze = new Vector();		
		DataInputStream disIndexesCze = new DataInputStream(this.getClass().getResourceAsStream("/data/cze.index"));
		while (true) {
			try {
				Index index = new Index(disIndexesCze.readUTF(), disIndexesCze.readShort());
				indexesCze.addElement(index);
			} catch (EOFException e) {
				break;
			}
		}
		disIndexesCze.close();

		indexesEng = new Vector();
		DataInputStream disIndexesEng = new DataInputStream(this.getClass().getResourceAsStream("/data/eng.index"));
		while (true) {
			try {
				Index index = new Index(disIndexesEng.readUTF(), disIndexesEng.readShort());
				indexesEng.addElement(index);
			} catch (EOFException e) {
				break;
			}
		}
		disIndexesEng.close();
	}

	public void search(String word, int direction) {
		dictionarySearch = new DictionarySearch(word, direction);
		Thread threadDictionarySearch = new Thread(dictionarySearch);
		threadDictionarySearch.start();
	}

	private class DictionarySearch extends Thread {
		private String searchWord;
		private String file;
		private int fileIndex;

		private DictionarySearch(String searchWord, int direction) {
			this.searchWord = Dictionary.lowerCase(searchWord);

			if(direction == ENG_CZE) {
				file = ENG;
				for (int i = indexesEng.size() - 1; i >= 0; i--) {
					Index index = (Index) indexesEng.elementAt(i);
					if (index.getWord().compareTo(searchWord) < 0) {
						fileIndex = index.getFile();
						break;
					}
				}
			} else if(direction == CZE_ENG) {
				file = CZE;
				for (int i = indexesCze.size() - 1; i >= 0; i--) {
					Index index = (Index) indexesCze.elementAt(i);
					if (searchWord.compareTo(index.getWord()) < 0) {
						fileIndex = index.getFile();
						break;
					}
				}
			}
		}

		public void run() {
			search();
		}

		private void search() {
			startSearch();

			boolean resultsError = false;

			try {
				for (int index = fileIndex; index <= fileIndex + 1; index++) {
					InputStream dataFile = dictionary.getClass().getResourceAsStream("/data/" + file + index + ".dat");
					if (dataFile != null) {
						DataInputStream reader = new DataInputStream(dataFile);

						String word = "";

						int found = 0;

						stopSearching = false;
						while(true) {
							if((found == MAX_RESULTS) || stopSearching) {
								break;
							}

							try {
								word = reader.readUTF();

								if(Dictionary.lowerCase(word).startsWith(this.searchWord)) {
									String[] words = split(word, ":");

									for (int i = 1; i < words.length; i++) {
										original.addElement(words[0]);
										translate.addElement(words[i]);
										System.out.println(file + ": " + words[0] + " - " + words[i]);
									}
									dictionary.getResults().repaint();
									found++;
								}
							} catch (EOFException e) {
								break;
							}
						}

						reader.close();
					}
				}
			} catch(Exception e) {
				resultsError = true;
			}

			stopSearch();

			if(resultsError) {
				searchNotice = NOTICE_ERROR;
			}  else if (translate.isEmpty()) {
				searchNotice = NOTICE_NO_RESULTS;
			}
		}
		
		private String[] split(String original, String separator) {
			Vector nodes = new Vector();

			int index = original.indexOf(separator);
			while (index >= 0) {
				nodes.addElement(original.substring(0, index));
				original = original.substring(index + separator.length());
				index = original.indexOf(separator);
			}
			nodes.addElement(original);

			String[] result = new String[nodes.size()];
			if (nodes.size() > 0) {
				for(int loop=0; loop < nodes.size(); loop++) {
					result[loop] = (String)nodes.elementAt(loop);
				}
			}

			return result;
		}
	}

	private void startSearch() {
		heights.removeAllElements();
		original.removeAllElements();
		translate.removeAllElements();

		words_height_all = 0;
		words_position = 0;

		searchNotice = NOTICE_OK;

		searching_run = true;

		dictionary.getResults().repaint();
	}

	private void stopSearch() {
		searching_run = false;
		searching_image = 0;

		dictionary.getResults().repaint();
	}

	public void cancelSearching() {
		if(dictionarySearch != null) {
			stopSearching = true;
			dictionarySearch.interrupt();
			dictionarySearch = null;
		}
	}

	private final class Index {
		
		private String word = "";
		private short file = 0;

		public Index(String word, short file) {
			this.word = word;
			this.file = file;
		}

		public short getFile() {
			return file;
		}

		public String getWord() {
			return word;
		}
	}
}