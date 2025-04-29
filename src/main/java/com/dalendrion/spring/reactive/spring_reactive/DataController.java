package com.dalendrion.spring.reactive.spring_reactive;

import com.dalendrion.spring.reactive.spring_reactive.model.Customer;
import com.dalendrion.spring.reactive.spring_reactive.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.Map;

@RestController
public class DataController {
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostMapping("/customer/create")
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {
        return reactiveMongoTemplate.save(customer);
    }

    @GetMapping("/customer")
    public Mono<Customer> findCustomerByIdParam(@RequestParam("customerId") String customerId) {
        return reactiveMongoTemplate.findById(customerId, Customer.class);
    }

    @GetMapping("/customer/{customerId}")
    public Mono<Customer> findCustomerById(@PathVariable("customerId") String customerId) {
        Criteria criteria = Criteria.where("id").is(customerId);
        Query query = Query.query(criteria);
        return reactiveMongoTemplate.findOne(query, Customer.class);
    }

    @PostMapping("/order/create")
    public Mono<Order> createOrder(@RequestBody Order order) {
        return reactiveMongoTemplate.save(order);
    }

    @GetMapping("/order")
    public Mono<Order> findOrderByIdParam(@RequestParam("orderId") String orderId) {
        return reactiveMongoTemplate.findById(orderId, Order.class);
    }

    @GetMapping("/order/{orderId}")
    public Mono<Order> findOrderById(@PathVariable("orderId") String orderId) {
        Criteria criteria = Criteria.where("id").is(orderId);
        Query query = Query.query(criteria);
        return reactiveMongoTemplate.findOne(query, Order.class);
    }

    @GetMapping("sales/summary")
    public Mono<Map<String, BigDecimal>> calculateSummary() {
        return reactiveMongoTemplate.findAll(Customer.class)
                .flatMap(customer -> Mono.zip(Mono.just(customer), calculateSum(customer.getId())))
                .collectMap(tuple2 -> tuple2.getT1().getName(), Tuple2::getT2);
    }

    private Mono<BigDecimal> calculateSum(String customerId) {
        Criteria criteria = Criteria.where("customerId").is(customerId);
        Query query = Query.query(criteria);
        return reactiveMongoTemplate.find(query, Order.class)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
