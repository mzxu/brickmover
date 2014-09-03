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


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter  {

	    protected final ArrayBlockingQueue<Pair<Long, String>> q;
	    protected Thread producerThread;

	    public EchoServerHandler(ArrayBlockingQueue<Pair<Long, String>> _q, Thread _producerThread) {
	        q = _q;
	        producerThread = _producerThread;
	    }


	
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
        	//System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        		int inbyte = in.getInt(0);
                Pair<Long, String> record;
                
			if (inbyte == 0) {
				record = q.poll();
				while (record == null) {
				    if (producerThread.isAlive()) {
				        //Thread.sleep(30);
				        record = q.poll();
				    } else {
				        break;
				    }
				}
				if (record == null) {
				    processLine(null, null, ctx);
				} else {
				    processLine(record.getLeft(), record.getRight(),ctx);
				}
			}
        } 
        finally {
            //ctx.write(Unpooled.wrappedBuffer("msg received".getBytes()));
        } 
    }
	
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
    
    private static void reverse(byte[] buff, int s1, int s1Len, int s2, int s2Len) {
        int lp = s1;
        int rp = s2 + s2Len - 1;
        int lpe = s1 + s1Len;
        int rpe = s2 - 1;

        byte temp;
        while (true) {
            if (lp == lpe) {
                lp = s2;
            }
            if (rp == rpe) {
                rp = lpe - 1;
            }
            if (lp >= rp)
                break;
            temp = buff[lp];
            buff[lp] = buff[rp];
            buff[rp] = temp;
            ++lp;
            --rp;
        }
    }

    private void processLine(Long lineNum, String line, ChannelHandlerContext ctx)  {

        if (line != null) {

            byte[] b = line.getBytes();
            int idx = b.length / 3;
            int idx2 = idx + idx;
            int size = 4+8+b.length-idx;
        	ByteBuf out = ctx.alloc().buffer(size);
            out.writeInt(b.length - idx);
            out.writeLong(lineNum);
            reverse(b, 0, idx, idx2, b.length - idx2);
            out.writeBytes(b, 0, idx);
            out.writeBytes(b, idx2, b.length - idx2);
            ctx.writeAndFlush(out);


        } else {
        	ByteBuf out = ctx.alloc().buffer(4);
        	out.writeInt(-1);
        	ctx.writeAndFlush(out);
        	ctx.close();
        }
    }
}