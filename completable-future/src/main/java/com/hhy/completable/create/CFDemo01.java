package com.hhy.completable.create;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 描述: 无返回值 & 默认线程池
 * </p>
 *
 * @Author huhongyuan
 */
public class CFDemo01 {
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
    public static void main(String[] args) {
        mainTask();
        CompletableFuture.runAsync(()->{
            System.out.printf("\t【子线程】开始工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("\t【子线程】结束工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
        });
        mainTask();
    }
}
