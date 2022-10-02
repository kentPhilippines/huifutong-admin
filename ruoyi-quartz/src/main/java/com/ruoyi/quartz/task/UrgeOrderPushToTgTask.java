package com.ruoyi.quartz.task;

import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.ruoyi.alipay.domain.AlipayDealOrderEntity;
import com.ruoyi.alipay.service.IAlipayDealOrderEntityService;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.quartz.api.OrderApi;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private IAlipayDealOrderEntityService dealOrderEntityService;

    //notify内网地址
    @Value("${notifyLanUrl:http://localhost:8081/}")
    private String notifyLanUrl;
    @Value("${application.id:zb}")
    private String applicationId;

    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {
    }

    public void ryParams(String params) {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams() {
        System.out.println("执行无参方法");
    }


    public void runTask() {
        long l = System.currentTimeMillis();
        log.info("执行订催单推送方法");
        AlipayDealOrderEntity alipayDealOrderEntity = new AlipayDealOrderEntity();
        alipayDealOrderEntity.setUrge(1);

        List<AlipayDealOrderEntity> orders = dealOrderEntityService.selectUrgeOrders();
        if(CollectionUtils.isEmpty(orders))
        {
            log.info("当前没有需要处理的催单,{}",new DateTime().toMsStr());
            return;
        }

        String template ="当前有"+orders.size()+"笔催单需要处理：";

        List<String> messages = orders.stream().map(order->{
            return order.getOrderId()+","+order.getExternalOrderId()+","+order.getDealDescribe()+",卡商"+order.getOrderQrUser();
        }).collect(Collectors.toList());
        template += String.join(";\n", messages);
        String completeUrl = notifyLanUrl+"/tg/push/"+applicationId+"/urge";
        log.info("催单推送信息：{},{}",completeUrl,template);
        HttpUtils.sendGet(completeUrl,"text="+ URLEncoder.encode(template));
        long h = System.currentTimeMillis();
        long a = h - l;
        log.info("执行订催单推送方法执行完毕，消耗时间：" + a + "");
    }
}
