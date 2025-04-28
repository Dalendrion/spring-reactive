package com.dalendrion.spring.reactive.spring_reactive;

import com.dalendrion.spring.reactive.spring_reactive.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReactiveRepository extends ReactiveMongoRepository<Customer, String> {
}
