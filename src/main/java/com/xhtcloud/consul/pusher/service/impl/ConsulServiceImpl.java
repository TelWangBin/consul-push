package com.xhtcloud.consul.pusher.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.xhtcloud.consul.pusher.service.ConsulService;

/**
 * 提供consul配置中心业务逻辑操作
 *
 * @author xht wbin
 */
@Service
public final class ConsulServiceImpl implements ConsulService {

    private static final Logger log = LogManager.getLogger(ConsulServiceImpl.class);

    @Autowired
    private ConsulClient consulClient;


    private final static String keyPrefix="config/"; // key所在的目录前缀，格式为：config/应用名称/

	@Override
	public void setKVValue(String key, String value) {
		
		try {
		      this.consulClient.setKVValue(keyPrefix + key, value);
		    } catch (Exception e) {
		       log.error("推送到consul失败，message={},key={}", e.getMessage(), key);
		    }
		
	}

	/**
	 * 获取版本号的map集合
	 */
	@Override
	public Map<String,Object> getVersionMap(String value) {
		String versionValue="";
		Map<String, Object> newMap = new HashMap<>();
		try {
	            	Yaml yaml = new Yaml();	            	  
	            	Map map = (Map)yaml.load(value);
	            	newMap = formatMap(map);	  
	            
		    } catch (Exception e) {
		       log.error("获取yml对应的map失败，message={}", e.getMessage());
		     
		    }
		 return newMap;
	}
	 public Map<String, Object> formatMap(Map<String, Object> map) {
	        Map<String, Object> newMap = new HashMap<>();
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
	            if (entry.getValue().getClass() == LinkedHashMap.class) {
	                Map<String, Object> subMap = formatMap((Map<String, Object>) entry.getValue());
	                newMap.put(entry.getKey(), subMap);
	            } else if (entry.getValue().getClass() == ArrayList.class) {
	                JSONArray jsonArray = new JSONArray((ArrayList) entry.getValue());
	                newMap.put(entry.getKey(), jsonArray);
	            } else {
	                newMap.put(entry.getKey(), entry.getValue().toString());
	            }
	        }
	        return newMap;
	    }
  
	

		@Override
		public Map<String,Object> getConsulVersionMap() {
			GetValue  keyContent = this.consulClient.getKVValue(keyPrefix+"azy-version/data").getValue();	
	        if (keyContent != null) {
	        	String value = keyContent.getValue();	       	
	        	value =new String(Base64.getDecoder().decode(value));	
	            return	getVersionMap(value);
	            
	        }
	       
	        return null;
		}
		
		public List<String> getKeyByMapCompar(Map<String, Object> yamlMap,Map<String, Object> consulMap) {
			
			List<String> listKey=new ArrayList<>();
			for (Map.Entry<String, Object> yamlEntry : yamlMap.entrySet()) {
				String m1value = yamlEntry.getValue() == null ? "" : yamlEntry.getValue().toString();
				String m2value="";
				if(consulMap!=null){
					 m2value = consulMap.get(yamlEntry.getKey()) == null ? "" : consulMap.get(yamlEntry.getKey()).toString();
				}
				
				if (!m1value.equals(m2value)) {
					String key=yamlEntry.getKey();
					listKey.add(key);
				}
			}
			return listKey;
		}
	

		
}