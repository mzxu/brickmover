package com.alipay.middleware.reactor;


import reactor.core.Environment;
import reactor.function.Consumer;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpConnection;
import reactor.tcp.TcpServer;
 
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {
	 
	final private static Environment environment = new Environment();
	 
	public static void simpleRequestReplyWithNettyTcp() throws Exception {
		final TcpClient client = new TestClient(environment).client();;
		try{

			final TcpConnection<String, String> connection = (TcpConnection<String, String>) client.open().await();
			 
			connection.consume(new Consumer<String>() {
	 
				public void accept(String t) {
					System.out.println("received: " + t);
					connection.send("request block");
					if (t==null) {
						client.close();
					}
				}
			});
			connection.send("client is ready");

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
			Thread.sleep(5000);
 
		} finally {
			if(client != null){
				client.close();
			}
		}
	}
	public static void main(String[] arge) throws Exception {
		simpleRequestReplyWithNettyTcp();
	}
	
}