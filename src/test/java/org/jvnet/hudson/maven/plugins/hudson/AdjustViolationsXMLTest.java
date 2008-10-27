package org.jvnet.hudson.maven.plugins.hudson;

import java.io.File;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jvnet.hudson.maven.plugins.hudson.AdjustViolationsMojo;

import com.mtvi.plateng.testing.ReflectionHelper;
import com.mtvi.plateng.testing.xml.JDOMAssert;

public class AdjustViolationsXMLTest extends TestCase {

    private AdjustViolationsMojo mojo;

    @Override
    public void setUp() throws Exception {
        mojo = new AdjustViolationsMojo();
        ReflectionHelper.setField(mojo, "min", 20);
        ReflectionHelper.setField(mojo, "max", 9991);
        ReflectionHelper.setField(mojo, "unstable", 19999);
    }

    public void testOne() throws Exception {
        Document before = new SAXBuilder().build(new File(
                "src/test/resources/unit/violations/before_1.xml"));
        Document after = new SAXBuilder().build(new File(
                "src/test/resources/unit/violations/after_1.xml"));
        assertTrue(mojo.changeViolations(before));

        JDOMAssert.assertXMLEqualWhenCompact(after, before);

    }

}
