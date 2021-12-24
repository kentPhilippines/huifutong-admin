package com.ruoyi.quartz.task;

import com.ruoyi.RuoYiApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatementTaskTest {

    @Autowired
    private StatementTask statementTask;

    //模拟短时间内的并发请求量
    private static final int threadNum = 20;

    //倒计时器，用于模拟高并发

    private CountDownLatch countDownLatch =new CountDownLatch(threadNum);

    @Test
    public void test() throws InterruptedException {
        for (int i =0; i< threadNum; i++) {

            new Thread(()->{
                statementTask.runTask();
                countDownLatch.countDown();
            }).start();

        }
        //Assert.assertTrue(countDownLatch.getCount()==0);

        Thread.sleep(5000*10);
        log.info("ending...");
    }

}