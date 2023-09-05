package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.AlipayWithdrawEntity;
import com.ruoyi.common.core.domain.StatisticsEntity;
import com.ruoyi.common.core.domain.WitStatisticsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 会员提现记录Mapper接口
 *
 * @author kent
 * @date 2020-03-17
 */
public interface AlipayWithdrawEntityMapper {
    /**
     * 查询会员提现记录
     *
     * @param id 会员提现记录ID
     * @return 会员提现记录
     */
    AlipayWithdrawEntity selectAlipayWithdrawEntityById(Long id);

    /**
     * 查询会员提现记录
     *
     * @param orderIds 会员提现记录ID
     * @return 会员提现记录
     */
    List<AlipayWithdrawEntity> selectAlipayWithdrawEntityByIds(List<String> orderIds);



    List<AlipayWithdrawEntity> selectAlipayWithdrawEntityByIds2(List<String> ids);
    int updateByIds(List<String> ids);

    /**
     * 查询会员提现记录列表
     *
     * @param alipayWithdrawEntity 会员提现记录
     * @return 会员提现记录集合
     */
    List<AlipayWithdrawEntity> selectAlipayWithdrawEntityList(AlipayWithdrawEntity alipayWithdrawEntity);


    @Select("select " +
            "COALESCE(SUM(amount),0) totalAmount," +
            "COALESCE(SUM(CASE orderStatus WHEN 2 THEN amount ELSE 0 END),0) successAmount," +
            "COUNT(1) totalCount," +
            "COUNT(CASE orderStatus WHEN 2 THEN orderId ELSE null END) successCount " +
            "from " +
            "alipay_withdraw where createTime BETWEEN #{dayStart} AND #{dayEnd} and withdrawType = 1 and status = 1")
    StatisticsEntity selectPayforStatDataByDay(@Param("dayStart") String dayStart, @Param("dayEnd") String dayEnd);


    @Select("<script>" +
            "select " +
            " userId as userId ," +
            "COALESCE(SUM(amount),0) totalAmount," +
            "COALESCE(SUM(CASE orderStatus WHEN 2 THEN amount ELSE 0 END),0) successAmount," +
            "COUNT(1) totalCount," +
            "COUNT(CASE orderStatus WHEN 2 THEN orderId ELSE null END) successCount ," +
            "coalesce(CASE retain1 WHEN 2   THEN 'API' ELSE 'MANAGE' END) userAgent " +     //api代付标示
            "from " +
            "alipay_withdraw where " +
            "createTime BETWEEN #{statisticsEntity.params.dayStart} AND #{statisticsEntity.params.dayEnd} " +
            "and withdrawType = 1 and status = 1 " +
            "<if test = \"statisticsEntity.userId != null and statisticsEntity.userId != ''\">" +
            "and userId = #{statisticsEntity.userId} " +
            "</if>" +
            "group by userId, witChannel ,channelId , retain1" +
            "</script>")
    List<StatisticsEntity> statisticsWit(@Param("statisticsEntity") StatisticsEntity statisticsEntity);



    @Select("<script>\n" +
            "    SELECT\n" +
            "        userId,\n" +
            "        SUM(CASE WHEN orderStatus = 2 THEN 1 ELSE 0 END) AS totalSuccessOrders,\n" +
            "        SUM(CASE WHEN orderStatus = 1 THEN 1 ELSE 0 END) AS totalPendingOrders,\n" +
            "        SUM(CASE WHEN orderStatus = 3 THEN 1 ELSE 0 END) AS totalFailureOrders,\n" +
            "        COUNT(*) AS totalTransactions,\n" +
            "        SUM(CASE WHEN orderStatus = 1 THEN amount ELSE 0 END) AS totalPendingAmount,\n" +
            "        SUM(CASE WHEN orderStatus = 2 THEN amount ELSE 0 END) AS totalSuccessAmount,\n" +
            "        SUM(CASE WHEN orderStatus = 3 THEN amount ELSE 0 END) AS totalFailureAmount,\n" +
            "        COALESCE(SUM(fee), 0) AS totalFee,\n" +
            "        SUM(CASE WHEN orderStatus = 1 THEN actualAmount ELSE 0 END) AS totalPendingActualAmount,\n" +
            "        SUM(CASE WHEN orderStatus = 2 THEN actualAmount ELSE 0 END) AS totalSuccessActualAmount,\n" +
            "        SUM(CASE WHEN orderStatus = 3 THEN actualAmount ELSE 0 END) AS totalFailureActualAmount\n" +
            "    FROM alipay_withdraw\n" +
            "    WHERE createTime BETWEEN #{statisticsEntity.params.dayStart} AND #{statisticsEntity.params.dayEnd} AND status = 1\n" +
            "    <if test=\"statisticsEntity.userId != null and statisticsEntity.userId != ''\">\n" +
            "        AND userId = #{statisticsEntity.userId}\n" +
            "    </if>\n" +
            "    GROUP BY userId\n" +
            "</script>\n")
    List<WitStatisticsEntity> statisticsWitForDay(@Param("statisticsEntity") StatisticsEntity statisticsEntity);


    @Update("update alipay_withdraw set orderStatus = 4 where id = #{id}")
    void updateWitStatus(@Param("id") Long id);


    @Select("select * from alipay_withdraw where  createTime between #{starTime} and #{endTime} limit #{page} , #{size} ")
    List<AlipayWithdrawEntity> findWitLimit(@Param("starTime") String starTime, @Param("endTime") String endTime, @Param("page") Integer page, @Param("size") Integer size);

    AlipayWithdrawEntity selectAlipayWithdrawEntityListSum(AlipayWithdrawEntity alipayWithdrawEntity);


    /**
     * 查询代付订单
     * @return
     */
    @Select("select * from alipay_withdraw where   orderId = #{orderId}")
    AlipayWithdrawEntity findWitOrder(@Param("orderId")String orderId);
    @Update("update alipay_withdraw set watingTime = #{watingTime}   where id = #{id}")
    void batchUpdateMacthMoreWatingTime(@Param("id") String id,@Param("watingTime") Integer watingTime);
    @Update("update alipay_withdraw set moreMacth = #{moreMacth}   where id = #{id}")
    void updateMacthMoreById(String id, Integer moreMacth);

    void updateByPrimaryKeySelective(AlipayWithdrawEntity alipayWithdrawEntity);
 
}
