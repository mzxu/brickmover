package com.alipay.middleware.reactor;

/**
 * Created by Bytes on 8/31/14.
 */

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Producer implements Runnable {
    protected final ArrayBlockingQueue<Pair<Long, String>> q;
    private BufferedReader br = null;

    public Producer(ArrayBlockingQueue<Pair<Long, String>> _q, String input_f) throws IOException {
        q = _q;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input_f)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("Executing File Reading Producer");
        String s;
        long line = 0;
        try {
            while (true) {
                s = br.readLine();
                if (s == null)
                    break;
                try {
                    q.put(new Pair<Long, String>(line++, s));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
