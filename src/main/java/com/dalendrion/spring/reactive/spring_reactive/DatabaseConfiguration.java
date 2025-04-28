package com.dalendrion.spring.reactive.spring_reactive;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
public class DatabaseConfiguration extends AbstractReactiveMongoConfiguration {
    @Bean
    public MongoClient getMongoClient() {
        return MongoClients.create();
    }

    @Bean
    public ReactiveMongoTemplate getTemplate() {
        return new ReactiveMongoTemplate(getMongoClient(), getDatabaseName());
    }

    @Override
    protected String getDatabaseName() {
        return "genuine_coder";
    }
}
