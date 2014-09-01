package com.alipay.middleware.reactor;

import java.util.Arrays;

import reactor.event.Event;
import reactor.function.Consumer;
import reactor.io.Buffer;


	public class EchoConsumer implements Consumer<Event<Buffer>> {
		 
		  public void accept(final Event<Buffer> bufferEvent) {
		    System.out.println(Arrays.toString(bufferEvent.getData().asBytes()));
		  }
		 
		}

