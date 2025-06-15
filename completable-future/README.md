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
任务串联：链式调用、非阻塞回调
~~~
thenApply()：注册同步回调，同步转换结果，适合于依赖前阶段结果的场景
thenAccept()：注册无返回值的回调，定义消费逻辑，适合于最终处理结果的场景
thenCompose()：注册异步回调，异步连接任务，避免嵌套
~~~
