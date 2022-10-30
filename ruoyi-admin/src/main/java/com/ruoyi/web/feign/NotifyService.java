package com.ruoyi.web.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "notify")
public interface NotifyService {
    @GetMapping(value = "/tg/push/{platform}/urge",consumes = "application/json", produces = "application/json")
    public Object push(@RequestParam("text") String text,@PathVariable("platform") String platform);
}
