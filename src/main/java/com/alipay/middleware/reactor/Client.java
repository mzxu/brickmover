package com.alipay.middleware.reactor;


import reactor.core.Environment;
import reactor.function.Consumer;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpConnection;
import reactor.tcp.TcpServer;
 
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
	 
	final private static Environment environment = new Environment();
	private final static CountDownLatch latch = new CountDownLatch(1);
	private static AtomicInteger count = new AtomicInteger(0);
	 
	public static void simpleRequestReplyWithNettyTcp() throws Exception {
		final TcpClient client = new TestClient(environment).client();;
		try{
			System.out.println("client started: ");

			final TcpConnection<String, String> connection = (TcpConnection<String, String>) client.open().await();
			 
			connection.consume(new Consumer<String>() {
	 
				public void accept(String t) {
					System.out.println("received No." + count.getAndAdd(1) + "->" + t);
					connection.send("request block");
					if (t==null) {
						client.close();
						latch.countDown();
					}
				}
			});
			connection.send("client is ready");
			latch.await();
//			connection.send("request block").onSuccess(new Consumer<Void>() {
//	 
//				public void accept(Void data) {
//					System.out.println("data sent");
//				}
//	 
//			}).onError(new Consumer<Throwable>() {
//	 
//				public void accept(Throwable t) {
//					t.printStackTrace();
//				}
//			});
 
		} finally {
			if(client != null){
				client.close();
			}
		}
	}
	
	public CountDownLatch latch() {
		return latch;
	}
	
	public static void main(String[] arge) throws Exception {
		simpleRequestReplyWithNettyTcp();
	}
	
}