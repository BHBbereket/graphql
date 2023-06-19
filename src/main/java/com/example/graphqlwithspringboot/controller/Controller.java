package com.example.graphqlwithspringboot.controller;

import com.example.graphqlwithspringboot.dao.PersonRepository;
import com.example.graphqlwithspringboot.enitty.Person;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class Controller {
    @Autowired
    private PersonRepository repository;
    @Value("classpath:person.graphqls")
    private Resource schemaResource;

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);

        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema=new SchemaGenerator().makeExecutableSchema(registry,wiring);

        graphQL=GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        DataFetcher<List<Person>> fetcher1 = data -> {
            return (List<Person>) repository.findAll();
        };
        DataFetcher<Person> fetcher2 = data -> {
            return repository.findByEmail(data.getArgument("email"));
        };
        return RuntimeWiring.newRuntimeWiring().type("Query", typeWiring ->
                        typeWiring.dataFetcher("getAllUsers", fetcher1).dataFetcher("findPerson", fetcher2))
                .build();

    }


    @PostMapping("/addusers")
    public String addPerson(@RequestBody List<Person> people) {
        repository.saveAll(people);
        return "added successfuly " + people.size() + " users added";
    }

    @GetMapping("/")
    public List<Person> getPeople() {
        return (List<Person>) repository.findAll();
    }


    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll(@RequestBody String query){
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getByEmail")
    public ResponseEntity<Object> getByEmail(@RequestBody String query){
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
