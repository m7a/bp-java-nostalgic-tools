import java.io.File;

class DisplayThread extends Thread {

	private static final char HIGHLIGHTED  = '#';
	private static final char EMPTY        = '.';
	private static final int  STATUS_LINES = 3;
	private static final char FILLED       = 'o';
	private static final char EMPTY_BLOCK  = ' ';

	private final Status s;
	private int currentLine;
	private int currentColumn;

	private int ratioMax;
	private int cRun;

	DisplayThread(Status s) {
		super();
		this.s = s;
		currentLine = 0;
		currentColumn = 0;
		cRun = 1;
	}

	public void run() {
		while(!isInterrupted()) {
			displayStatistics();
			try {
				sleep(250);
			} catch(InterruptedException ex) {
				break;
			}
			if(cRun == 4) {
				updateDiagram();
				cRun = 1;
			} else {
				cRun++;
			}
		}
		displayStatistics();
	}

	private void displayStatistics() {
		clear();
		printMeta();
		printProgress();
		printDiagram();
		printFooter();
	}

	private void clear() {
		currentLine = 0;
	}

	private void printMeta() {
		printFileAssoc("Copying ", s.oSrc);
		printFileAssoc("To      ", s.oDst);
	}

	private void printFileAssoc(String key, File val) {
		print(key);
		print(fit(val.getAbsolutePath()));
		nl();
	}

	private void print(String data) {
		System.out.print(data);
		currentColumn += data.length();
	}

	private String fit(String str) {
		return fit(str, s.width - currentColumn);
	}

	private String fit(String str, int toWidth) {
		if(str.length() <= toWidth) {
			return str;
		} else {
			return str.substring(0, toWidth - 3) + "...";
		}
	}

	private void nl() {
		System.out.println();
		currentLine++;
		currentColumn = 0;
	}

	private void printProgress() {
		ratioMax = 0;
		String allF = strRatio(s.copiedFiles,       s.totalFiles);
		String allB = strRatio(s.copiedData,        s.totalData);
		String dirF = strRatio(s.dirCopiedFiles,    s.dirTotalFiles);
		String dirB = strRatio(s.dirCopiedData,     s.dirTotalData);
		String file = strRatio(s.currentFileCopied, s.currentFileSize);
		// ratioMax now contains the longset calculated string for
		// the ratio strings.
		printBar("Files   ", allF, s.copiedFiles,    s.totalFiles);
		printBar("Data    ", allB, s.copiedData,     s.totalData);
		print("Current ");
		print(fit(s.currentFile + " from " + s.currentDirectory));
		nl();
		printBar("Dir (f) ", dirF, s.dirCopiedFiles, s.dirTotalFiles);
		printBar("Dir (d) ", dirB, s.dirCopiedData,  s.dirTotalData);
		printBar("File    ", file,
				s.currentFileCopied, s.currentFileSize);
	}

	private String strRatio(long a, long b) {
		String ret = " ";
		if(b == 0) {
			ret += String.valueOf(a);
		} else {
			ret += String.valueOf(a) + '/' + String.valueOf(b);
		}
		if(ret.length() > ratioMax) {
			ratioMax = ret.length();
		}
		return ret;
	}

	private void printBar(String title, String ratio, long a, long b) {
		print(title + "[");
		// 2: Closing bracket
		int progressLength = s.width - currentColumn - ratioMax - 1;
		int highlight;
		if(b == 0) {
			highlight = 0;
		} else {
			highlight = (int)(a * progressLength / b);
		}
		char[] bar = new char[progressLength];
		for(int i = 0; i < progressLength; i++) {
			if(i < highlight) {
				bar[i] = HIGHLIGHTED;
			} else {
				bar[i] = EMPTY;
			}
		}
		print(new String(bar));
		print("]" + ratio);
		nl();
	}

	private void printDiagram() {
		int freeLines = s.height - currentLine - STATUS_LINES - 1;
		if(freeLines <= 0) {
			return; // not enough space...
		}
		long max = getMaxSpeed();
		if(max == 0) {
			print("        <speed diagram not available>");
			nl();
			return;
		}
		double block = (double)max / (double)freeLines;
		// TODO PROBABLY NOT OPTIMAL... round to one or two digits
		// 				after the dot
		print("Speed   1 block is " + (int)(block / 1024 / 1024) +
					" MiB per second, x is time in sec.");
		nl();
		char[][] blocks = new char[freeLines][s.width]; // Y/X
		for(int i = 0; i < blocks.length; i++) {
			for(int j = 0; j < blocks[i].length; j++) {
				blocks[i][j] = EMPTY_BLOCK;
			}
		}
		int oPos = 0;
		for(int i = s.diagramPos; i < s.width; i++) {
			calculateDiagramBar(i, blocks, block, oPos);
			oPos++;
		}
		for(int i = 0; i <= s.diagramPos - 1; i++) {
			calculateDiagramBar(i, blocks, block, oPos++);
		}
		for(int i = blocks.length - 1; i >= 0; i--) {
			print(new String(blocks[i]));
			nl();
		}
		nl();
	}

	private long getMaxSpeed() {
		long max = 0;
		for(int i = 0; i < s.speed.length; i++) {
			if(accessDiagram(i) > max) {
				max = accessDiagram(i);
			}
		}
		return max;
	}

	// Historical reasons: In the past, the return value depended on some
	// live-data but these were a performace problem...
	private long accessDiagram(int index) {
		return s.speed[index];
	}

	private void calculateDiagramBar(int pos, char[][] blocks,
						double blockSize, int oPos) {
		int fill = (int)(accessDiagram(pos) / blockSize);
		// second test to make sure no problems arise in very special
		// circumstances where the speed suddenly increases after the
		// maximum was already calculated
		for(int i = 0; i < fill && i < blocks.length; i++) {
			blocks[i][oPos] = FILLED;
		}
	}

	private void printFooter() {
		print("Status: " + s.status + ", Errors: " + s.errors);
		if(s.errors > 0) {
			print(", Last: ");
			print(fit(s.lastError));
		}
		nl();
		nl();
	}

	private void updateDiagram() {
		s.speed[s.diagramPos++] = s.cSecondWritten;
		synchronized(s.sync) {
			s.cSecondWritten = 0;
		}
		// Wrap around
		if(s.diagramPos == s.width) {
			s.diagramPos = 0;
		}
	}

}
