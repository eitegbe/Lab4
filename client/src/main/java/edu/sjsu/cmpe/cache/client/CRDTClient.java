package edu.sjsu.cmpe.cache.client;
import java.util.HashMap;
import java.util.Map;

public class CRDTClient {
    public static void readOnRepair(CacheServiceInterface arg1, CacheServiceInterface arg2,
    		CacheServiceInterface arg3) throws Exception {
    	CacheServiceInterface node1  = arg1;
    	CacheServiceInterface node2  = arg2;
    	CacheServiceInterface node3  = arg3;
    	
        long key = 1;
        String value = "a";
        
        node1.put(key, value);
        node2.put(key, value);
        node3.put(key, value);
        
        System.out.println("Initializing value: a");
        Thread.sleep(30000);
        
        node1.get(1);
	    node2.get(1);
	    node3.get(1);
	        
	    System.out.println("Reading Value: a");
	    Thread.sleep(1000);
	    
	    System.out.println("Node 1: " + node1.getValue());
	    System.out.println("Node 2: " + node2.getValue());
	    System.out.println("Node 3: " + node3.getValue());
        
        value = "b";
        node1.put(key, value);
        node2.put(key, value);
        node3.put(key, value);
        
        System.out.println("Initializing value b.....");
        Thread.sleep(30000);
	        
	    node1.get(1);
	    node2.get(1);
	    node3.get(1);
	        
	    System.out.println("Reading value b....");
	    Thread.sleep(1000);
	    
	    System.out.println("Node 1: " + node1.getValue());
	    System.out.println("Node 2: " + node2.getValue());
	    System.out.println("Node 3: " + node3.getValue());
	        
	    String[] values = {node1.getValue(), node2.getValue(), node3.getValue()};
	    
	    Map<String, Integer> map = new HashMap<String, Integer>();
	    String majority = null;
	    for (String eachValue : values) {
	        Integer countValue = map.get(eachValue);
	        map.put(eachValue, countValue != null ? countValue+1 : 1);
	        if (map.get(eachValue) > values.length / 2) {
	        	majority = eachValue;
	        	break;
	        }	
	    }
	    
	    node1.put(key, majority);
        node2.put(key, majority);
        node3.put(key, majority);
        
        System.out.println("Read repair....\n");
	    Thread.sleep(1000);
	    
	    node1.get(key);
        node2.get(key);
        node3.get(key);
        
        System.out.println("b after repair.....\n ");
	    Thread.sleep(1000);
	    
	    System.out.println("Node 1: " + node1.getValue());
	    System.out.println("Node 2: " + node2.getValue());
	    System.out.println("Node 3: " + node3.getValue());
    }
}
