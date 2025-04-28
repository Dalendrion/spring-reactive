package com.dalendrion.spring.reactive.spring_reactive;

import com.dalendrion.spring.reactive.spring_reactive.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DataController {
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostMapping("/customer/create")
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {
        return reactiveMongoTemplate.save(customer).log();
    }
}
