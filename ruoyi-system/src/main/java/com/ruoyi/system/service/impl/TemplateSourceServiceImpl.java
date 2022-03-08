package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TemplateSourceMapper;
import com.ruoyi.system.domain.TemplateSource;
import com.ruoyi.system.service.ITemplateSourceService;
import com.ruoyi.common.core.text.Convert;

/**
 * 来源匹配Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-08
 */
@Service
public class TemplateSourceServiceImpl implements ITemplateSourceService 
{
    @Autowired
    private TemplateSourceMapper templateSourceMapper;

    /**
     * 查询来源匹配
     * 
     * @param id 来源匹配ID
     * @return 来源匹配
     */
    @Override
    public TemplateSource selectTemplateSourceById(Long id)
    {
        return templateSourceMapper.selectTemplateSourceById(id);
    }

    /**
     * 查询来源匹配列表
     * 
     * @param templateSource 来源匹配
     * @return 来源匹配
     */
    @Override
    public List<TemplateSource> selectTemplateSourceList(TemplateSource templateSource)
    {
        return templateSourceMapper.selectTemplateSourceList(templateSource);
    }

    /**
     * 新增来源匹配
     * 
     * @param templateSource 来源匹配
     * @return 结果
     */
    @Override
    public int insertTemplateSource(TemplateSource templateSource)
    {
        return templateSourceMapper.insertTemplateSource(templateSource);
    }

    /**
     * 修改来源匹配
     * 
     * @param templateSource 来源匹配
     * @return 结果
     */
    @Override
    public int updateTemplateSource(TemplateSource templateSource)
    {
        return templateSourceMapper.updateTemplateSource(templateSource);
    }

    /**
     * 删除来源匹配对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteTemplateSourceByIds(String ids)
    {
        return templateSourceMapper.deleteTemplateSourceByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除来源匹配信息
     * 
     * @param id 来源匹配ID
     * @return 结果
     */
    @Override
    public int deleteTemplateSourceById(Long id)
    {
        return templateSourceMapper.deleteTemplateSourceById(id);
    }
}
