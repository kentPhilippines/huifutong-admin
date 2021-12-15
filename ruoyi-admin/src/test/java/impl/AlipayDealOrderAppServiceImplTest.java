package impl;


import com.alibaba.fastjson.JSON;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.alipay.domain.AlipayDealOrderApp;
import com.ruoyi.alipay.service.impl.AlipayDealOrderAppServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

public class AlipayDealOrderAppServiceImplTest {
    @Autowired
    private AlipayDealOrderAppServiceImpl alipayDealOrderAppService;


    public void selectAlipayDealOrderAppListGroupByOrderAccount() {
        AlipayDealOrderApp alipayDealOrderApp = new AlipayDealOrderApp();
        alipayDealOrderApp.setOrderAccount("jinxing");


    }
}