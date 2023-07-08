package com.ruoyi.web.controller.alipay;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.service.IMerchantInfoEntityService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.web.controller.alipay.bean.DepositRequestVO;
import com.ruoyi.web.controller.alipay.bean.WithdrawRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/alipay/demo")
@Controller
public class DemoContorller extends BaseController {

    @Autowired
    private IMerchantInfoEntityService merchantInfoEntityService;


    private String prefix = "alipay/demo";

//    @RequiresPermissions("alipay:demo:view")
    @GetMapping(value = "/deposit")
    public String deposit()
    {
        return prefix + "/deposit";
    }


//    @RequiresPermissions("alipay:demo:view")
    @GetMapping(value = "/wit")
    public String wit()
    {
        return prefix + "/wit";
    }


    @PostMapping(value = "/deposit")
    @ResponseBody
    public String deposit(@Valid DepositRequestVO deal)
    {
        logger.info("-----{}",JSONUtil.toJsonStr(deal));
        String userId = deal.getUserId();
        userId = userId == null?deal.getAppId():"";
        Map<String, Object> parMap = new HashMap<>();
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setUserId(userId);
        List<AlipayUserInfo> alipayUserInfos = merchantInfoEntityService.selectMerchantInfoEntityList(userInfo);
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = alipayUserInfos.get(0).getPayPasword();//交易密钥
        String publicKey = alipayUserInfos.get(0).getPublicKey();
        String dealurl = alipayUserInfos.get(0).getDealUrl();

        long amount = RandomUtil.randomLong(102, 1000);
//        deal.setAmount(300 + "");//金额
//		deal.setAppId(userid);//商户号
//        deal.setApplyDate(d.format(new Date()));
//        deal.setNotifyUrl("http://starpay168.com:5055");
//        deal.setPageUrl("http://starpay168.com:5055");
//        deal.setOrderId(IdUtil.objectId());
//        deal.setPassCode("KDD_ALIPAY");
//        deal.setSubject("订单交易");
//        deal.setUserId("张三");  //to userid
        Map<String, Object> objectToMap = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam = createParam(objectToMap);
        System.out.println("签名前请求串：" + createParam);
        String md5 = getKeyedDigestUTF8(createParam + key);
        System.out.println("签名：" + md5);
        deal.setSign(md5);
        Map<String, Object> objectToMap2 = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam2 = createParam(objectToMap2);
        System.out.println("加密前字符串：" + createParam2);
        System.out.println("加密前json字符串：" + JSONUtil.toJsonStr(objectToMap2));

        String post = HttpUtil.post(dealurl+"/v2/deal/pay", JSONUtil.toJsonStr(objectToMap2));
        System.out.println("相应结果集：" + post);

        return post;
    }

    @PostMapping(value = "/wit")
    @ResponseBody
    public String wit(@Valid WithdrawRequestVO deal)
    {
        String userId = deal.getAppid();
        Map<String, Object> parMap = new HashMap<>();
        AlipayUserInfo userInfo = new AlipayUserInfo();
        userInfo.setUserId(userId);
        List<AlipayUserInfo> alipayUserInfos = merchantInfoEntityService.selectMerchantInfoEntityList(userInfo);
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = alipayUserInfos.get(0).getPayPasword();//交易密钥
        String publicKey = alipayUserInfos.get(0).getPublicKey();
        String dealurl = alipayUserInfos.get(0).getDealUrl();

        long amount = RandomUtil.randomLong(102, 1000);
//        deal.setAmount(300 + "");//金额
//		deal.setAppId(userid);//商户号
//        deal.setApplyDate(d.format(new Date()));
//        deal.setNotifyUrl("http://starpay168.com:5055");
//        deal.setPageUrl("http://starpay168.com:5055");
//        deal.setOrderId(IdUtil.objectId());
//        deal.setPassCode("KDD_ALIPAY");
//        deal.setSubject("订单交易");
//        deal.setUserId("张三");  //to userid
        Map<String, Object> objectToMap = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam = createParam(objectToMap);
        System.out.println("签名前请求串：" + createParam);
        String md5 = getKeyedDigestUTF8(createParam + key);
        System.out.println("签名：" + md5);
        deal.setSign(md5);
        Map<String, Object> objectToMap2 = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam2 = createParam(objectToMap2);
        System.out.println("加密前字符串：" + createParam2);
        System.out.println("加密前json字符串：" + JSONUtil.toJsonStr(objectToMap2));

        String post = HttpUtil.post(dealurl+"/v2/deal/wit", JSONUtil.toJsonStr(objectToMap2));
        System.out.println("相应结果集：" + post);

        return post;
    }

    public static String createParam(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }
            Object[] key = map.keySet().toArray();
            Arrays.sort(key);
            StringBuffer res = new StringBuffer(128);
            for (int i = 0; i < key.length; i++) {
                if (ObjectUtil.isNotNull(map.get(key[i]))) {
                    res.append(key[i] + "=" + map.get(key[i]) + "&");
                }
            }
            String rStr = res.substring(0, res.length() - 1);
            return rStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getKeyedDigestUTF8(String strSrc) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));
            String result = "";
            byte[] temp;
            temp = md5.digest("".getBytes("UTF8"));
            for (int i = 0; i < temp.length; i++) {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
