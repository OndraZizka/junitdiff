package cz.dynawest.xslt

import java.io.*
import java.util.*
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 *
 * @author Ondrej Zizka
 */
object XsltTransformer {

    @JvmStatic
    fun main(arg: Array<String>) {
        if (arg.size != 3) {
            System.err.println(
                "Usage: SimpleXMLTransform " +
                        "<input.xml> <input.xsl> <output>"
            )
            System.exit(1)
        }
        val inXML = arg[0]
        val inXSL = arg[1]
        val outTXT = arg[2]

        try {
            transform(inXML, inXSL, outTXT)
        } catch (e: TransformerConfigurationException) {
            System.err.println("Invalid factory configuration")
            System.err.println(e)
        } catch (e: TransformerException) {
            System.err.println("Error during transformation")
            System.err.println(e)
        }
    }

    /**
     * Transform from file names.
     */
    @Throws(TransformerConfigurationException::class, TransformerException::class)
    fun transform(inXML: String, inXSL: String, outTXT: String) {
        val factory = TransformerFactory.newInstance()
        val xslStream = StreamSource(inXSL)
        val transformer = factory.newTransformer(xslStream)
        transformer.errorListener = MyErrorListener()
        val `in` = StreamSource(inXML)
        val out = StreamResult(outTXT)
        transformer.transform(`in`, out)
        println("The generated HTML file is: $outTXT")
    }

    /**
     * Transform from streams.
     */
    @JvmOverloads
    @Throws(TransformerConfigurationException::class, TransformerException::class)
    fun transform(inputStream: InputStream, xsl: InputStream, outputFile: File, params: Map<String, Any> = Collections.emptyMap()) {
        outputFile.parentFile.mkdirs()

        val factory = TransformerFactory.newInstance()
        val xslStream = StreamSource(xsl)
        val transformer = factory.newTransformer(xslStream)
        transformer.errorListener = MyErrorListener()
        val inSource = StreamSource(inputStream)
        val outResult = StreamResult(outputFile)
        for ((key, value) in params) {
            if (null == key) continue
            transformer.setParameter(key, value)
        }
        transformer.transform(inSource, outResult)
    }
}

internal class MyErrorListener : ErrorListener {
    @Throws(TransformerException::class)
    override fun warning(e: TransformerException) {
        show("Warning", e)
        throw e
    }

    @Throws(TransformerException::class)
    override fun error(e: TransformerException) {
        show("Error", e)
        throw e
    }

    @Throws(TransformerException::class)
    override fun fatalError(e: TransformerException) {
        show("Fatal Error", e)
        throw e
    }

    private fun show(type: String, e: TransformerException) {
        println(type + ": " + e.message)
        if (e.locationAsString != null) println(e.locationAsString)
    }
}