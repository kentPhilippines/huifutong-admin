package com.ruoyi.framework.aspectj;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceAuditAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAuditAspect.class);

    @Pointcut("execution (* com.ruoyi.alipay.service.*.*(..))")//controller的包
    public void pointcut() {
    }

    //方法执行前调用
    @Before("pointcut()")
    public void before(JoinPoint jp) {
        SysUser currentUser = ShiroUtils.getSysUser();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String controllerName = jp.getTarget().getClass().getName();
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();

        for(int i =0;i<args.length;i++)
        {
            if(args[i] instanceof BaseEntity)
            {
                BaseEntity arg = (BaseEntity) args[i];
                arg.setAuditLog(currentUser.getLoginName()+","+currentTime+";");

            }
        }

        /*if (null != currentUser) {
            logger.info("Current user: " + currentUser.getUserName() + " IN controller: " + controllerName +
                    ", method: " + methodName + ", time: " + currentTime);
        }*/
    }
}
