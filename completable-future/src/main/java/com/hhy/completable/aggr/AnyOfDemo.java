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
public class AnyOfDemo {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<Integer> taskA = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(102);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int result = new Random().nextInt(100) + 1;
            System.out.println("任务A已完成, 结果为 " + result);
            return result;
        });
        CompletableFuture<Integer> taskB = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(101);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int result = new Random().nextInt(100) + 1;
            System.out.println("任务B已完成, 结果为 " + result);
            return result;
        });
        CompletableFuture<Integer> taskC = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int result = new Random().nextInt(100) + 1;
            System.out.println("任务C已完成, 结果为 " + result);
            return result;
        });
        // 异步任务D，返回最先完成的那个任务
        CompletableFuture<Object> taskD = CompletableFuture.anyOf(taskA, taskB, taskC);
        taskD.thenAccept(System.out::println);
        Thread.sleep(100);
    }
}
