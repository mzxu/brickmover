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
	          
	        for(int i = 0; i < random.nextInt(200); i++) {  
	              
	            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
	            if( "char".equalsIgnoreCase(charOrNum) ) {  
	                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
	                val += (char)(random.nextInt(26) + temp);  
	            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
	                val += String.valueOf(random.nextInt(10));  
	            }  
	        }  
	        return val;  
	    }  
	
}
