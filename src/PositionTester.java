import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
  * PositionTester
  * 
  * Retrieves XML node at given line and column number in input document
  * 
  * @author		Nico Kutscherauer
  * @link		http://www.data2type.de
  * @date 		2013-02-09
  */
public class PositionTester {
	
	private Node element;

	public PositionTester(String uri, int line, int col) throws IOException, SAXException, XPathExpressionException {

		InputStream	is = new FileInputStream(new File(uri));
		Document document = PositionalXMLReader.readXML(is);


		XPathFactory xFactory = XPathFactory.newInstance();
		XPath xpath = xFactory.newXPath();
		XPathExpression expr = xpath.compile("//*");
		Object result = expr.evaluate(document,XPathConstants.NODESET);

		NodeList allNodes = (NodeList) result;
		int i = 0;
		Node node;
		while (allNodes.item(i) != null) {
			node = allNodes.item(i);
			i++;
			int ln = (Integer) node.getUserData("lineNumber");
			int cn = (Integer) node.getUserData("columnNumber");
			if(line == ln && col == cn){
				element = node;
				break;
			}
		}
	}

	public String getElementId(){
		return (String) this.element.getUserData("elementId");
	}
	public Node getElement(){
		return this.element;
	}
}
