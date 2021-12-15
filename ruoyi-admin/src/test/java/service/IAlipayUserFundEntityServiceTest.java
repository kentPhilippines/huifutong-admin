package service;

import com.alibaba.fastjson.JSON;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.alipay.service.IAlipayUserFundEntityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


public class IAlipayUserFundEntityServiceTest {
    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;

    public void findSumFundForMerchant() {
        alipayUserFundEntityService.findSumFundForMerchant();
    }
    public void findSumFundForCardDealer() {
     }
}