package com.hhy.completable.create;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 描述: 有返回值 & 使用默认线程池
 * </p>
 *
 * @Author huhongyuan
 */
public class CFDemo03 {
    public static void mainTask() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.printf("【主线程】" + i + "工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        mainTask();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.printf("\t【子线程】开始工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "\t【子线程】结束工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        });
        System.out.printf("\t【主线程】获取到子线程执行结果: \n\t%s，此时为: %s \n", future.get(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        mainTask();
    }
}
