//
// Version Change 1.0, Copyright (c) 2010 Ma_Sys.ma
// For further info send an e-mail to Ma_Sys.ma@web.de
//

// Special error-resistent version

import java.io.*;
import java.util.*;
import java.lang.*;
import java.security.*;

public class VersionChangeIgn {
	
	private static final int BUFFER_SIZE = 4096;
	
	private static Hashtable<String, String> readDatabase(File file) throws IOException {
		Hashtable<String, String> db = new Hashtable<String, String>();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while((line = in.readLine()) != null) {
			String[] keyVal = line.split(":");
			db.put(keyVal[0], keyVal[1]);
		}
		in.close();
		return db;
	}

	private static void writeDatabase(File file, Hashtable<String, String> db) throws IOException {
		StringBuffer data = new StringBuffer();
		Enumeration<String> keys = db.keys();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			out.write(key + ":" + db.get(key));
			out.newLine();
		}
		out.close();
	}
	
	private static Hashtable<String, String> createDBFrom(File file) throws IOException, NoSuchAlgorithmException {
		Hashtable<String, String> db = new Hashtable<String, String>();
		if(file.isDirectory()) {
			File[] list = file.listFiles();
			for(int i = 0; i < list.length; i++) {
				Hashtable<String, String> tmp = createDBFrom(list[i]);
				db.putAll(tmp);	
			}		
		} else {
			try {
				db.put(file.getCanonicalPath(), checksum(file));
			} catch(IOException ex) {
				System.out.println("catched  " + ex.toString());
				System.out.println("   file  " + file.getAbsolutePath());
			}
		}
		return db;
	}

	private static String checksum(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("sha-256");
		//MessageDigest digest = MessageDigest.getInstance("SHA-1");
		int numberOfBytes;
		byte[] buffer = new byte[BUFFER_SIZE];
		FileInputStream in = new FileInputStream(file);
		while((numberOfBytes = in.read(buffer)) > 0) {
			digest.update(buffer, 0, numberOfBytes);
		}
		in.close();
		byte[] hash = digest.digest();
		char[] ret  = new char[hash.length*2];
		int j = 0;
		for(int i = 0; i < hash.length; i++) {
			char[] hexData = Integer.toHexString(hash[i]).toCharArray();
			char[] hexDataLengthTwo = new char[2];
			if(hexData.length < 2) {
				hexDataLengthTwo[0] = '0';
				hexDataLengthTwo[1] = hexData[0];
			} else {
				hexDataLengthTwo[0] = hexData[0];
				hexDataLengthTwo[1] = hexData[1];
			}
			ret[j] = hexDataLengthTwo[0];
			j++;
			ret[j] = hexDataLengthTwo[1];
			j++;
		}
		return new String(ret);
	}

	private static void displayChanges(Hashtable<String, String> db, Hashtable<String, String> status) {
		Enumeration<String> statusKeys = status.keys();
		while(statusKeys.hasMoreElements()) {
			String key = statusKeys.nextElement();
			if(!db.containsKey(key)) {
				System.out.println("added    " + key);
			} else if(!db.get(key).equals(status.get(key))) {
				System.out.println("changed  " + key);
			}
		}
		Enumeration<String> dbKeys = db.keys();
		while(dbKeys.hasMoreElements()) {
			String key = dbKeys.nextElement();
			if(!status.containsKey(key)) {
				System.out.println("removed  " + key);
			}
		}
	}

	private static void displayHelp() {
		System.out.println("Usage   : java VersionChange option database dir");
		System.out.println();
		System.out.println("Options : ");
		System.out.println("          display    Displays differences between database and dir.");
		System.out.println("          accept     Saves the status of dir into database.");
	}

	public static void main(String[] args) {
		System.out.println("Version Change 1.0, Copyright (c) 2010 Ma_Sys.ma");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de");
		System.out.println();
		if(args.length != 3) {
			displayHelp();
			System.exit(4);
		} else {
			String mode      =          args[0] ;
			File   dbFile    = new File(args[1]);
			File   sourceDir = new File(args[2]);
			try {
				Hashtable<String, String> source = createDBFrom(sourceDir);
				if(mode.equals("display")) {
					Hashtable<String, String> db = readDatabase(dbFile);
					displayChanges(db, source);
				} else if(args[0].equals("accept")) {
					writeDatabase(dbFile, source);
				} else {
					displayHelp();
					System.exit(4);
				}
			} catch(IOException ex) {
				System.err.println("Input/Output Error.");
				ex.printStackTrace();
				System.exit(1);
			} catch(Exception ex) {
				System.err.println("Error.");
				ex.printStackTrace();
				System.exit(2);
			} catch(Throwable t) {
				System.err.println("Fatal error.");
				t.printStackTrace();
				System.exit(3);
			}
			System.exit(0);
		}
	}
	
}
