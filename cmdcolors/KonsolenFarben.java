// Ma_Sys.ma cmdcolors (Java) 1.0, Copyright (c) 2010 Ma_Sys.ma.
// For further info send an e-mail to Ma_Sys.ma@web.de.

public class KonsolenFarben {
	private static final String TEXT = "This is a colorized Example Text";
	private static final int[] ATTS = { 1, 2, 4, 5, 7, 9 }; // Maybe 8, but it makes only colorized Lines
	private static final int COLOR_START = 30;
	private static final int COLOR_END = 37;
	private static final int BG_START = COLOR_START+10;
	private static final int BG_END = COLOR_END+10;
	
	public static void main(String[] args) {
		System.out.println("Colorized String building : ");
		System.out.println(" \\033[<COLOR>;<BACKGROUND>;<ATTRIBUTE>m");
		System.out.println(" <TEXT>");
		System.out.println(" \\033[0;m");
		System.out.println("If you want to use this, you don't need the Line-breaks,");
		System.out.println("the last line of this example resets the default colors.");
		System.out.println("This Example may works only under Linux");
		System.out.println("Color | Attribute | Background | Text");
		for(int i = COLOR_START; i <= COLOR_END; i++) {
			for(int j = 0; j < ATTS.length; j++) {
				for(int k = BG_START; k <= BG_END; k++) {	
					System.out.println
					(
						i + "   " +                                    // Print out Color Number
						" | " +                                        // Print out Separator
						ATTS[j] +                                      // Print out Attribute
						"        " +                                   // Print out Space
						" | " +                                        // Print out Separator
						k +                                            // Print out Backgorund
						"        " +                                   // Print out Space
						" | " +                                        // Print out Separator
						"\033[" + i + ";" + k + ";" + ATTS[j] + "m" +  // Print out String
						TEXT +                                         // Print out Text
						"\033[0;m"                                     // Reset Console to Default
					);
				}
			}
		}
		System.out.println("Specails : ");
		for(int i = BG_START; i < BG_END; i++) {
			System.out.println
				(
					i-10 + "   " +                                 // Print out Color Number
					" | " +                                        // Print out Separator
					"8" +                                          // Print out Attribute
					"        " +                                   // Print out Space
					" | " +                                        // Print out Separator
					i +                                            // Print out Backgorund
					"        " +                                   // Print out Space
					" | " +                                        // Print out Separator
					"\033[30;" + i + ";" + 8 + "m" +               // Print out String
					TEXT +                                         // Print out Text
					"\033[0;m"                                     // Reset Console to Default
				);
		}
		System.out.println("Try to use java KonsolenFarbn | more to scroll through this list.");
		// System.out.println("COLOR : \033[34;1mDas ist blau\033[0;m");
		/*
		Remember the Table of Batch Colors : 
		Zeichen:  Grund:   Farbe:        |  Attribute
		30      40     schwarz           |    0    weiß auf schwarz
		31      41     rot               |    1    helle Zeichen
		32      42     grün              |    5    blinkende Zeichen
		33      43     braun/gelb        |    7    schwarz auf weiß
		34      44     blau              |    8    schwarz auf schwarz
		35      45     magenta (violett) |
		36      46     zyan (türkis)     |
		37      47     weiß              |
		*/
	}
}
