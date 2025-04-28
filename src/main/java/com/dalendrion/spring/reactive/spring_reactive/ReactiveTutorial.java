package com.dalendrion.spring.reactive.spring_reactive;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReactiveTutorial {

    // get the Publisher
    private Mono<String> testMono() {
        return Mono.justOrEmpty("Java");
    }

    private Flux<String> testFlux() {
        return Flux.just("Java", "C", "C++", "Python", "Rust");
    }

    private Flux<String> testMap() {
        return testFlux()
                .map(string -> string.toUpperCase(Locale.ROOT));
    }

    private Flux<String> testFlatMap() {
        return testFlux()
                .flatMap(string -> Mono.just(string.toUpperCase(Locale.ROOT)));
    }

    private Flux<String> testDelay() {
        return testFlux()
                .delayElements(Duration.ofSeconds(1));
    }

    private Flux<String> testSkip() {
        return testDelay()
                .skip(Duration.ofMillis(2010));
    }

    // Multiple fluxes create data one publisher after the other.
    private Flux<Integer> testConcat() {
        return Flux.concat(
                Flux.range(1, 20).delayElements(Duration.ofSeconds(1)),
                Flux.range(101, 10).delayElements(Duration.ofSeconds(2))
        );
    }

    // Multiple fluxes create data at the same time
    private Flux<Integer> testMerge() {
        return Flux.merge(
                Flux.range(1, 20).delayElements(Duration.ofSeconds(1)),
                Flux.range(101, 10).delayElements(Duration.ofSeconds(2))
        );
    }

    // Multiple fluxes create data at the same time
    private Flux<Tuple2<Integer, Integer>> testZip() {
        return Flux.zip(
                Flux.range(1, 20).delayElements(Duration.ofSeconds(1)),
                Flux.range(101, 10).delayElements(Duration.ofSeconds(2))
        );
    }

    // This will halt the program until the flux is finished.
    private List<Integer> testBlock() {
        return testCollect().block();
    }

    private Mono<List<Integer>> testCollect() {
        return Flux.range(1, 20)
                .collectList();
    }

    private Flux<List<Integer>> testBuffer() {
        Flux<Integer> flux = Flux.range(1, 20).delayElements(Duration.ofSeconds(1));
        return flux.buffer(Duration.ofSeconds(3));
    }

    private Mono<Map<Integer, Integer>> testCollectMap() {
        return Flux.range(1, 10)
                .collectMap(i -> i, i -> i * i);
    }

    private Flux<Integer> testDoFunctions() {
        Flux<Integer> flux = Flux.range(1, 10);
        return flux.doOnEach(s -> {
            switch (s.getType()) {
                case ON_COMPLETE: System.out.println("Done");
                case ON_NEXT: System.out.println(s.get());
            }
        });
    }

    private Flux<Integer> testDoFunctions2() {
        Flux<Integer> flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));
        return flux.doOnComplete(() -> System.out.println("Done"))
                .doOnNext(System.out::println)
                .doOnSubscribe(subscription -> System.out.println("Subscribed"))
                .doOnCancel(() -> System.out.println("Canceled"));
    }

    public static void main(String[] args) throws InterruptedException {
        ReactiveTutorial reactiveTutorial = new ReactiveTutorial();

        Disposable disposable = reactiveTutorial.testDoFunctions2().subscribe();

        Thread.sleep(Duration.ofMillis(3500));

        disposable.dispose();

//        Thread.sleep(Duration.ofSeconds(25));
    }
}
