import java.io.File;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmEmptySequence;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

/**
 * inputDocument
 * 
 * Reads given XML document and performs XPath tests using Saxon9B's s9api
 * 
 * @author		Tobias Fischer
 * @date 		2013-02-09
 */
public class inputDocument {
	
	private XPathCompiler xpath = null;
	private DocumentBuilder builder = null;
	private XdmNode doc = null;
	private File logname = null;
	

	public inputDocument(File logname) {

		this.logname = logname;
		
		Processor proc = new Processor(false);
		xpath = proc.newXPathCompiler();

		builder = proc.newDocumentBuilder();
		builder.setLineNumbering(true);
		builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
	}
	
	
	boolean testXpath(String xpathExpression) {
		try {
			doc = builder.build(logname);
			XPathSelector selector = xpath.compile(xpathExpression).load();
			selector.setContextItem(doc);
			XdmValue result = selector.evaluate();


			if(result instanceof XdmEmptySequence) {
				//System.out.println("Neuer Fehler!");
				return false;
			} else if(result.toString().equals("false")) {
				return false;
			} else {
				//System.out.println("Alter Fehler erneut!");
				//System.out.println(result);
				return true;
			}

		} catch (SaxonApiException e) {
//			e.printStackTrace();
			return false;
		}
	}


	public String returnXpathResult(String xpathExpression) {
		try {
			doc = builder.build(logname);
			XPathSelector selector = xpath.compile(xpathExpression).load();
			selector.setContextItem(doc);
			XdmValue result = selector.evaluate();
			
			return result.toString();

		} catch (SaxonApiException e) {
			return "";
		}
	}
}
