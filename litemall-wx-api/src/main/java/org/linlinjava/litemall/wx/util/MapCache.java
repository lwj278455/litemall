package org.linlinjava.litemall.wx.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 缓存工具类
 *
 */
public class MapCache {
    private static Map<String,String> map = new HashMap<>();

    public void  setMap(String key,String value){
        if (map.size()==0){
            synchronized (map){
                map.put(key,value);
            }
        }
    }


}
