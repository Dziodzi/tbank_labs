package io.github.dziodzi.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event name cannot be blank")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Event date cannot be null")
    @FutureOrPresent(message = "Event date must be in the present or future")
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotNull(message = "Place cannot be null")
    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}
