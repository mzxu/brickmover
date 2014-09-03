package com.alipay.middleware.reactor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import reactor.core.Environment;
import reactor.event.Event;
import reactor.function.Consumer;
import reactor.function.batch.BatchConsumer;
import reactor.io.Buffer;
import reactor.tcp.TcpClient;
import reactor.tcp.TcpConnection;
import reactor.tcp.netty.NettyTcpClient;
import reactor.tcp.spec.TcpClientSpec;

public class BatchingNode implements BatchConsumer<Event<Buffer>> {
	 
	  private final TcpClient<Buffer, Buffer> client;
	  private TcpConnection<Buffer, Buffer> conn = null;
	  private Buffer writeBuffer;
	 
	  public BatchingNode(String hostname, Environment env) {
	    client = new TcpClientSpec<Buffer, Buffer>(NettyTcpClient.class)
	      .env(env)
	      .connect(hostname, 11210)
	      .get();
	 
	    try {
	      conn = client.open().await();
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	 
	    writeBuffer = new Buffer(1500, true);
	 
	  }
	 
	  public void start() {
	  }
	 
	  public void end() {
	    flush();
	  }
	 
	  public void accept(final Event<Buffer> bufferEvent) {
	    Buffer buf = bufferEvent.getData();
	 
	    if (writeBuffer.remaining() <= buf.remaining()) {
	      flush();
	    }
	 
	    writeBuffer.append(buf);
	  }
	 
	  private void flush() {
	    final CountDownLatch latch = new CountDownLatch(1);
	    writeBuffer.flip();
	    conn.send(writeBuffer).onSuccess(new Consumer<Void>() {
	    	// 
			public void accept(Void data) {
//				System.out.println("sent:" + msg);
				latch.countDown();
			}
	    });
	    try {
	      latch.await(1, TimeUnit.SECONDS);
	    } catch (final Exception ex) {
	      throw new RuntimeException("got ex", ex);
	    }
	    writeBuffer.clear();
	  }
	 
	}