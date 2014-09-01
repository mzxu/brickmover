package com.alipay.middleware.reactor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileCreator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileWriter fw = new FileWriter("sample.txt");   

        long begin3 = System.currentTimeMillis();   
        
        for (int i = 0; i < Integer.parseInt(args[0]); i++) {   
        	
            fw.write(getStringRandom()+"\n");   

        }   

                    fw.close();   

        long end3 = System.currentTimeMillis();   

	}
	

	    public static String getStringRandom() {  
	          
	        String val = "";  
	        Random random = new Random();  
	          
	        //参数length，表示生成几位随机数  
	        for(int i = 0; i < random.nextInt(200); i++) {  
	              
	            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
	            //输出字母还是数字  
	            if( "char".equalsIgnoreCase(charOrNum) ) {  
	                //输出是大写字母还是小写字母  
	                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
	                val += (char)(random.nextInt(26) + temp);  
	            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
	                val += String.valueOf(random.nextInt(10));  
	            }  
	        }  
	        return val;  
	    }  
	
}
