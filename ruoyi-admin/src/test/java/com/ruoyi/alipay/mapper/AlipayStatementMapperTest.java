package com.ruoyi.alipay.mapper;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.alipay.domain.AlipayMediumEntity;
import com.ruoyi.alipay.domain.AlipayStatement;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.alipay.service.IAlipayStatementService;
import com.ruoyi.alipay.vo.AlipayUserMedium;
import com.ruoyi.common.utils.bean.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

public class AlipayStatementMapperTest {

    @Autowired
    private IAlipayStatementService alipayStatementService;

    @Autowired
    private IAlipayMediumEntityService alipayMediumEntityService;

    public void selectTotalDataTest() {

        List ids = alipayStatementService.selectTotalData().stream().map(alipayStatement -> alipayStatement.getUserId()).collect(Collectors.toList());
        List<AlipayMediumEntity> alipayMediumEntities = alipayMediumEntityService.selectByIds(ids);

        alipayStatementService.selectTotalData().stream().forEach(alipayStatement -> {

            List<AlipayUserMedium> userMediums = alipayMediumEntities.stream().filter(alipayMediumEntity -> alipayMediumEntity.getQrcodeId().equals(alipayStatement.getUserId())).map(medium -> BeanUtil.toBean(medium,AlipayUserMedium.class)).collect(Collectors.toList());

            alipayStatement.setCardBalanceDetail(JSON.toJSONString(userMediums));
         });
    }


}