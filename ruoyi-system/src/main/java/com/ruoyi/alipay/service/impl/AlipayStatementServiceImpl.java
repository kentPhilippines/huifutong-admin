package com.ruoyi.alipay.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alipay.mapper.AlipayStatementMapper;
import com.ruoyi.alipay.domain.AlipayStatement;
import com.ruoyi.alipay.service.IAlipayStatementService;
import com.ruoyi.common.core.text.Convert;

/**
 * 对账Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-12-11
 */
@Service
public class AlipayStatementServiceImpl implements IAlipayStatementService 
{
    @Autowired
    private AlipayStatementMapper alipayStatementMapper;

    /**
     * 查询对账
     * 
     * @param id 对账ID
     * @return 对账
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayStatement selectAlipayStatementById(Long id)
    {
        return alipayStatementMapper.selectAlipayStatementById(id);
    }

    /**
     * 查询对账列表
     * 
     * @param alipayStatement 对账
     * @return 对账
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayStatement> selectAlipayStatementList(AlipayStatement alipayStatement)
    {
        return alipayStatementMapper.selectAlipayStatementList(alipayStatement);
    }

    /**
     * 新增对账
     * 
     * @param alipayStatement 对账
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayStatement(AlipayStatement alipayStatement)
    {
        return alipayStatementMapper.insertAlipayStatement(alipayStatement);
    }

    /**
     * 修改对账
     * 
     * @param alipayStatement 对账
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayStatement(AlipayStatement alipayStatement)
    {
        return alipayStatementMapper.updateAlipayStatement(alipayStatement);
    }

    /**
     * 删除对账对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayStatementByIds(String ids)
    {
        return alipayStatementMapper.deleteAlipayStatementByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除对账信息
     * 
     * @param id 对账ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayStatementById(Long id)
    {
        return alipayStatementMapper.deleteAlipayStatementById(id);
    }
}
