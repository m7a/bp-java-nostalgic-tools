import java.io.*;
import java.nio.*;
import java.nio.channels.*;

class CopyThread extends Thread {

	private static final int TRANSFER_PER_TIME = 1 << 30;

	private final Status s;
	private final boolean inDir;

	CopyThread(Status s, boolean inDir) {
		super();
		this.s = s;
		this.inDir = inDir;
	}

	public void run() {
		s.status = "scanning";
		scan(s.oSrc);
		s.status = "copying";
		if(inDir || (!s.oSrc.isDirectory() && !s.oDst.isDirectory())) {
			copy(s.oSrc, s.oDst);
		} else {
			copy(s.oSrc, new File(s.oDst, s.oSrc.getName()));
		}
		s.status = "operation complete";
	}

	private void scan(File src) {
		if(src.isDirectory()) {
			File[] list = src.listFiles();
			if(list == null) {
				handleListError(src);
				return;
			}
			for(int i = 0; i < list.length; i++) {
				scan(list[i]);
			}
		} else if(src.canRead()) {
			s.totalData += src.length();
			s.totalFiles++;
		}
	}

	private void handleListError(File f) {
		s.errors++;
		s.lastError = "Could not list " + f.getAbsolutePath();
		if(s.result == 0) {
			s.result = 1;
		}
	}

	private void copy(File src, File dst) {
		if(src.isDirectory()) {
			if(!dst.exists()) {
				if(!dst.mkdir()) {
					s.errors++;
					s.lastError = "Could not create " +
							dst.getAbsolutePath();
				}
			}
			File[] cnt = src.listFiles();
			if(cnt == null) {
				handleListError(src);
				return;
			}
			int files = 0;
			long length = 0;
			for(int i = 0; i < cnt.length; i++) {
				if(cnt[i].isDirectory()) {
					copy(cnt[i], new File(dst,
							cnt[i].getName()));
					cnt[i] = null;
				} else {
					files++;
					length += cnt[i].length();
				}
			}
			s.currentDirectory = src.getName();
			s.dirCopiedFiles   = 0;
			s.dirCopiedData    = 0;
			s.dirTotalFiles    = files;
			s.dirTotalData     = length;
			for(int i = 0; i < cnt.length; i++) {
				if(cnt[i] != null) {
					copy(cnt[i], new File(dst,
							cnt[i].getName()));
				}
			}
		} else {
			try {
				copyFile(src, dst);
			} catch(IOException ex) {
				s.result = 2;
				s.errors++;
				s.lastError = ex.toString();
			}
		}
	}

	private void copyFile(File src, File dst) throws IOException {
		s.currentFile = src.getName();
		s.currentFileCopied = 0;

		FileInputStream wIn   = new FileInputStream(src);
		FileChannel cIn       = wIn.getChannel();
		RandomAccessFile wOut = new RandomAccessFile(dst, "rw");
		FileChannel cOut      = wOut.getChannel();

		s.currentFileSize = cIn.size();
		long step;
		if(s.currentFileSize > TRANSFER_PER_TIME) {
			step = TRANSFER_PER_TIME;
		} else {
			step = s.currentFileSize;
		}
		long pos = 0;

		while(pos < s.currentFileSize) {
			MappedByteBuffer mIn = cIn.map(
				FileChannel.MapMode.READ_ONLY, pos, step);
			MappedByteBuffer mOut = cOut.map(
				FileChannel.MapMode.READ_WRITE, pos, step);

			while(mIn.hasRemaining()) {
				mOut.put(mIn.get());
				s.copiedData++;
				s.dirCopiedData++;
				s.currentFileCopied++;
				synchronized(s.sync) {
					s.cSecondWritten++;
				}
			}

			pos += step;
			if(step > s.currentFileSize - pos) {
				step = s.currentFileSize - pos;
			}

			// Sort-of close
			mIn = null;
			mOut = null;
		}

		wIn.close();
		wOut.close();

		s.copiedFiles++;
		s.dirCopiedFiles++;
	}

}
