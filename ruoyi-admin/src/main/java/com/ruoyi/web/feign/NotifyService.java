package com.ruoyi.web.feign;

import com.aliyun.slb20140515.models.AddAccessControlListEntryResponse;
import com.aliyun.slb20140515.models.RemoveAccessControlListEntryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "notify")
public interface NotifyService {
    @GetMapping(value = "/tg/push/{platform}/urge",consumes = "application/json", produces = "application/json")
    public Object push(@RequestParam("text") String text,@PathVariable("platform") String platform);

    @GetMapping(value="/aliyun/addIpWhiteList",consumes = "application/json", produces = "application/json")
    public AddAccessControlListEntryResponse addIpWhiteList(@RequestParam("aclId")String aclId, @RequestParam("ip")String ip,@RequestParam("comment") String comment) throws Exception;


    @GetMapping(value="/aliyun/deleteIpWhiteList",consumes = "application/json", produces = "application/json")
    public RemoveAccessControlListEntryResponse deleteIpWhiteList(@RequestParam("aclId")String aclId, @RequestParam("ip")String ip,@RequestParam("comment") String comment) throws Exception ;
}
