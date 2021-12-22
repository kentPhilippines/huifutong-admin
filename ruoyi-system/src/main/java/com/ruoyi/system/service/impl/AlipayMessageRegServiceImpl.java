package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AlipayMessageRegMapper;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.service.IAlipayMessageRegService;
import com.ruoyi.common.core.text.Convert;

/**
 * 短信正则模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-12-22
 */
@Service
public class AlipayMessageRegServiceImpl implements IAlipayMessageRegService 
{
    @Autowired
    private AlipayMessageRegMapper alipayMessageRegMapper;

    /**
     * 查询短信正则模板
     * 
     * @param id 短信正则模板ID
     * @return 短信正则模板
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMessageReg selectAlipayMessageRegById(Long id)
    {
        return alipayMessageRegMapper.selectAlipayMessageRegById(id);
    }

    /**
     * 查询短信正则模板列表
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 短信正则模板
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMessageReg> selectAlipayMessageRegList(AlipayMessageReg alipayMessageReg)
    {
        return alipayMessageRegMapper.selectAlipayMessageRegList(alipayMessageReg);
    }

    /**
     * 新增短信正则模板
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayMessageReg(AlipayMessageReg alipayMessageReg)
    {

        return alipayMessageRegMapper.insertAlipayMessageReg(alipayMessageReg);
    }

    /**
     * 修改短信正则模板
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayMessageReg(AlipayMessageReg alipayMessageReg)
    {
        return alipayMessageRegMapper.updateAlipayMessageReg(alipayMessageReg);
    }

    /**
     * 删除短信正则模板对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMessageRegByIds(String ids)
    {
        return alipayMessageRegMapper.deleteAlipayMessageRegByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除短信正则模板信息
     * 
     * @param id 短信正则模板ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMessageRegById(Long id)
    {
        return alipayMessageRegMapper.deleteAlipayMessageRegById(id);
    }
}
