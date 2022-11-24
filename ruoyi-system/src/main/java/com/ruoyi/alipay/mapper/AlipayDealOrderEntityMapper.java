package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.AlipayDealOrderEntity;
import com.ruoyi.common.core.domain.StatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 交易订单Mapper接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface AlipayDealOrderEntityMapper {
    /**
     * 查询交易订单
     *
     * @param id 交易订单ID
     * @return 交易订单
     */
    AlipayDealOrderEntity selectAlipayDealOrderEntityById(Long id);

    /**
     * 查询交易订单列表
     *
     * @param alipayDealOrderEntity 交易订单
     * @return 交易订单集合
     */
    List<AlipayDealOrderEntity> selectAlipayDealOrderEntityList(AlipayDealOrderEntity alipayDealOrderEntity);

    List<AlipayDealOrderEntity> selectAlipayOrderList(AlipayDealOrderEntity alipayDealOrderEntity);
    List<AlipayDealOrderEntity> selectUrgeOrders();
    AlipayDealOrderEntity selectAlipayDealOrderEntityByOrderId(String orderId);

    /**
     * 新增拆单
     * @param alipayDealOrderEntity 交易订单
     * @return 结果
     */
    int updateAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity);
    int updateAlipayDealOrderEntityByOrder(AlipayDealOrderEntity alipayDealOrderEntity);
    int insertAlipayDealOrderEntity(AlipayDealOrderEntity alipayDealOrderEntity);

    @Select("<script>" +
            "select '所有' userId, 'USDT' productName, " +
            "coalesce(sum(dealAmount),0) totalAmount," +
            "coalesce(sum(case orderStatus when 2 then dealAmount else 0 end),0) successAmount," +
            "coalesce(sum(case orderStatus when 2 then retain3 else 0 end),0) profit," +
            "coalesce(sum(case orderStatus when 2 then retain2 else 0 end),0) grossCost," +
            "count(*) totalCount," +
            "count(case orderStatus when 2 then id else null end) successCount " +
            "from alipay_deal_order where createTime between #{statisticsEntity.params.dayStart}" +
            " and #{statisticsEntity.params.dayEnd} and ( orderType = 1  or orderType = 4 )" +
            "and currency = 'USDT' " +
            " union all " +
            "select '所有' userId, 'CNY' productName, " +
            "coalesce(sum(dealAmount),0) totalAmount," +
            "coalesce(sum(case orderStatus when 2 then dealAmount else 0 end),0) successAmount," +
            "coalesce(sum(case orderStatus when 2 then retain3 else 0 end),0) profit," +
            "coalesce(sum(case orderStatus when 2 then retain2 else 0 end),0) grossCost," +
            "count(*) totalCount," +
            "count(case orderStatus when 2 then id else null end) successCount " +
            "from alipay_deal_order where createTime between #{statisticsEntity.params.dayStart}" +
            " and #{statisticsEntity.params.dayEnd} and ( orderType = 1  or orderType = 4 ) " +
            "and currency = 'CNY' " +
            " union all " +
            "select o.orderQrUser userId, p.productName ," +
            "coalesce(sum(dealAmount),0.00) totalAmount," +
            "coalesce(sum(case orderStatus when 2 then dealAmount else 0 end),0) successAmount," +
            "coalesce(sum(case orderStatus when 2 then o.retain3 else 0 end),0) profit," +
            "coalesce(sum(case orderStatus when 2 then o.retain2 else 0 end),0) grossCost," +
            "count(*) totalCount," +
            "count(case orderStatus when 2 then o.id else null end) successCount " +
            "from alipay_deal_order o left join alipay_product p on o.retain1 = p.productId " +
            "where o.createTime between #{statisticsEntity.params.dayStart} and #{statisticsEntity.params.dayEnd} and (orderType = 1 or orderType = 4 ) " +
            "<if test = \"statisticsEntity.userId != null and statisticsEntity.userId != ''\">" +
            "and o.orderQrUser = #{statisticsEntity.userId} " +
            "</if>" +
            "<if test = \"statisticsEntity.currency != null and statisticsEntity.currency != ''\">" +
            "and o.currency = #{statisticsEntity.currency} " +
            "</if>" +
            "group by o.orderQrUser, o.retain1 " +
            "</script>")
    List<StatisticsEntity> selectStatDateByDay(@Param("statisticsEntity") StatisticsEntity statisticsEntity, @Param("dayStart") String dayStart, @Param("dayEnd") String dayEnd);


    @Select("<script>" +
            "select o.orderQrUser userId, p.productName ," +
            "coalesce(sum(dealAmount),0.00) totalAmount," +
            "coalesce(sum(case orderStatus when 2 then dealAmount else 0 end),0) successAmount," +
            "coalesce(sum(case orderStatus when 2 then o.retain3 else 0 end),0) profit," +
            "count(*) totalCount," +
            "count(case orderStatus when 2 then o.id else null end) successCount ," +
            "DATE_FORMAT(o.createTime,'%Y%m%d%H') time " +
            "from alipay_deal_order o left join alipay_product p on o.retain1 = p.productId " +
            "where o.createTime between #{statisticsEntity.params.dayStart} and #{statisticsEntity.params.dayEnd} and orderType = 1 " +
            "<if test = \"statisticsEntity.userId != null and statisticsEntity.userId != ''\">" +
            "and o.orderQrUser = #{statisticsEntity.userId} " +
            "</if>" +
            "<if test = \"statisticsEntity.currency != null and statisticsEntity.currency != ''\">" +
            "and o.currency = #{statisticsEntity.currency} " +
            "</if>" +
            "group by o.orderQrUser, o.retain1 , time" +
            "</script>")
    List<StatisticsEntity> selectStatDateByHours(@Param("statisticsEntity") StatisticsEntity statisticsEntity, @Param("dayStart") String dayStart, @Param("dayEnd") String dayEnd);


    /**
     * 分页查询条件
     *
     * @param starTime
     * @param endTime
     * @param page
     * @param size
     * @return
     */
    @Select("select * from alipay_deal_order where createTime between #{starTime} and #{endTime}  limit #{page} , #{size}")
    List<AlipayDealOrderEntity> findOrderLimit(@Param("starTime") String starTime, @Param("endTime") String endTime, @Param("page") Integer page, @Param("size") Integer size);

    @Select("select orderQr,SUM(dealAmount) dealAmount  from alipay_deal_order ado where orderType =4 and orderStatus =1 group by orderQr")
    List<AlipayDealOrderEntity> getSumAmountOfPendingWithdral();

    @Select("select orderQrUser,SUM(dealAmount) dealAmount  from alipay_deal_order ado where orderType =4 and orderStatus =1 group by orderQrUser")
    List<AlipayDealOrderEntity> getSumAmountOfPendingWithdralGroupByQrUser();


    @Update("update alipay_deal_order set orderQrUser = #{userId} ,orderQr = #{orderQr} , " +
            "retain2 = #{fee} , feeId =#{feeId}  ,retain3 = #{profit} , lockWit  = 0  , payImg = null,  enterPayTime  = null , grabOrder = 0   where orderId = #{orderId} ")
    int updateOrderQr(@Param("orderId") String orderId, @Param("userId") String userId,
                      @Param("orderQr") String orderQr, @Param("feeId") Long feeId,
                      @Param("fee") Double fee, @Param("profit") Double profit);

    @Select("select * from alipay_deal_order where orderId = #{order}")
    AlipayDealOrderEntity findOrderByOrderId(@Param("order") String order);

    @Select("select * from alipay_deal_order where associatedId = #{order}")
    AlipayDealOrderEntity findOrderByAssociatedId(@Param("order") String order);

    AlipayDealOrderEntity selectAlipayDealOrderEntityListSum(AlipayDealOrderEntity alipayDealOrderEntity);
    AlipayDealOrderEntity selectAlipayDealOrderEntityListSumCharen(AlipayDealOrderEntity alipayDealOrderEntity);



    @Update("update alipay_deal_order set  orderQr = '' , dealAmount = #{nowAmount}  , actualAmount = #{nowAmount} , dealFee = 0  , " +
            "        retain2 = #{fee} ,  retain3 = #{profit}  where orderId = #{orderId} ")
    int updateAmountOrder( @Param("nowAmount") Double nowAmount, @Param("orderId") String orderId , @Param("fee") Double fee,@Param("profit") Double profit);

    @Update("update alipay_deal_order set  lockWit = 0  " +
            "      where id = #{id} ")
    int updateUnLock(Long id);

    @Update("update alipay_deal_order set  lockWit = 2  " +
            "      where id = #{id} ")
    int updateLockWitToAuto(Long id);

    @Update("update alipay_deal_order set  lockWit = 1  " +
            "      where id = #{id} ")
    int updateLockWitToManual(Long id);

    List<AlipayDealOrderEntity> selectAlipayDealOrderEntityListIsCharen(AlipayDealOrderEntity alipayDealOrderEntity);

}
