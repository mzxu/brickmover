package com.alipay.middleware.reactor;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import reactor.core.Environment;
import reactor.event.dispatch.RingBufferDispatcher;
import reactor.function.Consumer;
import reactor.io.encoding.StandardCodecs;
import reactor.tcp.TcpConnection;
import reactor.tcp.TcpServer;
import reactor.tcp.netty.NettyTcpServer;
import reactor.tcp.spec.TcpServerSpec;
import reactor.util.StringUtils;
 
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
 
/**
 * @author Stephane Maldini
 */
public class TestServer {
 
	private static final int NUMBER_OF_REPLIES = 2000;
 
	private final TcpServer<String, String> server;
	private final CountDownLatch latch = new CountDownLatch(1);
	private AtomicInteger count = new AtomicInteger(0);

	public TestServer(Environment env,  final BufferedReader br) throws Exception {
		Consumer<TcpConnection<String, String>> serverConsumer = new Consumer<TcpConnection<String, String>>() {
 
			public void accept(final TcpConnection<String, String> connection) {
 
				connection.consume(new Consumer<String>() {
 
					public void accept(String data) {
						System.out.println("Received data from client No. "+count.getAndAdd(1)+" -> " + data);
						try {
							String line = br.readLine();
							if (StringUtils.isEmpty(line)) {
								System.out.println("Empty line found!");
								line = line + "empty line!";
							}
							connection.send(line).onSuccess(new Consumer<Void>() {
								 
								public void accept(Void data) {
//									latch.countDown();
								}

							}).onError(new Consumer<Throwable>() {

								public void accept(Throwable t) {
									t.printStackTrace();
								}
							});		;
							if (line == null) {
								latch.countDown();
								System.out.println("all lines are read!");
								
							}
							System.out.println("Sent -> " + line);

						}
						catch (IOException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//send data
//						BufferedReader br;
//						try {
//							br = new BufferedReader(new FileReader("sample.txt"));
//
//				        String line;
//
//							while((line = br.readLine()) != null) {
//								final String msg = "msg-" + line;
//								 
//								connection.send(msg).onSuccess(new Consumer<Void>() {
// 
//									public void accept(Void data) {
//										System.out.println("sent:" + msg);
////										latch.countDown();
//									}
// 
//								}).onError(new Consumer<Throwable>() {
// 
//									public void accept(Throwable t) {
//										t.printStackTrace();
//									}
//								});		
//								}
//						} 
//						catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}

					}
				});
			}
		};
		
		// server
		TcpServer<String, String> server = new TcpServerSpec<String, String>(NettyTcpServer.class)
				.env(env)
				.listen("localhost", 15151)
				.codec(StandardCodecs.STRING_CODEC)
				.consume(serverConsumer).get();
 
		
		server.start();
		this.server = server;
	}
 
	public TcpServer<String, String> server() {
		return server;
	}
 
	public CountDownLatch latch() {
		return latch;
	}
}