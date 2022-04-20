package com.ruoyi.alipay.init;


import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.service.IAlipayMessageRegService;
import com.ruoyi.system.service.impl.AlipayMessageRegServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AlipayMediumInitCache implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${otc.appName}")
    private String appName;



    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        IAlipayMessageRegService service = contextStartedEvent.getApplicationContext().getBean(IAlipayMessageRegService.class);
        service.refreshAllCache();
        //alipayMessageRegCache.put(appName,service.selectAll());
        //log.info("{} load {} rows AlipayMessageReg success!",appName,alipayMessageRegCache.get(appName).size());
    }
}
