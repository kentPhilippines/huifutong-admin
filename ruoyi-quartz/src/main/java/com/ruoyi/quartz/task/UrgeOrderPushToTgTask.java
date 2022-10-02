package com.ruoyi.quartz.task;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ruoyi.quartz.api.OrderApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 催单定时任务 推送到tg
 *
 * @author ruoyi
 */
@Component("urgeOrderPushToTgTask")
public class UrgeOrderPushToTgTask {
    Log log = LogFactory.get();
    @Autowired
    private OrderApi orderApi;

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
    }

    public void ryParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams() {
        System.out.println("执行无参方法");
    }


    public void checkOrder() {
        long l = System.currentTimeMillis();
        log.info("执行订单核对方法");
        orderApi.checkOrder();
        long h = System.currentTimeMillis();
        long a = h - l;
        log.info("订单核对方法执行完毕，消耗时间：" + a + "");
    }
}
