package com.alipay.middleware.reactor;

import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import reactor.core.Environment;
import reactor.event.dispatch.RingBufferDispatcher;
import reactor.function.Consumer;
import reactor.io.encoding.StandardCodecs;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpConnection;
import reactor.tcp.TcpServer;
import reactor.tcp.spec.TcpClientSpec;
import reactor.tcp.netty.NettyTcpClient;

public class TestClient {

 
	private final TcpClient<String, String> client;
 
	public TestClient(Environment env) throws Exception {
		TcpClient<String, String> client = new TcpClientSpec<String, String>(NettyTcpClient.class)
				.env(env)
				.dispatcher(Environment.RING_BUFFER)
				.codec(StandardCodecs.STRING_CODEC)
				.connect("localhost", 15151)
				.get();
		

 
		this.client = client;
	}
 
	public TcpClient<String, String> client() {
		return client;
	}
}