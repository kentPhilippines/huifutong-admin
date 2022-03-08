package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.TemplateSource;
import java.util.List;

/**
 * 来源匹配Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-08
 */
public interface TemplateSourceMapper 
{
    /**
     * 查询来源匹配
     * 
     * @param id 来源匹配ID
     * @return 来源匹配
     */
    public TemplateSource selectTemplateSourceById(Long id);

    /**
     * 查询来源匹配列表
     * 
     * @param templateSource 来源匹配
     * @return 来源匹配集合
     */
    public List<TemplateSource> selectTemplateSourceList(TemplateSource templateSource);

    /**
     * 新增来源匹配
     * 
     * @param templateSource 来源匹配
     * @return 结果
     */
    public int insertTemplateSource(TemplateSource templateSource);

    /**
     * 修改来源匹配
     * 
     * @param templateSource 来源匹配
     * @return 结果
     */
    public int updateTemplateSource(TemplateSource templateSource);

    /**
     * 删除来源匹配
     * 
     * @param id 来源匹配ID
     * @return 结果
     */
    public int deleteTemplateSourceById(Long id);

    /**
     * 批量删除来源匹配
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTemplateSourceByIds(String[] ids);
}
