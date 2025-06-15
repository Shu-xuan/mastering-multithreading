package com.hhy.completable.aggr;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 描述: TODO
 * </p>
 *
 * @Author huhongyuan
 */
public class AllOfDemo {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<Integer> taskA = CompletableFuture.supplyAsync(() -> new Random().nextInt(100) + 1);
        CompletableFuture<Integer> taskB = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("TaskB Exception");
        });
        CompletableFuture<Integer> taskC = CompletableFuture.supplyAsync(() -> new Random().nextInt(100) + 1);
        // 异步任务D，依赖三个任务的结果
        CompletableFuture<Void> taskD = CompletableFuture.allOf(taskA, taskB, taskC);
        taskD.thenApply(result -> taskA.join() + taskB.join() + taskC.join())
                        .thenAccept(System.out::println);
        Thread.sleep(100);
    }
}
