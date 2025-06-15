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
public class HandleDemo {
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
                .handle((result, exception) -> {
                    if (exception != null) {
                        System.out.printf("【Handle】发生异常：%s\n", exception);
                        return new RuntimeException("前序阶段执行异常"); // 返回异常执行结果
                    }
                    // 返回正常结果
                    return result;
                })
                .thenAccept(result -> System.out.println("结果: " + result));

        Thread.sleep(100);
    }
}
