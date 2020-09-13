import java.io.*;
import java.util.*;
import java.net.*;

public class NetRead {
	
	private static final int defaultPort = 5971;

	private static Socket socket;

	private static class PartialHexViewer {

		public static String formatAsHex(byte number) {
			String ret = null;
			String hex = Integer.toHexString(number);
			int len = hex.length();
			if(len == 1) {
				ret = "0" + hex;
			} else if(len == 2) {
				ret = hex;
			} else if(len > 2) {
				ret = hex.substring(len-2);
			}
			return ret;
		}

		private String formatBytesHexadecimal(byte[] data_bytes, int max) {
			StringBuffer data_hex = new StringBuffer();
			for(int i = 0, chars = 0; i < max; i++, chars += 4) {
				if((chars % 80) == 0 && i != 0) {
					data_hex.append("\n");
				}
				data_hex.append(" ");
				data_hex.append(formatAsHex(data_bytes[i]));
				data_hex.append(" ");
			}
			return data_hex.toString();
		}

		private String formatBytesAsText(byte[] data_bytes, int max) {
			StringBuffer data_text = new StringBuffer(" ");
			for(int i = 0; i < max; i++) {
				if((i % 20) == 0 && i != 0) {
					data_text.append(" ");
					data_text.append("\n");
					data_text.append(" ");
				}
				if(Character.isLetterOrDigit(data_bytes[i])) {
					char character = (char)data_bytes[i];
					if(character == '\n' || character == '\r' || character == '\t') {
						data_text.append(" ");
					} else {
						data_text.append(character);
					}
				} else {
					data_text.append(".");
				}
			}
			return data_text.toString();
		}

		public String toTextView(byte[] data, int max) {
			String binary = formatBytesHexadecimal(data, max);
			String textv = formatBytesAsText(data, max);
			StringTokenizer lines1 = new StringTokenizer(binary, "\n");
			StringTokenizer lines2 = new StringTokenizer(textv, "\n");
			StringBuffer output = new StringBuffer();
			while(lines1.hasMoreElements()) {
				String cline = lines1.nextToken();
				output.append(cline);
				if(!lines1.hasMoreElements()) {
					char[] spc = new char[80 - cline.length()];
					Arrays.fill(spc, ' ');
					output.append(new String(spc));
				}
				output.append(lines2.nextToken());
				output.append('\n');
			}
			return output.toString();
		}
	}

	public static void main(String[] args) {
		System.out.println("NetRead 1.0, Copyright (c) 2012 Ma_Sys.ma.");
		System.out.println("For further info send an e-mail to Ma_Sys.ma@web.de.");
		System.out.println();
		final int port;
		if(args.length == 0) {
			port = defaultPort;
		} else if(args[0].equals("--help")) {
			System.out.println("Usage: java NetRead <port>");
			return;
		} else {
			try {
				port = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				System.err.println("Invalid port: " + args[0] + ".");
				ex.printStackTrace();
				System.err.println("Try --help.");
				return;
			}
		}
		System.out.println("Listening on port " + port);
		System.out.println("Press enter to terminate application.");
		socket = null;
		final Thread listen = new Thread() {
			public void run() {
				final ServerSocket server;
				try {
					server = new ServerSocket(port);
				} catch(IOException ex) {
					System.err.println("Could not create Server socket:");
					ex.printStackTrace();
					System.exit(5);
					return;
				}
				PartialHexViewer hex = new PartialHexViewer();
				byte[] buffer = new byte[0x800];
				while(!isInterrupted()) {
					try {
						System.out.println();
						socket = server.accept();
						BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
						int len;
						while((len = in.read(buffer, 0, buffer.length)) != -1) {
							System.out.print(hex.toTextView(buffer, len));
						}
					} catch(Exception ex) {
						System.err.println("Exception occurred:");
						ex.printStackTrace();
					}
				}
			}
		};
		Thread term = new Thread() {
			public void run() {
				try {
					new BufferedReader(new InputStreamReader(System.in)).readLine();
					listen.interrupt();
					if(socket != null) {
						socket.close();
					}
					System.exit(0);
				} catch(IOException ex) {
					System.err.println("Could not grab stdin -- you will need to have to terminate 'by hand' (ctrl-c)");
					ex.printStackTrace();
				}
			}
		};
		listen.start();
		term.start();
	}
}
