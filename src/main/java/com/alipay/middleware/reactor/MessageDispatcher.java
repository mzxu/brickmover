package com.alipay.middleware.reactor;

import reactor.core.Environment;
import reactor.core.processor.Operation;
import reactor.core.processor.Processor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.event.Event;
import reactor.function.Supplier;
import reactor.io.Buffer;
 
public class MessageDispatcher {
 
  private final Environment environment;
 
  private final Processor<Event<Buffer>> writeProcessor;
 
  public MessageDispatcher() {
    this(new Environment());
  }
 
  MessageDispatcher(final Environment env) {
    environment = env;
 
    writeProcessor = new ProcessorSpec<Event<Buffer>>()
      .dataSupplier(new Supplier<Event<Buffer>>() {
        public Event<Buffer> get() {
          return new Event<Buffer>(new Buffer());
        }
      })
      .consume(new EchoConsumer())
      .get();
 
  }
 
  public void write(final Buffer data) {
    final Operation<Event<Buffer>> op = writeProcessor.get();
    op.get().setData(data);
    op.commit();
  }
	public static void main(String[] arge) throws Exception {
		
		Buffer[] buffers = new Buffer[10];
	    for (int i = 0; i < 10; i++)  {
	        Buffer buf = new Buffer(30, false);
	        buf.append((byte)0x80); // Magic Byte
	        buf.append((byte)0x09); // GETQ Opcode
	        buf.append(new byte[] {0x00, 0x03}); // 3 byte keylength (KEY)
	        buf.append((byte)0x00); // Extra Length
	        buf.append((byte)0x00); // data type
	        buf.append(new byte[] {0x00, 0x00}); // reserved
	        buf.append(new byte[] {0x00, 0x00, 0x00, 0x03}); // total body size
	        buf.append(new byte[] {0x00, 0x00, 0x00, 0x00}); // Opaque
	        buf.append(new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); // CAS
	        buf.append(new byte[] {0x65, 0x6F, (byte)(i % 9)});
	        buffers[i] = buf;
	    }
	    long startTime = System.currentTimeMillis();
	    MessageDispatcher dispatcher = new MessageDispatcher();
	    
	    //long amountOfOps = 100000000;
	    long amountOfOps = 100000;
	    for (long i = 0; i < amountOfOps; i++) {
	        dispatcher.write(new Buffer(buffers[(int)(i % 9)]).flip());
	    }
	    long endTime = System.currentTimeMillis();
		double elapsed = endTime - startTime;
		double throughput = amountOfOps / (elapsed / 1000);
		System.out.println("Executed "+ (int) throughput +"/sec in" +(int) elapsed + "%d ms");
	} 
}
