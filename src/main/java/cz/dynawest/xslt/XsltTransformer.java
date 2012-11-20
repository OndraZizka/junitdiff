
package cz.dynawest.xslt;


import java.io.File;
import java.io.InputStream;
import java.util.logging.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 *
 * @author Ondrej Zizka
 */
public class XsltTransformer
{
  private static final Logger log = Logger.getLogger( XsltTransformer.class.getName() );
  

    static public void main(String[] arg) {
        if(arg.length != 3) {
            System.err.println("Usage: SimpleXMLTransform " +
                "<input.xml> <input.xsl> <output>");
            System.exit(1);
        }
        String inXML  = arg[0];
        String inXSL  = arg[1];
        String outTXT = arg[2];

        XsltTransformer st = new XsltTransformer();
        try {
            XsltTransformer.transform( inXML, inXSL, outTXT );
        } catch(TransformerConfigurationException e) {
            System.err.println("Invalid factory configuration");
            System.err.println(e);
        } catch(TransformerException e) {
            System.err.println("Error during transformation");
            System.err.println(e);
        }
    }
    
    
    
    /**
     *  Transform from file names.
     */
    public static void transform( String inXML, String inXSL, String outTXT )
                throws TransformerConfigurationException, TransformerException 
    {
        TransformerFactory factory = TransformerFactory.newInstance();

        StreamSource xslStream = new StreamSource(inXSL);
        Transformer transformer = factory.newTransformer(xslStream);
        transformer.setErrorListener(new MyErrorListener());

        StreamSource in = new StreamSource(inXML);
        StreamResult out = new StreamResult(outTXT);
        transformer.transform(in,out);
        System.out.println("The generated HTML file is:" + outTXT);
    }
    

    
    /**
     *  Transform from streams.
     */
    public static void transform( InputStream in, InputStream xsl, File out )
                throws TransformerConfigurationException, TransformerException
    {
        TransformerFactory factory = TransformerFactory.newInstance();

        StreamSource xslStream = new StreamSource( xsl );
        Transformer transformer = factory.newTransformer(xslStream);
        transformer.setErrorListener(new MyErrorListener());

        StreamSource inSource = new StreamSource( in );
        StreamResult outResult = new StreamResult( out );
        transformer.transform( inSource, outResult );
    }
    
    

}// class XsltTransformer






class MyErrorListener implements ErrorListener {
    @Override
    public void warning(TransformerException e)
                throws TransformerException {
        show("Warning",e);
        throw(e);
    }
    @Override
    public void error(TransformerException e)
                throws TransformerException {
        show("Error",e);
        throw(e);
    }
    @Override
    public void fatalError(TransformerException e)
                throws TransformerException {
        show("Fatal Error",e);
        throw(e);
    }
    private void show(String type,TransformerException e) {
        System.out.println(type + ": " + e.getMessage());
        if(e.getLocationAsString() != null)
            System.out.println(e.getLocationAsString());
    }
}  
