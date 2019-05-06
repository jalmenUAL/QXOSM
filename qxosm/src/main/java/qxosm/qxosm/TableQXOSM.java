package qxosm.qxosm;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class TableQXOSM {

	public Component getTable(String file) {
		 
		NodeList nl = xPath(file, "/result/row/x/text()");
		NodeList nl2 = xPath(file, "/result/row/y/text()");

		FormLayout fl = new FormLayout();

		fl.setMargin(true);
		fl.setSpacing(true);

		TextField[] textfields = new TextField[nl.getLength()];
		for (int i = 0; i < nl2.getLength(); i++) {
			textfields[i] = new TextField();
			textfields[i].setCaption(nl.item(i).getNodeValue());
			textfields[i].setValue(nl2.item(i).getNodeValue());
			fl.addComponent(textfields[i]);
			textfields[i].setEnabled(false);
			textfields[i].setStyleName("mytextfield");

		}
		return fl;
	}

	public NodeList xPath(String expression, String XPath) {
		NodeList nodeList = null;
		try {

			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			Document xmlDocument = builder.parse(new InputSource(new StringReader(expression)));

			XPath xPath = XPathFactory.newInstance().newXPath();

			nodeList = (NodeList) xPath.compile(XPath).evaluate(xmlDocument, XPathConstants.NODESET);

		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
			e.printStackTrace();
		}
		return nodeList;
	}
}
