package impl;

import com.alibaba.fastjson.JSON;
import com.ruoyi.RuoYiApplication;
import com.ruoyi.alipay.domain.AlipayRunOrderEntity;
import com.ruoyi.alipay.service.IAlipayRunOrderEntityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlipayRunOrderEntityServiceImplTest {

    @Autowired
    private IAlipayRunOrderEntityService alipayRunOrderEntityService;

    @Test
    public void selectProfitGroupByDate() {
        List list =alipayRunOrderEntityService.selectProfitGroupByDate(new AlipayRunOrderEntity());
        log.info(JSON.toJSONString(list));
        ;
    }
}