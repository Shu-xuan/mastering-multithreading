# Future
`Future`的缺点为存在“结果屏障”，即 `get()` 方法获取结果是阻塞式的，虽然任务异步，但获取
结果是同步的，通过结果依赖建立了同步约束。 

执行代码 com.hhy.future.FutureTest 即可发现
>1．阻塞式获取结果：Future.get()方法会阻塞主线程。若任务耗时较长，会导致线程资源浪
费，影响系统吞吐量。
> 
> 2．异常处理不完善：Future仅在调用get()时抛出ExecutionException，无法在异常发生
的第一时间进行处理（如重试或记录日志），万一主线程不关心结果（即不调用get方法），异常
将完全丢失。
> 
> 3．任务编排能力不足：Future仅表示单个异步任务的结果，无法描述任务间的依赖关系或组合
逻辑，不适合构建异步工作流。想要编排就需要直接改动代码，太不优雅。
> 
> 4．不支持回调机制：无法在任务完成后自动触发后续操作，需依赖轮询或手动检查状态，代码冗
余且效率低。

# CompletableFuture
CompletableFuture的APl挺多的，按功能划分：**任务创建、任务串联、任务聚合、异常捕获、超时控制** 等等。
> 1．任务创建：执行有返回值、无返回值的异步任务，默认在 ForkJoinPool 的守护线程中执行
>
> 2．任务串联：同步处理结果并返回新值；消费结果无返回值；链接两个异步任务 
> 
> 3．任务聚合：二元聚合(合并两个任务结果)；多元聚合 
> 
> 4．异常捕获：捕获异常并返回默认值 
> 
> 5．超时控制：超时抛出TimeoutException；超时返回默认值

## 任务创建
任务创建主要通过 runAsync() 和 supplyAsync() 实现，二者分别对应无返回值任务和有返回值任务，是异步编程的起点。
### 小结
任务创建：执行有返回值、无返回值的异步任务，默认在ForkJoinPool的守护线程中执行
~~~
// 使用默认线程池
runAsync(Runnable runnable); // 无返回值
supplyAsync(Supplier<T> supplier); // 有返回值

// 使用自定义线程池
runAsync(Runnable runnable, Executor executor); // 无返回值
supplyAsync(Supplier<T> supplier, Executor executor); // 有返回值
~~~

## 任务串联
任务串联通过**链式调用**将多个异步任务按逻辑顺序连接，形成依赖关系明确的流水线，使后置任务与前置任务的状态深度绑定。其本质是通过**非阻塞的回调机制**，实现任务间的有序协作。
~~~
// 注册同步回调。thenApply()在调用时会等待前一个阶段完成，获取到结果后再执行回调函数。默认与前阶段同一个线程
public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn);

// 注册无返回值的回调。为前阶段的结果定义消费逻辑，默认与前阶段同一个线程
public CompletableFuture<Void> thenAccept(Consumer<? super T> action);

// 注册异步回调。链接前后两个CompletableFuture，将结果作为下阶段的输入，触发新的异步操作（可能切换新线程）
public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
~~~
### 小结
任务串联（thenApply() | thenAccept() | thenCompose()）解决的是两个任务之间的依赖问题（即任务C等任务B、任务B又等任务A），也称为“一元依赖”问题，实现简单业务场景的流程编排；
~~~
thenApply()：注册同步回调，同步转换结果，适合于依赖前阶段结果的场景
thenAccept()：注册无返回值的回调，定义消费逻辑，适合于最终处理结果的场景
thenCompose()：注册异步回调，异步连接任务，避免嵌套
~~~

## 任务聚合
任务聚合 解决的是“多元依赖”问题，即多个任务之间的依赖关系，通过定义任务间的 AND（全依赖）、OR（任一依赖）逻辑，实现更为复杂业务场景的流程编排。

### 二元聚合
等两个任务全完成，然后聚合两个任务的结果。两个任务在不同线程中并行执行，线程安全无需同步控制。
~~~
// 两个独立任务完成后，将它们的结果进行聚合，返回新的 CompletableFuture 对象
public <U, V> CompletableFuture<V> thenCombine(
    CompletionStage<? extends U> other,
    BiFunction<? super T, ? super U, ? extends V> fn
)
~~~

### 多元聚合
~~~
// 等多个任务，所有任务完成才触发。创建多元AND聚合
// 特点：不直接提供聚合结果，需手动获取结果；所等的任意一个任务失败会导致整体失败
public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs);

// 等多个任务，任意一个任务完成即触发。创建多元OR聚合
public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs);
~~~
~~~
// 获取异步任务的结果
public T join();
~~~

### 总结
`CompletableFuture` 通过 `thenCombine/allOf/anyOf` 构建多粒度依赖;
`thenCombine()` 在两个任务全部完成后触发；`allOf()` 在多个任务全部完成后触发；
`anyOf()` 在多个任务中任意一个完成时立即触发，适用于快速响应优先场景。

## 异常捕获
### 异常的级联中断
异步任务中的异常，遵循**级联中断原则**

**级联中断:** 当某个异步任务抛出异常时，整个依赖链会立即中断，未执行的阶段会被跳过。

### 捕获异常并返回默认值
`CompletableFuture` 中 `exceptionally()` 方法用于捕获异常并返回默认值；

该方法仅接收"**异常对象**"一个参数。

>注意：仅在前一个阶段抛出异常时执行。如果前一个阶段未抛出异常或已处理完异常，该方法不会被调用!
~~~
// 捕获并返回默认值，使用前序线程执行或主线程(异步任务返回结果时无耗时、瞬间返回)
public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn);
~~~

### 异常处理和结果转换
CompletableFuture 中 `handle()` 方法用于**处理异常并转换返回类型**；

该方法接收 "**任务结果、异常对象**" 两个参数。
>注意：该方法无论正常/异常都会触发，提供了更灵活的处理机制!
~~~
// 异常处理和结果转换
public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
~~~

### 发生异常与捕获异常的时效性问题
传统的异步任务（如 `Future` 接口），由于仅在调用 `get()` 获取结果时才能捕获到异常，所以异常发生与捕获是割裂的，无法第一时间采取措施（如记录日志、重试等）

### 线程使用
线程使用问题，讨论的目标是 `exceptionally()` 与 `handle()` 究竟使用什么线程执行。

默认情况下，两者均使用 `ForkJoinPool` 中的线程，较少使用主线程；

为避免主线程阻塞，可考虑使用下面的版本：

~~~
// 异步捕获异常返回默认值，强制重开线程执行
public CompletableFuture<T> exceptionallyAsync(Function<Throwable, ? extends T> fn);

// 异步异常处理和结果转换，强制重开线程执行
public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
~~~

### 总结
实战中，推荐使用如下方式来处理业务:
~~~
CompletableFuture.supplyAsync(/*...*/) // 执行异步任务
    .handle(logResultAndError) // 第一层：日志记录
    .exceptionally(triggerAlert) // 第二层：告警触发
    .thenApply(businessLogic) // 第三层：业务处理
    .exceptionally(finalFallback); // 最终兜底
~~~

