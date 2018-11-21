package com.duangframework.kit;

import com.duangframework.mvc.http.enums.ConstEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolKit {
	
	private final static Logger logger = LoggerFactory.getLogger(ThreadPoolKit.class);

	private static long KEEP_ALIVE_TIME = 30L;			// KeepAlive时间,默认30分钟
	private static int MIN_POOL_NUMBER = 10;			// 最小线程数
	private static int MAX_POOL_NUMBER = 100;		// 最大线程数
	/**
	 * 　corePoolSize - 池中所保存的线程数，包括空闲线程。

	 　　maximumPoolSize - 池中允许的最大线程数。

	 　　keepAliveTime - 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。

	 　　unit - keepAliveTime 参数的时间单位。

	 　　workQueue - 执行前用于保持任务的队列。此队列仅保持由 execute 方法提交的 Runnable 任务。
	 */
	private static final ExecutorService es = new ThreadPoolExecutor(
			MIN_POOL_NUMBER, MAX_POOL_NUMBER,
			KEEP_ALIVE_TIME,
			TimeUnit.MINUTES,
			new LinkedBlockingQueue<Runnable>(MAX_POOL_NUMBER),  //使用该队列能有效提升并发效率
			new NamedThreadFactory());

	/**
	 * 执行线程
	 * @param thread
	 */
	public static void execute(Thread thread){
		try{
			es.execute(thread);
		} catch( Exception e ) {
			logger.warn("ThreadPoolKit execute is onException: "+ e.getMessage(), e);
		}
	}

	/**
	 * 执行线程
	 * @param runnable
	 */
	public static void execute(Runnable runnable){
		try{
			es.execute(runnable);
		} catch( Exception e ) {
			logger.warn("ThreadPoolKit execute is onException: "+ e.getMessage(), e);
		}
	}

	public static void shutdown() {
		if(null != es && !es.isShutdown()) {
			es.shutdown();
		}
	}

	/**
	 * 执行任务
	 * @param futureTask	线程任务
	 * @return
	 */
	public static <T> FutureTask<T> execute(Callable<T> futureTask) {
		try{
			return (FutureTask)es.submit(futureTask);
		} catch (Exception e) {
			logger.warn("ThreadPoolKit execute is onException: "+ e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 执行任务
	 * @param thread	线程任务
	 * @return
	 */
	public static <T> FutureTask<T> run(Thread thread) {
		try{
			return (FutureTask)es.submit(thread);
		} catch (Exception e) {
			logger.warn("ThreadPoolKit execute is onException: "+ e.getMessage(), e);
			return null;
		}
	}

	static class NamedThreadFactory implements ThreadFactory {
//		private static final AtomicInteger pool_seq = new AtomicInteger(1);
		private final AtomicInteger mThreadNum = new AtomicInteger(1);
		private final String mPrefix;
		private final ThreadGroup mGroup;

		public NamedThreadFactory() {
			mPrefix = ConstEnums.FRAMEWORK_OWNER.getValue() + "@thread-";
			SecurityManager s = System.getSecurityManager();
			mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
		}

		@Override
		public Thread newThread(Runnable runnable) {
			String name = mPrefix + mThreadNum.getAndIncrement();
			Thread ret = new Thread(mGroup, runnable, name, 0);
			ret.setDaemon(false);
			return ret;
		}

		public ThreadGroup getThreadGroup() {
			return mGroup;
		}
	}
}
