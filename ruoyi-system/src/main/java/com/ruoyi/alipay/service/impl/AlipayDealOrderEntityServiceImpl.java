package com.ruoyi.alipay.service.impl;

import com.google.common.collect.Lists;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.mapper.AlipayDealOrderAppMapper;
import com.ruoyi.alipay.mapper.AlipayDealOrderEntityMapper;
import com.ruoyi.alipay.service.IAlipayBankListEntityService;
import com.ruoyi.alipay.service.IAlipayDealOrderEntityService;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.alipay.service.IAlipayWithdrawEntityService;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.enums.DataSourceType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易订单Service业务层处理
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Service
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
        if (isCharen) {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityListIsCharen(alipayDealOrderEntity);
        } else {
            return alipayDealOrderEntityMapper.selectAlipayDealOrderEntityList(alipayDealOrderEntity);
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
        if(CollectionUtils.isEmpty(orderIds))
        {
            return;
        }
        List<AlipayWithdrawEntity> witOrders = iAlipayWithdrawEntityService.selectAlipayWithdrawEntityByIds(orderIds);//出款订单 入款人


        alipayDealOrders.stream().forEach(alipayDealOrder -> {
            List<String> reasonList = Lists.newArrayList();
            //条件1  1.出款卡 和入款卡的姓名,卡号匹配
            AlipayWithdrawEntity withdrawEntity = witOrders.stream().filter(witOrder -> alipayDealOrder.getAssociatedId().equals(witOrder.getOrderId())).findFirst().get();
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
            List<AlipayMediumEntity> allMediums = iAlipayMediumEntityService.selectAll();
            List<AlipayBankListEntity> banks = iAlipayBankListEntityService.selectAll();
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
            alipayDealOrder.setRiskReason(reasonList.stream().collect(Collectors.joining("-","","")));
        });

    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayDealOrderEntity> selectAlipayOrderList(AlipayDealOrderEntity alipayDealOrderEntity) {
        return alipayDealOrderEntityMapper.selectAlipayOrderList(alipayDealOrderEntity);
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

}
