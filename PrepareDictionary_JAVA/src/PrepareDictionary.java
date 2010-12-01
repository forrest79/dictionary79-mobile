// PrepareDictionary - 1.0.0 - 28.11.2006
import java.io.*;
import java.lang.*;

class PrepareDictionary {
	public static void main (String[] args) {
		String lineInput = "";
		String lineOutput = "";
		String fileInput = "slovnik_data.txt";
		String fileOutput1 = "eng";
		String fileOutput2 = "cze";
		int wordTimes = 1;
		int maxWordsCount = 0;

		if((args.length >= 1) && (args[0] != "")) fileInput = args[0];
		if((args.length >= 2) && (args[1] != "")) fileOutput1 = args[1];
		if((args.length >= 3) && (args[2] != "")) fileOutput2 = args[2];
		if((args.length >= 4) && (args[3] != "")) wordTimes = Integer.parseInt(args[3]);
		if((args.length >= 5) && (args[4] != "")) maxWordsCount = Integer.parseInt(args[4]);

		InputStream fis = null;
		InputStreamReader reader = null;

		OutputStream fos1 = null;
		OutputStream fos2 = null;

		int binWordsCount = 0;

		try {
			System.out.println("Writing text files...");

			fis = new FileInputStream(fileInput);
			reader = new InputStreamReader(fis, "8859_2");

			fos1 = new FileOutputStream(fileOutput1 + ".txt");
			OutputStream bos1 = new BufferedOutputStream(fos1);
			OutputStreamWriter writer1 = new OutputStreamWriter(bos1, "UTF8");

			fos2 = new FileOutputStream(fileOutput2 + ".txt");
			OutputStream bos2 = new BufferedOutputStream(fos2);
			OutputStreamWriter writer2 = new OutputStreamWriter(bos2, "UTF8");

			int wordsCount = 0;
			int linesCount = 0;
			int c;
			boolean endTab = false;
			String original = "";
			String translated = "";
			while((c = reader.read()) != -1) {
				if(((char) c) == '\n') {
					lineInput = lineInput.trim();
					linesCount++;

					if((lineInput.length() > 0) && (lineInput.charAt(0) != '#')) {
						endTab = false;
						lineOutput = "";
						original = "";
						translated = "";

						int x = 0;
						for(x = 0; x < lineInput.length(); x++) {
							if((lineInput.charAt(x) != '\t') && !endTab) original = original + lineInput.charAt(x);
							else if((lineInput.charAt(x) != '\t') && endTab) translated = translated + lineInput.charAt(x);
							else if((lineInput.charAt(x) == '\t') && !endTab) endTab = true;
							else if((lineInput.charAt(x) == '\t') && endTab) break;
						}

						if(testWord(original.trim()) && testWord(translated.trim()) && ((linesCount % wordTimes) == 0)) {
							lineOutput = original.trim() + '\n';
							writer1.write(lineOutput);
							lineOutput = translated.trim() + '\n';
							writer2.write(lineOutput);

							wordsCount++;
						}
					}

					lineInput = "";
				} else lineInput = lineInput + ((char) c);

				if((maxWordsCount == wordsCount) && (maxWordsCount > 0)) break;
			}

			writer1.flush();
			writer1.close();
			fos1.close();

			writer2.flush();
			writer2.close();
			fos2.close();

			reader.close();
			fis.close();

			System.out.println("Total lines: " + linesCount);
			System.out.println("Words to dictionary: " + wordsCount);

			binWordsCount = wordsCount;
		} catch(Exception e) {
			System.out.println("ERROR!");
			e.printStackTrace();
		}

		try {
			System.out.println("Writing binary files...");

			fis = new FileInputStream(fileInput);
			reader = new InputStreamReader(fis, "8859_2");

			fos1 = new FileOutputStream(fileOutput1 + ".bin");
			DataOutputStream writer1 = new DataOutputStream(fos1);

			fos2 = new FileOutputStream(fileOutput2 + ".bin");
			DataOutputStream writer2 = new DataOutputStream(fos2);

			writer1.writeInt(binWordsCount);
			writer2.writeInt(binWordsCount);

			int wordsCount = 0;
			int linesCount = 0;
			int c;
			boolean endTab = false;
			String original = "";
			String translated = "";
			while((c = reader.read()) != -1) {
				if(((char) c) == '\n') {
					lineInput = lineInput.trim();
					linesCount++;

					if((lineInput.length() > 0) && (lineInput.charAt(0) != '#')) {
						endTab = false;
						lineOutput = "";
						original = "";
						translated = "";

						int x = 0;
						for(x = 0; x < lineInput.length(); x++) {
							if((lineInput.charAt(x) != '\t') && !endTab) original = original + lineInput.charAt(x);
							else if((lineInput.charAt(x) != '\t') && endTab) translated = translated + lineInput.charAt(x);
							else if((lineInput.charAt(x) == '\t') && !endTab) endTab = true;
							else if((lineInput.charAt(x) == '\t') && endTab) break;
						}

						if(testWord(original.trim()) && testWord(translated.trim()) && ((linesCount % wordTimes) == 0)) {
							lineOutput = original.trim();
							writer1.writeUTF(lineOutput);
							lineOutput = translated.trim();
							writer2.writeUTF(lineOutput);

							wordsCount++;
						}
					}

					lineInput = "";
				} else lineInput = lineInput + ((char) c);

				if((maxWordsCount == wordsCount) && (maxWordsCount > 0)) break;
			}

			writer1.flush();
			writer1.close();
			fos1.close();

			writer2.flush();
			writer2.close();
			fos2.close();

			reader.close();
			fis.close();

			System.out.println("Total lines: " + linesCount);
			System.out.println("Words to dictionary: " + wordsCount);
		} catch(Exception e) {
			System.out.println("ERROR!");
			e.printStackTrace();
		}
	}

	public static boolean testWord(String word) {
		if(word == "") return false;
		else if(word.length() < 2) return false;
		else if(word.length() > 20) return false;
		else if(word.indexOf(" ") > -1) return false;
		else if(word.indexOf("1") > -1) return false;
		else if(word.indexOf("2") > -1) return false;
		else if(word.indexOf("3") > -1) return false;
		else if(word.indexOf("4") > -1) return false;
		else if(word.indexOf("5") > -1) return false;
		else if(word.indexOf("6") > -1) return false;
		else if(word.indexOf("7") > -1) return false;
		else if(word.indexOf("8") > -1) return false;
		else if(word.indexOf("9") > -1) return false;
		else if(word.indexOf("0") > -1) return false;
		else if(word.indexOf("!") > -1) return false;
		else if(word.indexOf("~") > -1) return false;
		else if(word.indexOf("@") > -1) return false;
		else if(word.indexOf("#") > -1) return false;
		else if(word.indexOf("$") > -1) return false;
		else if(word.indexOf("%") > -1) return false;
		else if(word.indexOf("^") > -1) return false;
		else if(word.indexOf("&") > -1) return false;
		else if(word.indexOf("*") > -1) return false;
		else if(word.indexOf("(") > -1) return false;
		else if(word.indexOf(")") > -1) return false;
		else if(word.indexOf("'") > -1) return false;
		else if(word.indexOf("\"") > -1) return false;
		else if(word.indexOf(":") > -1) return false;
		else if(word.indexOf(".") > -1) return false;
		else if(word.indexOf(",") > -1) return false;
		else if(word.indexOf("?") > -1) return false;
		else if(word.indexOf("=") > -1) return false;
		else if(word.indexOf("-") > -1) return false;
		else if(word.indexOf("_") > -1) return false;
		else if(word.indexOf("<") > -1) return false;
		else if(word.indexOf(">") > -1) return false;
		else if(word.indexOf("\\") > -1) return false;
		else if(word.indexOf("/") > -1) return false;
		else if(word.indexOf("A") > -1) return false;
		else if(word.indexOf("B") > -1) return false;
		else if(word.indexOf("C") > -1) return false;
		else if(word.indexOf("D") > -1) return false;
		else if(word.indexOf("E") > -1) return false;
		else if(word.indexOf("F") > -1) return false;
		else if(word.indexOf("G") > -1) return false;
		else if(word.indexOf("H") > -1) return false;
		else if(word.indexOf("I") > -1) return false;
		else if(word.indexOf("J") > -1) return false;
		else if(word.indexOf("K") > -1) return false;
		else if(word.indexOf("L") > -1) return false;
		else if(word.indexOf("M") > -1) return false;
		else if(word.indexOf("N") > -1) return false;
		else if(word.indexOf("O") > -1) return false;
		else if(word.indexOf("P") > -1) return false;
		else if(word.indexOf("Q") > -1) return false;
		else if(word.indexOf("R") > -1) return false;
		else if(word.indexOf("S") > -1) return false;
		else if(word.indexOf("T") > -1) return false;
		else if(word.indexOf("U") > -1) return false;
		else if(word.indexOf("V") > -1) return false;
		else if(word.indexOf("W") > -1) return false;
		else if(word.indexOf("X") > -1) return false;
		else if(word.indexOf("Y") > -1) return false;
		else if(word.indexOf("Z") > -1) return false;
		else return true;
	}
}