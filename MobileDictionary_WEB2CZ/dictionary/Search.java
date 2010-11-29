package dictionary;

import javax.microedition.lcdui.*;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

public class Search {
	public static final int DICTIONARY_CZE = 0;
	public static final int DICTIONARY_ENG = 1;

	public static final int NOTICE_OK = 0;
	public static final int NOTICE_NO_RESULTS = 1;
	public static final int NOTICE_ERROR = 2;

	private Dictionary dictionary = null;
	private Canvas results = null;
	private int COUNT_WORDS = 0;

	private static final int MAX_RESULTS = 50;

	private Timer timer_searching = null;
	private TimerTask task_searching = null;

	public Vector original = null;
	public Vector translate = null;
	public Vector heights = null;

	public int results_original;
	public int results_translate;

	public boolean searching_run = false;
	public int searching_image = 0;

	public int search_notice = NOTICE_OK;

	public int words_height_all = 0;
	public int words_position = 0;

	private DictionarySearch dictionarySearch = null;

	private boolean stopSearching = false;

	private Vector indexesCze = null;
	private Vector indexesEng = null;

	public Search(Dictionary dictionary, Canvas results) throws IOException {
		this.dictionary = dictionary;
		this.results = results;

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

		private DictionarySearch(String word, int direction) {
			//this.word = czUTF_toLowerCase(word);
			this.searchWord = word.toLowerCase();

			if(direction == 0) {
				file = "eng";
				for (int i = indexesEng.size() - 1; i >= 0; i--) {
					Index index = (Index) indexesEng.elementAt(i);
					if (word.compareTo(index.getWord()) > 0) {
						file += index.getFile() + ".dat";
					}
				}

				results_original = DICTIONARY_ENG;
				results_translate = DICTIONARY_CZE;
			} else if(direction == 1) {
				file = "cze";
				for (int i = indexesCze.size() - 1; i >= 0; i--) {
					Index index = (Index) indexesCze.elementAt(i);
					if (word.compareTo(index.getWord()) > 0) {
						file += index.getFile() + ".dat";
					}
				}

				results_original = DICTIONARY_CZE;
				results_translate = DICTIONARY_ENG;
			}
		}

		public void run() {
			try {
				search();
			} catch (Exception e) {}
		}

		private void search() {
			startSearch();

			boolean results_error = false;

			try {
				DataInputStream reader = new DataInputStream(dictionary.getClass().getResourceAsStream("/data/" + file));

				String word = "";

				int found = 0;

				stopSearching = false;
				while(true) {
					try {
						word = reader.readUTF();

						if(word.startsWith(this.searchWord)) {
							String[] words = split(word, ":");

							for (int i = 1; i < words.length; i++) {
								original.addElement(words[0]);
								translate.addElement(words[i]);
								System.out.println(file + ": " + words[0] + " - " + words[i]);
							}
							results.repaint();
							found++;
						}

						if((found == MAX_RESULTS) || stopSearching) {
							break;
						}
					} catch (EOFException e) {
						break;
					}
				}

				reader.close();
			} catch(Exception e) {
				results_error = true;
			}

			stopSearch();

			if(results_error) {
				search_notice = NOTICE_ERROR;
			}  else if (translate.isEmpty()) {
				search_notice = NOTICE_NO_RESULTS;
			}
		}
		
		private String[] split(String original, String separator) {
			Vector nodes = new Vector();
			// Parse nodes into vector
			int index = original.indexOf(separator);
			while (index >= 0) {
				nodes.addElement(original.substring(0, index));
				original = original.substring(index + separator.length());
				index = original.indexOf(separator);
			}
			// Get the last node
			nodes.addElement(original);

			// Create splitted string array
			String[] result = new String[nodes.size()];
			if (nodes.size() > 0) {
				for(int loop=0; loop < nodes.size(); loop++) {
					result[loop] = (String)nodes.elementAt(loop);
				}
			}

			return result;
		}
	}


	private String czUTF_toLowerCase(String utf) {
		if(utf == null) return utf;
		else utf = utf.toLowerCase();

		StringBuffer lower = new StringBuffer(utf.length());
		try {
			char c, n;
			int i;
			for(i = 0; i < utf.length(); i++) {
				c = utf.charAt(i);

				if(c == 193) n = 225;
				else if(c == 268) n = 269;
				else if(c == 270) n = 271;
				else if(c == 201) n = 233;
				else if(c == 282) n = 283;
				else if(c == 205) n = 237;
				else if(c == 327) n = 328;
				else if(c == 211) n = 243;
				else if(c == 344) n = 345;
				else if(c == 352) n = 353;
				else if(c == 356) n = 357;
				else if(c == 218) n = 250;
				else if(c == 366) n = 367;
				else if(c == 221) n = 253;
				else if(c == 381) n = 382;
				else n = c;

				lower.append(n);
			}
		} catch(Exception e) {
			return(null);
		}

		return(lower.toString());
	}

	private void startSearch() {
		heights.removeAllElements();
		original.removeAllElements();
		translate.removeAllElements();

		words_height_all = 0;
		words_position = 0;

		search_notice = NOTICE_OK;

		searching_run = true;

		results.repaint();
	}

	private void stopSearch() {
		searching_run = false;
		searching_image = 0;

		timer_searching.cancel();
		timer_searching = null;
		task_searching = null;

		results.repaint();
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