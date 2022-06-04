package com.ruoyi.cache;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.thread.ThreadUtil;
import com.alicp.jetcache.AutoReleaseLock;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheTest {

    @CreateCache(name = "ALIPAY_WITHDRAWAL_LOCK:", expire = 60, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.LOCAL)
    private Cache<String, String> cache;

    private ReentrantLock reentrantLock = new ReentrantLock();
    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    @Test
    public void testDoubleClick() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        String orderId = RandomStringUtils.randomAlphabetic(8);
        for (int a = 0; a <= 10; a++) {

            ThreadUtil.execAsync(() -> {
                try {
                    reentrantLock.tryLock(10,TimeUnit.SECONDS);
                    if(concurrentHashMap.get(orderId)!=null)
                    {
                        log.info("1分钟内不允许重复操作");
                        return;
                    }else {
                        log.info("addlock");
                        concurrentHashMap.put(orderId, Thread.currentThread().getName());
                    }
                    log.info("thread orderid:{},{}",cache.get(orderId), DateTime.now().toString(DatePattern.NORM_DATETIME_MS_FORMAT));
                    log.info("我还是进来了");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    reentrantLock.unlock();
                }

                /*if(concurrentHashMap.get(orderId)!=null)
                {
                    log.info("1分钟内不允许重复操作");
                    return;
                }else {
                    log.info("addlock");
                    concurrentHashMap.put(orderId, Thread.currentThread().getName());
                }
                log.info("thread orderid:{},{}",cache.get(orderId), DateTime.now().toString(DatePattern.NORM_DATETIME_MS_FORMAT));
                log.info("我还是进来了");*/




            });

        }
        //Thread.sleep(1000);
        //log.info("orderid:{}",cache.get(orderId));

    }

    public AjaxResult getResult()
    {
        return AjaxResult.error("1分钟内不允许重复操作");
    }

}
