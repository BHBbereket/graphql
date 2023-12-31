package com.example.graphqlwithspringboot.dao;

import com.example.graphqlwithspringboot.enitty.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person,Integer> {
    Person findByEmail(String email);
}
