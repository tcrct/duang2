//package com.duangframework.hotswap;
//
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.*;
//
///**
// * 监听 class path 下 .class 文件变动，触发 UndertowServer.restart()
// */
//public class HotSwapWatcher extends Thread {
//
//	protected UndertowServer undertowServer;
//
//	// protected int watchingInterval = 1000;	// 1900 与 2000 相对灵敏
//	protected int watchingInterval = 500;
//
//	protected List<Path> watchingPaths;
//	private WatchKey watchKey;
//	protected volatile boolean running = true;
//
//	public HotSwapWatcher(UndertowServer undertowServer) {
//		setName("HotSwapWatcher");
//		// 避免在调用 deploymentManager.stop()、undertow.stop() 后退出 JVM
//		setDaemon(false);
//		setPriority(Thread.MAX_PRIORITY);
//
//		this.undertowServer = undertowServer;
//		this.watchingPaths = buildWatchingPaths();
//	}
//
//	protected List<Path> buildWatchingPaths() {
//		Set<String> watchingDirSet = new HashSet<>();
//		String[] classPathArray = System.getProperty("java.class.path").split(File.pathSeparator);
//		for (Stri
//			 ng classPath : classPathArray) {
//			buildDirs(new File(classPath.trim()), watchingDirSet);
//		}
//
//		List<String> dirList = new ArrayList<String>(watchingDirSet);
//		Collections.sort(dirList);
//
//		List<Path> pathList = new ArrayList<Path>(dirList.size());
//		for (String dir : dirList) {
//			pathList.add(Paths.get(dir));
//		}
//
//		return pathList;
//	}
//
//	private void buildDirs(File file, Set<String> watchingDirSet) {
//		if (file.isDirectory()) {
//			watchingDirSet.add(file.getPath());
//
//			File[] fileList = file.listFiles();
//			for (File f : fileList) {
//				buildDirs(f, watchingDirSet);
//			}
//		}
//	}
//
//	public void run() {
//		try {
//			doRun();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	protected void doRun() throws IOException {
//		WatchService watcher = FileSystems.getDefault().newWatchService();
//		addShutdownHook(watcher);
//
//		for (Path path : watchingPaths) {
//			path.register(
//					watcher,
//					// StandardWatchEventKinds.ENTRY_DELETE,
//					StandardWatchEventKinds.ENTRY_MODIFY,
//					StandardWatchEventKinds.ENTRY_CREATE
//			);
//		}
//
//		while (running) {
//			try {
//				// watchKey = watcher.poll(watchingInterval, TimeUnit.MILLISECONDS);	// watcher.take(); 阻塞等待
//				// 比较两种方式的灵敏性，或许 take() 方法更好，起码资源占用少，测试 windows 机器上的响应
//				watchKey = watcher.take();
//
//				if (watchKey == null) {
//					// System.out.println(System.currentTimeMillis() / 1000);
//					continue ;
//				}
//			} catch (Exception e) {						// 控制台 ctrl + c 退出 JVM 时也将抛出异常
//				running = false;
//				if (e instanceof InterruptedException) {	// 另一线程调用 hotSwapWatcher.interrupt() 抛此异常
//					Thread.currentThread().interrupt();	// Restore the interrupted status
//				}
//				break ;
//			}
//
//			List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
// 			for(WatchEvent<?> event : watchEvents) {
//				String fileName = event.context().toString();
//				if (fileName.endsWith(".class")) {
//					if (undertowServer.isStarted()) {
//						undertowServer.restart();
//						resetWatchKey();
//
//						while((watchKey = watcher.poll()) != null) {
//							// System.out.println("---> poll() ");
//							watchKey.pollEvents();
//							resetWatchKey();
//						}
//
//						break ;
//					}
//				}
//			}
//
//			resetWatchKey();
//		}
//	}
//
//	private void resetWatchKey() {
//		if (watchKey != null) {
//			watchKey.reset();
//			watchKey = null;
//		}
//	}
//
//	/**
//	 * 添加关闭钩子在 JVM 退出时关闭 WatchService
//	 *
//	 * 注意：addShutdownHook 方式添加的回调在 kill -9 pid 强制退出 JVM 时不会被调用
//	 *      kill 不带参数 -9 时才回调
//	 */
//	protected void addShutdownHook(WatchService watcher) {
//		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			try {
//				watcher.close();
//			} catch (IOException e) {
//				UndertowKit.doNothing(e);
//			}
//		}));
//	}
//
//	public void exit() {
//		running = false;
//		try {
//			this.interrupt();
//		} catch (Exception e) {
//			UndertowKit.doNothing(e);
//		}
//	}
//
////	public static void main(String[] args) throws InterruptedException {
////		HotSwapWatcher watcher = new HotSwapWatcher(null);
////		watcher.start();
////
////		System.out.println("启动成功");
////		Thread.currentThread().join(99999999);
////	}
//}
//
//
//
//
//
//
//
