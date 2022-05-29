package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayCorrelation;
import com.ruoyi.alipay.vo.UserCountBean;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 代理关系表Service接口
 * 
 * @author ruoyi
 * @date 2020-03-17
 */
public interface IAlipayCorrelationService 
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

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayCorrelation> selectSubAlipayCorrelationList(AlipayCorrelation alipayCorrelation);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayCorrelation> selectByParentNameAlipayCorrelationList(AlipayCorrelation alipayCorrelation);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayCorrelation> selectTopAlipayCorrelationList(AlipayCorrelation alipayCorrelation);

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
     * 批量删除代理关系表
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayCorrelationByIds(String ids);

    /**
     * 删除代理关系表信息
     * 
     * @param id 代理关系表ID
     * @return 结果
     */
    public int deleteAlipayCorrelationById(Long id);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    UserCountBean findMyDateAgen(String userId);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    UserCountBean findDealDate(@NotNull String userId);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    int[][] findOnline(String userId);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    String findAgent(String qrcodeId);
}
