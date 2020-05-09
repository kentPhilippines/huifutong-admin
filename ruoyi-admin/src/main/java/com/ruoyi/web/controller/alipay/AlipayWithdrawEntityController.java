package com.ruoyi.web.controller.alipay;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.service.IMerchantInfoEntityService;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.HashKit;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.MapDataUtil;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.alipay.domain.AlipayWithdrawEntity;
import com.ruoyi.alipay.service.IAlipayWithdrawEntityService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 会员提现记录Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/withdrawal")
public class AlipayWithdrawEntityController extends BaseController {
    private String prefix = "alipay/withdrawal";

    @Autowired
    private IAlipayWithdrawEntityService alipayWithdrawEntityService;
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @Autowired
    private IMerchantInfoEntityService merchantInfoEntityService;

    @GetMapping("/qr")
    public String qr_withdrawal() {
        return prefix + "/qr_withdrawal";
    }

    /**
     * 查询码商提现记录列表
     */
    @PostMapping("/qr/list")
    @ResponseBody
    public TableDataInfo qr_list(AlipayWithdrawEntity alipayWithdrawEntity) {
        startPage();
        alipayWithdrawEntity.setWithdrawType("2");
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService
                .selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        return getDataTable(list);
    }

    @GetMapping("/merchant")
    public String merchant_withdrawal() {
        return prefix + "/merchant_withdrawal";
    }

    /**
     * 查询商户提现记录列表
     */
    @PostMapping("/merchant/list")
    @ResponseBody
    public TableDataInfo merchant_list(AlipayWithdrawEntity alipayWithdrawEntity) {
        startPage();
        alipayWithdrawEntity.setWithdrawType("1");
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService
                .selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        return getDataTable(list);
    }

    /**
     * 显示商户提现详情页
     */
    @GetMapping("/merchant/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        return prefix + "/edit";
    }

    /**
     * 财务审核会员提现记录
     */
    @Log(title = "财务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/approval")
    @ResponseBody
    public AjaxResult apporval(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_7);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("orderId", alipayWithdrawEntity.getOrderId());//订单号
        mapParam.put("userId", alipayWithdrawEntity.getUserId());
        mapParam.put("orderStatus", alipayWithdrawEntity.getOrderStatus());
        mapParam.put("approval", currentUser.getLoginName());
        mapParam.put("comment", alipayWithdrawEntity.getComment());
        if ("3".equals(alipayWithdrawEntity.getOrderStatus())) {
            mapParam.put("ip", IpUtils.getHostIp());
        }
        return HttpUtils.adminRequest2Gateway(mapParam, ipPort + urlPath);
    }


    /**
     * 导出码商提现记录列表
     */
    @Log(title = "码商代付订单", businessType = BusinessType.EXPORT)
    @PostMapping("/qr/export")
    @ResponseBody
    public AjaxResult qr_export(AlipayWithdrawEntity alipayWithdrawEntity) {
        alipayWithdrawEntity.setWithdrawType("2");
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService.selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        ExcelUtil<AlipayWithdrawEntity> util = new ExcelUtil<AlipayWithdrawEntity>(AlipayWithdrawEntity.class);
        return util.exportExcel(list, "withdrawal");
    }

    /**
     * 导出商户提现记录列表
     */
    @Log(title = "商户代付订单", businessType = BusinessType.EXPORT)
    @PostMapping("/merchant/export")
    @ResponseBody
    public AjaxResult export(AlipayWithdrawEntity alipayWithdrawEntity) {
        alipayWithdrawEntity.setWithdrawType("1");
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService.selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        ExcelUtil<AlipayWithdrawEntity> util = new ExcelUtil<AlipayWithdrawEntity>(AlipayWithdrawEntity.class);
        return util.exportExcel(list, "withdrawal");
    }

    /**
     * 显示商户提现详情页
     */
    @GetMapping("/merchant/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        return prefix + "/detail";
    }

    /**
     * 商户提现统计当天数据
     * @param mmap
     * @return
     */
    @GetMapping("/statistics/merchant/payfor")
    public String dayStat(ModelMap mmap) {
        StatisticsEntity statisticsEntity = alipayWithdrawEntityService.selectPayforStatisticsDataByDay(DateUtils.dayStart(), DateUtils.dayEnd());
        if (statisticsEntity.getTotalCount() == 0) {
            statisticsEntity.setSuccessPercent(0.00);
        } else {
            BigDecimal percent = BigDecimal.valueOf((float) statisticsEntity.getSuccessCount() / statisticsEntity.getTotalCount());
            Double successPercent = percent.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            statisticsEntity.setSuccessPercent(successPercent);
        }
        mmap.put("statisticsEntity", statisticsEntity);
        return prefix + "/currentData";
    }

}
