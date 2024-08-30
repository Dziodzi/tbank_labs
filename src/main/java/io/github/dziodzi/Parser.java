package io.github.dziodzi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.dziodzi.entities.City;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Parser {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final String currentDirectory = System.getProperty("user.dir");
    private static final Path jsonDirPath = Paths.get(currentDirectory, "src", "main", "java", "io", "github", "dziodzi", "jsons");
    private static final Path xmlDirPath = Paths.get(currentDirectory, "src", "main", "java", "io", "github", "dziodzi", "xmls");

    private static City parseCityFromJson(String filePath) {
        try {
            log.info("Reading file: {}.json", filePath);
            City city = objectMapper.readValue(new File(filePath), City.class);
            log.info("File successfully read. City: {}, Longitude: {}, Latitude: {}",
                    city.getSlug(), city.getCoords().getLon(), city.getCoords().getLat());
            return city;
        } catch (IOException e) {
            log.error("Error reading file: {} - {}", filePath, e.getMessage());
            return null;
        }
    }

    private static void toXML(City city, String outputPath) {
        try {
            log.info("Converting object to XML...");
            String xmlContent = xmlMapper.writeValueAsString(city);
            log.debug("XML Content: \n{}", xmlContent);

            File xmlFile = new File(outputPath);
            xmlMapper.writeValue(xmlFile, city);
            log.info("XML file successfully saved to: {}", outputPath);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to XML: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Error saving XML file: {}", e.getMessage());
        }
    }

    public static void processingJson(String filename) {
        log.info("Starting parsing of file {}", filename);

        City city = Parser.parseCityFromJson(jsonDirPath + "\\" + filename + ".json");

        if (city != null) {
            log.debug("City successfully retrieved: {}", city.getSlug());
            Parser.toXML(city, xmlDirPath + "\\" + filename + ".xml");
        } else {
            log.warn("Failed to parse file {}.json", filename);
        }
    }
}
