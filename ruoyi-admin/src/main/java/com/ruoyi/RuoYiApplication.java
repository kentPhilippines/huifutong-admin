package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling //轻量级定时任务
//@NacosPropertySource(dataId = "${nacos.config.data-id}",autoRefreshed = true)
@EnableDiscoveryClient
@EnableFeignClients
public class RuoYiApplication {
	public static void main(String[] args) {
		SpringApplication.run(RuoYiApplication.class, args);
		System.out.println("(♥◠‿◠)ﾉﾞ  系统启动成功   ლ(´ڡ`ლ)ﾞ  \n" + " .-------.       ____     __        \n"
				+ " |  _ _   \\      \\   \\   /  /    \n" + " | ( ' )  |       \\  _. /  '       \n"
				+ " |(_ o _) /        _( )_ .'         \n" + " |replace into sys_user_onli (_,_).' __  ___(_ o _)'          \n"
				+ " |  |\\ \\  |  ||   |(_,_)'         \n" + " |  | \\ `'   /|   `-'  /           \n"
				+ " |  |  \\    /  \\      /           \n" + " ''-'   `'-'    `-..-'              ");
	}
}