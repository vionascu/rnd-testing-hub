package com.rnd.testinghub.infrastructure.parser;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JunitXmlParser {

    public static class TestCaseInfo {
        public final String name;
        public final String status;
        public final long durationMs;
        public final String errorMessage;

        public TestCaseInfo(String name, String status, long durationMs, String errorMessage) {
            this.name = name;
            this.status = status;
            this.durationMs = durationMs;
            this.errorMessage = errorMessage;
        }
    }

    public static class TestSuiteInfo {
        public final String name;
        public final int tests;
        public final int passed;
        public final int failed;
        public final int skipped;
        public final long durationMs;
        public final List<TestCaseInfo> cases;

        public TestSuiteInfo(String name, int tests, int passed, int failed, int skipped, long durationMs, List<TestCaseInfo> cases) {
            this.name = name;
            this.tests = tests;
            this.passed = passed;
            this.failed = failed;
            this.skipped = skipped;
            this.durationMs = durationMs;
            this.cases = cases;
        }
    }

    public List<TestSuiteInfo> parse(String xmlContent) throws Exception {
        List<TestSuiteInfo> suites = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        NodeList testsuites = doc.getElementsByTagName("testsuite");

        for (int i = 0; i < testsuites.getLength(); i++) {
            Element testsuiteElement = (Element) testsuites.item(i);
            suites.add(parseTestSuite(testsuiteElement));
        }

        // If no testsuites found, try parsing as a single testsuite
        if (suites.isEmpty()) {
            Element root = doc.getDocumentElement();
            if ("testsuite".equals(root.getTagName())) {
                suites.add(parseTestSuite(root));
            }
        }

        return suites;
    }

    private TestSuiteInfo parseTestSuite(Element testsuiteElement) {
        String name = testsuiteElement.getAttribute("name");
        int tests = Integer.parseInt(testsuiteElement.getAttribute("tests"));
        int failures = Integer.parseInt(testsuiteElement.getAttribute("failures") != null && !testsuiteElement.getAttribute("failures").isEmpty()
                ? testsuiteElement.getAttribute("failures") : "0");
        int skipped = Integer.parseInt(testsuiteElement.getAttribute("skipped") != null && !testsuiteElement.getAttribute("skipped").isEmpty()
                ? testsuiteElement.getAttribute("skipped") : "0");
        double time = Double.parseDouble(testsuiteElement.getAttribute("time") != null && !testsuiteElement.getAttribute("time").isEmpty()
                ? testsuiteElement.getAttribute("time") : "0");
        long durationMs = (long) (time * 1000);
        int passed = tests - failures - skipped;

        List<TestCaseInfo> cases = new ArrayList<>();
        NodeList testcases = testsuiteElement.getElementsByTagName("testcase");

        for (int i = 0; i < testcases.getLength(); i++) {
            Element testcaseElement = (Element) testcases.item(i);
            cases.add(parseTestCase(testcaseElement));
        }

        return new TestSuiteInfo(name, tests, passed, failures, skipped, durationMs, cases);
    }

    private TestCaseInfo parseTestCase(Element testcaseElement) {
        String classname = testcaseElement.getAttribute("classname");
        String name = testcaseElement.getAttribute("name");
        double time = Double.parseDouble(testcaseElement.getAttribute("time") != null && !testcaseElement.getAttribute("time").isEmpty()
                ? testcaseElement.getAttribute("time") : "0");
        long durationMs = (long) (time * 1000);

        String status = "passed";
        String errorMessage = null;

        NodeList failures = testcaseElement.getElementsByTagName("failure");
        if (failures.getLength() > 0) {
            status = "failed";
            errorMessage = failures.item(0).getTextContent();
        }

        NodeList skipped = testcaseElement.getElementsByTagName("skipped");
        if (skipped.getLength() > 0) {
            status = "skipped";
            errorMessage = skipped.item(0).getTextContent();
        }

        String fullTestName = classname != null && !classname.isEmpty() ? classname + "." + name : name;

        return new TestCaseInfo(fullTestName, status, durationMs, errorMessage);
    }
}
