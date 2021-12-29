package com.ruoyi.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("globalConfig")
public class GlobalConfig {
    @Value("${otc.siteName}")
    public String siteName;
}
