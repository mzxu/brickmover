package com.alipay.middleware.reactor;

/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;


/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {
    protected static final int QUEUE_CAPACITY = 1 << 22;
    protected static int port;
    protected static int numOfBossThreads;




	protected static int numOfWorkerThreads;

    protected static ArrayBlockingQueue<Pair<Long, String>> q;
    private static String file;

    protected static Thread producerThread;
    
    public static void main(String[] args) throws Exception {
        // Configure SSL.
        if (args.length < 2) {
            throw new RuntimeException("need sourcefile port !");
        }
        setFile(args[0]);
        setPort(Integer.parseInt(args[1]));
        setNumOfBossThreads(Integer.parseInt(args[2]));
        setNumOfWorkerThreads(Integer.parseInt(args[3]));
        q = new ArrayBlockingQueue<Pair<Long, String>>(QUEUE_CAPACITY);
        try {
            producerThread = new Thread(new Producer(q, file));
            producerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Configure the server.
        EpollEventLoopGroup BossEventLoopGroup=new EpollEventLoopGroup(numOfBossThreads, new PriorityThreadFactory("@+listener",Thread.NORM_PRIORITY));
        EpollEventLoopGroup WorkerEventLoopGroup=new EpollEventLoopGroup(numOfWorkerThreads, new PriorityThreadFactory("@+I/O",Thread.NORM_PRIORITY));
         try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(BossEventLoopGroup, WorkerEventLoopGroup)
                .channel(EpollServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();

                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(new EchoServerHandler(q, producerThread));
                     
                 }
             });

            // Start the server.
            ChannelFuture f = b.bind(new InetSocketAddress(port)).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
        	BossEventLoopGroup.shutdownGracefully();
        	WorkerEventLoopGroup.shutdownGracefully();
        }
    }

    public static void setNumOfBossThreads(int numOfBossThreads) {
		EchoServer.numOfBossThreads = numOfBossThreads;
	}

	public static void setNumOfWorkerThreads(int numOfWorkerThreads) {
		EchoServer.numOfWorkerThreads = numOfWorkerThreads;
	}

	public static void setPort(int port) {
		EchoServer.port = port;
	}

	public static void setFile(String file) {
		EchoServer.file = file;
	}
}