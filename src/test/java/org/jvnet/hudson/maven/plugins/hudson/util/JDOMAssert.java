package org.jvnet.hudson.maven.plugins.hudson.util;


import org.custommonkey.xmlunit.XMLAssert;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Enhancement to XMLAssert that includes methods to accept JDOM Document
 * objects.
 *
 * @author edelsonj
 *
 */
public class JDOMAssert extends XMLAssert {

    private static final DOMOutputter DOM_OUTPUTTER = new DOMOutputter();
    private static final XMLOutputter COMPACT_OUTPUTTER = new XMLOutputter(Format
            .getCompactFormat());

    private static org.w3c.dom.Document DOM(Document doc) throws JDOMException {
        return DOM_OUTPUTTER.output(doc);
    }

    private static String COMPACT(Document doc) throws Exception {
        return COMPACT_OUTPUTTER.outputString(doc);
    }

    public static void assertXMLEqual(Document control, Document test) throws Exception {
        assertXMLEqual(DOM(control), DOM(test));
    }

    public static void assertXMLEqualWhenCompact(Document control, Document test) throws Exception {
        assertXMLEqual(COMPACT(control), COMPACT(test));
    }

    public static void assertXMLEqual(String msg, Document control, Document test)
            throws JDOMException {
        assertXMLEqual(msg, DOM(control), DOM(test));
    }

    public static void assertXMLEqualWhenCompact(String msg, Document control, Document test)
            throws Exception {
        assertXMLEqual(msg, COMPACT(control), COMPACT(test));
    }

}
