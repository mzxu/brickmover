package com.alipay.middleware.reactor;


import reactor.core.Environment;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpServer;
 
import java.util.concurrent.TimeUnit;

public class Sample {
	 
	final private static Environment environment = new Environment();
 
	public static void simpleRequestReplyWithNettyTcp() throws Exception {
//		TcpServer server = null;
//		TcpClient client = null;
//		try{
//			TestServer testServer = new TestServer(environment);
//			server = testServer.server();
//			client = new TestClient(environment).client();
// 
//			testServer.latch().await();
// 
// 
//		} finally {
//			if(client != null){
//				client.close();
//			}
//			if(server != null){
//				server.shutdown().await();
//			}
//		}
	}
	public static void main(String[] arge) throws Exception {
		simpleRequestReplyWithNettyTcp();
	}
	
}
 
