package com.hhy.completable.recall;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 描述: TODO
 * </p>
 *
 * @Author huhongyuan
 */
public class Demo02 {

    private static CompletableFuture<String> methodA() {
        return CompletableFuture.supplyAsync(() -> "2");
    }

    private static CompletableFuture<Integer> methodB(String value) {
        return CompletableFuture.supplyAsync(() -> Integer.parseInt(value));
    }

    private static CompletableFuture<Integer> methodC(Integer value) {
        return CompletableFuture.supplyAsync(() -> value * 10);
    }

    public static void main(String[] args) throws InterruptedException {
        // 任务A实现返回字符串"2"
        // 任务B基于任务A的结果，转换为数字2
        // 任务C基于任务B的结果，计算2*10的结果
        // 关键点：任务A、B、C都是异步任务；

        // thenApply()方式，我勒个回调地狱
        methodA().thenApply(aResult -> methodB(aResult))
                .thenApply(bFuture -> {
                    return bFuture.thenApply(bResult -> {
                                return methodC(bResult);
                            })
                            .thenApply(cFuture -> {
                                return cFuture.thenAccept(System.out::println);
                            });
                });

    // thenCompose方式
    methodA()
            .thenCompose(Demo02::methodB)
            .thenCompose(Demo02::methodC)
            .thenAccept(System.out::println);

    // 主线程休眠
        Thread.sleep(500);
}


}
