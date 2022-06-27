package com.kamesuta.totalplayercount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 複数の非同期Futureを固める
 */
public class JoinAllCollector {
    /**
     * Stream<CompletableFuture<A>> を固めて CompletableFuture<Stream<A>> に変換
     */
    public static <E> Collector<
            CompletableFuture<E>,
            List<CompletableFuture<E>>,
            CompletableFuture<Stream<E>>> toJoinAllFuture() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (a, b) -> Stream.of(a, b).flatMap(List::stream).collect(toList()),
                futures -> CompletableFuture
                        .allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(nothing -> futures.stream().map(CompletableFuture::join))
        );
    }
}
