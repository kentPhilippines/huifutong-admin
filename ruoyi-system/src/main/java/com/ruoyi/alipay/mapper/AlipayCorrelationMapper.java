package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.AlipayCorrelation;
import com.ruoyi.alipay.vo.UserCountBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代理关系表Mapper接口
 * 
 * @author ruoyi
 * @date 2020-03-17
 */
public interface AlipayCorrelationMapper 
{
    /**
     * 查询代理关系表
     * 
     * @param id 代理关系表ID
     * @return 代理关系表
     */
    public AlipayCorrelation selectAlipayCorrelationById(Long id);

    /**
     * 查询代理关系表列表
     * 
     * @param alipayCorrelation 代理关系表
     * @return 代理关系表集合
     */
    public List<AlipayCorrelation> selectAlipayCorrelationList(AlipayCorrelation alipayCorrelation);

    /**
     * 新增代理关系表
     * 
     * @param alipayCorrelation 代理关系表
     * @return 结果
     */
    public int insertAlipayCorrelation(AlipayCorrelation alipayCorrelation);

    /**
     * 修改代理关系表
     * 
     * @param alipayCorrelation 代理关系表
     * @return 结果
     */
    public int updateAlipayCorrelation(AlipayCorrelation alipayCorrelation);

    /**
     * 删除代理关系表
     * 
     * @param id 代理关系表ID
     * @return 结果
     */
    public int deleteAlipayCorrelationById(Long id);

    /**
     * 批量删除代理关系表
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayCorrelationByIds(String[] ids);



    @Select(" SELECT " +
            " COUNT(1) AS userAgentCount , " + 													//总下线人数
            " COUNT(CASE WHEN  childrenType = 1 AND distance >1 THEN 1 END) AS  moreAgent,  " + //多级代理
            " COUNT(CASE WHEN  distance = 1 THEN 1 END)   AS agent,  " + 						//直接下级代理
            " COUNT(CASE WHEN  distance >1 THEN 1 END) AS agentCount,   " + 					//多级总下线
            " COUNT(CASE WHEN  distance = 1 AND childrenType = 2 THEN 1 END) AS userAgent,   " +  //直接会员
            " COUNT(CASE WHEN  childrenType = 2 AND distance >1 THEN 1 END) AS userCount   " + //多级会员
            " FROM alipay_correlation  WHERE parentName  = #{userId} AND distance > 0 ")
    UserCountBean findMyDateAgen(@Param("userId")String userId);
    @Select("  SELECT COUNT(1) FROM "
            + "alipay_correlation WHERE"
            + " parentName  = #{userId} AND distance > 0  " +
            "  ")
    int findMyUserCount(@Param("userId")String userId);
    @Select("select parentName from alipay_correlation WHERE childrenName = #{qrcodeId}  ORDER BY distance DESC LIMIT 1")
    String findAgent(@Param("qrcodeId")String qrcodeId);

    @Select("  SELECT " +
            "  SUM(CASE WHEN  orderStatus = 2 and orderType = 1  THEN dealAmount END) AS moreAmountRunR , " +
            "  SUM(CASE WHEN  orderStatus = 2 and orderType = 4  THEN dealAmount END) AS moreAmountRunW , " +
            "  SUM(CASE WHEN  orderStatus = 2  THEN 1 END) AS moreDealCount" +
            "  FROM alipay_deal_order WHERE orderQrUser IN (" +
            "  SELECT childrenName FROM alipay_correlation WHERE parentName  = #{userId} )  AND  TO_DAYS(createTime) = TO_DAYS(now())"
    )
    UserCountBean findDealDate(@Param("userId") String userId );
    /*@Select("select  count(1) as  dataArray " +
            " from `alipay_correlation_data`"
            + " where  userId in "
            + "( select childrenName from "
            + "alipay_correlation WHERE parentName  = #{userId}"
            + " AND distance > 0  ) and orderStatus = 2  AND  TO_DAYS(createTime) = TO_DAYS(NOW())"
            + "group by userId")
    List<DataArray> findOnline(@Param("userId")String userId);*/
}
