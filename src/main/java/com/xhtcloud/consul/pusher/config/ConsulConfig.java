package com.xhtcloud.consul.pusher.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import com.xhtcloud.consul.pusher.service.ConsulService;

/**
 * 提供加载配置到consul中的操作
 *
 * @author xht wbin
 */
@Component
public class ConsulConfig {

    private static final Logger log = LogManager.getLogger(ConsulConfig.class);
    @Autowired
    private ConsulService consulServiceImpl;

  
    /**
     * 加载配置信息到consul中
     *
     * @param key     配置的key
     * @param value   配置的值
     * @param keyList 在consul中已存在的配置信息key集合
     */
    private void visitProps(String key, String value) {      
    	this.consulServiceImpl.setKVValue(key+"data", value);
    	ClassPathResource resource = new ClassPathResource("azy-version.yml");    
    	String fileName = resource.getFilename();
	        
	        try (InputStream input = resource.getInputStream()) {
	           if (fileName.endsWith(".yml")) {
	                Yaml yaml = new Yaml();
	                Map data =  yaml.load(resource.getInputStream());
	                String versionValue=yaml.dumpAsMap(data);	
	                this.consulServiceImpl.setKVValue("azy-version/data", versionValue);
	                
	           }
	           
	        }catch (Exception e) {
						// TODO: handle exception
					
	           
	        }
    }

    /**
     * 启动时加载application.yml配置文件信息到consul配置中心
     */
    @PostConstruct
    private void init() {
    	
    	/*先读取consul上的版本文件*/
    	Map<String,Object> consulVersionMap=	this.consulServiceImpl.getConsulVersionMap();
    	Map<String,Object> ymlVersionMap=null;
    	/*读取本地的版本文件*/
    	ClassPathResource yamlResource = new ClassPathResource("azy-version.yml");  
    	String yamlFileName = yamlResource.getFilename();  
    	try (InputStream yamlInput = yamlResource.getInputStream()) {   
    		if (yamlFileName.endsWith(".yml")) {     
    			Yaml yaml = new Yaml();    
    			Object data =  yaml.load(yamlResource.getInputStream());     
    			String value=yaml.dump(data);     
    			ymlVersionMap=this.consulServiceImpl.getVersionMap(value); 
    		}      
    	}catch (Exception e) {				
    		// TODO: handle exception			          
    	} 
    	/*consul配置中心的版本文件跟本地版本文件做对比，版本号不一致则推送到配送中心*/
    	List<String> listKey=null;
    	if(ymlVersionMap!=null){
        	  listKey=this.consulServiceImpl.getKeyByMapCompar(ymlVersionMap, consulVersionMap);
    	}
    	for(String key : listKey){
    		    String keys[]=key.split("-");
    		    String yamlName=keys[0]+"-"+keys[1]+".yml";
    	        ClassPathResource resource = new ClassPathResource(yamlName);
    	        String fileName = resource.getFilename();    	        
    	        try (InputStream input = resource.getInputStream()) {
    	           if (fileName.endsWith(".yml")) {
    	                Yaml yaml = new Yaml();
    	                Map data = yaml.load(resource.getInputStream());    	            
    	                String value=yaml.dumpAsMap(data);	
    	                /*上传至配置中心的目录层级名称，例manger-dev-version,取manger-dev*/    	              
    	                String keyFileName=keys[0]+","+keys[1]+"/";   	                
    	                visitProps(keyFileName,value);  
    	            }
    	        }
    	        catch (IOException e) {
    	        	log.error("未找到需要加载的文件fileName={},message: {} ", fileName, e.getMessage());
    	        }   
    	}	
    } 
}


