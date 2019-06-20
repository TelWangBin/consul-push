package com.xhtcloud.consul.pusher.service;


import java.util.List;
import java.util.Map;


/**
 * 
 * @author xht wbin
 */
public interface ConsulService {

	/**
	 * 向consul配置中心添加配置文件 
	 */
    public void setKVValue(String key, String value);
    /**
	 * 获取 yaml格式 version
	 */
    public Map<String,Object> getVersionMap(String value);
    /**
  	 * 获取 consul配置中心的version
  	 */
    public Map<String,Object> getConsulVersionMap();
    /**
  	 * 统一map格式
  	 */
    public Map<String, Object> formatMap(Map<String, Object> map);
    /**
  	 * 比较两个map返回不同的key
  	 */
    public List<String> getKeyByMapCompar(Map<String, Object> yamlMap,Map<String, Object> consulMap) ;

}