package com.hhy.completable.exp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 描述: 发生异常与捕获异常的时效性
 * </p>
 *
 * @Author huhongyuan
 */
public class ExceptionTimeliness {
    public static void main(String[] args) throws InterruptedException {
        // 异步任务
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                    System.out.printf("【异步任务】发生异常: %s\n",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    throw new RuntimeException("taskAException");
                })
                .handle((result, exception) -> {
                    if (exception != null) {
                        System.out.printf("【handle】捕获异常: %s\n",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                        throw new RuntimeException(exception.getCause().getMessage());
                    }
                    return result;
                })
                .exceptionally(exception -> {
                    System.out.printf("【exceptionally】捕获异常: %s\n",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    return 0;
                });

        Thread.sleep(100);
    }
}
