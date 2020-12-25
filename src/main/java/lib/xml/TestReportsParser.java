package lib.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lib.xml.XMLHandler.Action;

public class TestReportsParser {
	
	public static <T extends DefaultHandler> void parse(File file, T handler) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(file, handler);
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		String path = "build/test-results/test";
		File file = new File(path+"/TEST-tests.lib.TestStringLib.xml");
		parse(file, new XMLHandler.Builder()
			.add(new Action.Builder("testcase")
				.handle("time", time -> System.out.printf("Time=%s|", time))
				.handle("name", name -> System.out.printf("Name=%s|", name))
				.handle("classname", classname -> System.out.printf("%nClassName=%s|", classname))
				.build())
			.add(new Action.Builder("testsuite")
				.handle("time", time -> System.out.printf("SuiteTime=%s|", time))
				.handle("timestamp", timestamp -> System.out.printf("Timestamp=%s|", timestamp))
				.handle("errors", errors -> System.out.printf("Errors=%s|", errors))
				.handle("failures", failures -> System.out.printf("Failures=%s|", failures))
				.handle("skipped", skipped -> System.out.printf("Skipped=%s|", skipped))
				.handle("tests", tests -> System.out.printf("Tests=%s|", tests))
				.build())
			.build()
		);
	}
}
