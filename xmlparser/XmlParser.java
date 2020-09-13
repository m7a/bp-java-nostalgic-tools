import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParser extends DefaultHandler {

	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage java XmlParser file.xml");
		} else {
			int ret = 0;
			// -- Copied from the STNE Map Generator --
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setValidating(true);
				factory.setXIncludeAware(true);
				SAXParser parser = factory.newSAXParser();
				parser.parse(new File(args[0]), new XmlParser());
			} catch(Exception ex) {
				System.err.println("Your file is invalid.");
				ex.printStackTrace();
				ret = 1;
			}
			// -- END --
			System.exit(ret);
		}
	}

	// -- Exactly copied from the STNE Map Generator --
	
	@Override
	public void warning(SAXParseException ex) throws SAXException {
		super.warning(ex);
		saxMessage("SAX generated a warning", ex);
	}
	
	@Override
	public void error(SAXParseException ex) throws SAXException {
		super.error(ex);
		saxMessage("Error", ex);
	}
	
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		super.fatalError(ex);
		saxMessage("Fatal error", ex);
	}

	private void saxMessage(String prefix, SAXParseException ex) {
		System.err.println(prefix + " while processing the input XML file.");
		System.err.println("Location: line = " + ex.getLineNumber() + ", column = " + ex.getColumnNumber() + 
			", public = " + ex.getPublicId() + ", system = " + ex.getSystemId());
		ex.printStackTrace(System.err);
	}

	// -- END --

}
