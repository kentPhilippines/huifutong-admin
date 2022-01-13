package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.AlipayMediumEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 收款媒介列Mapper接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface AlipayMediumEntityMapper {
    public List<AlipayMediumEntity> selectAlipayMediumEntityByIds(List list);
    public List<AlipayMediumEntity> selectByMediumIds(List list);
    /**
     * 查询收款媒介列
     *
     * @param id 收款媒介列ID
     * @return 收款媒介列
     */
    public AlipayMediumEntity selectAlipayMediumEntityById(Long id);

    /**
     * 查询收款媒介列列表
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 收款媒介列集合
     */
    public List<AlipayMediumEntity> selectAlipayMediumEntityList(AlipayMediumEntity alipayMediumEntity);

    /**
     * 新增收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    public int insertAlipayMediumEntity(AlipayMediumEntity alipayMediumEntity);

    /**
     * 修改收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    public int updateAlipayMediumEntity(AlipayMediumEntity alipayMediumEntity);


    /**
     * 修改收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    public int updateAlipayMediumEntityByBankName(AlipayMediumEntity alipayMediumEntity);



    /**
     * 根据code修改上限金额
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    public int updateAlipayMediumEntityByCode(AlipayMediumEntity alipayMediumEntity);

    /**
     * 删除收款媒介列
     *
     * @param id 收款媒介列ID
     * @return 结果
     */
    public int deleteAlipayMediumEntityById(Long id);

    /**
     * 批量删除收款媒介列
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayMediumEntityByIds(String[] ids);

    AlipayMediumEntity findUserId(@Param("mediumNumber") String mediumNumber);




    @Select(
            "SELECT  sum(mountNow) as mountNow ,   " +
            "sum(mountSystem) as mountSystem , " +
            " 'all' as amounttype " +
            "from   alipay_medium   " +
            "WHERE  qrcodeId  =  #{userId}  and isDeal  = '2'      " +
            "union all  " +
            "SELECT  " +
            "sum(mountNow) as mountNow ,  " +
            "sum(mountSystem) as mountSystem, " +
            " 'open' as amounttype  " +
            "from  alipay_medium  " +
            "WHERE  qrcodeId  = #{userId} and isDeal  = '2' and status = 1  ")
    List<AlipayMediumEntity> findBankSum(@Param("userId") String userId);




    @Select("select * from alipay_medium where   isDeal  = '2' and status = 1")
    List<AlipayMediumEntity> findOpenMed();


    @Select("select account from alipay_medium group by account")
    List<String> findAllBankNames();

    @Select("select * from alipay_medium ")
    List<AlipayMediumEntity> findAll();




    @Select(" select sum(mountNow) as mountNow ,  sum( toDayDeal - toDayWit  + yseToday ) as toDayDeal  , qrcodeId  from huifutong_alipay.alipay_medium group by qrcodeId ")
    List<AlipayMediumEntity> findMedSum();
}
