import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;

/**
 * xmlLog
 * 
 * Creates or updates a given logfile with Xerces error messages
 * 
 * @author		Tobias Fischer
 * @date 		2013-02-09
 */
public class xmlLog {

	private static File logFile;
	private static Element log;
	private static Document dokument;

	
	public static void newLog(File logF, int run) {
		logFile = logF;
		log = new Element("log");
		log.setAttribute(new Attribute("run", Integer.toString(run)));
		dokument = new Document(log);
	}


	public static void update(File logF, int run) {
		logFile = logF;

		SAXBuilder builder = new SAXBuilder();
		try {
			dokument = (Document) builder.build(logF);
			log = dokument.getRootElement();
			log.setAttribute(new Attribute("run", Integer.toString(run)));

		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void add(int run, String type, int line, int col, String errMsg, String nodeContent) {
		log.addContent(new Element("entry")
		.setAttribute(new Attribute("run", Integer.toString(run)))
		.setAttribute(new Attribute("type", type))
		.setAttribute(new Attribute("line", Integer.toString(line)))
		.setAttribute(new Attribute("col", Integer.toString(col)))
		.addContent(new Element("error").addContent(errMsg))
		.addContent(new Element("nodeContent").addContent(nodeContent)));
	}


	public static void write() {
		try {
			FileOutputStream output = new FileOutputStream(logFile);

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(dokument,output);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}