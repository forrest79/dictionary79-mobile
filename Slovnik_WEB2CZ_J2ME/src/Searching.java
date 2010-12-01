import javax.microedition.lcdui.*;
import java.util.Vector;
import javax.microedition.io.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Searching {

	private Slovnik_WEB2CZ midlet = null;
	private Canvas results = null;
	private String URL = "";
	private int COUNT_WORDS = 0;

	public static final int NOTICE_OK = 0;
	public static final int NOTICE_NO_RESULTS = 1;
	public static final int NOTICE_ERROR = 2;

	private static final int MAX_RESULTS = 30;

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

	private DictionarySearch tDictionarySearch = null;
	private NetSearch tNetSearch = null;

	private boolean stopSearching = false;

	public Searching(Slovnik_WEB2CZ midlet, Canvas results, int COUNT_WORDS, String URL) {
		this.midlet = midlet;
		this.results = results;
		this.COUNT_WORDS = COUNT_WORDS;
		this.URL = URL;

		original = new Vector();
		translate = new Vector();
		heights = new Vector();
	}

	public void dictionarySearch(String word, int direction, boolean similar) {
		try {
			tDictionarySearch = new DictionarySearch(word, direction, similar);
			Thread threadDictionarySearch = new Thread(tDictionarySearch);
			threadDictionarySearch.start();
		} catch(Exception e) {}
	}

	public void netSearch(String word, int direction, boolean similar, boolean logical) {
		try {
			tNetSearch = new NetSearch(word, direction, similar, logical);
			Thread threadNetSearch = new Thread(tNetSearch);
			threadNetSearch.start();
		} catch(Exception e) {}
	}

	private class DictionarySearch extends Thread {
		private String word;
		private String fileOriginal;
		private String fileTranslate;
		private boolean similar;

		private DictionarySearch(String word, int direction, boolean similar) {
			this.word = czUTF_toLowerCase(word);

			if(direction == 0) {
				fileOriginal = "/eng";
				fileTranslate = "/cze";

				results_original = midlet.DICTIONARY_ENG;
				results_translate = midlet.DICTIONARY_CZE;
			} else if(direction == 1) {
				fileOriginal = "/cze";
				fileTranslate = "/eng";

				results_original = midlet.DICTIONARY_CZE;
				results_translate = midlet.DICTIONARY_ENG;
			}

			this.similar = similar;
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
				InputStream fis_o = midlet.getClass().getResourceAsStream(fileOriginal);
				DataInputStream readerOriginal = new DataInputStream(fis_o);

				InputStream fis_t = midlet.getClass().getResourceAsStream(fileTranslate);;
				DataInputStream readerTranslate = new DataInputStream(fis_t);

				int c;
				String wordOriginal = "";
				String wordTranslate = "";

				int found = 0;

				readerOriginal.readInt(); // skip first int in original
				readerTranslate.readInt(); // skip first int in translate

				stopSearching = false;
				for(int i = 0; i < COUNT_WORDS; i++) {
					wordOriginal = readerOriginal.readUTF();

					wordTranslate = readerTranslate.readUTF();

					if((!similar && wordOriginal.equals(word)) || (similar && (wordOriginal.indexOf(word) > -1))) {
						original.addElement(wordOriginal);
						translate.addElement(wordTranslate);
						results.repaint();
						found++;
					}

					if((found == MAX_RESULTS) || stopSearching) break;
				}

				readerOriginal.close();

				readerTranslate.close();
			} catch(Exception e) {
				results_error = true;
			}

			stopSearch();

			if(results_error) search_notice = NOTICE_ERROR;
			else if(translate.size() == 0) search_notice = NOTICE_NO_RESULTS;
		}
	}

	private class NetSearch extends Thread {
		private String word;
		private int direction;
		private boolean similar;
		private boolean logical;

		private NetSearch(String word, int direction, boolean similar, boolean logical) {
			this.word = czUTF_toLowerCase(word);
			this.direction = direction;
			this.similar = similar;
			this.logical = logical;
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
				HttpConnection connection = null;
				InputStream inputStream = null;
				InputStreamReader streamReader = null;

				try {
					String get = "?word=" + urlEncode(czUTF2ISO(word));
					get = get + "&direction=" + direction;
					get = get + "&max_results=" + MAX_RESULTS;
					if(similar) get = get + "&similar=1";
					else get = get + "&similar=0";
					if(logical) get = get + "&logical=1";
					else get = get + "&logical=0";

					connection = (HttpConnection) Connector.open(URL + "/j2me/search.php" + get);
					connection.setRequestMethod(HttpConnection.GET);

					inputStream = connection.openInputStream();

					streamReader = new InputStreamReader(inputStream, "UTF-8");

					boolean read_original = true;
					String read_line = "";

					StringBuffer buffer = new StringBuffer();
					boolean first_line = true;

					int read = 0;

					stopSearching = false;
					while(((read = streamReader.read()) != -1) && !stopSearching) {
						buffer.append((char) read);

						if(((char) read) == '\n') {
							if(first_line) {
								read_line = buffer.toString().trim();
								buffer = new StringBuffer();

								if(read_line.compareTo("1") == 0) {
									results_original = midlet.DICTIONARY_ENG;
									results_translate = midlet.DICTIONARY_CZE;
								} else if(read_line.compareTo("2") == 0) {
									results_original = midlet.DICTIONARY_CZE;
									results_translate = midlet.DICTIONARY_ENG;
								} else results_error = true;

								first_line = false;
							} else {
								read_line = buffer.toString().trim();
								buffer = new StringBuffer();

								if(read_original) {
									original.addElement(read_line);
									read_original = false;
								} else {
									translate.addElement(read_line);
									results.repaint();
									read_original = true;
								}
							}
						}
					}
				} catch(Exception e) {
					results_error = true;
				} finally {
					if(streamReader != null) streamReader.close();
					if(inputStream != null) inputStream.close();
					if(connection != null) connection.close();
				}
			} catch(Exception e) {
				results_error = true;
			}

			stopSearch();

			if(results_error) search_notice = NOTICE_ERROR;
			else if(translate.size() == 0) search_notice = NOTICE_NO_RESULTS;
		}

		private String urlEncode(String url) {
			if(url == null) return url;

			StringBuffer enc = new StringBuffer();
			try {
				char c;

				for (int i = 0; i < url.length(); i++) {
					c = url.charAt(i);

					if(c == ' ') enc.append('+');
					else if(((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || (c == '_')) enc.append(c);
					else {
						enc.append('%');
						if(c > 15) enc.append(new String(Integer.toHexString((int) c)).toUpperCase());
						else enc.append("0" + new String(Integer.toHexString((int) c)).toUpperCase());
					}
				}
			} catch(Exception e) {
				return(null);
			}

			return(enc.toString());
		}

		private String czUTF2ISO(String utf) {
			if(utf == null) return utf;

			StringBuffer iso = new StringBuffer(utf.length());
			try {
				char c, n;
				int i;
				for(i = 0; i < utf.length(); i++) {
					c = utf.charAt(i);

					if(c == 268) n = 200;
					else if(c == 269) n = 232;
					else if(c == 270) n = 207;
					else if(c == 271) n = 239;
					else if(c == 282) n = 204;
					else if(c == 283) n = 236;
					else if(c == 327) n = 210;
					else if(c == 328) n = 242;
					else if(c == 344) n = 216;
					else if(c == 345) n = 248;
					else if(c == 352) n = 169;
					else if(c == 353) n = 185;
					else if(c == 356) n = 171;
					else if(c == 357) n = 187;
					else if(c == 366) n = 217;
					else if(c == 367) n = 249;
					else if(c == 381) n = 174;
					else if(c == 382) n = 190;
					else n = c;

					iso.append(n);
				}
			} catch(Exception e) {
				return(null);
			}

			return(iso.toString());
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

		task_searching = new ResultsSearching();
		timer_searching = new Timer();
		timer_searching.schedule(task_searching, 0, 200);

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

	private class ResultsSearching extends TimerTask {
		public void run() {
			if(searching_image == 0) searching_image = 1;
			else searching_image = 0;
			results.repaint();
		}
	}

	public void cancelSearching() {
		if(tDictionarySearch != null) {
			stopSearching = true;
			tDictionarySearch.interrupt();
			tDictionarySearch = null;
		}

		if(tNetSearch != null) {
			stopSearching = true;
			tNetSearch.interrupt();
			tNetSearch = null;
		}
	}
}