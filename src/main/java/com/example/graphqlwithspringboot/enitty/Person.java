package com.example.graphqlwithspringboot.enitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String Mobile;
    private String email;
    @ElementCollection
    @CollectionTable(name = "address_array",joinColumns = @JoinColumn(name = "id"))
    private List<String> address;

}
