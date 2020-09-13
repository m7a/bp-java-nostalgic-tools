import java.io.File;

class Status { // Struct

	final int height;           final int width;
	final File oSrc;            final File oDst;
	long totalFiles;            long totalData;
	long copiedFiles;           long copiedData;
	long dirTotalFiles;         long dirTotalData;
	long dirCopiedFiles;        long dirCopiedData;
	long currentFileSize;       long currentFileCopied;
	String currentFile;         String currentDirectory;

	Object sync;
	long cSecondWritten;
	int diagramPos;
	long[] speed;
	
	String status;
	int errors;
	String lastError;

	int result;

	Status(String src, String dst) {
		super();
		oSrc              = new File(src);
		oDst              = new File(dst);
		height            = 24;
		width             = 80;

		totalFiles        = 0;
		totalData         = 0;
		copiedFiles       = 0;
		copiedData        = 0;
		dirTotalFiles     = 0;
		dirTotalData      = 0;
		dirCopiedFiles    = 0;
		dirCopiedData     = 0;
		currentFileSize   = 0;
		currentFileCopied = 0;
		sync              = new Object();
		cSecondWritten    = 0;
		diagramPos        = 0;
		status            = "initalizing";
		errors            = 0;
		lastError         = "<none>";
		result            = 0;

		currentFile       = "<unknown>";
		currentDirectory  = "<unknown>";

		speed             = new long[width];
		for(int i = 0; i < speed.length; i++) {
			speed[i]  = 0;
		}
	}

}
