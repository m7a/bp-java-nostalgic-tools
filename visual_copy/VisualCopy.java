import java.io.File;

public class VisualCopy {

	public static void main(String[] args) {
		printCopyright();
		if(args.length != 2) {
			help();
			System.exit(1);
		} else {
			Status s = new Status(args[0], args[1]);
			DisplayThread display = new DisplayThread(s);
			display.start();
			CopyThread action = new CopyThread(
					s, args[0].endsWith(File.separator));
			action.start();
			try {
				action.join();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
			display.interrupt();
			try {
				display.join();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
			if(s.errors != 0) {
				System.out.println("Errors occurred during " +
							"the transfer.");
				System.out.println("Last error:");
				System.out.println(s.lastError);
			}
			System.exit(s.result);
		}
	}

	private static void printCopyright() {
		System.out.println(
			"Ma_Sys.ma Visual Copy (Java) 1.0.0.0, " +
			"Copyright (c) 2013 Ma_Sys.ma."
		);
		System.out.println(
			"For further info send an e-mail to " +
			"Ma_Sys.ma@web.de."
		);
		System.out.println();
	}

	private static void help() {
		System.out.println("Usage java VisualCopy <src> <dst>");
		System.out.println();
		System.out.println("If you use a source/ (source and slash)");
		System.out.println("notation and source is a directory, its");
		System.out.println("contents are copied into destination.");
		System.out.println("Otherwise, a new directory called");
		System.out.println("\"source\" is created in destination.");
		System.out.println();
		System.out.println("Failure return codes:");
		System.out.println(" 1: failed to list some directories");
		System.out.println("    or: incorrect commandline arguments.");
		System.out.println(" 2: failed to copy some files");
		System.out.println("64: Termination as result of a bug");
		System.out.println();
		System.out.println("If you do not need the displays, use ");
		System.out.println("\"cp -R\" instead.");
	}

}
