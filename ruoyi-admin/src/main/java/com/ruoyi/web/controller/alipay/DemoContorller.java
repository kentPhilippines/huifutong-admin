package com.ruoyi.web.controller.alipay;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.service.IAlipayUserInfoService;
import com.ruoyi.alipay.service.IMerchantInfoEntityService;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.framework.util.DictionaryUtils;
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
import java.util.Map;

@RequestMapping("/alipay/demo")
@Controller
public class DemoContorller extends BaseController {
    @Autowired
    private DictionaryUtils dictionaryUtils;
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


    @Autowired
    private IAlipayUserInfoService alipayUserInfoService;
    @PostMapping(value = "/deposit")
    @ResponseBody
    public String deposit(@Valid DepositRequestVO deal)
    {
        logger.info("-----{}",JSONUtil.toJsonStr(deal));
        AlipayUserInfo merchantInfoByUserId = alipayUserInfoService.findMerchantInfoByUserId(deal.getAppId());
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = merchantInfoByUserId.getPayPasword();//交易密钥
        String dealurl = merchantInfoByUserId.getDealUrl();
        Map<String, Object> objectToMap = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam = createParam(objectToMap);
        logger.info("签名前请求串：" + createParam);
        String md5 = getKeyedDigestUTF8(createParam + key);
        logger.info("签名：" + md5);
        deal.setSign(md5);
        Map<String, Object> objectToMap2 = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam2 = createParam(objectToMap2);
        logger.info("加密前字符串：" + createParam2);
        logger.info("加密前json字符串：" + JSONUtil.toJsonStr(objectToMap2));
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_6);

        String post = HttpUtil.post(ipPort+"/v2/deal/pay", JSONUtil.toJsonStr(objectToMap2));
        logger.info(ipPort+"/v2/deal/pay"+"相应结果集：" + post);
        return post;
    }

    @PostMapping(value = "/wit")
    @ResponseBody
    public String wit(@Valid WithdrawRequestVO deal)
    {
        logger.info("-----{}",JSONUtil.toJsonStr(deal));
        AlipayUserInfo merchantInfoByUserId = alipayUserInfoService.findMerchantInfoByUserId(deal.getAppid());
        SimpleDateFormat d = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = merchantInfoByUserId.getPayPasword();//交易密钥
        String dealurl = merchantInfoByUserId.getDealUrl();
        Map<String, Object> objectToMap = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam = createParam(objectToMap);
        logger.info("签名前请求串：" + createParam);
        String md5 = getKeyedDigestUTF8(createParam + key);
        logger.info("签名：" + md5);
        deal.setSign(md5);
        Map<String, Object> objectToMap2 = JSONUtil.toBean(JSONUtil.toJsonStr(deal),Map.class);
        String createParam2 = createParam(objectToMap2);
        logger.info("加密前字符串：" + createParam2);
        logger.info("加密前json字符串：" + JSONUtil.toJsonStr(objectToMap2));
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);

        String post = HttpUtil.post(ipPort+"/v2/deal/wit", JSONUtil.toJsonStr(objectToMap2));
        logger.info("相应结果集：" + post);
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
