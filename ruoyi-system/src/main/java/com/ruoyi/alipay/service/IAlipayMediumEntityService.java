package com.ruoyi.alipay.service;

import com.alicp.jetcache.anno.Cached;
import com.ruoyi.alipay.domain.AlipayMediumEntity;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.system.domain.ImportBankVerifyDto;
import com.ruoyi.system.domain.SysUser;

import java.util.List;

/**
 * 收款媒介列Service接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface IAlipayMediumEntityService {
    List<AlipayMediumEntity> selectAll();

    public List<AlipayMediumEntity> selectByIds(List<String> ids);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayMediumEntity> selectByMediumIds(List<String> ids);

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
    public int updateAlipayMediumEntity(AlipayMediumEntity alipayMediumEntity) ;

    /**
     * 修改收款媒介列 by bankName
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
     * 批量删除收款媒介列
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayMediumEntityByIds(String ids);

    /**
     * 删除收款媒介列信息
     *
     * @param id 收款媒介列ID
     * @return 结果
     */
    public int deleteAlipayMediumEntityById(AlipayMediumEntity alipayMediumEntity, SysUser sysUser);

    //int deleteAlipayMediumEntityById(AlipayMediumEntity alipayMediumEntity);

    AlipayMediumEntity findUserId(String med);

    /**
     * 查询 收款媒介 汇总信息
     * @param userId
     * @return
     */
    AlipayMediumEntity findBankSum(String userId);

    List<AlipayMediumEntity> findOpenMed();

    /**
     * 查询所有的银行名称
     * @return
     */
    public List<String> findAllBankNames();





    List<AlipayMediumEntity> findMedSum();

    String importData(List<ImportBankVerifyDto> dataList, boolean updateSupport, String operName);
}
