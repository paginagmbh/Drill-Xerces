import java.io.File;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;


/**
  * SaxonXSLT
  * 
  * Performs XSL transformation on inputXML using Saxon9B's s9api
  * 
  * @author		Tobias Fischer / Marko Hedler
  * @date 		2013-02-09
  */
public class SaxonXSLT {

	protected static final Boolean NO_DTD_VALIDATION = false;
	protected static final Boolean DTD_VALIDATION = true;
	
	public static void doTransform(String inputXML, int level) {

		String transformationXSLT_1;
		
		if (level == 1) {
			 transformationXSLT_1 = SaxonXSLT.class.getResource(
					"/resources/destroy1.xsl").toString();
		} else if (level == 2) {
			transformationXSLT_1 = SaxonXSLT.class.getResource(
					"/resources/destroy2.xsl").toString();
		} else {
			 transformationXSLT_1 = SaxonXSLT.class.getResource(
					"/resources/destroy1.xsl").toString();
		}

		String outputFile_1 = inputXML;

		try {
			run(inputXML, transformationXSLT_1, outputFile_1, NO_DTD_VALIDATION);

		} catch (SaxonApiException e) {
			e.printStackTrace();
		}

	}

	public static void run(String sourceDoc, String xslDoc, String resultDoc,
			Boolean dtdValidation) throws SaxonApiException {

		/*
		 * http://www.cs.duke.edu/courses/fall08/cps116/docs/saxon/samples/java/
		 * S9APIExamples.java
		 */

		Processor proc = new Processor(false);

		XsltCompiler comp = proc.newXsltCompiler();
		XsltExecutable exec = comp.compile(new StreamSource(xslDoc));
		XsltTransformer transformer = exec.load();

		DocumentBuilder builder = proc.newDocumentBuilder();
		builder.setLineNumbering(true);
		builder.setDTDValidation(dtdValidation);
		XdmNode source = builder.build(new StreamSource(new File(sourceDoc)));

		Serializer out = new Serializer();
		// out.setOutputProperty(Serializer.Property.METHOD, "html");
		out.setOutputProperty(Serializer.Property.INDENT, "yes");
		out.setOutputFile(new File(resultDoc));

		transformer.setInitialContextNode(source);
		transformer.setDestination(out);
		transformer.transform();
	}

}
