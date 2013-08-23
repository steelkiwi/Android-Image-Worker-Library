package com.steelkiwi.imagemanager.thirdparty;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static final String tag = MemoryCache.class.getSimpleName();

    private Map<String, Bitmap> cache;
    private long size=0;
    private long limit=1000000;

    public MemoryCache(float memoryQuota){
    	cache =Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 0.75f, true));
        limit = (long)(Runtime.getRuntime().maxMemory()*memoryQuota);
        Log.i(tag, "memcache limit - " + memoryQuota + ", KBytes - " + (limit/1024));
        Log.i(tag, "memcache maxMemory - " + (Runtime.getRuntime().maxMemory()/1024));
    }
    
    public Bitmap get(String id){
        try{
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id))
                size-=getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    private void checkSize() {
        if(size>limit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit){
                	break;
                }
            }
        }
    }

    public void clear() {
        try{
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    private long getSizeInBytes(Bitmap bitmap) {
        return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
    }
}
