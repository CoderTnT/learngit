package com.shaojie.ZhiHu.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {


    public  Map<String,Object> map = new HashMap<>();




    public void set(String key,Object value){
        map.put(key,value);

    }


    public Object get(String key){

        return map.get(key);
    }






}
