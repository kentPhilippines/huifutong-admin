package service;

import cn.hutool.json.JSONUtil;
import com.aliyun.slb20140515.models.AddAccessControlListEntryResponse;
import com.aliyun.slb20140515.models.AddListenerWhiteListItemResponse;
import com.aliyun.slb20140515.models.DescribeListenerAccessControlAttributeResponse;
import com.aliyun.tea.TeaException;

public class AliyunTest {




    /**
     * 使用AK&SK初始化账号Client
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

    public static void main(String[] args_) throws Exception {
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
    }

    public static void main2(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.slb20140515.Client client = AliyunTest.createClient("LTAI5t8J7joDFvsic3xiEpjx", "G8piwzrwEKQ7EZJhkwnEYy41GNLZKJ");
        com.aliyun.slb20140515.models.DescribeListenerAccessControlAttributeRequest describeListenerAccessControlAttributeRequest = new com.aliyun.slb20140515.models.DescribeListenerAccessControlAttributeRequest()
                .setLoadBalancerId("lb-3ns48x2twavvyw027ixay")
                .setListenerPort(7854);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            DescribeListenerAccessControlAttributeResponse response = client.describeListenerAccessControlAttributeWithOptions(describeListenerAccessControlAttributeRequest, runtime);
            System.out.println(JSONUtil.toJsonStr(response));
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }

    public static void main1(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.slb20140515.Client client = AliyunTest.createClient("LTAI5t8J7joDFvsic3xiEpjx", "G8piwzrwEKQ7EZJhkwnEYy41GNLZKJ");
        com.aliyun.slb20140515.models.AddListenerWhiteListItemRequest addListenerWhiteListItemRequest = new com.aliyun.slb20140515.models.AddListenerWhiteListItemRequest()
                .setRegionId("cn-hongkong")
                .setLoadBalancerId("lb-3ns48x2twavvyw027ixay")
                .setListenerPort(7854)
                .setSourceItems("127.0.0.1");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            AddListenerWhiteListItemResponse response = client.addListenerWhiteListItemWithOptions(addListenerWhiteListItemRequest, runtime);
            System.out.println(JSONUtil.toJsonStr(response));
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
}