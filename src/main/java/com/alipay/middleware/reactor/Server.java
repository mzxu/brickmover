package com.alipay.middleware.reactor;


import reactor.core.Environment;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpServer;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

public class Server {
	 
	final private static Environment environment = new Environment();
 
	public static void simpleRequestReplyWithNettyTcp() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("sample.txt"));
		
		TcpServer server = null;
		try{
			TestServer testServer = new TestServer(environment, br);
			server = testServer.server();

			testServer.latch().await();
 
 
		} finally {
			if(server != null){
				server.shutdown().await();
			}
		}
	}
	public static void main(String[] arge) throws Exception {
		simpleRequestReplyWithNettyTcp();
	}
	
}
 
