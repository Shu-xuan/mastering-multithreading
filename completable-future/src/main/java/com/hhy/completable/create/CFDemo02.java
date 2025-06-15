package com.hhy.completable.create;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: 无返回值 & 自定义线程池
 * </p>
 *
 * @Author huhongyuan
 */
public class CFDemo02 {
    public static void mainTask() {
        for (int i = 1; i <= 5; i++) {
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
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

        CompletableFuture.runAsync(()->{
            System.out.printf("\t【子线程】开始工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("\t【子线程】结束工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
        }, pool);

        mainTask();

        pool.shutdown();
    }
}
