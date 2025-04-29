package com.dalendrion.spring.reactive.spring_reactive;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class BackpressureTests {
    public Flux<Long> createNoOverflowFlux() {
        return Flux.range(1, Integer.MAX_VALUE)
                .log()
                .concatMap(x -> Mono.delay(Duration.ofMillis(100))); // simulate that processing takes time
    }

    public Flux<Long> createOverflowFlux() {
        return Flux.interval(Duration.ofMillis(1))
                .log()
                .concatMap(x -> Mono.delay(Duration.ofMillis(100)));
    }

    public Flux<Long> createOverflowFluxDropExcess() {
        return Flux.interval(Duration.ofMillis(1))
                .onBackpressureDrop()
                .concatMap(a -> Mono.delay(Duration.ofMillis(100)).thenReturn(a))
                .doOnNext(a -> System.out.println("Element kept by customer: " + a));
    }

    public Flux<Long> createOverflowFluxBufferExcess() {
        return Flux.interval(Duration.ofMillis(1))
                .onBackpressureBuffer(50, BufferOverflowStrategy.DROP_OLDEST)
                .concatMap(a -> Mono.delay(Duration.ofMillis(100)).thenReturn(a))
                .doOnNext(a -> System.out.println("Element kept by customer: " + a));
    }

    @Test
    public void testNoOverflowFlux() {
        createNoOverflowFlux()
                .blockLast();
        // The .blockLast() method subscribes to the producer.
        // Here we have control over the producer.
        // Each time the subscription calls onNext(), a new value will be produced.
        // So there will be no memory overflow.
    }

    @Test
    public void testOverflowFlux() {
        createOverflowFlux()
                .blockLast();
        // The .blockLast() method subscribes to the producer.
        // Here we have control over the producer.
        // The producer provides a new value every millisecond. But each value takes 100 ms to be processed.
        // So there will be memory overflow.
    }

    @Test
    public void testOverflowFluxDropExcess() {
        createOverflowFluxDropExcess()
                .blockLast();
        // The producer provides a new value every millisecond. But each value takes 100 ms to be processed.
        // We drop the data we can't process. So we keep the 1, and drop the remaining 99.
    }

    @Test
    public void testOverflowBufferExcess() {
        createOverflowFluxBufferExcess()
                .blockLast();
        // The producer provides a new value every millisecond. But each value takes 100 ms to be processed.
        // We buffer the data we can't process, up to 50 datasets. We still won't be able to handle all items.
        // so once the buffer also overflows, we drop the oldest and keep the newest data.
    }

}
