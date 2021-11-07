package com.ruoyi.web.controller.alipay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 交易订单Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/orderDeal")
public class AlipayDealOrderEntityController extends BaseController {
    private String prefix = "alipay/orderDeal";
    private String code_prefix = "alipay/file";
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @Autowired
    private IAlipayDealOrderAppService alipayDealOrderAppService;
    @Autowired
    private IAlipayDealOrderEntityService alipayDealOrderEntityService;
    @Autowired
    private IAlipayWithdrawEntityService iAlipayWithdrawEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IAlipayProductService iAlipayProductService;
    @Autowired
    private IAlipayUserRateEntityService iAlipayUserRateEntityService;
    @Autowired
    private IAlipayChanelFeeService alipayChanelFeeService;
    @Autowired
    private IAlipayUserRateEntityService alipayUserRateEntityService;
    @Autowired
    private IAlipayUserInfoService alipayUserInfoService;
    @GetMapping()
    @RequiresPermissions("orderDeal:qr:view")
    public String orderDeal(ModelMap mmap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("productList", list);
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();
        mmap.put("rateList", rateList);
        return prefix + "/orderDeal";
    }

    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;

    /**
     * 查询交易订单列表
     */
    @PostMapping("/list")
    @RequiresPermissions("orderDeal:qr:list")
    @ResponseBody
    public TableDataInfo list(AlipayDealOrderEntity alipayDealOrderEntity) {
        if (StrUtil.isNotEmpty(alipayDealOrderEntity.getOrderQrUser1())) {
            alipayDealOrderEntity.setOrderQrUser(alipayDealOrderEntity.getOrderQrUser1());
        }
        startPage();
        List<AlipayDealOrderEntity> list = alipayDealOrderEntityService
                .selectAlipayDealOrderEntityList(alipayDealOrderEntity);
        SysUser user = new SysUser();
        List<SysUser> sysUsers = userService.selectUserList(user);
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        List<AlipayProductEntity> productlist = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        List<AlipayUserFundEntity> listFund = alipayUserFundEntityService.findUserFundAll();
        ConcurrentHashMap<String, AlipayUserFundEntity> userCollect1 = listFund.stream().collect(Collectors.toConcurrentMap(AlipayUserFundEntity::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        ConcurrentHashMap<String, AlipayProductEntity> prCollect = productlist.stream().collect(Collectors.toConcurrentMap(AlipayProductEntity::getProductId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        ConcurrentHashMap<String, SysUser> userCollect = new ConcurrentHashMap<String, SysUser>();
        for (SysUser user1 : sysUsers) {
            if (StrUtil.isNotBlank(user1.getMerchantId())) {
                userCollect.put(user1.getMerchantId(), user1);
            }
        }
        for (AlipayDealOrderEntity order : list) {
            order.setChannelName(userCollect1.get(order.getOrderQrUser()).getUserName());
            AlipayProductEntity product = prCollect.get(order.getRetain1());
            if (ObjectUtil.isNotNull(product)) {
                order.setRetain1(product.getProductName());
            }
            if (ObjectUtil.isNotNull(userCollect.get(order.getOrderAccount()))) {
                order.setUserName(userCollect.get(order.getOrderAccount()).getUserName());
            }
        }
        userCollect = null;
        AlipayDealOrderEntity deal = alipayDealOrderEntityService.selectAlipayDealOrderEntityListSum(alipayDealOrderEntity);
        if (null != deal && CollUtil.isNotEmpty(list)) {
            for (int mark = 0; mark < 1; mark++) {
                list.get(mark).setSunCountAmountFee(deal.getSunCountAmountFee());
                list.get(mark).setSunCountAmount(deal.getSunCountAmount());
                list.get(mark).setSunCountActualAmount(deal.getSunCountActualAmount());

            }
        }
        return getDataTable(list);
    }

    /**
     * 交由财务处理
     */
    @PostMapping("/updataOrder")
    @RequiresPermissions("orderDeal:qr:status")
    @ResponseBody
    @Log(title = "订单转交财务", businessType = BusinessType.UPDATE)
    public AjaxResult updataOrder(String id) {
        AlipayDealOrderEntity order = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(Long.valueOf(id));
        order.setOrderStatus("7");//人工处理
        int i = alipayDealOrderEntityService.updateAlipayDealOrderEntity(order);
        return toAjax(i);
    }

    @Autowired
    IAlipayUserInfoService iAlipayUserInfoServiceImpl;

    /**
     * 代付主交易订单修改卡商账户
     *
     * @param
     * @return
     */
    @GetMapping("/updateBankCardShow/{userId}")
    @RequiresPermissions("orderDeal:qr:status:updateBankCardShow")
    @Log(title = "修改出款卡商或拆单", businessType = BusinessType.INSERT)
    public String updateBankCardShow(ModelMap mmap, @PathVariable("userId") String orderId) {
        List<AlipayUserFundEntity> listFund = alipayUserFundEntityService.findUserFundAllToBank();
        List<AlipayUserInfo> listInfo = iAlipayUserInfoServiceImpl.findUserUserAllToBank();
        ConcurrentHashMap<String, AlipayUserInfo> userMap = listInfo.stream().collect(Collectors.toConcurrentMap(AlipayUserInfo::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        List<AlipayUserFundEntity> list = new ArrayList<>();
        for (AlipayUserFundEntity fund : listFund) {
            AlipayUserInfo alipayUserInfo = userMap.get(fund.getUserId());
            if (null != alipayUserInfo) {
                list.add(fund);//满足的出款账户
            }
        }
        //入款金额 - 出款金额   差额最小  且根据入款金额排序
        Collections.sort(list, new Comparator<AlipayUserFundEntity>() {
            @Override
            public int compare(AlipayUserFundEntity o1, AlipayUserFundEntity o2) {
                int i = 1;
                if ((o1.getTodayDealAmount() - o1.getTodayOtherWitAmount()) > (o2.getTodayDealAmount() - o2.getTodayOtherWitAmount())) {
                    i = -1;
                } else {
                    i = 1;
                }
                ;
                return i;
            }
        });
        if (StrUtil.isNotEmpty(orderId)) {
            String[] split = orderId.split("====");
            if (split.length > 1 && StrUtil.isNotEmpty(split[1])) {
                if (split[1].equals("amount")) {
                    mmap.put("listFund", list);
                    mmap.put("orderId", split[0]);
                    return prefix + "/updateBankCardEditMore";
                }
            }
        }
        AlipayDealOrderEntity order = alipayDealOrderEntityService.findOrderByOrderId(orderId);
        if (1 == order.getLockWit()) {
            Date lockWitTime = order.getLockWitTime();
            if (DateUtil.isExpired(lockWitTime, DateField.SECOND, Integer.valueOf(600), new Date())) {
                mmap.put("errorMessage", "当前订单已锁定，请解锁后重新配置");
                return prefix + "/business";
            }
        }
        mmap.put("listFund", list);
        mmap.put("orderId", orderId);
        return prefix + "/updateBankCardEdit";
    }


    /**
     * 代付主交易订单修改卡商账户
     */
    @PostMapping("/updateBankCard")
    @RequiresPermissions("orderDeal:qr:status:updateBankCard")
    @ResponseBody
    public AjaxResult updateBankCard(String orderId, String userId) {
        AlipayDealOrderEntity orderEntityList = alipayDealOrderEntityService.findOrderByOrderId(orderId);
        AlipayUserRateEntity rate = iAlipayUserRateEntityService.findWitRate(userId);
        Double fee = rate.getFee();
        Double dealAmount = orderEntityList.getDealAmount();
        fee = fee * dealAmount;
        Double profit = Double.valueOf(orderEntityList.getRetain3());
        profit = Double.valueOf(orderEntityList.getDealFee()) - fee;
        return toAjax(alipayDealOrderEntityService.updateOrderQr(orderId, userId, "", rate.getId(), fee, profit));
    }

    /**
     * 代付主交易订单修改卡商账户
     */
    @PostMapping("/updateBankCardMore")
    @RequiresPermissions("orderDeal:qr:status:updateBankCard")
    @ResponseBody
    @Log(title = "确认拆单", businessType = BusinessType.INSERT)
    public AjaxResult updateBankCard(String orderId, String userId, String amount) {
        AlipayDealOrderEntity orderEntityList = alipayDealOrderEntityService.findOrderByOrderId(orderId);
        AlipayUserRateEntity rate = iAlipayUserRateEntityService.findWitRate(userId);
        Double dealAmount = orderEntityList.getDealAmount();
        if (dealAmount < Double.valueOf(amount)) {
            return error("金额超限制");
        }
        Double nowAmount = dealAmount - Double.valueOf(amount);    //原代付订单现在金额
        Double fee = rate.getFee();
        fee = fee * nowAmount;
        Double profit = Double.valueOf(orderEntityList.getRetain3());
        profit = Double.valueOf(orderEntityList.getDealFee()) - fee;
        int a = alipayDealOrderEntityService.updateAmountOrder(nowAmount, orderId, fee, profit);
        orderEntityList.setOrderId(findOderId(orderEntityList.getOrderId()));
        orderEntityList.setOrderQrUser(userId);
        orderEntityList.setOrderQr("");
        orderEntityList.setDealAmount(Double.valueOf(amount));
        orderEntityList.setActualAmount(Double.valueOf(amount));
        orderEntityList.setDealFee(0.0);
        orderEntityList.setExternalOrderId("");
        orderEntityList.setCreateTime(null);
        orderEntityList.setSubmitTime(null);
        orderEntityList.setCreatetime(null);
        orderEntityList.setLockWit(0);
        orderEntityList.setFeeId(rate.getId().intValue());
        return toAjax(alipayDealOrderEntityService.insertAlipayDealOrderEntity(orderEntityList));
    }

    String findOderId(String orderId) {
        AlipayDealOrderEntity orderByOrderId = alipayDealOrderEntityService.findOrderByOrderId(orderId);
        if (null != orderByOrderId) {
            orderId += "_1";
            return findOderId(orderId);
        }
        return orderId;

    }

    /**
     * 导出交易订单列表
     */
    @Log(title = "码商交易订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayDealOrderEntity alipayDealOrderEntity) {
        List<AlipayDealOrderEntity> list = alipayDealOrderEntityService
                .selectAlipayDealOrderEntityList(alipayDealOrderEntity);
        ExcelUtil<AlipayDealOrderEntity> util = new ExcelUtil<AlipayDealOrderEntity>(AlipayDealOrderEntity.class);
        return util.exportExcel(list, "orderDeal");
    }

    /**
     * 显示交易订单详情
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayDealOrderEntity alipayDealOrderEntity = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(id);
        String orderType = alipayDealOrderEntity.getOrderType();
        if ("4".equals(orderType)) {
            AlipayWithdrawEntity witOrder = iAlipayWithdrawEntityService.findWitOrder(alipayDealOrderEntity.getAssociatedId());
            mmap.put("wit", witOrder);
        }
        mmap.put("alipayDealOrderEntity", alipayDealOrderEntity);
        return prefix + "/edit";
    }

    /**
     * 显示交易订单详情
     */
    @GetMapping("/backOrder/{id}")
    public String backOrder(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayDealOrderEntity alipayDealOrderEntity = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(id);
//        String orderType = alipayDealOrderEntity.getOrderType();
//        if ("4".equals(orderType)) {
//            AlipayWithdrawEntity witOrder = iAlipayWithdrawEntityService.findWitOrder(alipayDealOrderEntity.getAssociatedId());
//            mmap.put("wit", witOrder);
//        }
        mmap.put("alipayDealOrderEntity", alipayDealOrderEntity);
        return prefix + "/backOrder";
    }

    /**
     * 显示交易订单详情
     */
    @PostMapping("/backOrder")
    public AjaxResult backOrderSave(AlipayDealOrderEntity alipayDealOrderEntity) {
        int i = 0;
        try {
                AlipayDealOrderEntity dataOrigin = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(alipayDealOrderEntity.getId());
                if (dataOrigin == null) {
                    AjaxResult.error();
                }
                logger.info("【当前操作为补单操作，操作备注为：" + alipayDealOrderEntity.getDealDescribe() + "，操作订单号为：" + dataOrigin.getOrderId() + "】");
                AlipayDealOrderApp alipayDealOrderApp = new AlipayDealOrderApp();
                alipayDealOrderApp.setOrderId(dataOrigin.getAssociatedId());
                AlipayDealOrderApp data = alipayDealOrderAppService.selectAlipayDealOrderApp(alipayDealOrderApp);
                if (data == null) {
                    AjaxResult.error();
                }
                data.setSubmitTime(new Date());
                data.setCreateTime(new Date());
                data.setOrderId(data.getOrderId() + "_1");
                dataOrigin.setExternalOrderId(dataOrigin.getOrderId());
                data.setAppOrderId(data.getAppOrderId() + "_1");
                dataOrigin.setOrderId(dataOrigin.getOrderId() + "_1");
                dataOrigin.setAssociatedId(dataOrigin.getAssociatedId() + "_1");
                dataOrigin.setSubmitTime(new Date());
                dataOrigin.setCreateTime(new Date());
                //更新金额
                dataOrigin.setDealAmount(alipayDealOrderEntity.getDealAmount());
                dataOrigin.setActualAmount(alipayDealOrderEntity.getDealAmount());
                //获取费率
                String product = dataOrigin.getRetain1();//产品类型
                String orderQrUser = dataOrigin.getOrderQrUser();//渠道类型    这个渠道类型 要划分为  三方渠道类型和 四方渠道类型 ， 他们走的  费率关系不一样  在不通的地方核算成本
                AlipayUserInfo userInfo = new AlipayUserInfo();
                userInfo.setUserId(orderQrUser);
                List<AlipayUserInfo> alipayUserInfos = alipayUserInfoService.selectAlipayUserInfoList(userInfo);
                AlipayUserInfo first = CollUtil.getFirst(alipayUserInfos);
                data.setAppOrderId(data.getAppOrderId() + "_1");
                AlipayUserRateEntity userRate = alipayUserRateEntityService.findUserByChannel(dataOrigin.getOrderAccount(), product, orderQrUser);//商户费率
                String channel = "";
                Double fee = userRate.getFee();//当前用户交易费率
                // 1   商户  2 卡商   3 渠道
                if( 2   == first.getUserType()    ){
                    AlipayUserRateEntity bankcardRate =   alipayUserRateEntityService.findBankcardRate(orderQrUser, product,2);//卡商费率
                      channel = bankcardRate.getFee()+"";
                }else if(3 == first.getUserType()  ){
                    AlipayChanelFee channelFee = alipayChanelFeeService.findChannelBy(orderQrUser, product);//渠道费率
                    channel = channelFee.getChannelRFee()+"";
                }
                Double channelDFee = Double.valueOf(channel);
                //用户当前手续费为
                double dealFee = fee * dataOrigin.getDealAmount();
                double feec = channelDFee * dataOrigin.getDealAmount();
                dataOrigin.setDealFee(dealFee);
                dataOrigin.setRetain3(feec + "");
                data.setRetain3(dealFee + "");
                String orderQr = dataOrigin.getOrderQr();
                if (StrUtil.isEmpty(orderQr)) {
                    orderQr = "";
                }
                dataOrigin.setOrderStatus("7");
                data.setOrderStatus("1");
                dataOrigin.setRetain4("1");//结算标记
                data.setOrderAmount(dataOrigin.getDealAmount());
                dataOrigin.setActualAmount(dataOrigin.getDealAmount() - dataOrigin.getDealFee());
                dataOrigin.setOrderQr(orderQr + "【操作备注：】" + alipayDealOrderEntity.getDealDescribe());
                return toAjax(alipayDealOrderEntityService.insertAlipayDealOrderEntity(dataOrigin, data));

        } catch (Exception e) {
            e.printStackTrace();
            i = -1;
        }

        return i == 1 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 显示二维码
     *
     * @param imgId
     * @param mmap
     * @return
     */
    @Log(title = "查看交易凭证", businessType = BusinessType.INSERT)
    @GetMapping("/showCode/{imgId}")
    public String showCode(@PathVariable("imgId") String imgId, ModelMap mmap) {
        //获取二维码服务器地址
        String qrServerAddr = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_VALUE);
        String qrServerPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_PATH);
        mmap.put("imgUrl", qrServerAddr + qrServerPath + imgId);
        return code_prefix + "/view_code";
    }


    @Log(title = "交易订单", businessType = BusinessType.UPDATE)
    @PostMapping("/renotify")
    @ResponseBody
    public AjaxResult renotify(AlipayDealOrderEntity alipayDealOrderEntity) {
        //调用通知方法
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_8);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("orderId", alipayDealOrderEntity.getOrderId());
        return HttpUtils.adminGet2Gateway(mapParam, ipPort + urlPath);
    }

    /**
     * 显示统计table
     */
    @GetMapping("/statistics/qr/table")
    public String showTable() {
        return prefix + "/currentTable";
    }

    /**
     * 显示具体统计内容
     */
    @PostMapping("/statistics/qr/order")
    @RequiresPermissions("orderDeal:qr:statistics")
    @ResponseBody
    public TableDataInfo dayStat(StatisticsEntity statisticsEntity) {
        startPage();
        List<StatisticsEntity> list = alipayDealOrderEntityService.selectStatisticsDataByDate(statisticsEntity, DateUtils.dayStart(), DateUtils.dayEnd());
        List<AlipayUserFundEntity> listFund = alipayUserFundEntityService.findUserFundAll();
        ConcurrentHashMap<String, AlipayUserFundEntity> userCollect = listFund.stream().collect(Collectors.toConcurrentMap(AlipayUserFundEntity::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        for (StatisticsEntity sta : list) {
            if (ObjectUtil.isNotNull(userCollect.get(sta.getUserId()))) {
                sta.setUserName(userCollect.get(sta.getUserId()).getUserName());
            }
        }
        return getDataTable(list);
    }


    @Log(title = "客服审核确认出款", businessType = BusinessType.UPDATE)
    @PostMapping("/enterPay")
    @ResponseBody
    public AjaxResult enterPay(AlipayDealOrderEntity alipayDealOrderEntity) {
        alipayDealOrderEntity.setEnterPay("1");
        alipayDealOrderEntity.setEnterPayTime(new Date());
        return toAjax(alipayDealOrderEntityService.updateAlipayDealOrderEntity(alipayDealOrderEntity));
    }

    @Log(title = "解锁出款订单", businessType = BusinessType.UPDATE)
    @PostMapping("/unLockPay")
    @ResponseBody
    public AjaxResult unLockPay(AlipayDealOrderEntity alipayDealOrderEntity) {
        return toAjax(alipayDealOrderEntityService.updateUnLock(alipayDealOrderEntity.getId()));
    }


}
