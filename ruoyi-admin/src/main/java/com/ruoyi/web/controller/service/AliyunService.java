package com.ruoyi.web.controller.service;

import cn.hutool.json.JSONUtil;
import com.aliyun.slb20140515.models.AddAccessControlListEntryResponse;
import com.aliyun.slb20140515.models.RemoveAccessControlListEntryRequest;
import com.aliyun.slb20140515.models.RemoveAccessControlListEntryResponse;
import com.ruoyi.common.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AliyunService {
    private static final String IP_SUFFIX = "/32";
    @Autowired
    private GlobalConfig globalConfig;

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.slb20140515.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "slb.aliyuncs.com";
        return new com.aliyun.slb20140515.Client(config);
    }

    public AddAccessControlListEntryResponse addIpWhiteList(String aclId, String ip, String comment) throws Exception {
        com.aliyun.slb20140515.Client client = createClient(globalConfig.accessKeyId, globalConfig.accessKeySecret);

        String formatEbtrys = String.format("[{\"entry\":\"%s\",\"comment\":\"%s\"}]", ip + IP_SUFFIX, comment);
        com.aliyun.slb20140515.models.AddAccessControlListEntryRequest addAccessControlListEntryRequest = new com.aliyun.slb20140515.models.AddAccessControlListEntryRequest()
                .setAclId(aclId)
                .setAclEntrys(formatEbtrys)
                .setRegionId("cn-hongkong");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        // 复制代码运行请自行打印 API 的返回值
        AddAccessControlListEntryResponse response = client.addAccessControlListEntry(addAccessControlListEntryRequest);
        log.info("aliyun add whitelist:{}", JSONUtil.toJsonStr(response));
        return response;

    }




    public RemoveAccessControlListEntryResponse deleteIpWhiteList(String aclId, String ip, String comment) throws Exception {
        com.aliyun.slb20140515.Client client = createClient(globalConfig.accessKeyId, globalConfig.accessKeySecret);

        String formatEbtrys = String.format("[{\"entry\":\"%s\",\"comment\":\"%s\"}]", ip + IP_SUFFIX, comment);
        RemoveAccessControlListEntryRequest removeAccessControlListEntryRequest = new RemoveAccessControlListEntryRequest()
                .setAclId(aclId)
                .setAclEntrys(formatEbtrys)
                .setRegionId("cn-hongkong");

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        // 复制代码运行请自行打印 API 的返回值
        RemoveAccessControlListEntryResponse response = client.removeAccessControlListEntry(removeAccessControlListEntryRequest);
        log.info("aliyun delete whitelist:{}", JSONUtil.toJsonStr(response));
        return response;

    }


    /*public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.slb20140515.Client client = AliyunTest.createClient("LTAI5t8J7joDFvsic3xiEpjx", "G8piwzrwEKQ7EZJhkwnEYy41GNLZKJ");
        com.aliyun.slb20140515.models.AddAccessControlListEntryRequest addAccessControlListEntryRequest = new com.aliyun.slb20140515.models.AddAccessControlListEntryRequest()
                .setAclId("acl-3ns5cmflk6lmzh4jkate1")
                .setAclEntrys("[{\"entry\":\"127.0.0.2/32\",\"comment\":\"privaterule1\"}]")
                .setRegionId("cn-hongkong");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            AddAccessControlListEntryResponse response = client.addAccessControlListEntry(addAccessControlListEntryRequest);
            System.out.println(JSONUtil.toJsonStr(response));
        } catch (TeaException error) {
            error.printStackTrace();
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }*/
}
