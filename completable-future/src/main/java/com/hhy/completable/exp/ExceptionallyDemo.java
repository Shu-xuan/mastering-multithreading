package com.hhy.completable.exp;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 描述: TODO
 * </p>
 *
 * @Author huhongyuan
 */
public class ExceptionallyDemo {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<Integer> taskA = CompletableFuture.supplyAsync(() -> {
//            return new Random().nextInt(100) + 1;
            throw new RuntimeException("TaskA Exception");
        });
        CompletableFuture<Integer> taskB = CompletableFuture.supplyAsync(() -> {
            return new Random().nextInt(100) + 1;
//            throw new RuntimeException("TaskB Exception");
        });
        CompletableFuture<Void> taskC = CompletableFuture.allOf(taskA, taskB);
        taskC.thenApply(result -> taskA.join() + taskB.join())
                .exceptionally(ex -> {
                    System.out.printf("【Exceptionally】发生异常：%s\n", ex.getCause().getMessage());
                    // 必须是result原定的类型
                    return 0;
                })
                .thenAccept(result -> System.out.println("结果: " + result));

        Thread.sleep(100);
    }
}
