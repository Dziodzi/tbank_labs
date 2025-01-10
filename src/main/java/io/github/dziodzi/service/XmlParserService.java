package io.github.dziodzi.service;

import io.github.dziodzi.tools.LogExecutionTime;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

@Service
@Log4j2
@LogExecutionTime
public class XmlParserService {
    public String getValueOfCode(String code, String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            document.getDocumentElement().normalize();

            NodeList valutes = document.getElementsByTagName("Valute");
            for (int i = 0; i < valutes.getLength(); i++) {
                Element valute = (Element) valutes.item(i);

                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();

                if (charCode.equals(code)) {
                    return valute.getElementsByTagName("VunitRate").item(0).getTextContent();
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
