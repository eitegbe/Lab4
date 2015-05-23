package edu.sjsu.cmpe.cache.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

public class Client {
	private static CacheServiceInterface node1 = null;
	private static CacheServiceInterface node2 = null;
	private static CacheServiceInterface node3 = null;
	
    public static void main(String[] args) {
    	try {
    		System.out.println("Initialize all server nodes....");
    		
            node1 = new DistributedCacheService("http://localhost:3000");
            node2 = new DistributedCacheService("http://localhost:3001");
            node3 = new DistributedCacheService("http://localhost:3002");
            
	    	if (args.length > 0) {
	    		if (args[0].equals("write")) {
	    			write();
	    		} else if (args[0].equals("read")) {
	    			CRDTClient.readOnRepair(node1, node2, node3);
	    		}
	    	}
	    	
	    	System.out.println("Exit....\n");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}        
    }
    
    public static void write() throws Exception {       
        long key = 1;
        String value = "a";
        
        Future<HttpResponse<JsonNode>> node3000 = node1.put(key, value);
        Future<HttpResponse<JsonNode>> node3001 = node2.put(key, value);
        Future<HttpResponse<JsonNode>> node3002 = node3.put(key, value);
        
        final CountDownLatch countDown = new CountDownLatch(3);
        
        try {
        	node3000.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	node3001.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }
        
        try {
        	node3002.get();
        } catch (Exception e) {
        } finally {
        	countDown.countDown();
        }

        countDown.await();
        
        if (DistributedCacheService.successCount.intValue() < 2) {	        	
        	node1.delete(key);
        	node2.delete(key);
        	node3.delete(key);
        } else {
        	node1.get(key);
        	node2.get(key);
        	node3.get(key);
        	Thread.sleep(1000);
        	System.out.println("Node A: " + node1.getValue());
    	    System.out.println("Node B: " + node2.getValue());
    	    System.out.println("Node C: " + node3.getValue());
        }
        DistributedCacheService.successCount = new AtomicInteger();
    }
}
