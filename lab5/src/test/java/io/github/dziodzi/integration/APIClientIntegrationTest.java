package io.github.dziodzi.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.dziodzi.entity.Category;
import io.github.dziodzi.entity.Location;
import io.github.dziodzi.service.APIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class APIClientIntegrationTest {

    @Container
    public static GenericContainer<?> wireMockContainer = new GenericContainer<>("wiremock/wiremock:latest")
            .withExposedPorts(8080)
            .waitingFor(Wait.forListeningPort());

    private APIClient apiClient;

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        String baseUrl = "https://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080);
        registry.add("custom.api.categories-url", () -> baseUrl + "/public-api/v1.4/place-categories");
        registry.add("custom.api.locations-url", () -> baseUrl + "/public-api/v1.4/locations");
    }

    @BeforeEach
    public void setup() {
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));

        stubFor(get(urlEqualTo("/public-api/v1.4/place-categories"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\": 89, \"slug\": \"amusement\", \"name\": \"Развлечения\"}, " +
                                "{\"id\": 114, \"slug\": \"animal-shelters\", \"name\": \"Питомники\"}]")));

        stubFor(get(urlEqualTo("/public-api/v1.4/locations"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"slug\":\"spb\",\"name\":\"Санкт-Петербург\"}, " +
                                "{\"slug\":\"msk\",\"name\":\"Москва\"}]")));
    }


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.apiClient = new APIClient(restTemplate,
                "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080) + "/public-api/v1.4/place-categories",
                "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getMappedPort(8080) + "/public-api/v1.4/locations",
                4, 2
        );
    }

    @Test
    public void fetchCategories_Success_ShouldReturnListOfCategories() {
        List<Category> categories = apiClient.fetchCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals("Развлечения", categories.get(0).getName());
        assertEquals("Питомники", categories.get(1).getName());
        assertEquals("amusement", categories.get(0).getSlug());
    }

    @Test
    public void fetchLocations_Success_ShouldReturnListOfLocations() {
        List<Location> locations = apiClient.fetchLocations();

        assertNotNull(locations);
        assertEquals(2, locations.size());
        assertEquals("Санкт-Петербург", locations.get(0).getName());
        assertEquals("Москва", locations.get(1).getName());
    }

    @Test
    public void fetchCategories_EmptyList_ShouldReturnEmptyList() {
        stubFor(get(urlEqualTo("/public-api/v1.4/place-categories"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<Category> categories = apiClient.fetchCategories();

        assertNotNull(categories);
        assertTrue(categories.isEmpty(), "Список категорий должен быть пустым");
    }

    @Test
    public void fetchLocations_ServerError_ShouldThrowException() {
        stubFor(get(urlEqualTo("/public-api/v1.4/locations"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<Location> locations = apiClient.fetchLocations();

        assertNotNull(locations);
        assertTrue(locations.isEmpty(), "Список категорий должен быть пустым");
    }
}
