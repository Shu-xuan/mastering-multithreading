package com.hhy.completable.aggr;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <p>
 * 描述: TODO
 * </p>
 *
 * @Author huhongyuan
 */
public class ThenCombineDemo {
    public static void main(String[] args) throws InterruptedException {
        // 问：任务A、任务B各出一个随机数（1-100），任务C计算这两个随机数的和？
        // 关键：任务A、任务B、任务C都是异步任务
        CompletableFuture<Integer> taskA = CompletableFuture.supplyAsync(() -> {
            System.out.printf("当前线程：%s\n", Thread.currentThread().getName());
            return new Random().nextInt(100) + 1;
        });
        CompletableFuture<Integer> taskB = CompletableFuture.supplyAsync(() -> {
            System.out.printf("当前线程：%s\n", Thread.currentThread().getName());
            return new Random().nextInt(100) + 1;
        });
        // 等A、B完成并获取它们的结果
        CompletableFuture<Integer> taskC = taskA.thenCombine(taskB, (r1, r2) -> r1 + r2);
        taskC.thenAccept(System.out::println);

        Thread.sleep(100);
    }
}
