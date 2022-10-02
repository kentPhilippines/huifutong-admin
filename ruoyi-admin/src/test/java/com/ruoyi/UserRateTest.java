package com.ruoyi;

import com.ruoyi.alipay.mapper.AlipayBankConfigMapper;
import com.ruoyi.alipay.service.IAlipayBankConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRateTest {
    @Autowired
    private IAlipayBankConfigService alipayBankConfigService;
    @Autowired
    private AlipayBankConfigMapper alipayBankConfigMapper;
    @Test
    public void initBank()
    {
        /*BankTypeUtil.BANK_MAP_NAME.keySet().forEach(key->{
            AlipayBankConfig alipayBankConfig = new AlipayBankConfig();
            alipayBankConfig.setCodeValue(key.toString());
            alipayBankConfig.setBankName(BankTypeUtil.BANK_MAP_NAME.get(key).toString());
            alipayBankConfig.setAlias1(BankTypeUtil.BANK_MAP.get(key).toString());
            alipayBankConfigService.insertAlipayBankConfig(alipayBankConfig);
            log.info(key.toString());
        });
        log.info("done:{}",BankTypeUtil.BANK_MAP_NAME.keySet().size());*/

    }
}
