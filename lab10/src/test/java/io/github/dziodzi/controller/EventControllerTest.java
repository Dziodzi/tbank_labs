package io.github.dziodzi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dziodzi.entity.Event;
import io.github.dziodzi.entity.Place;
import io.github.dziodzi.repository.EventRepository;
import io.github.dziodzi.repository.PlaceRepository;
import io.github.dziodzi.service.interfaces.EventService;
import org.junit.jupiter.api.*;
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
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13")
                    .withDatabaseName("lab10")
                    .withUsername("postgres")
                    .withPassword("7296");
    
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    
    private Place createTestPlace() {
        return Place.builder()
                .slug("slug")
                .name("name")
                .build();
    }
    
    private Event createTestEvent(Place place) {
        return Event.builder()
                .name("Test Event")
                .date(LocalDate.now())
                .place(place)
                .build();
    }
    
    @Test
    @Order(1)
    public void createEvent_ShouldSaveToDatabase() throws Exception {
        Place place = createTestPlace();
        
        mockMvc.perform(post("/api/v2/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(place)))
                .andExpect(status().isCreated());
        
        Place savedPlace = placeRepository.findById(1L).orElseThrow();
        
        Event event = createTestEvent(savedPlace);
        mockMvc.perform(post("/api/v2/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());
        
        List<Event> eventList = eventService.getAllEvents();
        assertThat(eventList).hasSize(1);
        assertThat(eventList.get(0).getName()).isEqualTo(event.getName());
    }
    
    @Test
    public void createEvent_ShouldReturnNotFoundForNonExistentPlace() throws Exception {
        Event event = Event.builder()
                .name("name")
                .date(LocalDate.now())
                .place(Place.builder().id(5L).build())
                .build();
        
        MvcResult result = mockMvc.perform(post("/api/v2/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("Place not found for ID: 5");
    }
    
    @Test
    @Order(2)
    public void updateEvent_ShouldUpdateEventInDatabase() throws Exception {
        Event updatedEvent = Event.builder()
                .name("Updated Event")
                .date(LocalDate.now().plusDays(1))
                .place(Place.builder().id(1L).build())
                .build();
        
        mockMvc.perform(put("/api/v2/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk());
        
        Event eventFromDb = eventService.getEventById(1L);
        assertThat(eventFromDb.getName()).isEqualTo(updatedEvent.getName());
        assertThat(eventFromDb.getDate()).isEqualTo(updatedEvent.getDate());
    }
    
    @Test
    public void updateEvent_ShouldReturnNotFound_WhenEventDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v2/events/1337")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Event())))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void deleteEvent_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v2/events/1"))
                .andExpect(status().isNoContent());
        
        assertThat(eventRepository.findById(1L)).isEmpty();
    }
    
    @Test
    public void deleteEvent_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v2/events/1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void searchEvents_ShouldReturnFilteredEvents() throws Exception {
        createAndSaveEvent("Event 1", LocalDate.now());
        createAndSaveEvent("Event 2", LocalDate.now().plusDays(1));
        createAndSaveEvent("Event 3", LocalDate.now().plusDays(2));
        createAndSaveEvent("Event 4", LocalDate.now().plusDays(3));
        
        MvcResult result = mockMvc.perform(get("/api/v2/events/search")
                        .param("placeId", "1")
                        .param("name", "Event")
                        .param("fromDate", LocalDate.now().toString())
                        .param("toDate", LocalDate.now().plusDays(3).toString()))
                .andExpect(status().isOk())
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        List<Event> returnedEvents = objectMapper.readValue(response, new TypeReference<>() {});
        
        assertThat(returnedEvents).hasSize(4);
        assertThat(returnedEvents).extracting("name")
                .containsExactlyInAnyOrder("Event 1", "Event 2", "Event 3", "Event 4");
    }
    
    private void createAndSaveEvent(String name, LocalDate date) throws Exception {
        Event event = Event.builder()
                .name(name)
                .date(date)
                .place(Place.builder().id(1L).build())
                .build();
        
        mockMvc.perform(post("/api/v2/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());
    }
}
