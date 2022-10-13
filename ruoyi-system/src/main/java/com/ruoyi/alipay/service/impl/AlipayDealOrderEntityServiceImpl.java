package com.ruoyi.alipay.service.impl;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.mapper.AlipayDealOrderAppMapper;
import com.ruoyi.alipay.mapper.AlipayDealOrderEntityMapper;
import com.ruoyi.alipay.service.*;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.enums.DeductStatusEnum;
import com.ruoyi.common.enums.RefundDeductType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ruoyi.common.enums.RefundDeductType.DEDUCT_FREEZE_TYPE;
import static com.ruoyi.common.enums.RefundDeductType.REFUND_FREEZE_TYPE;

/**
 * 交易订单Service业务层处理
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Service
@Slf4j
public class AlipayDealOrderEntityServiceImpl implements IAlipayDealOrderEntityService {
    @Resource
    private AlipayDealOrderEntityMapper alipayDealOrderEntityMapper;
    @Resource
    private AlipayDealOrderAppMapper alipayDealOrderAppMapper;
    @Autowired
    private IAlipayWithdrawEntityService iAlipayWithdrawEntityService;
    @Autowired
    private IAlipayMediumEntityService iAlipayMediumEntityService;
    @Autowired
    private IAlipayBankListEntityService iAlipayBankListEntityService;

    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;

    /**
     * 查询交易订单
     *
     * @param id 交易订单ID
     * @return 交易订单
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayDealOrderEntity selectAlipayDealOrderEntityById(Long id) {
        return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityById(id);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity) {
        return alipayDealOrderEntityMapper.insertAlipayDealOrderEntity(alipayDealOrderEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Transactional
    public int insertAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity, AlipayDealOrderApp alipayDealOrderApp) {
        int i = alipayDealOrderAppMapper.insertAlipayDealOrderApp(alipayDealOrderApp);
        if (i < 1) {
            throw new RuntimeException("商户数据插入失败");
        }
        int i1 = alipayDealOrderEntityMapper.insertAlipayDealOrderEntity(alipayDealOrderEntity);
        if (i1 < 1) {
            throw new RuntimeException("主订单数据插入失败");
        }
        return i1;
    }

    /**
     * 查询交易订单列表
     *
     * @param alipayDealOrderEntity 交易订单
     * @param isCharen
     * @return 交易订单
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> selectAlipayDealOrderEntityList(AlipayDealOrderEntity alipayDealOrderEntity, Boolean isCharen) {
        //this.identifyOrderId(alipayDealOrderEntity);
        if (isCharen) {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityListIsCharen(alipayDealOrderEntity);
        } else {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityList(alipayDealOrderEntity);
        }
    }

    /**
     * 查询催单中的数据
     * @return
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> selectUrgeOrders()
    {
            return alipayDealOrderEntityMapper.selectUrgeOrders();
    }

    /**
     * 
     * BA  和BW  是。主订单字段
     *
     * 
     * ZBW 和 ANO。是 关联订单号查询
     *
     *
     * 其他 就是外部订单查询
     * @param alipayDealOrderEntity
     */
    private void identifyOrderId(AlipayDealOrderEntity alipayDealOrderEntity)
    {
        String orderId =alipayDealOrderEntity.getOrderId();
        if(StringUtils.isEmpty(orderId))
        {
            return;
        }
        if(orderId.startsWith("BA") || orderId.startsWith("BW"))
        {

        }
        else if(orderId.startsWith("ZBW") || orderId.startsWith("ANO"))
        {
            alipayDealOrderEntity.setAssociatedId(orderId);
            alipayDealOrderEntity.setOrderId(null);
        }else{
            alipayDealOrderEntity.setExternalOrderId(orderId);
            alipayDealOrderEntity.setOrderId(null);
        }

    }

    /**
     * 填充风控原因
     *
     * @return
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public void fillRiskReasonForWithdrwal(List<AlipayDealOrderEntity> alipayDealOrders) {

        List orderIds = alipayDealOrders.stream().map(AlipayDealOrderEntity::getAssociatedId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(orderIds)) {
            return;
        }
        List<AlipayWithdrawEntity> witOrders = iAlipayWithdrawEntityService.selectAlipayWithdrawEntityByIds(orderIds);//出款订单 入款人

        List<AlipayMediumEntity> allMediums = iAlipayMediumEntityService.selectAll();
        List<AlipayBankListEntity> banks = iAlipayBankListEntityService.selectAll();
        alipayDealOrders.stream().forEach(alipayDealOrder -> {
            List<String> reasonList = Lists.newArrayList();
            //条件1  1.出款卡 和入款卡的姓名,卡号匹配
            witOrders.stream().filter(witOrder -> alipayDealOrder.getAssociatedId().equals(witOrder.getOrderId())).findFirst().ifPresent(withdrawEntity ->
            {
                if (alipayDealOrder.getOrderQr() != null && alipayDealOrder.getOrderQr().contains(withdrawEntity.getAccname())) {
                    reasonList.add("出款卡和入款卡名字相同");
                }
                if (alipayDealOrder.getOrderQr() != null && alipayDealOrder.getOrderQr().contains(withdrawEntity.getBankNo())) {
                    reasonList.add("出款卡和入款卡卡号相同");
                }
                //条件2.短信和哪个匹配？ 和 出款卡姓名匹配
                if (alipayDealOrder.getPayInfo() != null && alipayDealOrder.getPayInfo().contains(withdrawEntity.getAccname())) {
                    reasonList.add("短信内容包含出款人名字");
                }
                //条件 3.入款卡 卡号或者姓名 和内部卡(medium,bank_list) 匹配

                if (allMediums.stream().anyMatch(alipayMediumEntity ->
                        withdrawEntity.getAccname().equalsIgnoreCase(alipayMediumEntity.getMediumHolder())
                                || withdrawEntity.getBankNo().equalsIgnoreCase(alipayMediumEntity.getMediumNumber()))) {
                    reasonList.add("媒介包含出款人名字或卡号");
                }

                if (banks.stream().anyMatch(alipayBankListEntity ->
                        withdrawEntity.getAccname().equalsIgnoreCase(alipayBankListEntity.getAccountHolder())
                                || withdrawEntity.getBankNo().equalsIgnoreCase(alipayBankListEntity.getBankcardAccount()))) {
                    reasonList.add("banklist包含出款人名字或卡号");
                }
                alipayDealOrder.setRiskReason(reasonList.stream().collect(Collectors.joining("-", "", "")));
            });


        });

    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> selectAlipayOrderList(AlipayDealOrderEntity alipayDealOrderEntity) {
        return alipayDealOrderEntityMapper.selectAlipayOrderList(alipayDealOrderEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayDealOrderEntity selectAlipayDealOrderEntityByOrderId(String orderId) {
        return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityByOrderId(orderId);
    }



    /**
     * 修改交易订单
     *
     * @param alipayDealOrderEntity 交易订单
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity) {
        return alipayDealOrderEntityMapper.updateAlipayDealOrderEntity(alipayDealOrderEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Transactional
    public AjaxResult urgeOrder(AlipayDealOrderEntity alipayDealOrderEntity, SysUser currentUser, String url)
    {
        //userId: tanqiuba
        //amountType: 3
        //accountBalance: 1843947.433
        //amount: 100
        //dealDescribe: test
        AlipayAmountEntity alipayAmountEntity = new AlipayAmountEntity();
        alipayAmountEntity.setUserId(alipayDealOrderEntity.getOrderQrUser());
        alipayAmountEntity.setAmountType(RefundDeductType.DEDUCT_FREEZE_TYPE.getCode()+"");
        alipayAmountEntity.setAmount(alipayDealOrderEntity.getDealAmount());

        // 获取当前的用户
//        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());

        //获取alipay处理接口URL
//        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
//        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_5);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("userId", alipayAmountEntity.getUserId());
        mapParam.put("amount", alipayAmountEntity.getAmount());
        mapParam.put("type", alipayAmountEntity.getType());
        mapParam.put("dealDescribe", "催单冻结，请勿人为操作");
        if (alipayAmountEntity.getAmountType().toString().equals(DEDUCT_FREEZE_TYPE.getCode().toString())) {
            mapParam.put("amountType", DEDUCT_FREEZE_TYPE.getCode());//账户冻结
            AlipayUserFundEntity alipayUserFundByUserId = alipayUserFundEntityService.findAlipayUserFundByUserId(alipayAmountEntity.getUserId());
            Double accountBalance = alipayUserFundByUserId.getAccountBalance();
            if (accountBalance < alipayAmountEntity.getAmount()) {
               throw new BusinessException("当前账户余额不足，不支持账户冻结");
            }
        } else if (alipayAmountEntity.getAmountType().toString().equals(RefundDeductType.DELETE_QUOTA_TYPE.getCode().toString())) {//减少授权额度
            mapParam.put("amountType", RefundDeductType.DELETE_QUOTA_TYPE.getCode());//账户冻结
        } else {
            mapParam.put("amountType", RefundDeductType.DEDUCT_TYPE.getCode());//减款
        }
        mapParam.put("accname", currentUser.getLoginName());//申请人
        mapParam.put("orderStatus", DeductStatusEnum.DEDUCT_STATUS_PROCESS.getCode());
        mapParam.put("orderId", alipayDealOrderEntity.getOrderId());
        log.info("urgeorder :url,{},data,{}",url, JSONUtil.toJsonStr(mapParam));
        AjaxResult result = HttpUtils.adminRequest2Gateway(mapParam, url);
        log.info("urgeorder result:{}",JSONUtil.toJsonStr(result));
        if(result.isSuccess())
        {
            alipayDealOrderEntity.setUrge(1);
            alipayDealOrderEntity.setDealDescribe("催单人:"+currentUser.getLoginName());
//            alipayDealOrderEntity.setOrderStatus("7");
            alipayDealOrderEntityMapper.updateAlipayDealOrderEntity(alipayDealOrderEntity);
        }
        return result;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Transactional
    public AjaxResult cancelUrgeOrder(AlipayDealOrderEntity alipayDealOrderEntity, SysUser currentUser, String url)
    {
        //userId: tanqiuba
        //amountType: 3
        //accountBalance: 1843947.433
        //amount: 100
        //dealDescribe: test
        AlipayAmountEntity alipayAmountEntity = new AlipayAmountEntity();
        alipayAmountEntity.setUserId(alipayDealOrderEntity.getOrderQrUser());
        alipayAmountEntity.setAmountType(REFUND_FREEZE_TYPE.getCode()+"");
        alipayAmountEntity.setAmount(alipayDealOrderEntity.getDealAmount());

        // 获取当前的用户
//        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());

        //获取alipay处理接口URL
//        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
//        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_5);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("userId", alipayAmountEntity.getUserId());
        mapParam.put("amount", alipayAmountEntity.getAmount());
        mapParam.put("type", alipayAmountEntity.getType());
        mapParam.put("dealDescribe", "cancelurgeorder");

        mapParam.put("amountType", REFUND_FREEZE_TYPE.getCode());//账户解开冻结
        mapParam.put("accname", currentUser.getLoginName());//申请人
        mapParam.put("orderStatus", DeductStatusEnum.DEDUCT_STATUS_PROCESS.getCode());
        mapParam.put("orderId", alipayDealOrderEntity.getOrderId());
        AjaxResult result = HttpUtils.adminRequest2Gateway(mapParam, url);
        if(result.isSuccess())
        {
            alipayDealOrderEntity.setUrge(0);
//            alipayDealOrderEntity.setOrderStatus("7");
            alipayDealOrderEntityMapper.updateAlipayDealOrderEntity(alipayDealOrderEntity);
        }
        return result;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayDealOrderEntityByOrder(AlipayDealOrderEntity alipayDealOrderEntity) {
        return alipayDealOrderEntityMapper.updateAlipayDealOrderEntityByOrder(alipayDealOrderEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<StatisticsEntity> selectStatisticsDataByDate(StatisticsEntity statisticsEntity, String dayStart, String dayEnd) {
        return alipayDealOrderEntityMapper.selectStatDateByDay(statisticsEntity, dayStart, dayEnd);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<StatisticsEntity> selectStatisticsDataByHours(StatisticsEntity statisticsEntity, String dayStart, String dayEnd) {
        return alipayDealOrderEntityMapper.selectStatDateByHours(statisticsEntity, dayStart, dayEnd);
    }

    @Override
    public List<AlipayDealOrderEntity> findOneHoursOrderBySuccess(String strTime, String endTime) {
        return null;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> findOrderLimit(String starTime, String endTime, Integer page, Integer size) {
        return alipayDealOrderEntityMapper.findOrderLimit(starTime, endTime, page, size);
    }

    /**
     * 临时修改订单渠道方，以及渠道方结算
     *
     * @param orderId
     * @param userId
     * @param orderQr
     * @param fee
     * @param profit
     * @return
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateOrderQr(String orderId, String userId, String orderQr, Long feeId, Double fee, Double profit) {
        return alipayDealOrderEntityMapper.updateOrderQr(orderId, userId, orderQr, feeId, fee, profit);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayDealOrderEntity findOrderByOrderId(String order) {
        return alipayDealOrderEntityMapper.findOrderByOrderId(order);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayDealOrderEntity selectAlipayDealOrderEntityListSum(AlipayDealOrderEntity alipayDealOrderEntity, Boolean flag) {


        if (flag) {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityListSumCharen(alipayDealOrderEntity);
        } else {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityListSum(alipayDealOrderEntity);

        }
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAmountOrder(Double nowAmount, String orderId, Double fee, Double profit) {
        return alipayDealOrderEntityMapper.updateAmountOrder(nowAmount, orderId, fee, profit);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateUnLock(Long id) {
        return alipayDealOrderEntityMapper.updateUnLock(id);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateLockWitToAuto(Long id) {
        return alipayDealOrderEntityMapper.updateLockWitToAuto(id);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateLockWitToManual(Long id) {
        return alipayDealOrderEntityMapper.updateLockWitToManual(id);
    }


    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> getSumAmountOfPendingWithdral() {
        return alipayDealOrderEntityMapper.getSumAmountOfPendingWithdral();
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> getSumAmountOfPendingWithdralGroupByQrUser() {
        return alipayDealOrderEntityMapper.getSumAmountOfPendingWithdralGroupByQrUser();
    }

}
