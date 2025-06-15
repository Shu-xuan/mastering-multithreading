package com.hhy.future;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * <p>
 * 描述: TODO
 * </p>
 *
 * @Author huhongyuan
 */
public class FutureTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        testAsync();
//        testExp();
        testNoArrangement();
    }

    private static void testNoArrangement() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Future<String> futureA = executor.submit(() -> {
            System.out.printf("【%s】执行任务A\n", Thread.currentThread().getName());
            Thread.sleep(1000);
            return "任务A";
        });
        Future<String> futureB = executor.submit(() -> {
            System.out.printf("【%s】执行任务B\n", Thread.currentThread().getName());
            Thread.sleep(1000);
            return "任务B";
        });
        Future<String> futureC = executor.submit(() -> {
            System.out.printf("【%s】执行任务C\n", Thread.currentThread().getName());
            Thread.sleep(1000);
            return "任务C";
        });
        executor.shutdown();
    }


    private static void testExp() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            System.out.println("\t子线程开始工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            Thread.sleep(1000);
            throw new RuntimeException("\t子线程执行异常，发生异常时间为: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        for (int i = 0; i < 5; i++) {
            System.out.println("主线程" + i + "工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            Thread.sleep(1000);
        }
        try {
            System.out.println("主线程获取子线程执行结果时间为: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            // 这里不调用get方法就会导致子线程抛出的异常无法被处理
            future.get();
        } catch (ExecutionException e) {
            System.out.println("捕获到子线程异常: " + e.getCause().getMessage() + ", 时间为: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        } finally {
            executor.shutdown();
        }

    }

    private static void testAsync() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            System.out.println("\t子线程开始工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            Thread.sleep(3000);
            System.out.println("\t子线程结束工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            return "子线程执行完毕";
        });
        for (int i = 0; i < 5; i++) {
            System.out.println("主线程" + i + "工作, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (i == 1) {
                // 这里会导致主线程阻塞等待子线程的执行结果
                System.out.println("主线程获取子线程执行结果: [" + future.get() + "]" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
            Thread.sleep(1000);
        }
        executor.shutdown();
    }
}
