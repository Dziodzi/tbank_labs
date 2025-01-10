package io.github.dziodzi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dziodzi.entity.Place;
import io.github.dziodzi.repository.PlaceRepository;
import io.github.dziodzi.service.interfaces.PlaceService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlaceControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PlaceService placeService;
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("lab10")
            .withUsername("postgres")
            .withPassword("7296");
    
    @DynamicPropertySource
    static void setDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    
    @Test
    @Order(1)
    void createPlace_ShouldSaveToDatabase() throws Exception {
        Place place = Place.builder()
                .slug("slug")
                .name("name")
                .build();
        
        mockMvc.perform(post("/api/v2/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated());
        
        List<Place> placeList = placeService.getAllPlaces();
        assertThat(placeList).hasSize(1);
        assertThat(placeList.get(0).getName()).isEqualTo(place.getName());
    }
    
    @Test
    @Order(2)
    void getAllPlaces_ShouldReturnAllPlaces() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v2/places"))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        List<Place> returnedPlaces = objectMapper.readValue(response, new TypeReference<>() {});
        
        assertThat(returnedPlaces).hasSize(1);
    }
    
    @Test
    void getPlaceById_ShouldReturnPlace() throws Exception {
        mockMvc.perform(get("/api/v2/places/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    @Order(4)
    void getPlaceById_NonExistent_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v2/places/5"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void updatePlace_ShouldUpdatePlace() throws Exception {
        Place updatedPlace = Place.builder()
                .slug("updated-slug")
                .name("updated-name")
                .build();
        
        mockMvc.perform(put("/api/v2/places/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPlace)))
                .andExpect(status().isOk());
        
        Place placeFromDb = placeService.getPlaceById(1L);
        assertThat(placeFromDb.getName()).isEqualTo(updatedPlace.getName());
    }
    
    @Test
    void deletePlace_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v2/places/1"))
                .andExpect(status().isNoContent());
        
        assertThat(placeRepository.findById(1L)).isEmpty();
    }
    
    @Test
    void deletePlace_NonExistent_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v2/places/5"))
                .andExpect(status().isNotFound());
    }
    
}
