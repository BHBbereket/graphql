package com.example.graphqlwithspringboot.service;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.ResponseCache;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private PersonRepository repository;
    @Value("classpath:person.graphqls")
    private Resource schemaResource;

    private Logger logger= LogManager.getLogger(Service.class);

    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        logger.info("Loading of the schema for graphQL started.");
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);

        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema=new SchemaGenerator().makeExecutableSchema(registry,wiring);

        graphQL=GraphQL.newGraphQL(schema).build();
        logger.info("GraphQl creation was completed and waiting for query execution");
    }

    private RuntimeWiring buildWiring() {
        logger.info("building wiring method excekuted for RuntimeWiring");
        DataFetcher<List<Person>> fetcher1 = data -> {
            return (List<Person>) repository.findAll();
        };
        DataFetcher<Person> fetcher2 = data -> {
            return repository.findByEmail(data.getArgument("email"));
        };
        logger.info("building wiring was completed");
        return RuntimeWiring.newRuntimeWiring().type("Query", typeWiring ->
                        typeWiring.dataFetcher("getAllUsers", fetcher1).dataFetcher("findPerson", fetcher2))
                .build();

    }

    public String addPeople(List<Person> people){

        repository.saveAll(people);
        logger.info("added successfuly \" {} \" users added",people.size());
        return "added successfuly " + people.size() + " users added";
    }

    public List<Person> getAll(){
        return (List<Person>) repository.findAll();
    }

    public ResponseEntity<Object> getAllByGQL(String query){
        ExecutionResult result= graphQL.execute(query);
        logger.info("getAllBGQL \n{} ",result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity<Object> getByEmailGQL(String query){
        ExecutionResult result= graphQL.execute(query);
        logger.info("getByEmail \n{} ",result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
