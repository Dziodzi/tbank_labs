package io.github.dziodzi.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "places")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Place {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Place slug cannot be blank")
    @Column(nullable = false)
    private String slug;
    
    @NotBlank(message = "Place name cannot be blank")
    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Event> events;
}
