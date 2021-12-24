package com.ruoyi.quartz.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.ruoyi.alipay.domain.AlipayMediumEntity;
import com.ruoyi.alipay.domain.AlipayStatement;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.alipay.service.IAlipayStatementService;
import com.ruoyi.alipay.vo.AlipayUserMedium;
import com.ruoyi.common.utils.bean.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class StatementTask {

    Log log = LogFactory.get();
    private IAlipayStatementService alipayStatementService;

    private IAlipayMediumEntityService alipayMediumEntityService;

    @CreateCache(name = "STATEMENT_TASK_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> cache;

    @Autowired
    public StatementTask(IAlipayStatementService alipayStatementService, IAlipayMediumEntityService alipayMediumEntityService) {
        this.alipayMediumEntityService = alipayMediumEntityService;
        this.alipayStatementService = alipayStatementService;
    }

    public void runTask()
    {
        String dateStr = DateTime.now().toString(DatePattern.NORM_DATETIME_MS_FORMAT);
        cache.tryLockAndRun(dateStr + "", 300, TimeUnit.SECONDS, () -> {
            log.info("task start :{}",dateStr);
            runTask1();
            log.info("task end :{}",DateTime.now().toString(DatePattern.NORM_DATETIME_MS_FORMAT));
        });
    }

    private void runTask1() {

        try {
            List ids = alipayStatementService.selectTotalData().stream().map(alipayStatement -> alipayStatement.getUserId()).collect(Collectors.toList());
            List<AlipayMediumEntity> alipayMediumEntities = alipayMediumEntityService.selectByIds(ids);

            List<AlipayStatement> statements = alipayStatementService.selectTotalData();

            statements.stream().forEach(alipayStatement -> {

                List<AlipayUserMedium> userMediums = alipayMediumEntities.stream().filter(alipayMediumEntity -> alipayMediumEntity.getQrcodeId().equals(alipayStatement.getUserId())).map(medium -> {
                    AlipayUserMedium userMedium = BeanUtil.toBean(medium, AlipayUserMedium.class);
                    BigDecimal businessBalance = medium.getToDayDeal().subtract(medium.getToDayWit()).add(medium.getYseToday());//今天的入款-今天出款+昨天的结余=业务余额
                    userMedium.setBusinessBalance(businessBalance);
                    userMedium.setReferBalance(new BigDecimal(medium.getMountNow()));
                    return userMedium;
                }).collect(Collectors.toList());
                alipayStatement.setCardBalanceDetail(JSON.toJSONString(userMediums));

                alipayStatementService.insertAlipayStatement(alipayStatement);
            });
        } catch (Exception e) {
            log.info("excute fail:{}", e);
            e.printStackTrace();
        }
        log.info("excute success");


    }
}
