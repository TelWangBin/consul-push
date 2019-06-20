
package com.xhtcloud.consul.pusher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 推送配置文件到consul
 *
 * @author xht wbin
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling // 启用定时调度功能，consul需要使用此功能来监控配置改变
public class PusherApplication {

	 public static void main(String[] args) {
	        SpringApplication.run(PusherApplication.class);
	    }

 
}
