package com.ruoyi.web.controller.alipay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.Constants;
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
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
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

    @Autowired
    private IBankInfoSplitResultService bankInfoSplitResultService;

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
        //获取二维码服务器地址
        String qrServerAddr = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_VALUE);
        String qrServerPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_PATH);
        mmap.put("imgUrl", qrServerAddr + qrServerPath );
        return prefix + "/orderDeal";
    }

    @GetMapping("/Wit")
    @RequiresPermissions("orderDeal:qr:view")
    public String orderDealWit(ModelMap mmap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("productList", list);
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();
        return prefix + "/orderDealWit";
    }
    @GetMapping("/WitOrder")
    @RequiresPermissions("orderDeal:qr:view")
    public String orderDealWitOrder(ModelMap mmap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("productList", list);
        List<AlipayUserFundEntity> rateList = alipayUserFundEntityService.findUserFundRate();
        return prefix + "/merchant_withdrawal";
    }

    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;

    /**
     * 查询交易订单列表
     */
    @PostMapping("/list")
    @RequiresPermissions("orderDeal:qr:list")
    @ResponseBody
    public TableDataInfo list(AlipayDealOrderEntity alipayDealOrderEntity,Boolean isCharen) {
        if(null == isCharen){
            isCharen = Boolean.FALSE;
        }
        if (StrUtil.isNotEmpty(alipayDealOrderEntity.getOrderQrUser1())) {
            alipayDealOrderEntity.setOrderQrUser(alipayDealOrderEntity.getOrderQrUser1());
        }

   //     mmap.put("imgUrl", qrServerAddr + qrServerPath + imgId);
        startPage1();
        List<AlipayDealOrderEntity> list = alipayDealOrderEntityService
                .selectAlipayDealOrderEntityList(alipayDealOrderEntity,isCharen);
        //出款操作 做风控判断 填充  riskReason  start
        if(alipayDealOrderEntity.getOrderType().equals("4")) {
            alipayDealOrderEntityService.fillRiskReasonForWithdrwal(list);
        }
        //做风控判断 填充  riskReason  end

        //支付宝扫码 关联查询payinfo from medium start
        List<String> mediumIds = list.stream().filter(order->order.getRetain1().contains("ALIPAY")).map(AlipayDealOrderEntity::getOrderQr).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(mediumIds)) {
            List<AlipayMediumEntity> mediumEntities = alipayMediumEntityService.selectByMediumIds(mediumIds);
            list.stream().filter(order -> order.getRetain1().contains("ALIPAY")).forEach(order -> {
                try {
                    AlipayMediumEntity mediumEntity = mediumEntities.stream().filter(medium -> medium.getMediumId().equals(order.getOrderQr())).findFirst().get();
                    order.setPayInfoForImgUrl(mediumEntity.getPayInfo());
                    order.setOrderQr(mediumEntity.getMediumNumber()+":"+mediumEntity.getMediumHolder()+":"+mediumEntity.getMediumPhone());
                }catch (Throwable t ){

                }
            });
        }
        //支付宝扫码 关联查询payinfo from medium end
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

        Map<String, AlipayWithdrawEntity> dataMap = new HashMap<>();
        List<String> orderIds = list.stream().map(AlipayDealOrderEntity::getAssociatedId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(orderIds)&&"4".equals(alipayDealOrderEntity.getOrderType())) {
            List<AlipayWithdrawEntity> alipayWithdrawEntities = iAlipayWithdrawEntityService.selectAlipayWithdrawEntityByIds(orderIds);
            if (CollUtil.isNotEmpty(alipayWithdrawEntities)) {
                dataMap = alipayWithdrawEntities.stream().collect(Collectors.toMap(AlipayWithdrawEntity::getOrderId, alipayWithdrawEntity -> alipayWithdrawEntity));
            }
        }
        String qrServerAddr = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_VALUE);
        String qrServerPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_KEY, StaticConstants.ALIPAY_QR_CODE_SERVER_ADDR_PATH);
        for (AlipayDealOrderEntity order : list) {
            if (StrUtil.isEmpty(order.getOrderQrUser())) {
                continue;
            }
            if (ObjectUtil.isNotNull(userCollect1.get(order.getOrderQrUser()))) {
                order.setChannelName(userCollect1.get(order.getOrderQrUser()).getUserName());
            }
            if (CollUtil.isNotEmpty(orderIds)&&"4".equals(alipayDealOrderEntity.getOrderType())) {

                String payImg = order.getPayImg();

                order.setPayImg( qrServerAddr + qrServerPath + payImg);


                AlipayWithdrawEntity alipayWithdrawEntity = dataMap.get(order.getAssociatedId());
                order.setBankAccount("入款信息："+alipayWithdrawEntity.getBankName() + ":"+alipayWithdrawEntity.getAccname()+":"+alipayWithdrawEntity.getBankNo() +" 金额 ："+alipayWithdrawEntity.getAmount());
                order.setOrderNo(alipayWithdrawEntity.getOrderId());
            }
            AlipayProductEntity product = prCollect.get(order.getRetain1());
            if (ObjectUtil.isNotNull(product)) {
                order.setRetain1(product.getProductName());
            }
            if (ObjectUtil.isNotNull(userCollect.get(order.getOrderAccount()))) {
                order.setUserName(userCollect.get(order.getOrderAccount()).getUserName());
            }
        }
        userCollect = null;
        AlipayDealOrderEntity deal = alipayDealOrderEntityService.selectAlipayDealOrderEntityListSum(alipayDealOrderEntity,isCharen);
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

    @PostMapping("/updataOrderback")
    @RequiresPermissions("orderDeal:qr:status")
    @ResponseBody
    @RepeatSubmit
    @Log(title = "卡商代付订单回滚", businessType = BusinessType.UPDATE)
    public AjaxResult updataOrderback(String id) {
        AlipayDealOrderEntity order = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(Long.valueOf(id));
        String loginName =
                ShiroUtils.getLoginName();
        String status = order.getOrderStatus();
        SysUser currentUser = ShiroUtils.getSysUser();
        Long userId = currentUser.getUserId();
        @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符") String userName = currentUser.getUserName();
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        if (!"2".equals(status)) {
            return AjaxResult.error("当前订单状态不允许修改");
        }
        mapParam.put("orderId", order.getOrderId());
        mapParam.put("userName", userName);
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_9);
        AjaxResult post = post(ipPort + urlPath, mapParam);
        return post;
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
    @RepeatSubmit
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
                if ((o1.getTodayDealAmount() - o1.getTodayOtherWitAmount()) >= (o2.getTodayDealAmount() - o2.getTodayOtherWitAmount())) {
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
        String operater = order.getOperater();
        if(StrUtil.isNotEmpty(operater) && !ShiroUtils.getLoginName().equals(operater)){
            mmap.put("errorMessage", "请与第一切款人联系完成切款，当前账号暂无权限");
            return prefix + "/business";
        }
        if (1 == order.getLockWit()) {
            Date lockWitTime = order.getLockWitTime();
            if (DateUtil.isExpired(lockWitTime, DateField.SECOND, Integer.valueOf(600), new Date())) {
                mmap.put("errorMessage", "当前订单已锁定，请解锁后重新配置");
                return prefix + "/business";
            }
        }
        mmap.put("listFund", list);
        mmap.put("orderId", orderId);
/*        AlipayDealOrderEntity alipayDealOrderEntity = new AlipayDealOrderEntity();
        alipayDealOrderEntity.setOperater(ShiroUtils.getLoginName());
        alipayDealOrderEntity.setRecordType("2");
        alipayDealOrderEntity.setOrderId(orderId);
        alipayDealOrderEntityService.updateAlipayDealOrderEntityByOrder(alipayDealOrderEntity);*/
        return prefix + "/updateBankCardEdit";
    }

    @Autowired
    private IAlipayMediumEntityService alipayMediumEntityService;
    private static final String MARK = ":";

    /**
     * 代付主交易订单修改卡商账户
     */
    @GetMapping("/updateBankCard")
    @RequiresPermissions("orderDeal:qr:status:updateBankCard")
    @ResponseBody
    @RepeatSubmit
    @Log(title = "确认拆单", businessType = BusinessType.INSERT)
    public AjaxResult updateBankCard1(
            String orderId, String qrcodeId, String mediumNumber
    ) {
        if (StrUtil.isEmpty(mediumNumber)) {
            return error("请选择银行卡");
        }

        String orderBankOld = "";
        String orderBankNew = "";
        String orderIdOld = "";
        AlipayDealOrderEntity orderEntityList = new AlipayDealOrderEntity();
        int i = 0;
        try {
            orderEntityList = alipayDealOrderEntityService.findOrderByOrderId(orderId);//原交易订单， 修改银行卡后 原银行卡资金退回
            Integer grabOrder = orderEntityList.getGrabOrder();
            Integer lockWit = orderEntityList.getLockWit();
            if(grabOrder == 1 ){
                return error("当前订单状态错误，请让卡商放弃出款");
            }
            String operater = orderEntityList.getOperater();
            if(StrUtil.isNotEmpty(operater) && !ShiroUtils.getLoginName().equals(operater)){
                return error("请与第一切款人联系完成切款，当前账号暂无权限");
            }
            if(grabOrder == 1 ){
                return error("当前订单状态错误，请让卡商放弃出款");
            }
            if(lockWit == 1 ){
                return error("当前订单正在处理中，请核实");
            }
            orderIdOld = orderId;
            orderBankOld = orderEntityList.getOrderQrUser();
            orderEntityList = alipayDealOrderEntityService.findOrderByOrderId(orderId);
            AlipayUserRateEntity rate = iAlipayUserRateEntityService.findWitRate(qrcodeId);
            Double fee = rate.getFee();
            Double dealAmount = orderEntityList.getDealAmount();
            fee = fee * dealAmount;
            Double profit = Double.valueOf(orderEntityList.getRetain3());
            profit = Double.valueOf(orderEntityList.getDealFee()) - fee;
            String bankInfo = "";
            if (StrUtil.isNotEmpty(mediumNumber)) {
                AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
                alipayMediumEntity.setMediumNumber(mediumNumber);
                List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
                if (CollUtil.isNotEmpty(list)) {
                    AlipayMediumEntity first = CollUtil.getFirst(list);
                    String mediumHolder = first.getMediumHolder();//开户人
                    String account = first.getAccount();//开户行
                    String mediumNumber1 = first.getMediumNumber();//银行卡号
                    String mediumPhone = first.getMediumPhone();
                    bankInfo = account + MARK + mediumHolder + MARK + mediumNumber1 + MARK + "电话" + MARK + mediumPhone;
                }
            }
            i = alipayDealOrderEntityService.updateOrderQr(orderId, qrcodeId, bankInfo, rate.getId(), fee, profit);
        } catch (Throwable t) {
            i = 0;
        }
        if (i == 1) {
            AlipayDealOrderEntity alipayDealOrderEntity = new AlipayDealOrderEntity();
            alipayDealOrderEntity.setOperater(ShiroUtils.getLoginName());
            alipayDealOrderEntity.setRecordType("2");
            alipayDealOrderEntity.setOrderId(orderId);
            alipayDealOrderEntityService.updateAlipayDealOrderEntityByOrder(alipayDealOrderEntity);
            updateBankAmount(orderIdOld, orderBankOld, "sub", ShiroUtils.getLoginName(), orderEntityList.getDealAmount().toString());
            updateBankAmount(orderIdOld, orderBankNew, "add", ShiroUtils.getLoginName(), orderEntityList.getDealAmount().toString());


            AlipayExceptionOrder exOrder = new AlipayExceptionOrder();
            exOrder.setOrderId(orderIdOld);
            exOrder.setExceptOrderAmount(orderEntityList.getDealAmount()+"");
            exOrder.setOrderAccount(orderBankOld);
            addEc(exOrder,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服切款订单 切款金额为 ："+orderEntityList.getDealAmount()+"，原金额为："+orderEntityList.getDealAmount()+"，原账户为："+orderBankOld+"，现账户为："+orderBankNew);

            AlipayExceptionOrder exOrder1 = new AlipayExceptionOrder();
            exOrder1.setOrderId(orderEntityList.getOrderId());
            exOrder1.setExceptOrderAmount(orderEntityList.getDealAmount()+"");
            exOrder1.setOrderAccount(orderBankNew);
            addEc(exOrder1,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服切款订单 切款金额为 ："+orderEntityList.getDealAmount()+"，原金额为："+orderEntityList.getDealAmount()+"，原账户为："+orderBankOld+"，现账户为："+orderBankNew);


        }
        return toAjax(i);
    }

    /**
     * 代付主交易订单修改卡商账户
     */
    @GetMapping("/updateBankCardMore")
    @RequiresPermissions("orderDeal:qr:status:updateBankCard")
    @ResponseBody
    @RepeatSubmit
    @Log(title = "确认拆单", businessType = BusinessType.INSERT)
    public AjaxResult updateBankCard(String orderId, String amount, String mediumNumber, String qrcodeId) {
        if (StrUtil.isEmpty(mediumNumber)) {
            return error("请选择银行卡");
        }
        String orderBankOld = "";
        String orderBankNew = "";
        String orderIdOld = "";
        Double dealAmount = 0.0;
        AlipayDealOrderEntity orderEntityList = new AlipayDealOrderEntity();
        int i = 0;
        try {
            orderEntityList = alipayDealOrderEntityService.findOrderByOrderId(orderId);
            Integer grabOrder = orderEntityList.getGrabOrder();
            Integer lockWit = orderEntityList.getLockWit();
            String operater = orderEntityList.getOperater();
            if(StrUtil.isNotEmpty(operater) && !ShiroUtils.getLoginName().equals(operater)){
                return error("请与第一切款人联系完成切款，当前账号暂无权限");
            }
            if(grabOrder == 1 ){
                return error("当前订单状态错误，请让卡商放弃出款");
            }
            if(lockWit == 1 ){
                return error("当前订单正在处理中，请核实");
            }
            orderIdOld = orderId;
            orderBankOld = orderEntityList.getOrderQrUser();
            AlipayUserRateEntity rate = iAlipayUserRateEntityService.findWitRate(qrcodeId);
             dealAmount = orderEntityList.getDealAmount();
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
            orderEntityList.setOrderQrUser(qrcodeId);
            orderEntityList.setDealAmount(Double.valueOf(amount));
            orderEntityList.setActualAmount(Double.valueOf(amount));
            orderEntityList.setDealFee(0.0);
            orderEntityList.setExternalOrderId("");
            orderEntityList.setCreateTime(null);
            orderEntityList.setSubmitTime(null);
            orderEntityList.setCreatetime(null);
            orderEntityList.setLockWit(0);
            orderEntityList.setFeeId(rate.getId().intValue());
            String bankInfo = "";
            if (StrUtil.isNotEmpty(mediumNumber)) {
                AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
                alipayMediumEntity.setMediumNumber(mediumNumber);
                List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
                if (CollUtil.isNotEmpty(list)) {
                    AlipayMediumEntity first = CollUtil.getFirst(list);
                    String mediumHolder = first.getMediumHolder();//开户人
                    String account = first.getAccount();//开户行
                    String mediumNumber1 = first.getMediumNumber();//银行卡号
                    String mediumPhone = first.getMediumPhone();
                    bankInfo = account + MARK + mediumHolder + MARK + mediumNumber1 + MARK + "电话" + MARK + mediumPhone;
                    orderBankNew = bankInfo;
                }
            }
            orderEntityList.setRetain2( rate.getFee() * orderEntityList.getDealAmount() +"");//卡商利润
            orderEntityList.setRetain3(orderEntityList.getDealFee() - (rate.getFee() * orderEntityList.getDealAmount()) +"");
            orderEntityList.setOrderQr(bankInfo);
            orderEntityList.setOperater(ShiroUtils.getLoginName());
            orderEntityList.setRecordType("3");
            orderEntityList.setExternalOrderId(orderEntityList.getExternalOrderId());
            orderEntityList.setAuditLog(ShiroUtils.getLoginName() +"-"+ DateUtil.now()+orderEntityList.getDealAmount() +"-->"+amount +"->"+qrcodeId);
            i = alipayDealOrderEntityService.insertAlipayDealOrderEntity(orderEntityList);






        } catch (Throwable t) {
            i = 0;
        }
        if (i == 1) {
            updateBankAmount(orderIdOld, orderBankOld, "add", ShiroUtils.getLoginName(), amount);//对于老的 老说是增加 代付余额
            updateBankAmount(orderEntityList.getOrderId(), orderBankNew, "sub", ShiroUtils.getLoginName(), amount);






            AlipayExceptionOrder exOrder = new AlipayExceptionOrder();
            exOrder.setOrderId(orderIdOld);
            exOrder.setExceptOrderAmount(amount+"");
            exOrder.setOrderAccount(orderBankOld);
            addEc(exOrder,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服切款订单 切款金额为 ："+amount+"，原金额为："+dealAmount+"，原账户为："+orderBankOld+"，现账户为："+qrcodeId);





            AlipayExceptionOrder exOrder1 = new AlipayExceptionOrder();
            exOrder1.setOrderId(orderEntityList.getOrderId());
            exOrder1.setExceptOrderAmount(amount+"");
            exOrder1.setOrderAccount(qrcodeId);
            addEc(exOrder1,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服切款订单 切款金额为 ："+amount+"，原金额为："+dealAmount+"，原账户为："+orderBankOld+"，现账户为："+qrcodeId);



        }
        return toAjax(i);
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
                .selectAlipayDealOrderEntityList(alipayDealOrderEntity,false);
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
    @GetMapping("/bankData/{orderQr}")
    public String view(@PathVariable("orderQr") String orderQr, ModelMap mmap) {
        String str = "";
        if (orderQr != null) {
            String[] split = orderQr.trim().split(":");
            str = split[2];
        }
        BankInfoSplitEntity bankInfoSplitEntity = new BankInfoSplitEntity();
        bankInfoSplitEntity.setBankId(str);
        List<BankInfoSplitEntity> bankInfoSplitEntitys = bankInfoSplitResultService.selectBankInfoSplitResult(bankInfoSplitEntity);

        mmap.put("bankInfoSplitEntity", bankInfoSplitEntitys);
        mmap.put("bankId", str);
        return prefix + "/bankData";
    }

    public static void main(String[] args) {
        String a = "  银行卡：恒丰银行:苏文荣:6230780100030570866:电话:13062550603";
        String[] split = a.trim().split(":");
        System.out.printf("split");
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
    @ResponseBody
    @RepeatSubmit
    @Log(title = "补单", businessType = BusinessType.INSERT)
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
                return AjaxResult.error();
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
            if (2 == first.getUserType()) {
                AlipayUserRateEntity bankcardRate = alipayUserRateEntityService.findBankcardRate(orderQrUser, product, 2);//卡商费率
                channel = bankcardRate.getFee() + "";
            } else if (3 == first.getUserType()) {
                AlipayChanelFee channelFee = alipayChanelFeeService.findChannelBy(orderQrUser, product);//渠道费率
                channel = channelFee.getChannelRFee() + "";
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
            dataOrigin.setRecordType("1");
            dataOrigin.setOperater(ShiroUtils.getLoginName());
            int i1 = alipayDealOrderEntityService.insertAlipayDealOrderEntity(dataOrigin, data);





            logger.info("插入数据:{}",i1);
            return toAjax(i1);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error();
        }
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



    /**
     * 重置操作人
     */
    @PostMapping("/reset/operater")
    //@RequiresPermissions("orderDeal:qr:resetOperater")
    @ResponseBody
    @Log(title = "重置操作人", businessType = BusinessType.UPDATE)
    public AjaxResult resetOperater( String id) {
        AlipayDealOrderEntity order = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(Long.valueOf(id));
        order.setOperater("");//
        int i = alipayDealOrderEntityService.updateAlipayDealOrderEntityByOrder(order);
        return toAjax(i);
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
        int i = alipayDealOrderEntityService.updateAlipayDealOrderEntity(alipayDealOrderEntity);
        AlipayExceptionOrder exOrder = new AlipayExceptionOrder();
        AlipayDealOrderEntity orderEntity = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(alipayDealOrderEntity.getId());
        exOrder.setOrderId(orderEntity.getOrderId());
        exOrder.setExceptOrderAmount(orderEntity.getDealAmount()+"");
        exOrder.setOrderAccount(orderEntity.getOrderQrUser());
        addEc(exOrder,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服审核确认出款");

        return toAjax(i);
    }

    @Log(title = "解锁出款订单", businessType = BusinessType.UPDATE)
    @PostMapping("/unLockPay")
    @ResponseBody
    public AjaxResult unLockPay(AlipayDealOrderEntity alipayDealOrderEntity) {
        int i = alipayDealOrderEntityService.updateUnLock(alipayDealOrderEntity.getId());
        AlipayExceptionOrder exOrder = new AlipayExceptionOrder();
        AlipayDealOrderEntity orderEntity = alipayDealOrderEntityService.selectAlipayDealOrderEntityById(alipayDealOrderEntity.getId());
        exOrder.setOrderId(orderEntity.getOrderId());
        exOrder.setExceptOrderAmount(orderEntity.getDealAmount()+"");
        exOrder.setOrderAccount(orderEntity.getOrderQrUser());
        addEc(exOrder,ShiroUtils.getLoginName(),ShiroUtils.getIp(),"客服解锁出款订单");
        return toAjax(i);
    }

    void updateBankAmount(String orderId, String bankInfo, String type, String apply, String amount) {
        if (StrUtil.isEmpty(bankInfo) || StrUtil.isEmpty(orderId)) {
            return;
        }
        logger.info("进入银行卡代付余额更新");
        ThreadUtil.execute(() -> {
          /*  //调用通知方法
            //获取alipay处理接口URL
            String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
            String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_10);
            Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
            String[] split = bankInfo.split(":");
            String bankId = split[2];

            mapParam.put("orderId", orderId);
            mapParam.put("apply", apply);
            mapParam.put("bankId", orderId);
            mapParam.put("amount", amount);
            mapParam.put("type", type);
            HttpUtils.adminGet2Gateway(mapParam, ipPort + urlPath);*/
        });
    }
    @Autowired
    private IAlipayExceptionOrderService alipayExceptionOrderService;
    @PostMapping("/childrenOrderList")
    @ResponseBody
    public TableDataInfo childrenOrderList(AlipayExceptionOrder alipayExceptionOrder) {
        startPage1();
        List<AlipayExceptionOrder> alipayExceptionOrders = alipayExceptionOrderService.selectAlipayExceptionOrderListBank(alipayExceptionOrder);
        return getDataTable(alipayExceptionOrders) ;
    }
    @PostMapping("/witOrderList")
    @ResponseBody
    public TableDataInfo witOrderList(AlipayDealOrderEntity alipayDealOrderEntity) {
        alipayDealOrderEntity.setOrderType("4");
          return list(alipayDealOrderEntity,true) ;
    }






   void addEc(AlipayExceptionOrder alipayExceptionOrder,String userId,String ip ,String msg){
          AlipayExceptionOrder exceptionOrder = new AlipayExceptionOrder();
       exceptionOrder.setOrderexceptId(UUID.randomUUID().toString());
       exceptionOrder.setOrderId(alipayExceptionOrder.getOrderId());
       exceptionOrder.setCreateTime(new Date());
       exceptionOrder.setExceptStatus(2);
       exceptionOrder.setExceptType(10);//卡商确认出款
       exceptionOrder.setOperation(userId);
       exceptionOrder.setOrderGenerationip(ip);
       exceptionOrder.setExceptOrderAmount(alipayExceptionOrder.getExceptOrderAmount());
       exceptionOrder.setExplains(msg);
       exceptionOrder.setOrderAccount(alipayExceptionOrder.getOrderAccount());
       alipayExceptionOrderService.insertAlipayExceptionOrder(exceptionOrder);
    }



}
