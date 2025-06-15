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
public class ExceptionDemo {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<Object> task = CompletableFuture.supplyAsync(() -> {
//            return new Random().nextInt(100) + 1;
            throw new RuntimeException("TaskA Exception");
        })
        .handle((result, exception) -> {
            if (exception != null) {
                System.out.printf("【handle】发生异常：%s\n", exception.getCause().getMessage());
                throw new RuntimeException(exception.getCause().getMessage());
            }
            return result;
        })
        .exceptionally(exception -> {
            System.out.printf("【exceptionally】发生异常：%s\n", exception.getCause().getMessage());
            return 0;
        });

        Thread.sleep(5000);
    }
}
