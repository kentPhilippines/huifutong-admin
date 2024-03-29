package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayDealOrderApp;
import com.ruoyi.alipay.domain.AlipayDealOrderEntity;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.system.domain.SysUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 交易订单Service接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface IAlipayDealOrderEntityService {
    /**
     * 查询交易订单
     *
     * @param id 交易订单ID
     * @return 交易订单
     */
    AlipayDealOrderEntity selectAlipayDealOrderEntityById(Long id);
     int  insertAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity);
     int  insertAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity, AlipayDealOrderApp alipayDealOrderApp);
    /**
     * 查询交易订单列表
     *
     * @param alipayDealOrderEntity 交易订单
     * @param isCharen
     * @return 交易订单集合
     */
    List<AlipayDealOrderEntity> selectAlipayDealOrderEntityList(AlipayDealOrderEntity alipayDealOrderEntity, Boolean isCharen);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayDealOrderEntity> selectUrgeOrders();

    void fillRiskReasonForWithdrwal(List<AlipayDealOrderEntity> alipayDealOrder);

    /**
     * 根据财务角色查看财务角色特定的  交易订单
     *
     * @param alipayDealOrderEntity 交易订单
     * @return 交易订单集合
     */
    List<AlipayDealOrderEntity> selectAlipayOrderList(AlipayDealOrderEntity alipayDealOrderEntity);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    AlipayDealOrderEntity selectAlipayDealOrderEntityByOrderId(String orderId);

    /**
     * 修改交易订单
     *
     * @param alipayDealOrderEntity 交易订单
     * @return 结果
     */
    int updateAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity);


    AjaxResult urgeOrder(AlipayDealOrderEntity alipayDealOrderEntity, SysUser currentUser, String url);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Transactional
    AjaxResult cancelUrgeOrder(AlipayDealOrderEntity alipayDealOrderEntity, SysUser currentUser, String url);

    int updateAlipayDealOrderEntityByOrder(AlipayDealOrderEntity alipayDealOrderEntity);

    /**
     * 根据时间查询统计数据
     *
     * @param dayStart 开始时间
     * @param dayEnd   结束时间
     * @return 返回结果
     */
    List<StatisticsEntity> selectStatisticsDataByDate(StatisticsEntity statisticsEntity, String dayStart, String dayEnd);

    List<StatisticsEntity> selectStatisticsDataByHours(StatisticsEntity statisticsEntity, String dayStart, String dayEnd);


    List<AlipayDealOrderEntity> findOneHoursOrderBySuccess(String strTime, String endTime);

    List<AlipayDealOrderEntity> findOrderLimit(String starTime, String endTime, Integer page, Integer size);


    /**
     * 修改订单渠道方，并将当前渠道方结算方式做出修改
     *
     * @param orderId
     * @param userId
     * @param orderQr
     * @param id
     * @param fee
     * @param profit
     * @return
     */
    int updateOrderQr(String orderId, String userId, String orderQr, Long id, Double fee, Double profit);


    AlipayDealOrderEntity findOrderByOrderId(String order);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    AlipayDealOrderEntity findOrderByAssociatedId(String order);

    AlipayDealOrderEntity selectAlipayDealOrderEntityListSum(AlipayDealOrderEntity alipayDealOrderEntity, Boolean flag );

    /**
     * 修改原订单金额
     * @param nowAmount     当前金额
     * @param orderId       订单号
     * @param id            费率id
     * @param fee           手续费
     * @param profit        利润
     * @return
     */
    int updateAmountOrder(Double nowAmount, String orderId, Double fee, Double profit);


    /**
     * 解锁
     * @param id
     * @return
     */
    int updateUnLock(Long id);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    int updateLockWitToAuto(Long id);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    int updateLockWitToManual(Long id);

    List<AlipayDealOrderEntity> getSumAmountOfPendingWithdral();

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayDealOrderEntity> getSumAmountOfPendingWithdralGroupByQrUser();
}
