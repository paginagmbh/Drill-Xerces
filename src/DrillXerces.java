import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException; 
import org.xml.sax.SAXNotSupportedException;
import java.io.File;
import java.io.IOException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


/**
 * DrillXerces
 * 
 * Fun implementation of Apaches free Xerces XML Parser ( http://xerces.apache.org/xerces-j/ )
 * Drill-Xerces tends to get angry if users don't follow his validation hints...
 * 
 * BE CAREFUL! If you make Drill-Xerces too angry, he'll take over your document...
 * Damage to your document is predicted! Use at your own risk!
 * 
 * 
 * @author		Tobias Fischer / Marko Hedler / Nico Kutscherauer
 * @link		http://www.github.com/paginagmbh/
 * @version		1.0
 * @date		2013-02-09
 */
public class DrillXerces {

	private static String inputDocument;
	private static int XSLTdestruction = 0;
	public static DOMParser parser = null;

	private static void setFeature(String feature, boolean setting){
		try {
			parser.setFeature(feature, setting);
		} catch (SAXNotRecognizedException e) {
			System.out.print("Unrecognized feature: ");
			System.out.println(feature);
		} catch (SAXNotSupportedException e) {
			System.out.print("Unrecognized feature: ");
			System.out.println(feature);
		}
	}

	public static void main (String args[]){

		inputDocument = args[0];
		final String inputDocName = new File(args[0]).getName();
		final String inputDocPath = (new File(args[0]).getParent() != null) ? new File(args[0]).getParent() : ".";

		String inputDocumentID = inputDocName.replace(".", "-");

		String validationRun = "1";
		if(new File(inputDocPath + File.separator + "log_" + inputDocumentID + ".xml").exists()) {
			inputDocument logDoc = new inputDocument(new File(inputDocPath + File.separator + "log_" + inputDocumentID + ".xml"));
			validationRun = logDoc.returnXpathResult("/log/@run").replace("run=", "").replace("\"", "");
			validationRun = Integer.toString(new Integer(validationRun) + 1);
		}


		final int run = new Integer(validationRun);

		final String currLogname = "log_" + inputDocumentID + ".xml";

		if(run == 1) {
			xmlLog.newLog(new File(inputDocPath + File.separator + currLogname), run);
			//System.out.println("new run: 1");
			System.out.println("Hi, I'm Drill-Xerces, I'll help you solving your XML problems...");

		} else {
			xmlLog.update(new File(inputDocPath + File.separator + currLogname), run);
			//System.out.println("update run: " + run);
		}

		parser = new DOMParser();

		ErrorHandler handler = new ErrorHandler() {
			public void warning(SAXParseException e) throws SAXException {
				explicitHandler(e, "warning");
			}

			public void error(SAXParseException e) throws SAXException {
				explicitHandler(e, "error");
			}

			public void fatalError(SAXParseException e) throws SAXException {
				explicitHandler(e, "fatal");
				//throw e;
			}

			private void explicitHandler(SAXParseException e, String type) {

				PositionTester posTest = null;
				try {
					posTest = new PositionTester(inputDocument, e.getLineNumber(), e.getColumnNumber());

				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				int errLineNum =  e.getLineNumber();
				int errColNum =  e.getLineNumber();
				String errMsg =  e.getMessage();
				String errorNodeContent = posTest.getElement().getTextContent();

				xmlLog.add(run, type, errLineNum, errColNum, errMsg, errorNodeContent);

				// logfile
				inputDocument inDoc = new inputDocument(new File(inputDocPath + File.separator + currLogname));
				//System.out.println(prevLogname.replace((run-1)+".xml", i+".xml"));

				int errorCounterForAllRuns = 1;
				for(int i = run-1; i >= 1; i--) {

					//System.out.println("run: "+ run +" | search for: " +i );

					// test type, line and column on logfile
					Boolean result1 = inDoc.testXpath("//entry[@run='"+i+"' and @type='"+type+"' and @line='"+errLineNum+"' and @col='"+errColNum+"']");
					//if(result1) { System.out.println("1"); }

					// search for same error message within line+-2 and col+-10 in logfile
					Boolean result2 = inDoc.testXpath("//entry[@run='"+i+"' and @type='"+type+"' and @line=("+(errLineNum-2)+" to "+(errLineNum+2)+") and @col=("+(errColNum-10)+" to "+(errColNum+10)+")]/error = \"" + errMsg + "\"");
					//if(result2) { System.out.println("2"); }

					// search for same NodeContent in logfile
					Boolean result3 = inDoc.testXpath("//entry[@run='"+(run-1)+"']/nodeContent = '" + errorNodeContent + "'");
					//if(result3) { System.out.println("3"); }

					if(result1 | result2 | result3) {
						errorCounterForAllRuns++;
					}
				}

				System.err.println("["+type+"]["+errorCounterForAllRuns+"x] " + e.getMessage());
				throwExplicitMessage(errorCounterForAllRuns);
			}
		};

		setFeature("http://xml.org/sax/features/validation", true); 
		setFeature("http://apache.org/xml/features/validation/schema",true); 

		try {
			parser.setErrorHandler(handler);
			parser.parse(inputDocument);

			xmlLog.write();

			if(XSLTdestruction > 0) {
				SaxonXSLT.doTransform(inputDocument, XSLTdestruction);
			}

		} catch (IOException ie){
			System.out.println("Could not read file.");
			ie.printStackTrace();
		} catch (SAXException e) {
			System.out.print("Could not create Document: ");
			System.out.println(e.getMessage());
		} catch (NullPointerException e) {
			// do nothing
		}
	}


	protected static void throwExplicitMessage(int errorCounter) {
		if(errorCounter == 1) {
			System.err.println("          Small mistakes happen from time to time... ;-)");

		} else if(errorCounter == 2) {
			System.err.println("          You also did this wrong last time. Time to fix it – you can do it! :)");

		} else if(errorCounter == 3) {
			System.err.println("          Dude! Third time no fix – you definately should book an XML course");
			System.err.println("          at www.pagina-online.de or www.data2type.de");

		} else if(errorCounter == 4) {
			System.err.println("          HEY, LISTEN! I'm trying to help you! Don't ignore my hints...");

		} else if(errorCounter == 5) {
			System.err.println("          Damn it! Last chance! Fix this shit or I'll get really angry!");

		} else if(errorCounter == 6) {
			System.err.println("          This will have concequences! TAKE THIS, you noob!");
			XSLTdestruction = 1;

		} else if(errorCounter == 7) {
			System.err.println("          REALLY?! Funny symbols in your document don't bother you? ROT IN HELL!");
			System.err.println("          I'm outta here...");
			XSLTdestruction = 2;

		}if(errorCounter > 7) {
			System.err.println("          Drill-Xerces left. No more help for you fool...");
			XSLTdestruction = 2;
		}
	}
}
