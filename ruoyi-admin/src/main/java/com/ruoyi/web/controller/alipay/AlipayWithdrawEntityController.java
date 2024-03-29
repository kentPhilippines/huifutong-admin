package com.ruoyi.web.controller.alipay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.core.domain.WitStatisticsEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.IpUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private IAlipayChanelFeeService alipayChanelFeeService;
    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;
    @Autowired
    private IAlipayUserInfoService alipayUserInfoServiceImpl;
    @Autowired
    private IAlipayWithdrawEntityService alipayWithdrawEntityService;
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @Autowired
    private IAlipayRunOrderEntityService alipayRunOrderEntityService;
    @Autowired
    private IMerchantInfoEntityService merchantInfoEntityService;
    @Autowired
    private IAlipayDealOrderEntityService alipayDealOrderEntityService;

    private ReentrantLock reentrantLock = new ReentrantLock();

    @Autowired
    private AlipayWithdrawAuditRuleService alipayWithdrawAuditRuleService;
    @CreateCache(name = "ALIPAY_WITHDRAWAL_LOCK:", expire = 60, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.LOCAL)
    private Cache<String, String> cache;

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


    @PostMapping("/qr/export/{ids}")
    @ResponseBody
    public AjaxResult qr_list(@PathVariable("ids") String ids) {
        List<String> idList = Lists.newArrayList( ids.split(","));
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService.exportNotExportedList(idList);
        List<AlipayWithdrawExportEntity> exportList = JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(list)),AlipayWithdrawExportEntity.class);
        ExcelUtil<AlipayWithdrawExportEntity> util = new ExcelUtil<AlipayWithdrawExportEntity>(AlipayWithdrawExportEntity.class);
        return util.exportExcel(exportList, "withdrawal");

//        startPage();
//        alipayWithdrawEntity.setWithdrawType("2");
//        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService
//                .selectAlipayWithdrawEntityList(alipayWithdrawEntity);
//        return getDataTable(list);
    }

    @PostMapping("/qr/export/bank/{ids}")
    @ResponseBody
    public AjaxResult qr_list_bank(@PathVariable("ids") String ids) {
        List<String> idList = Lists.newArrayList( ids.split(","));
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService.exportNotExportedListBank(idList);
        List<AlipayWithdrawExportEntity> exportList = JSONUtil.toList(JSONUtil.parseArray(JSONUtil.toJsonStr(list)),AlipayWithdrawExportEntity.class);
        ExcelUtil<AlipayWithdrawExportEntity> util = new ExcelUtil<AlipayWithdrawExportEntity>(AlipayWithdrawExportEntity.class);
        return util.exportExcel(exportList, "withdrawal");

//        startPage();
//        alipayWithdrawEntity.setWithdrawType("2");
//        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService
//                .selectAlipayWithdrawEntityList(alipayWithdrawEntity);
//        return getDataTable(list);
    }

    @RequiresPermissions("merchant:withdrawal:view")
    @GetMapping("/merchant")
    public String merchant_withdrawal(ModelMap modelMap) {
        List<AlipayUserFundEntity> channelId = alipayUserFundEntityService.findUserFundRate();
        modelMap.put("channelList", channelId);
        return prefix + "/merchant_withdrawal";
    }
    @Autowired  IAlipayProductService iAlipayProductService;
    /**
     * 查询商户提现记录列表
     */
    @PostMapping("/merchant/list")
    @RequiresPermissions("withdrawal:alipay:merchant:list")
    @ResponseBody
    public TableDataInfo merchant_list(AlipayWithdrawEntity alipayWithdrawEntity) {
        startPage();
        alipayWithdrawEntity.setWithdrawType("1");
        List<AlipayWithdrawEntity> list = alipayWithdrawEntityService
                .selectAlipayWithdrawEntityList(alipayWithdrawEntity);
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        ConcurrentHashMap<String, AlipayProductEntity> prCollect = productlist.stream().collect(Collectors.toConcurrentMap(AlipayProductEntity::getProductId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        List<AlipayUserFundEntity> channel = alipayUserFundEntityService.findUserFundRate();
        ConcurrentHashMap<String, AlipayUserFundEntity> channelMap = channel.stream().collect(Collectors.toConcurrentMap(AlipayUserFundEntity::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        AlipayWithdrawEntity witSum = alipayWithdrawEntityService.selectAlipayWithdrawEntityListSum(alipayWithdrawEntity);
        for (AlipayWithdrawEntity order : list) {
            AlipayProductEntity product = prCollect.get(order.getWitType());
            if (StrUtil.isNotBlank(order.getChannelId())) {
                order.setChannelId(channelMap.get(order.getChannelId()).getUserName());
            }
            if (StrUtil.isNotBlank(order.getWitChannel())) {
                order.setWitChannel(channelMap.get(order.getWitChannel()).getUserName());
            }
            if (ObjectUtil.isNotNull(product)) {
                order.setWitType(product.getProductName());
            }
        }
        if (null != witSum && CollUtil.isNotEmpty(list)) {
            for (int mark = 0; mark < 1; mark++) {
                list.get(mark).setSunCountAmountFee(witSum.getSunCountAmountFee());
                list.get(mark).setSunCountAmount(witSum.getSunCountAmount());
                list.get(mark).setSunCountActualAmount(witSum.getSunCountActualAmount());

            }
        }
        return getDataTable(list);
    }

    /**
     * 显示商户提现详情页
     */
    @GetMapping("/merchant/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        AlipayUserInfo userInfo = alipayUserInfoServiceImpl.findMerchantInfoByUserId(alipayWithdrawEntity.getUserId());
        mmap.put("autoWit", userInfo.getAutoWit());
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();//查询所有渠道账户
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        alipayProductEntity.setProductCode("1");
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("channelList", rateList);
        mmap.put("productList", productlist);


        List<AlipayRunOrderEntity> alipayRunOrders = alipayRunOrderEntityService.findAssocidOrder(alipayWithdrawEntity.getOrderId());
        if(CollectionUtil.isEmpty(alipayRunOrders))
        {
            //throw new BusinessException("订单尚未结算，请稍后再试。");
            mmap.put("errorMessage","订单尚未结算，请稍后再试。");
            return "/error/business";
        }



        return prefix + "/edit";
    }


    @Log(title = "批量修改等待时间", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/editsWatingTime")
    @ResponseBody
    public AjaxResult editsSaveWatingTime(String ids,  String watingTime) {
        logger.info("订单号："+ids );
        logger.info("时间："+watingTime );
        alipayWithdrawEntityService.batchUpdateMacthMoreWatingTime(ids,watingTime);
        return AjaxResult.success();
    }
    @GetMapping("/qr/edit/{id}")
    public String qrEdit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();//查询所有渠道账户
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        alipayProductEntity.setProductCode("1");
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("channelList", rateList);
        mmap.put("productList", productlist);
        return prefix + "/editQr";
    }
    /**
     * 显示商户提现详情页
     */
    @GetMapping("/merchant/edits/{ids}")
    public String edit(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds(CollUtil.list(true, ids.split(",")));
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayWithdrawEntity entity : alipayWithdrawEntitys) {
            strBuilder.append("id:").append(entity.getId()).append(" 系统订单：").append(entity.getOrderId()).append(" ").append("原状态：");
            if (entity.getMoreMacth() == 1) {
                strBuilder.append("启用挂起");
            } else if (entity.getMoreMacth() == 0) {
                strBuilder.append("停用挂起");
            }
            strBuilder.append(" ");
//                    .append("原渠道：").append(rateEntity.getChannelId()).append(" ").append("原产品：").append(rateEntity.getPayTypr() + " ");
        }
        mmap.put("rete", strBuilder.toString());
        return prefix + "/edits";
    }
    @Log(title = "出款订单批量修改", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/edits")
    @ResponseBody
    public AjaxResult editsSave(String ids, Integer status,String watingTime) {

        alipayWithdrawEntityService.batchUpdateMacthMore(ids, status,watingTime);
        return AjaxResult.success();
    }
    /**
     * 财务审核会员提现记录
     */
    @Log(title = "代付订单确认", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/approval")
    @ResponseBody
    @RepeatSubmit
    public AjaxResult apporval(AlipayWithdrawEntity alipayWithdrawEntity) {
        try {
            reentrantLock.tryLock(10,TimeUnit.SECONDS);
            if(cache.get(alipayWithdrawEntity.getOrderId())!=null)
            {
                return error("1分钟内不允许重复操作");
            }
            cache.put(alipayWithdrawEntity.getOrderId(),alipayWithdrawEntity.getOrderId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            reentrantLock.unlock();
        }



        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        if ("2".equals(alipayWithdrawEntity.getOrderStatus())) {
            if (StrUtil.isBlank(alipayWithdrawEntity.getChannelId()) && StrUtil.isBlank(alipayWithdrawEntity.getWitType())) {
                return error("实际出款渠道为空");
            }
            AlipayChanelFee channelBy = alipayChanelFeeService.findChannelBy(alipayWithdrawEntity.getChannelId(), alipayWithdrawEntity.getWitType());
            if (ObjectUtil.isNull(channelBy)) {
                return error("所选实际出款渠道未配置出款费率，请重新配置");
            }
        }
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_7);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("orderId", alipayWithdrawEntity.getOrderId());//订单号
        mapParam.put("userId", alipayWithdrawEntity.getUserId());
        mapParam.put("orderStatus", alipayWithdrawEntity.getOrderStatus());
        mapParam.put("approval", currentUser.getLoginName());
        mapParam.put("comment", alipayWithdrawEntity.getComment());
        mapParam.put("channelId", alipayWithdrawEntity.getChannelId());
        mapParam.put("witType", alipayWithdrawEntity.getWitType());
        if ("3".equals(alipayWithdrawEntity.getOrderStatus())) {
            List<AlipayRunOrderEntity> associdOrder = alipayRunOrderEntityService.findAssocidOrder(alipayWithdrawEntity.getOrderId());
            if (CollUtil.isEmpty(associdOrder)) {
                throw new BusinessException("操作失败，当前账户流水扣款失败");
            }
            mapParam.put("ip", IpUtils.getHostIp());
        } else if ("100".equals(alipayWithdrawEntity.getOrderStatus())) {//商户后台大夫推送处理，将订单修改为已推送
            AlipayDealOrderEntity alipayDealOrder =  alipayDealOrderEntityService.findOrderByAssociatedId(alipayWithdrawEntity.getOrderId());
            //订单推送过 不能重复推
            if(ObjectUtil.isNotNull(alipayDealOrder))
            {
                throw new BusinessException("该订单已经推送过");
            }
            alipayWithdrawEntityService.updateWitStatus(alipayWithdrawEntity.getId());
        }

        return HttpUtils.adminRequest2Gateway(mapParam, ipPort + urlPath);
    }

    @Log(title = "结算eth矿工手续费", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/ethFeePost")
    @ResponseBody
    public AjaxResult ethFeePost(AlipayWithdrawEntity alipayWithdrawEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_7);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("orderId", alipayWithdrawEntity.getOrderId());//订单号

         /* BigDecimal usdt ,   //花费usdt
            price  ,    //汽油价格
            used,       //使用汽油数
            eth,        //花费eth
            priceUsdt;  //eth - usdt 汇率
    String hash;        //订单hash* */
        mapParam.put("usdt", alipayWithdrawEntity.getUsdt());
        mapParam.put("price", alipayWithdrawEntity.getPrice());
        mapParam.put("approval", currentUser.getLoginName());
        mapParam.put("used", alipayWithdrawEntity.getUsed());
        mapParam.put("eth", alipayWithdrawEntity.getEth());
        mapParam.put("priceUsdt", alipayWithdrawEntity.getPriceUsdt());
        mapParam.put("hash", alipayWithdrawEntity.getHash());
        return HttpUtils.adminRequest2Gateway(mapParam, ipPort + urlPath + "USDT");
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
    @Log(title = "代付订单详情", businessType = BusinessType.OTHER)
    @GetMapping("/merchant/detail/{id}")
    public String detail(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        return prefix + "/detail";
    }

    @Log(title = "结算代付eth矿工费", businessType = BusinessType.OTHER)
    @GetMapping("/merchant/ethFee/{id}")
    public String ethFee(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        return prefix + "/ethFee";
    }

    /**
     * 商户提现统计当天数据
     *
     * @param mmap
     * @return
     */
    @Log(title = "统计商户当天交易数据", businessType = BusinessType.OTHER)
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

    @GetMapping("/statistics/merchant/channelWit")
    public String channelWit() {
        return prefix + "/currentTable";
    }

    @PostMapping("/statistics/merchant/wit")
    @ResponseBody
    public TableDataInfo staWit(StatisticsEntity statisticsEntity) {
        List<WitStatisticsEntity> staList = new ArrayList<>();
        startPage();
        staList = alipayWithdrawEntityService.statisticsWitForDay(statisticsEntity);


        return getDataTable(staList);
    }
    @GetMapping("/merchant/editAllPush/{ids}")
    public String editAllPush(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds1(CollUtil.list(true, ids.split(",")));
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayWithdrawEntity entity : alipayWithdrawEntitys) {
            strBuilder.append("id:").append(entity.getId()).append(" 系统订单：").append(entity.getOrderId()).append(" ") ;
//                    .append("原渠道：").append(rateEntity.getChannelId()).append(" ").append("原产品：").append(rateEntity.getPayTypr() + " ");
        }
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();//查询所有渠道账户
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        alipayProductEntity.setProductCode("1");
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("channelList", rateList);
        mmap.put("productList", productlist);
        mmap.put("rete", strBuilder.toString());
        return prefix + "/editAllPushs";
    }
    /**
     * 财务审核会员提现记录
     */
    @Log(title = "批量代付订单确认", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/apporvalAll")
    @ResponseBody
    public AjaxResult apporvalAll(String ids,String channelId , String witType,String comment,String orderStatus) {

        StringBuilder bu   = new StringBuilder();
        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds(CollUtil.list(true, ids.split(",")));
        for (AlipayWithdrawEntity alipayWithdrawEntity :alipayWithdrawEntitys){
            if(! alipayWithdrawEntity.getOrderStatus().equals("1")) {
                logger.info("审核中的订单才可以推送，当前订单号："+alipayWithdrawEntity.getOrderId());
                continue;
            }
            alipayWithdrawEntity.setComment(comment);
            alipayWithdrawEntity.setChannelId(channelId);
            alipayWithdrawEntity.setWitType(witType);
            alipayWithdrawEntity.setOrderStatus(orderStatus);
            try {
                reentrantLock.tryLock(10, TimeUnit.SECONDS);
                if (cache.get(alipayWithdrawEntity.getOrderId()) != null) {
                    return error("1分钟内不允许重复操作");
                }
                cache.put(alipayWithdrawEntity.getOrderId(), alipayWithdrawEntity.getOrderId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }


            alipayWithdrawAuditRuleService.checkRuleBeforeAudit(alipayWithdrawEntity);
            //新增逻辑 判断金流里是否有这笔失败的 如果有就返回
       /* if(alipayWithdrawEntity.getOrderStatus().equals("3"))
        {
            AlipayRunOrderEntity alipayRunOrderEntity = new AlipayRunOrderEntity();
            alipayRunOrderEntity.setAssociatedId(alipayWithdrawEntity.getOrderId());
            List<AlipayRunOrderEntity> list = alipayRunOrderEntityService.selectAlipayRunOrderEntityList(alipayRunOrderEntity);
            list.stream().filter(entity ->entity.getRunOrderType()==8 ||entity.getRunOrderType()==22 ).findAny().ifPresent(runOrderEntity->{
                throw new BusinessException("此订单金流里已有失败数据");
            });
        }*/

            // 获取当前的用户
            SysUser currentUser = ShiroUtils.getSysUser();
            if ("2".equals(alipayWithdrawEntity.getOrderStatus())) {
                if (StrUtil.isBlank(alipayWithdrawEntity.getChannelId()) && StrUtil.isBlank(alipayWithdrawEntity.getWitType())) {
                    return error("实际出款渠道为空");
                }
                AlipayChanelFee channelBy = alipayChanelFeeService.findChannelBy(alipayWithdrawEntity.getChannelId(), alipayWithdrawEntity.getWitType());
                if (ObjectUtil.isNull(channelBy)) {
                    return error("所选实际出款渠道未配置出款费率，请重新配置");
                }
            }
            //获取alipay处理接口URL
            String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
            String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_7);
            Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
            mapParam.put("orderId", alipayWithdrawEntity.getOrderId());//订单号
            mapParam.put("userId", alipayWithdrawEntity.getUserId());
            mapParam.put("orderStatus", alipayWithdrawEntity.getOrderStatus());
            mapParam.put("approval", currentUser.getLoginName());
            mapParam.put("comment", alipayWithdrawEntity.getComment());
            mapParam.put("channelId", alipayWithdrawEntity.getChannelId());
            mapParam.put("witType", alipayWithdrawEntity.getWitType());
            if ("3".equals(alipayWithdrawEntity.getOrderStatus())) {
                List<AlipayRunOrderEntity> associdOrder = alipayRunOrderEntityService.findAssocidOrder(alipayWithdrawEntity.getOrderId());
                if (CollUtil.isEmpty(associdOrder)) {
                    throw new BusinessException("操作失败，当前账户流水扣款失败");
                }
                mapParam.put("ip", IpUtils.getHostIp());
            } else if ("100".equals(alipayWithdrawEntity.getOrderStatus())) {//商户后台大夫推送处理，将订单修改为已推送
                alipayWithdrawEntityService.updateWitStatus(alipayWithdrawEntity.getId());
            }
            AjaxResult ajaxResult = HttpUtils.adminRequest2Gateway(mapParam, ipPort + urlPath);
            logger.info("响应结果："+ajaxResult.toString());
            String code = ajaxResult.get("code").toString();

            if(!code.equals("0")){
                code = ajaxResult.get("msg").toString();
            }else {
                code = "失败";
            }
            bu.append("订单："+alipayWithdrawEntity.getOrderId() + "，推送结果："+code);
        }
        return AjaxResult.success(bu);
    }
    /**
     * 批量修改推送渠道
     */
    @GetMapping("/merchant/editAllChannel/{ids}")
    public String editAllChannel(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds1(CollUtil.list(true, ids.split(",")));
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayWithdrawEntity entity : alipayWithdrawEntitys) {
            strBuilder.append("id:").append(entity.getId()).append(" 系统订单：").append(entity.getOrderId()).append(" ") ;

//                    .append("原渠道：").append(rateEntity.getChannelId()).append(" ").append("原产品：").append(rateEntity.getPayTypr() + " ");
        }
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();//查询所有渠道账户
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        alipayProductEntity.setProductCode("1");
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("channelList", rateList);
        mmap.put("productList", productlist);
        mmap.put("rete", strBuilder.toString());
        return prefix + "/editAllChannel";
    }

    @Log(title = "出款订单批量修改推送渠道", businessType = BusinessType.UPDATE)
    @PostMapping("/merchant/editAllChannel")
    @ResponseBody
    public AjaxResult editAllChannel(String ids,String witChannel,String witType) {

        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds1(CollUtil.list(true, ids.split(",")));

        alipayWithdrawEntitys.stream().forEach(e->{
            e.setWitChannel(witChannel);
            e.setWitType(witType);
        });

        alipayWithdrawEntityService.batchUpdateChannel(alipayWithdrawEntitys);
        return AjaxResult.success();
    }
    @GetMapping("/merchant/editWating/{ids}")
    public String editWating(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<AlipayWithdrawEntity> alipayWithdrawEntitys = alipayWithdrawEntityService.selectAlipayWithdrawEntityByIds1(CollUtil.list(true, ids.split(",")));
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayWithdrawEntity entity : alipayWithdrawEntitys) {
            strBuilder.append("  id:").append(entity.getId()).append(" 系统订单：").append(entity.getOrderId()).append(" ").append("配置渠道：");
            strBuilder.append("原等待时间");
            strBuilder.append("：  ");
            strBuilder.append(entity.getWatingTime());
        }
        mmap.put("rete", strBuilder.toString());
        return prefix + "/editWating";
    }
    @GetMapping("/merchant/editChannel/{id}")
    public String editChannel(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayWithdrawEntity alipayWithdrawEntity = alipayWithdrawEntityService.selectAlipayWithdrawEntityById(id);
        mmap.put("alipayWithdrawEntity", alipayWithdrawEntity);
        AlipayUserInfo userInfo = alipayUserInfoServiceImpl.findMerchantInfoByUserId(alipayWithdrawEntity.getUserId());
        mmap.put("autoWit", userInfo.getAutoWit());
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();//查询所有渠道账户
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        alipayProductEntity.setProductCode("1");
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("channelList", rateList);
        mmap.put("productList", productlist);
        return prefix + "/editChannel";
    }
    @PostMapping("/merchant/editChannel")
    @ResponseBody
    public AjaxResult editChannel(AlipayWithdrawEntity alipayWithdrawEntity) {

        try {
            alipayWithdrawEntityService.updateWithdrawEntityById(alipayWithdrawEntity);
        }catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

        return AjaxResult.success();
    }

}
