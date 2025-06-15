package com.hhy.completable.recall;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 描述: thenApply & thenAccept
 * </p>
 *
 * @Author huhongyuan
 */
public class Demo01 {
    public static void mainTask() {
        for (int i = 1; i <= 3; i++) {
            try {
                System.out.printf("【主线程】" + i + "工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        mainTask();
        // 回调函数不会像 get 反法那样阻塞主线程的运行
        CompletableFuture.supplyAsync(()->{
                    System.out.printf("\t【子线程】开始执行耗时任务: %s\n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "子线程执行完毕";
                })
                .thenApply(result -> {
                    return result + "执行完thenApply逻辑";
                })
                .thenAccept(result -> {
                            System.out.printf("\t【子线程】返回子线程的执行结果(%s): %s\n", result, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                        });
        mainTask();
    }
}
