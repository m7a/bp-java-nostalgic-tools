import java.io.*;

// This is something like the Unix "find" command, annotated 22.06.2012 20:26
public class Structure {

	public static void main(String[] args) {
		System.out.println("Structure 1.0, Copyright (c) 2011 Ma_Sys.ma.");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de");
		System.out.println();
		if(args.length != 2) {
			System.out.println("USAGE: java Structure DIR FILE");
			return;
		}
		try {
			BufferedWriter theOutput = new BufferedWriter(new FileWriter(args[1]));
			analyze(new File(args[0]), theOutput);
			theOutput.close();
		} catch(Throwable t) {
			System.err.println("Fatal error:");
			t.printStackTrace();
		}
	}

	private static void analyze(File file, BufferedWriter output) {
		if(file.isFile()) {
			try {
				output.write(file.getAbsolutePath());
				output.newLine();
			} catch(Exception ex) {
				System.err.println("File analyzing error:");
				ex.printStackTrace();
			}
		} else if(file.isDirectory()) {
			String[] cnt = file.list();
			if(cnt == null) {
				System.out.println("Skipping " + file.getAbsolutePath());
				return;
			}
			for(int i = 0; i < cnt.length; i++) {
				analyze(new File(file, cnt[i]), output);
			}
		}
	}
}
